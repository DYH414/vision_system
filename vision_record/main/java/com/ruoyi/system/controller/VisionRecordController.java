package com.ruoyi.system.controller;

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
import com.ruoyi.system.domain.VisionRecord;
import com.ruoyi.system.service.IVisionRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 视力监测记录Controller
 * 
 * @author ruoyi
 * @date 2025-08-19
 */
@RestController
@RequestMapping("/system/record")
public class VisionRecordController extends BaseController
{
    @Autowired
    private IVisionRecordService visionRecordService;

    /**
     * 查询视力监测记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(VisionRecord visionRecord)
    {
        startPage();
        List<VisionRecord> list = visionRecordService.selectVisionRecordList(visionRecord);
        return getDataTable(list);
    }

    /**
     * 导出视力监测记录列表
     */
    @PreAuthorize("@ss.hasPermi('system:record:export')")
    @Log(title = "视力监测记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, VisionRecord visionRecord)
    {
        List<VisionRecord> list = visionRecordService.selectVisionRecordList(visionRecord);
        ExcelUtil<VisionRecord> util = new ExcelUtil<VisionRecord>(VisionRecord.class);
        util.exportExcel(response, list, "视力监测记录数据");
    }

    /**
     * 获取视力监测记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(visionRecordService.selectVisionRecordById(id));
    }

    /**
     * 新增视力监测记录
     */
    @PreAuthorize("@ss.hasPermi('system:record:add')")
    @Log(title = "视力监测记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody VisionRecord visionRecord)
    {
        return toAjax(visionRecordService.insertVisionRecord(visionRecord));
    }

    /**
     * 修改视力监测记录
     */
    @PreAuthorize("@ss.hasPermi('system:record:edit')")
    @Log(title = "视力监测记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody VisionRecord visionRecord)
    {
        return toAjax(visionRecordService.updateVisionRecord(visionRecord));
    }

    /**
     * 删除视力监测记录
     */
    @PreAuthorize("@ss.hasPermi('system:record:remove')")
    @Log(title = "视力监测记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(visionRecordService.deleteVisionRecordByIds(ids));
    }
}
