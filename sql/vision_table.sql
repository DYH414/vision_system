-- 视力监测分析系统数据表初始化SQL脚本
-- 创建日期：2025-05-23
-- 说明：此脚本用于创建视力监测分析系统所需的数据表结构

-- 删除已存在的表（如果有）
DROP TABLE IF EXISTS vision_record;
DROP TABLE IF EXISTS person;

-- 人员表
CREATE TABLE person (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '人员ID',
  name         VARCHAR(50) NOT NULL COMMENT '姓名',
  gender       CHAR(1) NOT NULL COMMENT '性别 M/F',
  birth_date   DATE NULL COMMENT '出生日期',
  phone        VARCHAR(20) NULL COMMENT '联系方式',
  id_card      VARCHAR(18) UNIQUE COMMENT '身份证号（唯一）',
  check_code   VARCHAR(50) NULL COMMENT '体检编号（院内编号）',
  create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_name(name),
  INDEX idx_check_code(check_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人员信息表';

-- 视力监测表
CREATE TABLE vision_record (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
  person_id    BIGINT NOT NULL COMMENT '人员ID',
  check_date   DATE NOT NULL COMMENT '检测日期',
  year         INT NOT NULL COMMENT '年度（方便汇总）',
  left_eye     DECIMAL(3,1) NOT NULL COMMENT '左眼视力(小数制0.1-2.0建议)',
  right_eye    DECIMAL(3,1) NOT NULL COMMENT '右眼视力',
  diagnosis    VARCHAR(100) NULL COMMENT '诊断（正常/近视/散光/其他）',
  remark       VARCHAR(200) NULL COMMENT '备注',
  create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  CONSTRAINT fk_record_person FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE CASCADE,
  UNIQUE KEY uk_person_year (person_id, year),
  INDEX idx_year(year),
  INDEX idx_check_date(check_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视力监测记录表';

-- 插入测试数据（可选）
INSERT INTO person (name, gender, birth_date, phone, id_card, check_code) VALUES
('张三', 'M', '2000-01-15', '13800138001', '110101200001150011', 'TC20250001'),
('李四', 'F', '2001-05-20', '13800138002', '110101200105200022', 'TC20250002'),
('王五', 'M', '1999-11-08', '13800138003', '110101199911080033', 'TC20250003'),
('赵六', 'F', '2002-03-25', '13800138004', '110101200203250044', 'TC20250004');

-- 插入视力记录测试数据
INSERT INTO vision_record (person_id, check_date, year, left_eye, right_eye, diagnosis) VALUES
(1, '2025-01-10', 2025, 1.2, 1.0, '正常'),
(2, '2025-01-12', 2025, 0.8, 0.7, '轻度近视'),
(3, '2025-01-15', 2025, 0.5, 0.6, '中度近视'),
(4, '2025-01-18', 2025, 1.5, 1.5, '正常');

COMMIT; 