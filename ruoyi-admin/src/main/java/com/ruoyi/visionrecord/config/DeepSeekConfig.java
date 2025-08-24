package com.ruoyi.visionrecord.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DeepSeek AI配置类
 * 
 * @author system
 */
@Component
@ConfigurationProperties(prefix = "deepseek")
public class DeepSeekConfig {
    /** API密钥 */
    private String apiKey;

    /** API端点URL */
    private String baseUrl = "https://api.deepseek.com";

    /** 模型名称 */
    private String model = "deepseek-chat";

    /** 连接超时时间(毫秒) */
    private Integer connectTimeout = 15000;

    /** 读取超时时间(毫秒) */
    private Integer readTimeout = 90000;

    /** 请求超时时间(毫秒) - 保持向后兼容 */
    private Integer timeout = 30000;

    /** 最大token数 */
    private Integer maxTokens = 2000;

    /** 温度参数 */
    private Double temperature = 0.7;

    /** 重试次数 */
    private Integer maxRetry = 3;

    /** 重试延迟(毫秒) */
    private Long retryDelay = 2000L;

    /** 启用指数退避重试 */
    private Boolean retryBackoff = true;

    /** 代理是否启用 */
    private Boolean proxyEnabled = false;

    /** 代理主机 */
    private String proxyHost;

    /** 代理端口 */
    private Integer proxyPort;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(Integer maxRetry) {
        this.maxRetry = maxRetry;
    }

    public Long getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(Long retryDelay) {
        this.retryDelay = retryDelay;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Boolean getRetryBackoff() {
        return retryBackoff;
    }

    public void setRetryBackoff(Boolean retryBackoff) {
        this.retryBackoff = retryBackoff;
    }

    public Boolean isProxyEnabled() {
        return proxyEnabled;
    }

    public void setProxyEnabled(Boolean proxyEnabled) {
        this.proxyEnabled = proxyEnabled;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }
}