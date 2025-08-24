package com.ruoyi.visionrecord.service;

/**
 * DeepSeek AI服务接口
 * 
 * @author system
 */
public interface IDeepSeekService {
    /**
     * 生成AI分析报告
     * 
     * @param prompt 分析提示词
     * @return AI生成的分析报告
     * @throws Exception 调用异常
     */
    String generateAnalysisReport(String prompt) throws Exception;

    /**
     * 检查API连接状态
     * 
     * @return 连接是否正常
     */
    boolean checkApiConnection();
}