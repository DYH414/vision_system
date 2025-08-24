package com.ruoyi.visionrecord.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.visionrecord.config.DeepSeekConfig;
import com.ruoyi.visionrecord.service.IDeepSeekService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * DeepSeek AI服务实现类
 * 
 * @author system
 */
@Service
public class DeepSeekServiceImpl implements IDeepSeekService {
    private static final Logger log = LoggerFactory.getLogger(DeepSeekServiceImpl.class);

    @Autowired
    private DeepSeekConfig deepSeekConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String generateAnalysisReport(String prompt) throws Exception {
        if (StringUtils.isEmpty(deepSeekConfig.getApiKey())) {
            throw new Exception("DeepSeek API密钥未配置");
        }

        if (StringUtils.isEmpty(prompt)) {
            throw new Exception("分析提示词不能为空");
        }

        // 智能重试机制
        Exception lastException = null;
        for (int i = 0; i < deepSeekConfig.getMaxRetry(); i++) {
            try {
                return callDeepSeekAPI(prompt);
            } catch (Exception e) {
                lastException = e;
                String errorType = getErrorType(e);
                log.warn("DeepSeek API调用失败，第{}次重试，错误类型: {}, 错误信息: {}", i + 1, errorType, e.getMessage());

                if (i < deepSeekConfig.getMaxRetry() - 1) {
                    long delay = calculateRetryDelay(i);
                    log.info("等待{}毫秒后进行第{}次重试", delay, i + 2);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new Exception("重试过程被中断", ie);
                    }
                }
            }
        }

