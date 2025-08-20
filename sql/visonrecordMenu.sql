-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('视力监测记录', '2007', '1', 'visonrecord', 'vision/visonrecord/index', 1, 0, 'C', '0', '0', 'vision:visonrecord:list', '#', 'admin', sysdate(), '', null, '视力监测记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('视力监测记录查询', @parentId, '1',  '#', '', 1, 0, 'F', '0', '0', 'vision:visonrecord:query',        '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('视力监测记录新增', @parentId, '2',  '#', '', 1, 0, 'F', '0', '0', 'vision:visonrecord:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('视力监测记录修改', @parentId, '3',  '#', '', 1, 0, 'F', '0', '0', 'vision:visonrecord:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('视力监测记录删除', @parentId, '4',  '#', '', 1, 0, 'F', '0', '0', 'vision:visonrecord:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('视力监测记录导出', @parentId, '5',  '#', '', 1, 0, 'F', '0', '0', 'vision:visonrecord:export',       '#', 'admin', sysdate(), '', null, '');