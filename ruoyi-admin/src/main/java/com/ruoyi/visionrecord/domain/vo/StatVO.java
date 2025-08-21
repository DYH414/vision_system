package com.ruoyi.visionrecord.domain.vo;

import java.math.BigDecimal;

/**
 * 视力统计分析数据视图对象
 * 
 * @author ruoyi
 */
public class StatVO {
    /** 年度 */
    private Integer year;

    /** 总检测人数 */
    private Integer total;

    /** 左眼平均视力 */
    private BigDecimal avgLeft;

    /** 右眼平均视力 */
    private BigDecimal avgRight;

    /** 视力正常率 */
    private BigDecimal normalRate;

    /** 轻度视力问题率 */
    private BigDecimal mildRate;

    /** 中度视力问题率 */
    private BigDecimal moderateRate;

    /** 重度视力问题率 */
    private BigDecimal highRate;

    /** 近视率 */
    private BigDecimal myopiaRate;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
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

    public BigDecimal getMyopiaRate() {
        return myopiaRate;
    }

    public void setMyopiaRate(BigDecimal myopiaRate) {
        this.myopiaRate = myopiaRate;
    }
}