        throw new Exception("DeepSeek API调用失败，已重试" + deepSeekConfig.getMaxRetry() + "次", lastException);
    }

    @Override
    public boolean checkApiConnection() {
        try {
            log.info("开始检查DeepSeek API连接...");
            log.info("API配置 - Base URL: {}, Model: {}, API Key: {}***",
                    deepSeekConfig.getBaseUrl(),
                    deepSeekConfig.getModel(),
                    deepSeekConfig.getApiKey().substring(0, Math.min(10, deepSeekConfig.getApiKey().length())));

            // 先进行网络诊断
            boolean networkOk = checkNetworkConnectivity();
            if (!networkOk) {
                log.warn("网络连通性检查失败，但仍尝试API调用");
            }

            // 使用简单的测试消息
            String result = generateAnalysisReport("Hello, this is a connection test.");
            log.info("API连接测试成功，响应: {}", result.substring(0, Math.min(100, result.length())) + "...");
            return true;
        } catch (Exception e) {
            log.error("DeepSeek API连接检查失败: {}", e.getMessage(), e);
            // API调用失败时自动进行网络诊断
            checkNetworkConnectivity();
            return false;
        }
    }

    /**
     * 调用DeepSeek API
     * 
     * @param prompt 提示词
     * @return API响应内容
     * @throws Exception 调用异常
     */
    private String callDeepSeekAPI(String prompt) throws Exception {
        String apiUrl = deepSeekConfig.getBaseUrl() + "/chat/completions";

        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + deepSeekConfig.getApiKey());

        // 构建请求体
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", deepSeekConfig.getModel());
        requestBody.put("max_tokens", deepSeekConfig.getMaxTokens());
        requestBody.put("temperature", deepSeekConfig.getTemperature());

        JSONArray messages = new JSONArray();
        // 添加系统消息
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant that provides detailed health analysis reports.");
        messages.add(systemMessage);

        // 添加用户消息
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        requestBody.put("messages", messages);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toJSONString(), headers);

        long startTime = System.currentTimeMillis();
        log.info("调用DeepSeek API: {}", apiUrl);
        log.info("请求参数: model={}, max_tokens={}, temperature={}",
                deepSeekConfig.getModel(), deepSeekConfig.getMaxTokens(), deepSeekConfig.getTemperature());
        log.debug("完整请求体: {}", requestBody.toJSONString());

        try {
            // 发送请求
            String response = restTemplate.postForObject(apiUrl, entity, String.class);
            long duration = System.currentTimeMillis() - startTime;

            log.info("DeepSeek API调用成功，耗时: {}ms, 响应长度: {}",
                    duration, response != null ? response.length() : 0);

            if (StringUtils.isEmpty(response)) {
                throw new Exception("DeepSeek API返回空响应");
            }

            // 解析响应
            return parseResponse(response);

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("DeepSeek API客户端错误，耗时: {}ms, HTTP状态: {}, 响应: {}",
                    duration, e.getStatusCode(), e.getResponseBodyAsString());
            throw new Exception("API客户端错误: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);

        } catch (org.springframework.web.client.HttpServerErrorException e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("DeepSeek API服务器错误，耗时: {}ms, HTTP状态: {}, 响应: {}",
                    duration, e.getStatusCode(), e.getResponseBodyAsString());
            throw new Exception("API服务器错误: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);

        } catch (org.springframework.web.client.ResourceAccessException e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("DeepSeek API资源访问异常，耗时: {}ms, 错误: {}", duration, e.getMessage());

            if (e.getCause() instanceof java.net.SocketTimeoutException) {
                throw new Exception("API调用读取超时，请检查网络连接或增加超时时间", e);
            } else if (e.getCause() instanceof java.net.ConnectException) {
                throw new Exception("API调用连接超时，请检查网络连接", e);
            } else {
                throw new Exception("API调用网络异常: " + e.getMessage(), e);
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("DeepSeek API调用未知异常，耗时: {}ms, 错误: {}", duration, e.getMessage(), e);
            throw new Exception("API调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析API响应
     * 
     * @param response API响应JSON字符串
     * @return 分析报告内容
     * @throws Exception 解析异常
     */
    private String parseResponse(String response) throws Exception {
        try {
            JSONObject jsonResponse = JSON.parseObject(response);

            // 检查是否有错误
            if (jsonResponse.containsKey("error")) {
                JSONObject error = jsonResponse.getJSONObject("error");
                throw new Exception("DeepSeek API错误: " + error.getString("message"));
            }

            // 提取内容
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices == null || choices.isEmpty()) {
                throw new Exception("DeepSeek API响应中没有choices数据");
            }

            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            String content = message.getString("content");

            if (StringUtils.isEmpty(content)) {
                throw new Exception("DeepSeek API返回的内容为空");
            }

            log.info("DeepSeek API调用成功，返回内容长度: {}", content.length());
            return content;
        } catch (Exception e) {
            log.error("解析DeepSeek API响应失败: {}", e.getMessage());
            log.debug("响应内容: {}", response);
            throw new Exception("解析API响应失败: " + e.getMessage(), e);
        }
    }

    /**
     * 计算重试延迟时间
     * 
     * @param retryCount 重试次数
     * @return 延迟时间(毫秒)
     */
    private long calculateRetryDelay(int retryCount) {
        long baseDelay = deepSeekConfig.getRetryDelay();

        if (deepSeekConfig.getRetryBackoff()) {
            // 指数退避算法：延迟时间 = 基础延迟 × 2^重试次数
            long delay = baseDelay * (long) Math.pow(2, retryCount);
            // 限制最大延迟时间为30秒
            return Math.min(delay, 30000L);
        } else {
            // 固定延迟
            return baseDelay;
        }
    }

    /**
     * 获取异常类型
     * 
     * @param e 异常
     * @return 异常类型描述
     */
    private String getErrorType(Exception e) {
        if (e instanceof java.net.SocketTimeoutException) {
            return "读取超时";
        } else if (e instanceof java.net.ConnectException) {
            return "连接超时";
        } else if (e.getMessage() != null && e.getMessage().contains("timeout")) {
            return "网络超时";
        } else if (e.getMessage() != null && e.getMessage().contains("Connection refused")) {
            return "连接被拒绝";
        } else if (e.getMessage() != null && e.getMessage().contains("UnknownHostException")) {
            return "DNS解析失败";
        } else {
            return "其他错误";
        }
    }

    /**
     * 网络连通性检查
     * 
     * @return 网络是否正常
     */
    private boolean checkNetworkConnectivity() {
        log.info("开始网络诊断...");
        boolean allOk = true;

        // 1. DNS解析测试
        try {
            long startTime = System.currentTimeMillis();
            java.net.InetAddress.getByName("api.deepseek.com");
            long dnsTime = System.currentTimeMillis() - startTime;
            log.info("DNS解析成功，耗时: {}ms", dnsTime);

            if (dnsTime > 5000) {
                log.warn("DNS解析较慢，耗时: {}ms", dnsTime);
            }
        } catch (Exception e) {
            log.error("DNS解析失败: {}", e.getMessage());
            allOk = false;
        }

        // 2. 端口连通性测试
        try {
            long startTime = System.currentTimeMillis();
            java.net.Socket socket = new java.net.Socket();
            socket.connect(new java.net.InetSocketAddress("api.deepseek.com", 443), 10000);
            socket.close();
            long connectTime = System.currentTimeMillis() - startTime;
            log.info("端口连通性检查成功，耗时: {}ms", connectTime);

            if (connectTime > 3000) {
                log.warn("网络连接较慢，耗时: {}ms", connectTime);
            }
        } catch (Exception e) {
            log.error("端口连通性检查失败: {}", e.getMessage());
            allOk = false;
        }

        // 3. 网络质量评估
        if (allOk) {
            log.info("网络诊断完成，连接正常");
        } else {
            log.warn("网络诊断完成，发现连接问题");
        }

        return allOk;
    }
}