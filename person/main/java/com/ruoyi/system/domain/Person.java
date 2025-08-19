package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 人员信息对象 person
 * 
 * @author ruoyi
 * @date 2025-08-19
 */
public class Person extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 人员ID */
    private Long id;

    /** 姓名 */
    @Excel(name = "姓名")
    private String name;

    /** 性别 M/F */
    @Excel(name = "性别 M/F")
    private String gender;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出生日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date birthDate;

    /** 联系方式 */
    @Excel(name = "联系方式")
    private String phone;

    /** 身份证号（唯一） */
    @Excel(name = "身份证号", readConverterExp = "唯=一")
    private String idCard;

    /** 体检编号（院内编号） */
    @Excel(name = "体检编号", readConverterExp = "院=内编号")
    private String checkCode;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setGender(String gender) 
    {
        this.gender = gender;
    }

    public String getGender() 
    {
        return gender;
    }

    public void setBirthDate(Date birthDate) 
    {
        this.birthDate = birthDate;
    }

    public Date getBirthDate() 
    {
        return birthDate;
    }

    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }

    public void setIdCard(String idCard) 
    {
        this.idCard = idCard;
    }

    public String getIdCard() 
    {
        return idCard;
    }

    public void setCheckCode(String checkCode) 
    {
        this.checkCode = checkCode;
    }

    public String getCheckCode() 
    {
        return checkCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("gender", getGender())
            .append("birthDate", getBirthDate())
            .append("phone", getPhone())
            .append("idCard", getIdCard())
            .append("checkCode", getCheckCode())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
