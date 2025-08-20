package com.ruoyi.visionrecord.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.visionrecord.domain.Person;
import com.ruoyi.visionrecord.service.IPersonService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 人员信息Controller
 * 
 * @author dyh
 * @date 2025-08-20
 */
@RestController
@RequestMapping("/visionrecord/person")
public class PersonController extends BaseController
{
    @Autowired
    private IPersonService personService;

    /**
     * 查询人员信息列表
     */
    @PreAuthorize("@ss.hasPermi('visionrecord:person:list')")
    @GetMapping("/list")
    public TableDataInfo list(Person person)
    {
        startPage();
        List<Person> list = personService.selectPersonList(person);
        return getDataTable(list);
    }

    /**
     * 导出人员信息列表
     */
    @PreAuthorize("@ss.hasPermi('visionrecord:person:export')")
    @Log(title = "人员信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Person person)
    {
        List<Person> list = personService.selectPersonList(person);
        ExcelUtil<Person> util = new ExcelUtil<Person>(Person.class);
        util.exportExcel(response, list, "人员信息数据");
    }

    /**
     * 获取人员信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('visionrecord:person:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(personService.selectPersonById(id));
    }

    /**
     * 新增人员信息
     */
    @PreAuthorize("@ss.hasPermi('visionrecord:person:add')")
    @Log(title = "人员信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Person person)
    {
        return toAjax(personService.insertPerson(person));
    }

    /**
     * 修改人员信息
     */
    @PreAuthorize("@ss.hasPermi('visionrecord:person:edit')")
    @Log(title = "人员信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Person person)
    {
        return toAjax(personService.updatePerson(person));
    }

    /**
     * 删除人员信息
     */
    @PreAuthorize("@ss.hasPermi('visionrecord:person:remove')")
    @Log(title = "人员信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(personService.deletePersonByIds(ids));
    }
}
