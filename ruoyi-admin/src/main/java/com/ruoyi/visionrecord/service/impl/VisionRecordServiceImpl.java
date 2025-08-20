package com.ruoyi.visionrecord.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.visionrecord.mapper.VisionRecordMapper;
import com.ruoyi.visionrecord.domain.VisionRecord;
import com.ruoyi.visionrecord.service.IVisionRecordService;

/**
 * 视力监测记录Service业务层处理
 * 
 * @author dyh
 * @date 2025-08-20
 */
@Service
public class VisionRecordServiceImpl implements IVisionRecordService 
{
    @Autowired
    private VisionRecordMapper visionRecordMapper;

    /**
     * 查询视力监测记录
     * 
     * @param id 视力监测记录主键
     * @return 视力监测记录
     */
    @Override
    public VisionRecord selectVisionRecordById(Long id)
    {
        return visionRecordMapper.selectVisionRecordById(id);
    }

    /**
     * 查询视力监测记录列表
     * 
     * @param visionRecord 视力监测记录
     * @return 视力监测记录
     */
    @Override
    public List<VisionRecord> selectVisionRecordList(VisionRecord visionRecord)
    {
        return visionRecordMapper.selectVisionRecordList(visionRecord);
    }

    /**
     * 新增视力监测记录
     * 
     * @param visionRecord 视力监测记录
     * @return 结果
     */
    @Override
    public int insertVisionRecord(VisionRecord visionRecord)
    {
        visionRecord.setCreateTime(DateUtils.getNowDate());
        return visionRecordMapper.insertVisionRecord(visionRecord);
    }

    /**
     * 修改视力监测记录
     * 
     * @param visionRecord 视力监测记录
     * @return 结果
     */
    @Override
    public int updateVisionRecord(VisionRecord visionRecord)
    {
        visionRecord.setUpdateTime(DateUtils.getNowDate());
        return visionRecordMapper.updateVisionRecord(visionRecord);
    }

    /**
     * 批量删除视力监测记录
     * 
     * @param ids 需要删除的视力监测记录主键
     * @return 结果
     */
    @Override
    public int deleteVisionRecordByIds(Long[] ids)
    {
        return visionRecordMapper.deleteVisionRecordByIds(ids);
    }

    /**
     * 删除视力监测记录信息
     * 
     * @param id 视力监测记录主键
     * @return 结果
     */
    @Override
    public int deleteVisionRecordById(Long id)
    {
        return visionRecordMapper.deleteVisionRecordById(id);
    }
}
