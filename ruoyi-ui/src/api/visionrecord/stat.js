import request from '@/utils/request'

// 获取年度视力统计数据
export function getAnnualStat(year) {
    return request({
        url: '/visionrecord/stat/annual',
        method: 'get',
        params: { year }
    })
}

// 获取视力趋势对比数据
export function getTrendStat(query) {
    return request({
        url: '/visionrecord/stat/trend',
        method: 'get',
        params: query
    })
}

// 获取性别维度视力对比数据
export function getGenderCompareStat(year) {
    return request({
        url: '/visionrecord/stat/gender',
        method: 'get',
        params: { year }
    })
}

// 获取年龄段维度视力对比数据
export function getAgeGroupCompareStat(year) {
    return request({
        url: '/visionrecord/stat/agegroup',
        method: 'get',
        params: { year }
    })
}

// 获取智能分析报告
export function getAiAnalysisReport(year) {
    return request({
        url: '/visionrecord/stat/analysis',
        method: 'get',
        params: { year },
        timeout: 120000 // 专门为AI接口设置120秒超时
    })
}

// 导出Excel统计结果
export function exportStatExcel(year) {
    return request({
        url: '/visionrecord/stat/export/excel',
        method: 'get',
        params: { year },
        responseType: 'blob'
    })
}

// 导出PDF统计图表
export function exportStatPdf(year) {
    return request({
        url: '/visionrecord/stat/export/pdf',
        method: 'get',
        params: { year },
        responseType: 'blob'
    })
} 