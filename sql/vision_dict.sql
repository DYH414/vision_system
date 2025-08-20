-- 视力监测分析系统字典初始化SQL脚本
-- 创建日期：2025-05-23
-- 说明：此脚本用于初始化视力监测分析系统所需的字典数据

-- 添加视力等级字典类型（如果不存在）
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '视力等级', 'VISION_LEVEL', '0', 'admin', sysdate(), '视力等级字典'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'VISION_LEVEL');

-- 添加视力等级字典数据（如果不存在）
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time, remark)
SELECT 1, '正常', 'normal', 'VISION_LEVEL', 'success', 'Y', '0', 'admin', sysdate(), '视力正常 ≥ 1.0'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'VISION_LEVEL' AND dict_value = 'normal');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time, remark)
SELECT 2, '轻度', 'mild', 'VISION_LEVEL', 'warning', 'N', '0', 'admin', sysdate(), '轻度视力问题 0.8–<1.0'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'VISION_LEVEL' AND dict_value = 'mild');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time, remark)
SELECT 3, '中度', 'moderate', 'VISION_LEVEL', 'info', 'N', '0', 'admin', sysdate(), '中度视力问题 0.6–<0.8'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'VISION_LEVEL' AND dict_value = 'moderate');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time, remark)
SELECT 4, '重度', 'high', 'VISION_LEVEL', 'danger', 'N', '0', 'admin', sysdate(), '重度视力问题 < 0.6'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'VISION_LEVEL' AND dict_value = 'high');

-- 添加性别字典类型（如果系统中不存在）
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '性别', 'sys_user_sex', '0', 'admin', sysdate(), '性别字典'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'sys_user_sex');

-- 添加性别字典数据（如果系统中不存在）
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time, remark)
SELECT 1, '男', 'M', 'sys_user_sex', 'default', 'Y', '0', 'admin', sysdate(), '性别男'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'sys_user_sex' AND dict_value = 'M');

INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time, remark)
SELECT 2, '女', 'F', 'sys_user_sex', 'default', 'N', '0', 'admin', sysdate(), '性别女'
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'sys_user_sex' AND dict_value = 'F');

COMMIT; 