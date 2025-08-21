package com.ruoyi.visionrecord.service;

import java.util.List;
import com.ruoyi.visionrecord.domain.dto.StatQueryDTO;
import com.ruoyi.visionrecord.domain.vo.AgeGroupCompareVO;
import com.ruoyi.visionrecord.domain.vo.GenderCompareVO;
import com.ruoyi.visionrecord.domain.vo.StatTrendVO;
import com.ruoyi.visionrecord.domain.vo.StatVO;

/**
 * 视力统计分析服务接口
 * 
 * @author ruoyi
 */
public interface IStatService {
    /**
     * 获取年度视力统计数据
     * 
     * @param year 年度
     * @return 统计数据
     */
    public StatVO getAnnualStat(Integer year);

    /**
     * 获取视力趋势对比数据
     * 
     * @param queryDTO 查询条件
     * @return 趋势数据列表
     */
    public List<StatTrendVO> getTrendStat(StatQueryDTO queryDTO);

    /**
     * 获取性别维度视力对比数据
     * 
     * @param year 年度
     * @return 性别对比数据列表
     */
    public List<GenderCompareVO> getGenderCompareStat(Integer year);

    /**
     * 获取年龄段维度视力对比数据
     * 
     * @param year 年度
     * @return 年龄段对比数据列表
     */
    public List<AgeGroupCompareVO> getAgeGroupCompareStat(Integer year);

    /**
     * 获取智能分析报告
     * 
     * @param year 年度
     * @return 分析报告内容
     */
    public String getAiAnalysisReport(Integer year);
}