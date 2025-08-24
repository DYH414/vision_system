package com.ruoyi.visionrecord.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.visionrecord.domain.Person;
import com.ruoyi.visionrecord.domain.VisionRecord;
import com.ruoyi.visionrecord.domain.dto.StatQueryDTO;
import com.ruoyi.visionrecord.domain.vo.AgeGroupCompareVO;
import com.ruoyi.visionrecord.domain.vo.GenderCompareVO;
import com.ruoyi.visionrecord.domain.vo.StatTrendVO;
import com.ruoyi.visionrecord.domain.vo.StatVO;
import com.ruoyi.visionrecord.mapper.PersonMapper;
import com.ruoyi.visionrecord.mapper.VisionRecordMapper;
import com.ruoyi.visionrecord.service.IStatService;
import com.ruoyi.visionrecord.service.IDeepSeekService;

/**
 * 视力统计分析服务实现
 * 
 * @author ruoyi
 */
@Service
public class StatServiceImpl implements IStatService {

    @Autowired
    private VisionRecordMapper visionRecordMapper;

    @Autowired
    private PersonMapper personMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IDeepSeekService deepSeekService;

    /**
     * 视力等级阈值
     */
    private static final BigDecimal NORMAL_THRESHOLD = new BigDecimal("1.0");
    private static final BigDecimal MILD_THRESHOLD = new BigDecimal("0.8");
    private static final BigDecimal MODERATE_THRESHOLD = new BigDecimal("0.6");

    /**
     * 获取年度视力统计数据
     */
    @Override
    public StatVO getAnnualStat(Integer year) {
        if (year == null) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }

        // 查询该年度所有视力记录
        List<VisionRecord> records = visionRecordMapper.selectVisionRecordsByYear(year);

        StatVO statVO = new StatVO();
        statVO.setYear(year);

        // 总检测人数
        statVO.setTotal(records.size());

        if (records.isEmpty()) {
            return statVO;
        }

        // 计算左右眼平均视力
        BigDecimal totalLeft = BigDecimal.ZERO;
        BigDecimal totalRight = BigDecimal.ZERO;

        // 统计各视力等级人数
        int normalCount = 0;
        int mildCount = 0;
        int moderateCount = 0;
        int highCount = 0;

        for (VisionRecord record : records) {
            totalLeft = totalLeft.add(record.getLeftEye());
            totalRight = totalRight.add(record.getRightEye());

            // 取左右眼较低值判断视力等级
            BigDecimal lowerVision = record.getLeftEye().compareTo(record.getRightEye()) <= 0 ? record.getLeftEye()
                    : record.getRightEye();

            if (lowerVision.compareTo(NORMAL_THRESHOLD) >= 0) {
                normalCount++;
            } else if (lowerVision.compareTo(MILD_THRESHOLD) >= 0) {
                mildCount++;
            } else if (lowerVision.compareTo(MODERATE_THRESHOLD) >= 0) {
                moderateCount++;
            } else {
                highCount++;
            }
        }

        int total = records.size();
        statVO.setAvgLeft(totalLeft.divide(new BigDecimal(total), 2, RoundingMode.HALF_UP));
        statVO.setAvgRight(totalRight.divide(new BigDecimal(total), 2, RoundingMode.HALF_UP));

        // 计算各等级占比
        statVO.setNormalRate(new BigDecimal(normalCount).divide(new BigDecimal(total), 2, RoundingMode.HALF_UP));
        statVO.setMildRate(new BigDecimal(mildCount).divide(new BigDecimal(total), 2, RoundingMode.HALF_UP));
        statVO.setModerateRate(new BigDecimal(moderateCount).divide(new BigDecimal(total), 2, RoundingMode.HALF_UP));
        statVO.setHighRate(new BigDecimal(highCount).divide(new BigDecimal(total), 2, RoundingMode.HALF_UP));

        // 计算近视率（轻度+中度+重度）
        statVO.setMyopiaRate(BigDecimal.ONE.subtract(statVO.getNormalRate()));

