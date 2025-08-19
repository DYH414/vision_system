package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Person;

/**
 * 人员信息Service接口
 * 
 * @author ruoyi
 * @date 2025-08-19
 */
public interface IPersonService 
{
    /**
     * 查询人员信息
     * 
     * @param id 人员信息主键
     * @return 人员信息
     */
    public Person selectPersonById(Long id);

    /**
     * 查询人员信息列表
     * 
     * @param person 人员信息
     * @return 人员信息集合
     */
    public List<Person> selectPersonList(Person person);

    /**
     * 新增人员信息
     * 
     * @param person 人员信息
     * @return 结果
     */
    public int insertPerson(Person person);

    /**
     * 修改人员信息
     * 
     * @param person 人员信息
     * @return 结果
     */
    public int updatePerson(Person person);

    /**
     * 批量删除人员信息
     * 
     * @param ids 需要删除的人员信息主键集合
     * @return 结果
     */
    public int deletePersonByIds(Long[] ids);

    /**
     * 删除人员信息信息
     * 
     * @param id 人员信息主键
     * @return 结果
     */
    public int deletePersonById(Long id);
}
