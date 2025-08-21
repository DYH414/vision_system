package com.ruoyi.visionrecord.domain.vo;

import java.math.BigDecimal;

/**
 * 视力统计趋势分析数据视图对象
 * 
 * @author ruoyi
 */
public class StatTrendVO {
    /** 年度 */
    private Integer year;

    /** 左眼平均视力 */
    private BigDecimal avgLeft;

    /** 右眼平均视力 */
    private BigDecimal avgRight;

    /** 视力正常率 */
    private BigDecimal normalRate;

    /** 近视率 */
    private BigDecimal myopiaRate;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getAvgLeft() {
        return avgLeft;
    }

    public void setAvgLeft(BigDecimal avgLeft) {
        this.avgLeft = avgLeft;
    }

    public BigDecimal getAvgRight() {
        return avgRight;
    }

    public void setAvgRight(BigDecimal avgRight) {
        this.avgRight = avgRight;
    }

    public BigDecimal getNormalRate() {
        return normalRate;
    }

    public void setNormalRate(BigDecimal normalRate) {
        this.normalRate = normalRate;
    }

    public BigDecimal getMyopiaRate() {
        return myopiaRate;
    }

    public void setMyopiaRate(BigDecimal myopiaRate) {
        this.myopiaRate = myopiaRate;
    }
}