        return statVO;
    }

    /**
     * 获取视力趋势对比数据
     */
    @Override
    public List<StatTrendVO> getTrendStat(StatQueryDTO queryDTO) {
        List<StatTrendVO> trendList = new ArrayList<>();

        // 默认查询最近5年
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int startYear = queryDTO.getStartYear() != null ? queryDTO.getStartYear() : currentYear - 4;
        int endYear = queryDTO.getEndYear() != null ? queryDTO.getEndYear() : currentYear;

        for (int year = startYear; year <= endYear; year++) {
            StatVO annualStat = getAnnualStat(year);

            // 只有有数据的年份才添加到趋势中
            if (annualStat.getTotal() > 0) {
                StatTrendVO trendVO = new StatTrendVO();
                trendVO.setYear(year);
                trendVO.setAvgLeft(annualStat.getAvgLeft());
                trendVO.setAvgRight(annualStat.getAvgRight());
                trendVO.setNormalRate(annualStat.getNormalRate());
                trendVO.setMyopiaRate(annualStat.getMyopiaRate());

                trendList.add(trendVO);
            }
        }

        return trendList;
    }

    /**
     * 获取性别维度视力对比数据
     */
    @Override
    public List<GenderCompareVO> getGenderCompareStat(Integer year) {
        if (year == null) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }

        List<GenderCompareVO> resultList = new ArrayList<>();

        // 查询该年度所有视力记录
        List<VisionRecord> allRecords = visionRecordMapper.selectVisionRecordsByYear(year);

        // 按性别分组
        Map<String, List<VisionRecord>> recordsByGender = allRecords.stream()
                .collect(Collectors.groupingBy(record -> {
                    Person person = personMapper.selectPersonById(record.getPersonId());
                    return person != null ? person.getGender() : "未知";
                }));

        // 计算各性别的统计数据
        for (Map.Entry<String, List<VisionRecord>> entry : recordsByGender.entrySet()) {
            String gender = entry.getKey();
            List<VisionRecord> records = entry.getValue();

            GenderCompareVO vo = new GenderCompareVO();
            vo.setGender(gender);
            vo.setTotal(records.size());

            // 计算平均视力和各等级占比
            calculateVisionStats(records, vo);

            resultList.add(vo);
        }

        return resultList;
    }

    /**
     * 获取年龄段维度视力对比数据
     */
    @Override
    public List<AgeGroupCompareVO> getAgeGroupCompareStat(Integer year) {
        if (year == null) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }

        List<AgeGroupCompareVO> resultList = new ArrayList<>();

        // 查询该年度所有视力记录
        List<VisionRecord> allRecords = visionRecordMapper.selectVisionRecordsByYear(year);

        // 按年龄段分组
        Map<String, List<VisionRecord>> recordsByAgeGroup = allRecords.stream()
                .collect(Collectors.groupingBy(record -> {
                    Person person = personMapper.selectPersonById(record.getPersonId());
                    if (person != null && person.getBirthDate() != null) {
                        int age = calculateAge(person.getBirthDate(), new Date());
                        if (age <= 12)
                            return "0-12岁";
                        else if (age <= 18)
                            return "13-18岁";
                        else if (age <= 40)
                            return "19-40岁";
                        else
                            return "40+岁";
                    }
                    return "未知";
                }));

        // 计算各年龄段的统计数据
        for (Map.Entry<String, List<VisionRecord>> entry : recordsByAgeGroup.entrySet()) {
            String ageGroup = entry.getKey();
            List<VisionRecord> records = entry.getValue();

            AgeGroupCompareVO vo = new AgeGroupCompareVO();
            vo.setAgeGroup(ageGroup);
            vo.setTotal(records.size());

            // 计算平均视力和各等级占比
            calculateVisionStats(records, vo);

            resultList.add(vo);
        }

        return resultList;
    }

    /**
     * 获取智能分析报告
     */
    @Override
    public boolean testDeepSeekConnection() {
        return deepSeekService.checkApiConnection();
    }

    @Override
    public String getAiAnalysisReport(Integer year) {
        if (year == null) {
            year = Calendar.getInstance().get(Calendar.YEAR);
        }

        // 获取年度统计数据
        StatVO currentYearStat = getAnnualStat(year);

        // 获取上一年度数据进行对比
        StatVO lastYearStat = getAnnualStat(year - 1);

        // 获取性别对比数据
        List<GenderCompareVO> genderData = getGenderCompareStat(year);

        // 获取年龄段对比数据
        List<AgeGroupCompareVO> ageGroupData = getAgeGroupCompareStat(year);

        // 构建请求DeepSeek AI API的数据
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下视力检测数据，生成一份视力变化趋势分析报告，并提供视力问题预警及建议：\n\n");

        // 添加当前年度数据
        prompt.append(year).append("年度数据：\n");
        prompt.append("总检测人数：").append(currentYearStat.getTotal()).append("\n");
        prompt.append("左眼平均视力：").append(currentYearStat.getAvgLeft()).append("\n");
        prompt.append("右眼平均视力：").append(currentYearStat.getAvgRight()).append("\n");
        prompt.append("视力正常率：").append(currentYearStat.getNormalRate().multiply(new BigDecimal(100))).append("%\n");
        prompt.append("轻度视力问题率：").append(currentYearStat.getMildRate().multiply(new BigDecimal(100))).append("%\n");
        prompt.append("中度视力问题率：").append(currentYearStat.getModerateRate().multiply(new BigDecimal(100))).append("%\n");
        prompt.append("重度视力问题率：").append(currentYearStat.getHighRate().multiply(new BigDecimal(100))).append("%\n\n");

        // 添加上一年度数据
        if (lastYearStat.getTotal() > 0) {
            prompt.append(year - 1).append("年度数据：\n");
            prompt.append("总检测人数：").append(lastYearStat.getTotal()).append("\n");
            prompt.append("左眼平均视力：").append(lastYearStat.getAvgLeft()).append("\n");
            prompt.append("右眼平均视力：").append(lastYearStat.getAvgRight()).append("\n");
            prompt.append("视力正常率：").append(lastYearStat.getNormalRate().multiply(new BigDecimal(100))).append("%\n");
            prompt.append("轻度视力问题率：").append(lastYearStat.getMildRate().multiply(new BigDecimal(100))).append("%\n");
            prompt.append("中度视力问题率：").append(lastYearStat.getModerateRate().multiply(new BigDecimal(100)))
                    .append("%\n");
            prompt.append("重度视力问题率：").append(lastYearStat.getHighRate().multiply(new BigDecimal(100))).append("%\n\n");
        }

        // 添加性别对比数据
        prompt.append("性别对比数据：\n");
        for (GenderCompareVO vo : genderData) {
            prompt.append("性别：")
                    .append("M".equals(vo.getGender()) ? "男" : "F".equals(vo.getGender()) ? "女" : vo.getGender())
                    .append("\n");
            prompt.append("人数：").append(vo.getTotal()).append("\n");
            prompt.append("平均视力：").append(vo.getAvgVision()).append("\n");
            prompt.append("视力正常率：").append(vo.getNormalRate().multiply(new BigDecimal(100))).append("%\n");
        }
        prompt.append("\n");

        // 添加年龄段对比数据
        prompt.append("年龄段对比数据：\n");
        for (AgeGroupCompareVO vo : ageGroupData) {
            prompt.append("年龄段：").append(vo.getAgeGroup()).append("\n");
            prompt.append("人数：").append(vo.getTotal()).append("\n");
            prompt.append("平均视力：").append(vo.getAvgVision()).append("\n");
            prompt.append("视力正常率：").append(vo.getNormalRate().multiply(new BigDecimal(100))).append("%\n");
        }

        // 调用DeepSeek AI服务生成分析报告
        try {
            return deepSeekService.generateAnalysisReport(prompt.toString());
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("AI分析报告生成失败: " + e.getMessage());

            // 提供降级处理 - 生成基本分析报告
            return generateFallbackReport(currentYearStat, lastYearStat, year);
        }
    }

    /**
     * 计算视力统计数据
     * 
     * @param records 视力记录列表
     * @param vo      统计结果对象（GenderCompareVO或AgeGroupCompareVO）
     */
    private void calculateVisionStats(List<VisionRecord> records, Object vo) {
        if (records.isEmpty()) {
            return;
        }

        BigDecimal totalVision = BigDecimal.ZERO;
        int normalCount = 0;
        int mildCount = 0;
        int moderateCount = 0;
        int highCount = 0;

        for (VisionRecord record : records) {
            // 取左右眼平均值作为总体视力
            BigDecimal avgVision = record.getLeftEye().add(record.getRightEye())
                    .divide(new BigDecimal(2), 2, RoundingMode.HALF_UP);
            totalVision = totalVision.add(avgVision);

            // 取左右眼较低值判断视力等级
            BigDecimal lowerVision = record.getLeftEye().compareTo(record.getRightEye()) <= 0 ? record.getLeftEye()
                    : record.getRightEye();

            if (lowerVision.compareTo(NORMAL_THRESHOLD) >= 0) {
                normalCount++;
            } else if (lowerVision.compareTo(MILD_THRESHOLD) >= 0) {
                mildCount++;
            } else if (lowerVision.compareTo(MODERATE_THRESHOLD) >= 0) {
                moderateCount++;
            } else {
                highCount++;
            }
        }

        int total = records.size();
        BigDecimal avgVision = totalVision.divide(new BigDecimal(total), 2, RoundingMode.HALF_UP);
        BigDecimal normalRate = new BigDecimal(normalCount).divide(new BigDecimal(total), 2, RoundingMode.HALF_UP);
        BigDecimal mildRate = new BigDecimal(mildCount).divide(new BigDecimal(total), 2, RoundingMode.HALF_UP);
        BigDecimal moderateRate = new BigDecimal(moderateCount).divide(new BigDecimal(total), 2, RoundingMode.HALF_UP);
        BigDecimal highRate = new BigDecimal(highCount).divide(new BigDecimal(total), 2, RoundingMode.HALF_UP);

        // 根据对象类型设置统计结果
        if (vo instanceof GenderCompareVO) {
            GenderCompareVO genderVO = (GenderCompareVO) vo;
            genderVO.setAvgVision(avgVision);
            genderVO.setNormalRate(normalRate);
            genderVO.setMildRate(mildRate);
            genderVO.setModerateRate(moderateRate);
            genderVO.setHighRate(highRate);
        } else if (vo instanceof AgeGroupCompareVO) {
            AgeGroupCompareVO ageGroupVO = (AgeGroupCompareVO) vo;
            ageGroupVO.setAvgVision(avgVision);
            ageGroupVO.setNormalRate(normalRate);
            ageGroupVO.setMildRate(mildRate);
            ageGroupVO.setModerateRate(moderateRate);
            ageGroupVO.setHighRate(highRate);
        }
    }

    /**
     * 计算年龄
     * 
     * @param birthDate   出生日期
     * @param currentDate 当前日期
     * @return 年龄
     */
    private int calculateAge(Date birthDate, Date currentDate) {
        Calendar birth = Calendar.getInstance();
        Calendar current = Calendar.getInstance();

        birth.setTime(birthDate);
        current.setTime(currentDate);

        int age = current.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

        // 检查是否已过生日
        if (current.get(Calendar.MONTH) < birth.get(Calendar.MONTH) ||
                (current.get(Calendar.MONTH) == birth.get(Calendar.MONTH) &&
                        current.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }

    /**
     * 生成降级分析报告
     * 
     * @param currentYearStat 当前年度统计数据
     * @param lastYearStat    上一年度统计数据
     * @param year            年份
     * @return 基本分析报告
     */
    private String generateFallbackReport(StatVO currentYearStat, StatVO lastYearStat, Integer year) {
        StringBuilder report = new StringBuilder();

        report.append("# ").append(year).append("年度视力健康分析报告\n\n");

        // 基本统计信息
        report.append("## 基本统计信息\n");
        report.append("- 总检测人数：").append(currentYearStat.getTotal()).append("人\n");
        report.append("- 左眼平均视力：").append(currentYearStat.getAvgLeft()).append("\n");
        report.append("- 右眼平均视力：").append(currentYearStat.getAvgRight()).append("\n");
        report.append("- 视力正常率：").append(currentYearStat.getNormalRate().multiply(new BigDecimal(100))).append("%\n");
        report.append("- 近视率：").append(currentYearStat.getMyopiaRate().multiply(new BigDecimal(100))).append("%\n\n");

        // 年度对比分析
        if (lastYearStat.getTotal() > 0) {
            report.append("## 年度对比分析\n");

            BigDecimal normalRateChange = currentYearStat.getNormalRate().subtract(lastYearStat.getNormalRate());
            BigDecimal myopiaRateChange = currentYearStat.getMyopiaRate().subtract(lastYearStat.getMyopiaRate());

            if (normalRateChange.compareTo(BigDecimal.ZERO) > 0) {
                report.append("- 视力正常率较去年提升了").append(normalRateChange.multiply(new BigDecimal(100))).append("个百分点\n");
            } else if (normalRateChange.compareTo(BigDecimal.ZERO) < 0) {
                report.append("- 视力正常率较去年下降了").append(normalRateChange.abs().multiply(new BigDecimal(100)))
                        .append("个百分点\n");
            } else {
                report.append("- 视力正常率与去年基本持平\n");
            }

            if (myopiaRateChange.compareTo(BigDecimal.ZERO) > 0) {
                report.append("- 近视率较去年上升了").append(myopiaRateChange.multiply(new BigDecimal(100))).append("个百分点\n");
            } else if (myopiaRateChange.compareTo(BigDecimal.ZERO) < 0) {
                report.append("- 近视率较去年下降了").append(myopiaRateChange.abs().multiply(new BigDecimal(100)))
                        .append("个百分点\n");
            } else {
                report.append("- 近视率与去年基本持平\n");
            }
            report.append("\n");
        }

        // 健康建议
        report.append("## 健康建议\n");

        BigDecimal myopiaRate = currentYearStat.getMyopiaRate();
        if (myopiaRate.compareTo(new BigDecimal("0.5")) >= 0) {
            report.append("- 近视率较高，建议加强视力保护宣传教育\n");
            report.append("- 定期开展眼保健操和户外活动\n");
            report.append("- 控制用眼时间，避免长时间近距离用眼\n");
        } else if (myopiaRate.compareTo(new BigDecimal("0.3")) >= 0) {
            report.append("- 近视率处于中等水平，需要重点关注\n");
            report.append("- 建议定期进行视力检查\n");
            report.append("- 保持良好的用眼习惯\n");
        } else {
            report.append("- 视力健康状况良好，继续保持\n");
            report.append("- 建议继续加强视力保护意识\n");
        }

        report.append("\n*注：由于AI服务暂时不可用，此报告为基于数据的基本分析。*");

        return report.toString();
    }
}