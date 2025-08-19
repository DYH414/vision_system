-- 视力监测分析系统菜单初始化SQL脚本
-- 创建日期：2025-05-23
-- 说明：此脚本用于初始化视力监测分析系统的菜单结构和权限

-- 顶级目录：视力监测分析系统
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES ('视力监测分析系统', 0, 1, 'vision', NULL, 1, 0, 'M', '0', '0', NULL, 'eye', 'admin', sysdate());

-- 获取视力监测分析系统的菜单ID
SET @VISION_ID = LAST_INSERT_ID();

-- 人员管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
VALUES ('人员管理', @VISION_ID, 1, 'person', 'vision/person/index', 'C', '0', '0', 'vision:person:list', 'user', 'admin', sysdate());
SET @PERSON_ID = LAST_INSERT_ID();

-- 人员管理按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, perms, create_time)
VALUES
('新增人员', @PERSON_ID, 1, 'F', 'vision:person:add', sysdate()),
('编辑人员', @PERSON_ID, 2, 'F', 'vision:person:edit', sysdate()),
('删除人员', @PERSON_ID, 3, 'F', 'vision:person:remove', sysdate()),
('导入人员', @PERSON_ID, 4, 'F', 'vision:person:import', sysdate()),
('导出人员', @PERSON_ID, 5, 'F', 'vision:person:export', sysdate());

-- 年度视力监测
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, perms, icon, create_time)
VALUES ('年度视力监测', @VISION_ID, 2, 'record', 'vision/record/index', 'C', 'vision:record:list', 'form', sysdate());
SET @RECORD_ID = LAST_INSERT_ID();

-- 年度视力监测按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, perms, create_time)
VALUES
('新增记录', @RECORD_ID, 1, 'F', 'vision:record:add', sysdate()),
('编辑记录', @RECORD_ID, 2, 'F', 'vision:record:edit', sysdate()),
('删除记录', @RECORD_ID, 3, 'F', 'vision:record:remove', sysdate()),
('导入记录', @RECORD_ID, 4, 'F', 'vision:record:import', sysdate()),
('导出记录', @RECORD_ID, 5, 'F', 'vision:record:export', sysdate()),
('模板下载', @RECORD_ID, 6, 'F', 'vision:record:template', sysdate());

-- 统计与分析
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, perms, icon, create_time)
VALUES ('统计与分析', @VISION_ID, 3, 'stat', 'vision/stat/index', 'C', 'vision:stat:view', 'chart', sysdate());

-- 报告管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, perms, icon, create_time)
VALUES ('年度报告', @VISION_ID, 4, 'report', 'vision/report/index', 'C', 'vision:report:annual', 'documentation', sysdate());
SET @REPORT_ID = LAST_INSERT_ID();

-- 报告管理按钮
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, perms, create_time)
VALUES ('导出报告', @REPORT_ID, 1, 'F', 'vision:report:export', sysdate());

-- 添加视力等级字典类型
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
VALUES ('视力等级', 'VISION_LEVEL', '0', 'admin', sysdate(), '视力等级字典');
SET @DICT_TYPE_ID = LAST_INSERT_ID();

-- 添加视力等级字典数据
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time, remark)
VALUES 
(1, '正常', 'normal', 'VISION_LEVEL', 'success', 'Y', '0', 'admin', sysdate(), '视力正常 ≥ 1.0'),
(2, '轻度', 'mild', 'VISION_LEVEL', 'warning', 'N', '0', 'admin', sysdate(), '轻度视力问题 0.8–<1.0'),
(3, '中度', 'moderate', 'VISION_LEVEL', 'info', 'N', '0', 'admin', sysdate(), '中度视力问题 0.6–<0.8'),
(4, '重度', 'high', 'VISION_LEVEL', 'danger', 'N', '0', 'admin', sysdate(), '重度视力问题 < 0.6');

-- 添加角色与菜单关联
INSERT INTO sys_role_menu (role_id, menu_id) 
SELECT '2', menu_id FROM sys_menu WHERE parent_id = @VISION_ID OR menu_id = @VISION_ID;

-- 添加子菜单关联
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT '2', menu_id FROM sys_menu WHERE parent_id IN (@PERSON_ID, @RECORD_ID, @REPORT_ID);

COMMIT; 