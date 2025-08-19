package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.PersonMapper;
import com.ruoyi.system.domain.Person;
import com.ruoyi.system.service.IPersonService;

/**
 * 人员信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-08-19
 */
@Service
public class PersonServiceImpl implements IPersonService 
{
    @Autowired
    private PersonMapper personMapper;

    /**
     * 查询人员信息
     * 
     * @param id 人员信息主键
     * @return 人员信息
     */
    @Override
    public Person selectPersonById(Long id)
    {
        return personMapper.selectPersonById(id);
    }

    /**
     * 查询人员信息列表
     * 
     * @param person 人员信息
     * @return 人员信息
     */
    @Override
    public List<Person> selectPersonList(Person person)
    {
        return personMapper.selectPersonList(person);
    }

    /**
     * 新增人员信息
     * 
     * @param person 人员信息
     * @return 结果
     */
    @Override
    public int insertPerson(Person person)
    {
        person.setCreateTime(DateUtils.getNowDate());
        return personMapper.insertPerson(person);
    }

    /**
     * 修改人员信息
     * 
     * @param person 人员信息
     * @return 结果
     */
    @Override
    public int updatePerson(Person person)
    {
        person.setUpdateTime(DateUtils.getNowDate());
        return personMapper.updatePerson(person);
    }

    /**
     * 批量删除人员信息
     * 
     * @param ids 需要删除的人员信息主键
     * @return 结果
     */
    @Override
    public int deletePersonByIds(Long[] ids)
    {
        return personMapper.deletePersonByIds(ids);
    }

    /**
     * 删除人员信息信息
     * 
     * @param id 人员信息主键
     * @return 结果
     */
    @Override
    public int deletePersonById(Long id)
    {
        return personMapper.deletePersonById(id);
    }
}
