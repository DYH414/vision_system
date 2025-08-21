package com.ruoyi.visionrecord.domain.vo;

import java.math.BigDecimal;

/**
 * 性别对比分析数据视图对象
 * 
 * @author ruoyi
 */
public class GenderCompareVO {
    /** 性别 */
    private String gender;

    /** 总人数 */
    private Integer total;

    /** 平均视力 */
    private BigDecimal avgVision;

    /** 视力正常率 */
    private BigDecimal normalRate;

    /** 轻度视力问题率 */
    private BigDecimal mildRate;

    /** 中度视力问题率 */
    private BigDecimal moderateRate;

    /** 重度视力问题率 */
    private BigDecimal highRate;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public BigDecimal getAvgVision() {
        return avgVision;
    }

    public void setAvgVision(BigDecimal avgVision) {
        this.avgVision = avgVision;
    }

    public BigDecimal getNormalRate() {
        return normalRate;
    }

    public void setNormalRate(BigDecimal normalRate) {
        this.normalRate = normalRate;
    }

    public BigDecimal getMildRate() {
        return mildRate;
    }

    public void setMildRate(BigDecimal mildRate) {
        this.mildRate = mildRate;
    }

    public BigDecimal getModerateRate() {
        return moderateRate;
    }

    public void setModerateRate(BigDecimal moderateRate) {
        this.moderateRate = moderateRate;
    }

    public BigDecimal getHighRate() {
        return highRate;
    }

    public void setHighRate(BigDecimal highRate) {
        this.highRate = highRate;
    }
}