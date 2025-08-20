package com.ruoyi.visionrecord.mapper;

import java.util.List;
import com.ruoyi.visionrecord.domain.VisionRecord;

/**
 * 视力监测记录Mapper接口
 * 
 * @author dyh
 * @date 2025-08-20
 */
public interface VisionRecordMapper 
{
    /**
     * 查询视力监测记录
     * 
     * @param id 视力监测记录主键
     * @return 视力监测记录
     */
    public VisionRecord selectVisionRecordById(Long id);

    /**
     * 查询视力监测记录列表
     * 
     * @param visionRecord 视力监测记录
     * @return 视力监测记录集合
     */
    public List<VisionRecord> selectVisionRecordList(VisionRecord visionRecord);

    /**
     * 新增视力监测记录
     * 
     * @param visionRecord 视力监测记录
     * @return 结果
     */
    public int insertVisionRecord(VisionRecord visionRecord);

    /**
     * 修改视力监测记录
     * 
     * @param visionRecord 视力监测记录
     * @return 结果
     */
    public int updateVisionRecord(VisionRecord visionRecord);

    /**
     * 删除视力监测记录
     * 
     * @param id 视力监测记录主键
     * @return 结果
     */
    public int deleteVisionRecordById(Long id);

    /**
     * 批量删除视力监测记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteVisionRecordByIds(Long[] ids);
}
