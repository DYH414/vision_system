package com.ruoyi.visionrecord.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 视力监测记录对象 vision_record
 * 
 * @author dyh
 * @date 2025-08-20
 */
public class VisionRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long id;

    /** 人员ID */
    @Excel(name = "人员ID")
    private Long personId;

    /** 检测日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "检测日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date checkDate;

    /** 年度 */
    @Excel(name = "年度")
    private Long year;

    /** 左眼视力(小数制0.1-2.0建议) */
    @Excel(name = "左眼视力(小数制0.1-2.0建议)")
    private BigDecimal leftEye;

    /** 右眼视力 */
    @Excel(name = "右眼视力")
    private BigDecimal rightEye;

    /** 诊断（正常/近视/散光/其他） */
    @Excel(name = "诊断", readConverterExp = "正=常/近视/散光/其他")
    private String diagnosis;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setPersonId(Long personId) 
    {
        this.personId = personId;
    }

    public Long getPersonId() 
    {
        return personId;
    }

    public void setCheckDate(Date checkDate) 
    {
        this.checkDate = checkDate;
    }

    public Date getCheckDate() 
    {
        return checkDate;
    }

    public void setYear(Long year) 
    {
        this.year = year;
    }

    public Long getYear() 
    {
        return year;
    }

    public void setLeftEye(BigDecimal leftEye) 
    {
        this.leftEye = leftEye;
    }

    public BigDecimal getLeftEye() 
    {
        return leftEye;
    }

    public void setRightEye(BigDecimal rightEye) 
    {
        this.rightEye = rightEye;
    }

    public BigDecimal getRightEye() 
    {
        return rightEye;
    }

    public void setDiagnosis(String diagnosis) 
    {
        this.diagnosis = diagnosis;
    }

    public String getDiagnosis() 
    {
        return diagnosis;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("personId", getPersonId())
            .append("checkDate", getCheckDate())
            .append("year", getYear())
            .append("leftEye", getLeftEye())
            .append("rightEye", getRightEye())
            .append("diagnosis", getDiagnosis())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
