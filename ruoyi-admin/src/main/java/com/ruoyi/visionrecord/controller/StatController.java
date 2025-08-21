package com.ruoyi.visionrecord.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.visionrecord.domain.dto.StatQueryDTO;
import com.ruoyi.visionrecord.domain.vo.AgeGroupCompareVO;
import com.ruoyi.visionrecord.domain.vo.GenderCompareVO;
import com.ruoyi.visionrecord.domain.vo.StatTrendVO;
import com.ruoyi.visionrecord.domain.vo.StatVO;
import com.ruoyi.visionrecord.service.IStatService;

/**
 * 视力统计分析控制器
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/visionrecord/stat")
public class StatController extends BaseController {

    @Autowired
    private IStatService statService;

    /**
     * 获取年度视力统计数据
     */
    @PreAuthorize("@ss.hasPermi('vision:stat:view')")
    @GetMapping("/annual")
    public AjaxResult getAnnualStat(@RequestParam(required = false) Integer year) {
        StatVO statVO = statService.getAnnualStat(year);
        return AjaxResult.success(statVO);
    }

    /**
     * 获取视力趋势对比数据
     */
    @PreAuthorize("@ss.hasPermi('vision:stat:view')")
    @GetMapping("/trend")
    public AjaxResult getTrendStat(@RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear) {
        StatQueryDTO queryDTO = new StatQueryDTO();
        queryDTO.setStartYear(startYear);
        queryDTO.setEndYear(endYear);
        List<StatTrendVO> trendList = statService.getTrendStat(queryDTO);
        return AjaxResult.success(trendList);
    }

    /**
     * 获取性别维度视力对比数据
     */
    @PreAuthorize("@ss.hasPermi('vision:stat:view')")
    @GetMapping("/gender")
    public AjaxResult getGenderCompareStat(@RequestParam(required = false) Integer year) {
        List<GenderCompareVO> genderList = statService.getGenderCompareStat(year);
        return AjaxResult.success(genderList);
    }

    /**
     * 获取年龄段维度视力对比数据
     */
    @PreAuthorize("@ss.hasPermi('vision:stat:view')")
    @GetMapping("/agegroup")
    public AjaxResult getAgeGroupCompareStat(@RequestParam(required = false) Integer year) {
        List<AgeGroupCompareVO> ageGroupList = statService.getAgeGroupCompareStat(year);
        return AjaxResult.success(ageGroupList);
    }

    /**
     * 获取智能分析报告
     */
    @PreAuthorize("@ss.hasPermi('vision:stat:view')")
    @GetMapping("/analysis")
    public AjaxResult getAiAnalysisReport(@RequestParam(required = false) Integer year) {
        String report = statService.getAiAnalysisReport(year);
        return AjaxResult.success("获取成功", report);
    }
}