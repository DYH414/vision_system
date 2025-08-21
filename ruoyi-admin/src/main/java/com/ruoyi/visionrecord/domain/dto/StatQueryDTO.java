package com.ruoyi.visionrecord.domain.dto;

/**
 * 视力统计分析查询参数
 * 
 * @author ruoyi
 */
public class StatQueryDTO {
    /** 查询年度 */
    private Integer year;

    /** 开始年度 */
    private Integer startYear;

    /** 结束年度 */
    private Integer endYear;

    /** 性别 */
    private String gender;

    /** 年龄段 */
    private String ageGroup;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public Integer getEndYear() {
        return endYear;
    }

    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
}