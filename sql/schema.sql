-- Hotel/Homestay Management System Initialization Script
-- 酒店/民宿管理系统初始化脚本
-- Compatible with MySQL 5.7+/8.x (avoid ALTER ... ADD COLUMN IF NOT EXISTS syntax)
-- 兼容 MySQL 5.7+/8.x（避免使用 ALTER ... ADD COLUMN IF NOT EXISTS 语法）

CREATE DATABASE IF NOT EXISTS hotel_management
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE hotel_management;

SET NAMES utf8mb4;

SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID / User ID',
  username VARCHAR(64) NOT NULL COMMENT '用户名 / Username',
  password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希 / Password Hash',
  nickname VARCHAR(64) DEFAULT NULL COMMENT '昵称 / Nickname',
  mobile VARCHAR(32) DEFAULT NULL COMMENT '手机号 / Mobile Number',
  email VARCHAR(128) DEFAULT NULL COMMENT '邮箱 / Email',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用0停用) / Status (1 enabled, 0 disabled)',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否1是) / Logical Deleted (0 no, 1 yes)',
  UNIQUE KEY uk_sys_user_username (username),
  KEY idx_sys_user_mobile (mobile),
  KEY idx_sys_user_email (email)
) COMMENT='系统用户表 / System User Table';

CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID / Role ID',
  role_code VARCHAR(64) NOT NULL COMMENT '角色编码 / Role Code',
  role_name VARCHAR(128) NOT NULL COMMENT '角色名称 / Role Name',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 / Status',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_sys_role_code (role_code)
) COMMENT='系统角色表 / System Role Table';

CREATE TABLE IF NOT EXISTS sys_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID / Menu ID',
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父级菜单ID / Parent Menu ID',
  menu_name VARCHAR(128) NOT NULL COMMENT '菜单名称 / Menu Name',
  permission_code VARCHAR(128) DEFAULT NULL COMMENT '权限编码 / Permission Code',
  menu_type VARCHAR(16) NOT NULL DEFAULT 'MENU' COMMENT '菜单类型(MENU/BUTTON) / Menu Type (MENU/BUTTON)',
  path VARCHAR(255) DEFAULT NULL COMMENT '路由路径 / Route Path',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 / Status',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号 / Sort Number',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  KEY idx_sys_menu_parent_id (parent_id),
  KEY idx_sys_menu_permission_code (permission_code)
) COMMENT='系统菜单权限表 / System Menu Permission Table';

CREATE TABLE IF NOT EXISTS sys_user_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID / Primary Key ID',
  user_id BIGINT NOT NULL COMMENT '用户ID / User ID',
  role_id BIGINT NOT NULL COMMENT '角色ID / Role ID',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_sys_user_role (user_id, role_id),
  KEY idx_sys_user_role_role_id (role_id)
) COMMENT='用户角色关系表 / User Role Relation Table';

CREATE TABLE IF NOT EXISTS sys_role_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID / Primary Key ID',
  role_id BIGINT NOT NULL COMMENT '角色ID / Role ID',
  menu_id BIGINT NOT NULL COMMENT '菜单ID / Menu ID',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_sys_role_menu (role_id, menu_id),
  KEY idx_sys_role_menu_menu_id (menu_id)
) COMMENT='角色菜单关系表 / Role Menu Relation Table';

CREATE TABLE IF NOT EXISTS sys_dict (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典ID / Dictionary ID',
  dict_code VARCHAR(64) NOT NULL COMMENT '字典编码 / Dictionary Code',
  dict_name VARCHAR(128) NOT NULL COMMENT '字典名称 / Dictionary Name',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用0停用) / Status (1 enabled, 0 disabled)',
  sort_no INT NOT NULL DEFAULT 100 COMMENT '排序号 / Sort Number',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注 / Remark',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_sys_dict_code (dict_code),
  KEY idx_sys_dict_status_sort (status, sort_no)
) COMMENT='系统字典表 / System Dictionary Table';

CREATE TABLE IF NOT EXISTS sys_dict_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '字典项ID / Dictionary Item ID',
  dict_code VARCHAR(64) NOT NULL COMMENT '字典编码 / Dictionary Code',
  item_code VARCHAR(64) NOT NULL COMMENT '字典项编码 / Dictionary Item Code',
  item_name VARCHAR(128) NOT NULL COMMENT '字典项名称 / Dictionary Item Name',
  item_value VARCHAR(128) DEFAULT NULL COMMENT '字典项值 / Dictionary Item Value',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用0停用) / Status (1 enabled, 0 disabled)',
  sort_no INT NOT NULL DEFAULT 100 COMMENT '排序号 / Sort Number',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注 / Remark',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_sys_dict_item_code (dict_code, item_code),
  KEY idx_sys_dict_item_dict_status_sort (dict_code, status, sort_no)
) COMMENT='系统字典项表 / System Dictionary Item Table';

CREATE TABLE IF NOT EXISTS sys_param (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '参数ID / Parameter ID',
  param_key VARCHAR(128) NOT NULL COMMENT '参数键 / Parameter Key',
  param_value VARCHAR(512) NOT NULL COMMENT '参数值 / Parameter Value',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注 / Remark',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_sys_param_key (param_key)
) COMMENT='系统参数表 / System Parameter Table';

CREATE TABLE IF NOT EXISTS hotel_group (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '集团ID / Group ID',
  group_code VARCHAR(64) NOT NULL COMMENT '集团编码 / Group Code',
  group_name VARCHAR(128) NOT NULL COMMENT '集团名称 / Group Name',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 / Status',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_hotel_group_code (group_code)
) COMMENT='酒店集团表 / Hotel Group Table';

CREATE TABLE IF NOT EXISTS hotel_brand (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '品牌ID / Brand ID',
  group_id BIGINT NOT NULL COMMENT '集团ID / Group ID',
  brand_code VARCHAR(64) NOT NULL COMMENT '品牌编码 / Brand Code',
  brand_name VARCHAR(128) NOT NULL COMMENT '品牌名称 / Brand Name',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 / Status',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_hotel_brand_code (brand_code),
  KEY idx_hotel_brand_group_id (group_id)
) COMMENT='酒店品牌表 / Hotel Brand Table';

CREATE TABLE IF NOT EXISTS hotel_property (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '门店ID / Property ID',
  group_id BIGINT NULL COMMENT '集团ID(可空) / Group ID (Nullable)',
  brand_id BIGINT NULL COMMENT '品牌ID(可空) / Brand ID (Nullable)',
  property_code VARCHAR(64) NOT NULL COMMENT '门店编码 / Property Code',
  property_name VARCHAR(128) NOT NULL COMMENT '门店名称 / Property Name',
  business_mode VARCHAR(32) NOT NULL DEFAULT 'HOMESTAY' COMMENT '经营模式 / Business Mode',
  contact_phone VARCHAR(32) DEFAULT NULL COMMENT '联系电话 / Contact Phone',
  province VARCHAR(64) DEFAULT NULL COMMENT '省份 / Province',
  city VARCHAR(64) DEFAULT NULL COMMENT '城市 / City',
  district VARCHAR(64) DEFAULT NULL COMMENT '区县 / District',
  address VARCHAR(255) DEFAULT NULL COMMENT '详细地址 / Address',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 / Status',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_hotel_property_code (property_code),
  KEY idx_hotel_property_group (group_id),
  KEY idx_hotel_property_brand (brand_id),
  KEY idx_hotel_property_mode (business_mode)
) COMMENT='门店表 / Property Table';

CREATE TABLE IF NOT EXISTS hotel_room_type (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房型ID / Room Type ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  room_type_code VARCHAR(64) NOT NULL COMMENT '房型编码 / Room Type Code',
  room_type_name VARCHAR(128) NOT NULL COMMENT '房型名称 / Room Type Name',
  max_guest_count INT NOT NULL DEFAULT 2 COMMENT '最大入住人数 / Max Guest Count',
  bed_type VARCHAR(64) DEFAULT NULL COMMENT '床型 / Bed Type',
  base_price DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '基础价格 / Base Price',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 / Status',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_room_type_code (room_type_code),
  KEY idx_room_type_property_id (property_id)
) COMMENT='房型表 / Room Type Table';

CREATE TABLE IF NOT EXISTS hotel_room (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房间ID / Room ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  room_type_id BIGINT NOT NULL COMMENT '房型ID / Room Type ID',
  room_no VARCHAR(32) NOT NULL COMMENT '房号 / Room Number',
  floor_no VARCHAR(32) DEFAULT NULL COMMENT '楼层 / Floor Number',
  status VARCHAR(32) NOT NULL DEFAULT 'VACANT_CLEAN' COMMENT '房态 / Room Status',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_room_property_no (property_id, room_no),
  KEY idx_room_type_id (room_type_id),
  KEY idx_room_status (status)
) COMMENT='房间表 / Room Table';

CREATE TABLE IF NOT EXISTS hotel_room_status_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房态日志ID / Room Status Log ID',
  room_id BIGINT NOT NULL COMMENT '房间ID / Room ID',
  old_status VARCHAR(32) NOT NULL COMMENT '原房态 / Old Status',
  new_status VARCHAR(32) NOT NULL COMMENT '新房态 / New Status',
  reason VARCHAR(255) DEFAULT NULL COMMENT '变更原因 / Change Reason',
  operator VARCHAR(64) DEFAULT NULL COMMENT '操作人 / Operator',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  KEY idx_room_status_log_room_id (room_id),
  KEY idx_room_status_log_created_at (created_at)
) COMMENT='房态变更日志表 / Room Status Change Log Table';

CREATE TABLE IF NOT EXISTS channel_account (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '渠道账号ID / Channel Account ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  channel_code VARCHAR(32) NOT NULL COMMENT '渠道编码 / Channel Code',
  app_key VARCHAR(128) DEFAULT NULL COMMENT '应用标识 / App Key',
  app_secret VARCHAR(255) DEFAULT NULL COMMENT '应用密钥 / App Secret',
  auth_status TINYINT NOT NULL DEFAULT 0 COMMENT '授权状态 / Authorization Status',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_channel_property (property_id, channel_code)
) COMMENT='渠道账号表 / Channel Account Table';

CREATE TABLE IF NOT EXISTS channel_sync_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '同步日志ID / Sync Log ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  channel_code VARCHAR(32) NOT NULL COMMENT '渠道编码 / Channel Code',
  biz_type VARCHAR(32) NOT NULL COMMENT '业务类型(PRICE/INVENTORY/ORDER/CALLBACK) / Business Type',
  biz_id VARCHAR(128) NOT NULL COMMENT '业务标识 / Business Identifier',
  idempotent_key VARCHAR(128) NOT NULL COMMENT '幂等键 / Idempotent Key',
  sync_status VARCHAR(32) NOT NULL COMMENT '同步状态 / Sync Status',
  request_payload TEXT COMMENT '请求报文 / Request Payload',
  response_payload TEXT COMMENT '响应报文 / Response Payload',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  KEY idx_sync_log_property_channel (property_id, channel_code),
  KEY idx_sync_log_idempotent (idempotent_key)
) COMMENT='渠道同步日志表 / Channel Sync Log Table';

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- Seed Data / 初始化测试数据
-- =========================

INSERT INTO sys_user (id, username, password_hash, nickname, mobile, email, status, created_by, updated_by, deleted)
VALUES (1, 'admin', '$2b$12$Shy81wwJpP1bv22tA6r2Ruxketd61fSbggJjfkzix1ssC946nQmPq', '系统管理员', '13800000000', 'admin@example.com', 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO sys_role (id, role_code, role_name, status, created_by, updated_by, deleted)
VALUES (1, 'SUPER_ADMIN', '超级管理员', 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO sys_menu (id, parent_id, menu_name, permission_code, menu_type, path, status, sort_no, created_by, updated_by, deleted)
VALUES
(2000, 0, '工作台', NULL, 'MENU', '/workbench', 1, 10, 'system', 'system', 0),
(2001, 2000, '经营看板', 'report:read', 'MENU', '/workbench/dashboard', 1, 11, 'system', 'system', 0),
(2002, 2000, '今日入住', 'checkin:read', 'MENU', '/workbench/today-checkin', 1, 12, 'system', 'system', 0),
(2003, 2000, '今日退房', 'checkin:read', 'MENU', '/workbench/today-checkout', 1, 13, 'system', 'system', 0),
(2004, 2000, '待处理事项', 'ops:read', 'MENU', '/workbench/todos', 1, 14, 'system', 'system', 0),

(2100, 0, '房源管理', NULL, 'MENU', '/assets', 1, 20, 'system', 'system', 0),
(2101, 2100, '民宿管理', 'property:read', 'MENU', '/assets/homestays', 1, 21, 'system', 'system', 0),
(2102, 2100, '房型管理', 'roomType:read', 'MENU', '/assets/room-types', 1, 22, 'system', 'system', 0),
(2103, 2100, '房间管理', 'room:read', 'MENU', '/assets/rooms', 1, 23, 'system', 'system', 0),
(2104, 2100, '房态管理', 'room:read', 'MENU', '/assets/room-status', 1, 24, 'system', 'system', 0),
(2111, 2100, '民宿编辑', 'property:write', 'BUTTON', NULL, 1, 25, 'system', 'system', 0),
(2112, 2100, '房型编辑', 'roomType:write', 'BUTTON', NULL, 1, 26, 'system', 'system', 0),
(2113, 2100, '房间编辑', 'room:write', 'BUTTON', NULL, 1, 27, 'system', 'system', 0),

(2200, 0, '订单中心', NULL, 'MENU', '/orders', 1, 30, 'system', 'system', 0),
(2201, 2200, '预订订单', 'order:read', 'MENU', '/orders/reservations', 1, 31, 'system', 'system', 0),
(2202, 2200, '入住登记', 'checkin:read', 'MENU', '/orders/checkin', 1, 32, 'system', 'system', 0),
(2203, 2200, '在住管理', 'checkin:read', 'MENU', '/orders/inhouse', 1, 33, 'system', 'system', 0),
(2204, 2200, '退房管理', 'checkin:read', 'MENU', '/orders/checkout', 1, 34, 'system', 'system', 0),
(2205, 2200, '订单详情', 'order:read', 'MENU', '/orders/detail', 1, 35, 'system', 'system', 0),
(2211, 2200, '订单操作', 'order:write', 'BUTTON', NULL, 1, 36, 'system', 'system', 0),
(2212, 2200, '入住操作', 'checkin:write', 'BUTTON', NULL, 1, 37, 'system', 'system', 0),
(2213, 2200, '订单导出', 'order:export', 'BUTTON', NULL, 1, 38, 'system', 'system', 0),

(2300, 0, '价格与库存', NULL, 'MENU', '/pricing', 1, 40, 'system', 'system', 0),
(2301, 2300, '房价日历', 'pricing:read', 'MENU', '/pricing/calendar', 1, 41, 'system', 'system', 0),
(2302, 2300, '价格计划', 'pricing:read', 'MENU', '/pricing/plans', 1, 42, 'system', 'system', 0),
(2303, 2300, '可售库存', 'inventory:read', 'MENU', '/pricing/inventory', 1, 43, 'system', 'system', 0),
(2304, 2300, '超售预警', 'inventory:read', 'MENU', '/pricing/overbook-warning', 1, 44, 'system', 'system', 0),
(2311, 2300, '价格编辑', 'pricing:write', 'BUTTON', NULL, 1, 45, 'system', 'system', 0),
(2312, 2300, '库存调整', 'inventory:write', 'BUTTON', NULL, 1, 46, 'system', 'system', 0),

(2400, 0, '渠道管理', NULL, 'MENU', '/channels', 1, 50, 'system', 'system', 0),
(2401, 2400, '渠道配置', 'ota:read', 'MENU', '/channels/config', 1, 51, 'system', 'system', 0),
(2402, 2400, '渠道映射', 'ota:read', 'MENU', '/channels/mapping', 1, 52, 'system', 'system', 0),
(2403, 2400, '同步记录', 'ota:read', 'MENU', '/channels/sync-logs', 1, 53, 'system', 'system', 0),
(2404, 2400, '回调记录', 'ota:read', 'MENU', '/channels/callback-logs', 1, 54, 'system', 'system', 0),
(2411, 2400, '渠道操作', 'ota:write', 'BUTTON', NULL, 1, 55, 'system', 'system', 0),

(2500, 0, '客户与会员', NULL, 'MENU', '/crm', 1, 60, 'system', 'system', 0),
(2501, 2500, '客户档案', 'member:read', 'MENU', '/crm/customers', 1, 61, 'system', 'system', 0),
(2502, 2500, '会员管理', 'member:read', 'MENU', '/crm/members', 1, 62, 'system', 'system', 0),
(2503, 2500, '积分管理', 'member:read', 'MENU', '/crm/points', 1, 63, 'system', 'system', 0),
(2504, 2500, '优惠券管理', 'member:read', 'MENU', '/crm/coupons', 1, 64, 'system', 'system', 0),
(2511, 2500, '会员编辑', 'member:write', 'BUTTON', NULL, 1, 65, 'system', 'system', 0),

(2600, 0, '财务管理', NULL, 'MENU', '/finance', 1, 70, 'system', 'system', 0),
(2601, 2600, '收款记录', 'finance:read', 'MENU', '/finance/receipts', 1, 71, 'system', 'system', 0),
(2602, 2600, '退款记录', 'finance:read', 'MENU', '/finance/refunds', 1, 72, 'system', 'system', 0),
(2603, 2600, '账单明细', 'finance:read', 'MENU', '/finance/bills', 1, 73, 'system', 'system', 0),
(2604, 2600, '发票管理', 'finance:read', 'MENU', '/finance/invoices', 1, 74, 'system', 'system', 0),
(2605, 2600, '经营报表', 'report:read', 'MENU', '/finance/reports', 1, 75, 'system', 'system', 0),
(2611, 2600, '财务操作', 'finance:write', 'BUTTON', NULL, 1, 76, 'system', 'system', 0),
(2612, 2600, '报表导出', 'report:export', 'BUTTON', NULL, 1, 77, 'system', 'system', 0),

(2700, 0, '运营管理', NULL, 'MENU', '/operations', 1, 80, 'system', 'system', 0),
(2701, 2700, '保洁管理', 'ops:read', 'MENU', '/operations/housekeeping', 1, 81, 'system', 'system', 0),
(2702, 2700, '维修工单', 'ops:read', 'MENU', '/operations/maintenance', 1, 82, 'system', 'system', 0),
(2703, 2700, '房态记录', 'room:read', 'MENU', '/operations/room-status-record', 1, 83, 'system', 'system', 0),
(2704, 2700, '日志中心', 'report:read', 'MENU', '/operations/log-center', 1, 84, 'system', 'system', 0),
(2711, 2700, '运营工单操作', 'ops:write', 'BUTTON', NULL, 1, 85, 'system', 'system', 0),

(2800, 0, '系统管理', NULL, 'MENU', '/system', 1, 90, 'system', 'system', 0),
(2801, 2800, '用户管理', 'sys:user:read', 'MENU', '/system/users', 1, 91, 'system', 'system', 0),
(2802, 2800, '角色管理', 'sys:role:read', 'MENU', '/system/roles', 1, 92, 'system', 'system', 0),
(2803, 2800, '权限管理', 'sys:permission:read', 'MENU', '/system/permissions', 1, 93, 'system', 'system', 0),
(2804, 2800, '菜单管理', 'sys:menu:read', 'MENU', '/system/menus', 1, 94, 'system', 'system', 0),
(2805, 2800, '字典管理', 'sys:dict:read', 'MENU', '/system/dicts', 1, 95, 'system', 'system', 0),
(2806, 2800, '参数配置', 'sys:param:read', 'MENU', '/system/params', 1, 96, 'system', 'system', 0),
(2811, 2800, '用户编辑', 'sys:user:write', 'BUTTON', NULL, 1, 97, 'system', 'system', 0),
(2812, 2800, '角色编辑', 'sys:role:write', 'BUTTON', NULL, 1, 98, 'system', 'system', 0),
(2813, 2800, '权限编辑', 'sys:permission:write', 'BUTTON', NULL, 1, 99, 'system', 'system', 0),
(2814, 2800, '菜单编辑', 'sys:menu:write', 'BUTTON', NULL, 1, 100, 'system', 'system', 0),
(2815, 2800, '字典编辑', 'sys:dict:write', 'BUTTON', NULL, 1, 101, 'system', 'system', 0),
(2816, 2800, '参数编辑', 'sys:param:write', 'BUTTON', NULL, 1, 102, 'system', 'system', 0),

(2900, 0, '组织扩展能力', NULL, 'MENU', '/system/org-extension', 1, 110, 'system', 'system', 1),
(2901, 2900, '集团扩展', 'group:read', 'BUTTON', NULL, 1, 111, 'system', 'system', 0),
(2902, 2900, '品牌扩展', 'brand:read', 'BUTTON', NULL, 1, 112, 'system', 'system', 0),
(2903, 2900, '全量数据范围', 'scope:all', 'BUTTON', NULL, 1, 113, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- NOTE:
-- 1) Homestay records can be created without group/brand (hotel_property.group_id/brand_id are nullable).
-- 2) System write permissions are seeded above: sys:user:write, sys:role:write, sys:dict:write, sys:param:write.
-- 说明：
-- 1) 民宿可独立建档，hotel_property.group_id/brand_id 允许为空。
-- 2) 系统写权限已在上方初始化：sys:user:write、sys:role:write、sys:dict:write、sys:param:write。

INSERT INTO sys_user_role (user_id, role_id, created_by, updated_by, deleted)
VALUES (1, 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO sys_role_menu (role_id, menu_id, created_by, updated_by, deleted)
VALUES
(1, 2000, 'system', 'system', 0), (1, 2001, 'system', 'system', 0), (1, 2002, 'system', 'system', 0), (1, 2003, 'system', 'system', 0), (1, 2004, 'system', 'system', 0),
(1, 2100, 'system', 'system', 0), (1, 2101, 'system', 'system', 0), (1, 2102, 'system', 'system', 0), (1, 2103, 'system', 'system', 0), (1, 2104, 'system', 'system', 0), (1, 2111, 'system', 'system', 0), (1, 2112, 'system', 'system', 0), (1, 2113, 'system', 'system', 0),
(1, 2200, 'system', 'system', 0), (1, 2201, 'system', 'system', 0), (1, 2202, 'system', 'system', 0), (1, 2203, 'system', 'system', 0), (1, 2204, 'system', 'system', 0), (1, 2205, 'system', 'system', 0), (1, 2211, 'system', 'system', 0), (1, 2212, 'system', 'system', 0), (1, 2213, 'system', 'system', 0),
(1, 2300, 'system', 'system', 0), (1, 2301, 'system', 'system', 0), (1, 2302, 'system', 'system', 0), (1, 2303, 'system', 'system', 0), (1, 2304, 'system', 'system', 0), (1, 2311, 'system', 'system', 0), (1, 2312, 'system', 'system', 0),
(1, 2400, 'system', 'system', 0), (1, 2401, 'system', 'system', 0), (1, 2402, 'system', 'system', 0), (1, 2403, 'system', 'system', 0), (1, 2404, 'system', 'system', 0), (1, 2411, 'system', 'system', 0),
(1, 2500, 'system', 'system', 0), (1, 2501, 'system', 'system', 0), (1, 2502, 'system', 'system', 0), (1, 2503, 'system', 'system', 0), (1, 2504, 'system', 'system', 0), (1, 2511, 'system', 'system', 0),
(1, 2600, 'system', 'system', 0), (1, 2601, 'system', 'system', 0), (1, 2602, 'system', 'system', 0), (1, 2603, 'system', 'system', 0), (1, 2604, 'system', 'system', 0), (1, 2605, 'system', 'system', 0), (1, 2611, 'system', 'system', 0), (1, 2612, 'system', 'system', 0),
(1, 2700, 'system', 'system', 0), (1, 2701, 'system', 'system', 0), (1, 2702, 'system', 'system', 0), (1, 2703, 'system', 'system', 0), (1, 2704, 'system', 'system', 0), (1, 2711, 'system', 'system', 0),
(1, 2800, 'system', 'system', 0), (1, 2801, 'system', 'system', 0), (1, 2802, 'system', 'system', 0), (1, 2803, 'system', 'system', 0), (1, 2804, 'system', 'system', 0), (1, 2805, 'system', 'system', 0), (1, 2806, 'system', 'system', 0),
(1, 2811, 'system', 'system', 0), (1, 2812, 'system', 'system', 0), (1, 2813, 'system', 'system', 0), (1, 2814, 'system', 'system', 0), (1, 2815, 'system', 'system', 0), (1, 2816, 'system', 'system', 0),
(1, 2901, 'system', 'system', 0), (1, 2902, 'system', 'system', 0), (1, 2903, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO sys_dict (dict_code, dict_name, status, sort_no, remark, created_by, updated_by, deleted)
VALUES
('GENDER', '性别 / Gender', 1, 10, '入住人及会员性别字典 / guest and member gender dictionary', 'system', 'system', 0),
('BUSINESS_MODE', '经营模式 / Business Mode', 1, 20, '民宿与酒店经营模式 / homestay and hotel mode', 'system', 'system', 0),
('ROOM_STATUS', '房间状态 / Room Status', 1, 30, '房态字典 / room status dictionary', 'system', 'system', 0)
ON DUPLICATE KEY UPDATE
  dict_name = VALUES(dict_name),
  status = VALUES(status),
  sort_no = VALUES(sort_no),
  remark = VALUES(remark),
  updated_at = CURRENT_TIMESTAMP;

INSERT INTO sys_dict_item (dict_code, item_code, item_name, item_value, status, sort_no, remark, created_by, updated_by, deleted)
VALUES
('GENDER', 'MALE', '男 / Male', 'MALE', 1, 10, '男性 / Male', 'system', 'system', 0),
('GENDER', 'FEMALE', '女 / Female', 'FEMALE', 1, 20, '女性 / Female', 'system', 'system', 0),
('GENDER', 'UNKNOWN', '未知 / Unknown', 'UNKNOWN', 1, 30, '未知性别 / Unknown', 'system', 'system', 0),

('BUSINESS_MODE', 'HOMESTAY', '民宿 / Homestay', 'HOMESTAY', 1, 10, '民宿经营模式 / homestay mode', 'system', 'system', 0),
('BUSINESS_MODE', 'HOTEL', '酒店 / Hotel', 'HOTEL', 1, 20, '酒店经营模式 / hotel mode', 'system', 'system', 0),

('ROOM_STATUS', 'VACANT_CLEAN', '空净 / Vacant Clean', 'VACANT_CLEAN', 1, 10, '可直接入住 / ready to check in', 'system', 'system', 0),
('ROOM_STATUS', 'OCCUPIED', '在住 / Occupied', 'OCCUPIED', 1, 20, '住客在住 / currently occupied', 'system', 'system', 0),
('ROOM_STATUS', 'VACANT_DIRTY', '待清扫 / Vacant Dirty', 'VACANT_DIRTY', 1, 30, '待保洁 / waiting for housekeeping', 'system', 'system', 0),
('ROOM_STATUS', 'MAINTENANCE', '维修 / Maintenance', 'MAINTENANCE', 1, 40, '维修中 / under maintenance', 'system', 'system', 0),
('ROOM_STATUS', 'LOCKED', '锁房 / Locked', 'LOCKED', 1, 50, '不可售 / not for sale', 'system', 'system', 0)
ON DUPLICATE KEY UPDATE
  item_name = VALUES(item_name),
  item_value = VALUES(item_value),
  status = VALUES(status),
  sort_no = VALUES(sort_no),
  remark = VALUES(remark),
  updated_at = CURRENT_TIMESTAMP;

INSERT INTO sys_param (param_key, param_value, remark, created_by, updated_by, deleted)
VALUES
('hms.currentProperty.prefer', 'true', '日志筛选默认按当前门店收敛 / log filters prefer current property by default', 'system', 'system', 0),
('hms.ota.callback.maxRetry', '5', 'OTA回调最大重试次数 / max retry count for OTA callbacks', 'system', 'system', 0),
('hms.ota.callback.firstRetrySeconds', '30', 'OTA回调首次重试秒数 / first retry delay seconds for OTA callbacks', 'system', 'system', 0)
ON DUPLICATE KEY UPDATE
  param_value = VALUES(param_value),
  remark = VALUES(remark),
  updated_at = CURRENT_TIMESTAMP;

INSERT INTO hotel_group (id, group_code, group_name, status, created_by, updated_by, deleted)
VALUES (1, 'G001', '示例集团', 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO hotel_brand (id, group_id, brand_code, brand_name, status, created_by, updated_by, deleted)
VALUES (1, 1, 'B001', '示例品牌', 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO hotel_property (id, group_id, brand_id, property_code, property_name, business_mode, contact_phone, province, city, district, address, status, created_by, updated_by, deleted)
VALUES (1, 1, 1, 'P001', '示例民宿', 'HOMESTAY', '021-12345678', '上海市', '上海市', '浦东新区', '世纪大道100号', 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO hotel_room_type (id, property_id, room_type_code, room_type_name, max_guest_count, bed_type, base_price, status, created_by, updated_by, deleted)
VALUES (1, 1, 'RT001', '高级大床房', 2, 'King', 388.00, 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO hotel_room (id, property_id, room_type_id, room_no, floor_no, status, created_by, updated_by, deleted)
VALUES
(1, 1, 1, '1801', '18', 'VACANT_CLEAN', 'system', 'system', 0),
(2, 1, 1, '1802', '18', 'OCCUPIED', 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO hotel_room_status_log (room_id, old_status, new_status, reason, operator)
VALUES (2, 'VACANT_CLEAN', 'OCCUPIED', '初始测试数据入住', 'system');

-- =========================
-- Phase 2 & 3 Extended Tables / 第二三阶段扩展表
-- =========================

CREATE TABLE IF NOT EXISTS sys_user_property_scope (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID / Primary Key ID',
  user_id BIGINT NOT NULL COMMENT '用户ID / User ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_user_property_scope (user_id, property_id),
  KEY idx_user_property_scope_property_id (property_id)
) COMMENT='用户门店数据范围表 / User Property Scope Table';

CREATE TABLE IF NOT EXISTS hotel_reservation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预订ID / Reservation ID',
  reservation_no VARCHAR(64) NOT NULL COMMENT '预订号 / Reservation No',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  room_type_id BIGINT NOT NULL COMMENT '房型ID / Room Type ID',
  channel_code VARCHAR(32) DEFAULT 'DIRECT' COMMENT '来源渠道 / Channel Code',
  contact_name VARCHAR(64) NOT NULL COMMENT '联系人 / Contact Name',
  contact_mobile VARCHAR(32) NOT NULL COMMENT '联系人手机号 / Contact Mobile',
  guest_count INT NOT NULL DEFAULT 1 COMMENT '入住人数 / Guest Count',
  check_in_date DATE NOT NULL COMMENT '入住日期 / Check-in Date',
  check_out_date DATE NOT NULL COMMENT '离店日期 / Check-out Date',
  reservation_status VARCHAR(32) NOT NULL COMMENT '预订状态 / Reservation Status',
  estimated_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '预估金额 / Estimated Amount',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注 / Remark',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_hotel_reservation_no (reservation_no),
  KEY idx_hotel_reservation_property_date (property_id, check_in_date)
) COMMENT='预订表 / Reservation Table';

CREATE TABLE IF NOT EXISTS hotel_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID / Order ID',
  order_no VARCHAR(64) NOT NULL COMMENT '订单号 / Order No',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  room_type_id BIGINT NOT NULL COMMENT '房型ID / Room Type ID',
  source_channel VARCHAR(32) NOT NULL DEFAULT 'DIRECT' COMMENT '来源渠道 / Source Channel',
  guest_name VARCHAR(64) NOT NULL COMMENT '住客姓名 / Guest Name',
  guest_mobile VARCHAR(32) NOT NULL COMMENT '住客手机号 / Guest Mobile',
  check_in_date DATE NOT NULL COMMENT '入住日期 / Check-in Date',
  check_out_date DATE NOT NULL COMMENT '离店日期 / Check-out Date',
  total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单金额 / Total Amount',
  order_status VARCHAR(32) NOT NULL COMMENT '订单状态 / Order Status',
  channel_order_no VARCHAR(128) DEFAULT NULL COMMENT '渠道订单号 / Channel Order No',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_hotel_order_order_no (order_no),
  KEY idx_hotel_order_property_id (property_id),
  KEY idx_hotel_order_room_type_id (room_type_id),
  KEY idx_hotel_order_status (order_status)
) COMMENT='订单主表 / Hotel Order Table';

CREATE TABLE IF NOT EXISTS hotel_stay_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '入住记录ID / Stay Record ID',
  stay_no VARCHAR(64) NOT NULL COMMENT '入住单号 / Stay No',
  order_id BIGINT NOT NULL COMMENT '订单ID / Order ID',
  reservation_id BIGINT DEFAULT NULL COMMENT '预订ID / Reservation ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  room_id BIGINT NOT NULL COMMENT '房间ID / Room ID',
  stay_type VARCHAR(32) NOT NULL COMMENT '入住类型 / Stay Type',
  stay_status VARCHAR(32) NOT NULL COMMENT '入住状态 / Stay Status',
  check_in_date DATE NOT NULL COMMENT '入住日期 / Check-in Date',
  check_out_date DATE NOT NULL COMMENT '离店日期 / Check-out Date',
  actual_check_in_time DATETIME DEFAULT NULL COMMENT '实际入住时间 / Actual Check-in Time',
  actual_check_out_time DATETIME DEFAULT NULL COMMENT '实际离店时间 / Actual Check-out Time',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_hotel_stay_no (stay_no),
  KEY idx_hotel_stay_record_property (property_id),
  KEY idx_hotel_stay_record_order (order_id)
) COMMENT='入住记录表 / Stay Record Table';

CREATE TABLE IF NOT EXISTS hotel_guest_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '入住人ID / Guest Profile ID',
  order_id BIGINT NOT NULL COMMENT '订单ID / Order ID',
  stay_record_id BIGINT DEFAULT NULL COMMENT '入住记录ID / Stay Record ID',
  guest_name VARCHAR(64) NOT NULL COMMENT '姓名 / Name',
  guest_mobile VARCHAR(32) NOT NULL COMMENT '手机号 / Mobile',
  certificate_type VARCHAR(32) DEFAULT NULL COMMENT '证件类型 / Certificate Type',
  certificate_no VARCHAR(64) DEFAULT NULL COMMENT '证件号 / Certificate No',
  is_primary CHAR(1) NOT NULL DEFAULT 'N' COMMENT '主入住人(Y/N) / Primary Guest (Y/N)',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  KEY idx_hotel_guest_profile_order (order_id),
  KEY idx_hotel_guest_profile_stay (stay_record_id)
) COMMENT='入住人档案表 / Guest Profile Table';

CREATE TABLE IF NOT EXISTS hotel_payment_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付记录ID / Payment Record ID',
  order_id BIGINT NOT NULL COMMENT '订单ID / Order ID',
  stay_record_id BIGINT DEFAULT NULL COMMENT '入住记录ID / Stay Record ID',
  payment_type VARCHAR(32) NOT NULL COMMENT '支付类型 / Payment Type',
  payment_method VARCHAR(32) NOT NULL COMMENT '支付方式 / Payment Method',
  amount DECIMAL(10,2) NOT NULL COMMENT '金额 / Amount',
  payment_status VARCHAR(32) NOT NULL COMMENT '支付状态 / Payment Status',
  external_trade_no VARCHAR(128) DEFAULT NULL COMMENT '外部交易号 / External Trade No',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注 / Remark',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  KEY idx_hotel_payment_record_order (order_id)
) COMMENT='支付记录表 / Payment Record Table';

CREATE TABLE IF NOT EXISTS hotel_price_plan (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '价格计划ID / Price Plan ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  room_type_id BIGINT NOT NULL COMMENT '房型ID / Room Type ID',
  biz_date DATE NOT NULL COMMENT '营业日期 / Business Date',
  sale_price DECIMAL(10,2) NOT NULL COMMENT '销售价 / Sale Price',
  sellable_inventory INT NOT NULL DEFAULT 0 COMMENT '可售库存 / Sellable Inventory',
  overbook_limit INT NOT NULL DEFAULT 0 COMMENT '超售上限 / Overbook Limit',
  price_tag VARCHAR(64) DEFAULT NULL COMMENT '价格标签 / Price Tag',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_hotel_price_plan (property_id, room_type_id, biz_date),
  KEY idx_hotel_price_plan_property_date (property_id, biz_date)
) COMMENT='门店房价计划表 / Property Price Plan Table';

CREATE TABLE IF NOT EXISTS hotel_price_rule (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '价格规则ID / Price Rule ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  rule_name VARCHAR(128) NOT NULL COMMENT '规则名称 / Rule Name',
  rule_type VARCHAR(32) NOT NULL COMMENT '规则类型 / Rule Type',
  rule_value VARCHAR(255) NOT NULL COMMENT '规则值 / Rule Value',
  delta_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '增减金额 / Delta Amount',
  enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用 / Enabled',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  KEY idx_hotel_price_rule_property (property_id)
) COMMENT='房价规则表 / Price Rule Table';

CREATE TABLE IF NOT EXISTS hotel_inventory_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '库存记录ID / Inventory Record ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  room_type_id BIGINT NOT NULL COMMENT '房型ID / Room Type ID',
  biz_date DATE NOT NULL COMMENT '营业日期 / Business Date',
  total_inventory INT NOT NULL DEFAULT 0 COMMENT '总库存 / Total Inventory',
  occupied_inventory INT NOT NULL DEFAULT 0 COMMENT '占用库存 / Occupied Inventory',
  available_inventory INT NOT NULL DEFAULT 0 COMMENT '可用库存 / Available Inventory',
  warning_threshold INT NOT NULL DEFAULT 2 COMMENT '告警阈值 / Warning Threshold',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_hotel_inventory_uk (property_id, room_type_id, biz_date),
  KEY idx_hotel_inventory_property_date (property_id, biz_date)
) COMMENT='库存控制表 / Inventory Control Table';

CREATE TABLE IF NOT EXISTS channel_mapping (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '映射ID / Mapping ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  channel_code VARCHAR(32) NOT NULL COMMENT '渠道编码 / Channel Code',
  mapping_type VARCHAR(32) NOT NULL COMMENT '映射类型 / Mapping Type',
  local_biz_id BIGINT NOT NULL COMMENT '本地业务ID / Local Biz ID',
  channel_biz_id VARCHAR(128) NOT NULL COMMENT '渠道业务ID / Channel Biz ID',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注 / Remark',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_channel_mapping_unique (property_id, channel_code, mapping_type, local_biz_id),
  KEY idx_channel_mapping_property (property_id)
) COMMENT='渠道映射表 / Channel Mapping Table';

CREATE TABLE IF NOT EXISTS member_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会员ID / Member ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  member_no VARCHAR(64) NOT NULL COMMENT '会员编号 / Member No',
  member_name VARCHAR(64) NOT NULL COMMENT '会员姓名 / Member Name',
  mobile VARCHAR(32) NOT NULL COMMENT '手机号 / Mobile',
  gender VARCHAR(32) DEFAULT NULL COMMENT '性别编码 / Gender Code',
  level_code INT NOT NULL DEFAULT 1 COMMENT '会员等级 / Level Code',
  point_balance INT NOT NULL DEFAULT 0 COMMENT '积分余额 / Point Balance',
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态 / Status',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_member_profile_no (member_no),
  KEY idx_member_profile_property (property_id)
) COMMENT='会员档案表 / Member Profile Table';

CREATE TABLE IF NOT EXISTS member_point_ledger (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '积分流水ID / Point Ledger ID',
  member_id BIGINT NOT NULL COMMENT '会员ID / Member ID',
  point_delta INT NOT NULL COMMENT '积分变动 / Point Delta',
  biz_type VARCHAR(32) DEFAULT NULL COMMENT '业务类型 / Biz Type',
  biz_no VARCHAR(64) DEFAULT NULL COMMENT '业务单号 / Biz No',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注 / Remark',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  KEY idx_member_point_member (member_id)
) COMMENT='积分流水表 / Member Point Ledger Table';

CREATE TABLE IF NOT EXISTS coupon_template (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '券模板ID / Coupon Template ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  coupon_code VARCHAR(64) NOT NULL COMMENT '券编码 / Coupon Code',
  coupon_name VARCHAR(128) NOT NULL COMMENT '券名称 / Coupon Name',
  amount DECIMAL(10,2) NOT NULL COMMENT '券面值 / Amount',
  threshold_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '使用门槛 / Threshold Amount',
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态 / Status',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_coupon_template_code (coupon_code),
  KEY idx_coupon_template_property (property_id)
) COMMENT='优惠券模板表 / Coupon Template Table';

CREATE TABLE IF NOT EXISTS member_preference (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '偏好ID / Preference ID',
  member_id BIGINT NOT NULL COMMENT '会员ID / Member ID',
  preference_type VARCHAR(64) NOT NULL COMMENT '偏好类型 / Preference Type',
  preference_value VARCHAR(255) NOT NULL COMMENT '偏好值 / Preference Value',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  KEY idx_member_preference_member (member_id)
) COMMENT='会员偏好表 / Member Preference Table';

CREATE TABLE IF NOT EXISTS marketing_campaign (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '活动ID / Campaign ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  campaign_code VARCHAR(64) NOT NULL COMMENT '活动编码 / Campaign Code',
  campaign_name VARCHAR(128) NOT NULL COMMENT '活动名称 / Campaign Name',
  campaign_type VARCHAR(64) NOT NULL COMMENT '活动类型 / Campaign Type',
  start_date DATE DEFAULT NULL COMMENT '开始日期 / Start Date',
  end_date DATE DEFAULT NULL COMMENT '结束日期 / End Date',
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态 / Status',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  UNIQUE KEY uk_marketing_campaign_code (campaign_code),
  KEY idx_marketing_campaign_property (property_id)
) COMMENT='营销活动表 / Marketing Campaign Table';

CREATE TABLE IF NOT EXISTS finance_refund_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '退款记录ID / Refund Record ID',
  payment_id BIGINT NOT NULL COMMENT '支付记录ID / Payment Record ID',
  order_id BIGINT NOT NULL COMMENT '订单ID / Order ID',
  refund_amount DECIMAL(10,2) NOT NULL COMMENT '退款金额 / Refund Amount',
  refund_reason VARCHAR(255) DEFAULT NULL COMMENT '退款原因 / Refund Reason',
  refund_status VARCHAR(32) NOT NULL COMMENT '退款状态 / Refund Status',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  KEY idx_finance_refund_order (order_id)
) COMMENT='退款记录表 / Refund Record Table';

CREATE TABLE IF NOT EXISTS finance_bill_detail (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '账单明细ID / Bill Detail ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  order_id BIGINT NOT NULL COMMENT '订单ID / Order ID',
  bill_type VARCHAR(32) NOT NULL COMMENT '账单类型 / Bill Type',
  bill_item VARCHAR(128) NOT NULL COMMENT '账单项 / Bill Item',
  amount DECIMAL(10,2) NOT NULL COMMENT '金额 / Amount',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  KEY idx_finance_bill_property (property_id),
  KEY idx_finance_bill_order (order_id)
) COMMENT='账单明细表 / Bill Detail Table';

CREATE TABLE IF NOT EXISTS housekeeping_task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '保洁任务ID / Housekeeping Task ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  room_id BIGINT NOT NULL COMMENT '房间ID / Room ID',
  biz_date DATE NOT NULL COMMENT '任务日期 / Task Date',
  task_status VARCHAR(32) NOT NULL COMMENT '任务状态 / Task Status',
  assignee VARCHAR(64) DEFAULT NULL COMMENT '负责人 / Assignee',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注 / Remark',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  KEY idx_housekeeping_task_property_date (property_id, biz_date)
) COMMENT='保洁任务表 / Housekeeping Task Table';

CREATE TABLE IF NOT EXISTS maintenance_ticket (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '维修工单ID / Maintenance Ticket ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  room_id BIGINT NOT NULL COMMENT '房间ID / Room ID',
  issue_type VARCHAR(64) NOT NULL COMMENT '问题类型 / Issue Type',
  issue_description VARCHAR(255) NOT NULL COMMENT '问题描述 / Issue Description',
  ticket_status VARCHAR(32) NOT NULL COMMENT '工单状态 / Ticket Status',
  assignee VARCHAR(64) DEFAULT NULL COMMENT '负责人 / Assignee',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  KEY idx_maintenance_ticket_property (property_id)
) COMMENT='维修工单表 / Maintenance Ticket Table';

CREATE TABLE IF NOT EXISTS invoice_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '发票记录ID / Invoice Record ID',
  order_id BIGINT NOT NULL COMMENT '订单ID / Order ID',
  property_id BIGINT NOT NULL COMMENT '门店ID / Property ID',
  invoice_type VARCHAR(32) DEFAULT NULL COMMENT '发票类型 / Invoice Type',
  invoice_title VARCHAR(128) DEFAULT NULL COMMENT '发票抬头 / Invoice Title',
  tax_no VARCHAR(64) DEFAULT NULL COMMENT '税号 / Tax Number',
  amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '开票金额 / Invoice Amount',
  invoice_status VARCHAR(32) NOT NULL COMMENT '发票状态 / Invoice Status',
  created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人 / Created By',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人 / Updated By',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 / Logical Deleted',
  KEY idx_invoice_record_property (property_id),
  KEY idx_invoice_record_order (order_id)
) COMMENT='发票记录表 / Invoice Record Table';

CREATE TABLE IF NOT EXISTS audit_log_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '审计日志ID / Audit Log ID',
  module_code VARCHAR(64) NOT NULL COMMENT '模块编码 / Module Code',
  biz_no VARCHAR(64) DEFAULT NULL COMMENT '业务单号 / Biz No',
  action_type VARCHAR(64) NOT NULL COMMENT '动作类型 / Action Type',
  content VARCHAR(500) DEFAULT NULL COMMENT '内容 / Content',
  operator VARCHAR(64) DEFAULT NULL COMMENT '操作人 / Operator',
  group_id BIGINT NULL COMMENT '集团ID / Group ID',
  brand_id BIGINT NULL COMMENT '品牌ID / Brand ID',
  property_id BIGINT NULL COMMENT '门店ID / Property ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At'
) COMMENT='审计日志表 / Audit Log Table';

CREATE TABLE IF NOT EXISTS operation_log_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '操作日志ID / Operation Log ID',
  module_code VARCHAR(64) NOT NULL COMMENT '模块编码 / Module Code',
  operation VARCHAR(255) NOT NULL COMMENT '操作描述 / Operation',
  request_uri VARCHAR(255) DEFAULT NULL COMMENT '请求路径 / Request URI',
  request_method VARCHAR(16) DEFAULT NULL COMMENT '请求方法 / Request Method',
  operator VARCHAR(64) DEFAULT NULL COMMENT '操作人 / Operator',
  success_flag CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '是否成功 / Success Flag',
  message VARCHAR(500) DEFAULT NULL COMMENT '消息 / Message',
  group_id BIGINT NULL COMMENT '集团ID / Group ID',
  brand_id BIGINT NULL COMMENT '品牌ID / Brand ID',
  property_id BIGINT NULL COMMENT '门店ID / Property ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At'
) COMMENT='操作日志表 / Operation Log Table';

-- Phase 2/3 permissions have been merged into the homestay IA seed block above.
-- 第二三阶段权限已合并到上方“民宿信息架构”菜单初始化块中。

INSERT INTO sys_user_property_scope (user_id, property_id, created_by, updated_by, deleted)
VALUES (1, 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- =========================
-- Demo Data for All System Modules / 各系统模块演示数据
-- =========================

-- Additional Users / 额外用户
INSERT INTO sys_user (id, username, password_hash, nickname, mobile, email, status, created_by, updated_by, deleted)
VALUES 
(2, 'manager', '$2b$12$Shy81wwJpP1bv22tA6r2Ruxketd61fSbggJjfkzix1ssC946nQmPq', '民宿经理', '13900000002', 'manager@hotel.com', 1, 'system', 'system', 0),
(3, 'receptionist', '$2b$12$Shy81wwJpP1bv22tA6r2Ruxketd61fSbggJjfkzix1ssC946nQmPq', '前台接待', '13900000003', 'receptionist@hotel.com', 1, 'system', 'system', 0),
(4, 'housekeeper', '$2b$12$Shy81wwJpP1bv22tA6r2Ruxketd61fSbggJjfkzix1ssC946nQmPq', '保洁员', '13900000004', 'housekeeper@hotel.com', 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- Additional Roles / 额外角色
INSERT INTO sys_role (id, role_code, role_name, status, created_by, updated_by, deleted)
VALUES 
(2, 'MANAGER', '民宿经理', 1, 'system', 'system', 0),
(3, 'RECEPTIONIST', '前台接待', 1, 'system', 'system', 0),
(4, 'HOUSEKEEPER', '保洁员', 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- User Role Assignments / 用户角色分配
INSERT INTO sys_user_role (user_id, role_id, created_by, updated_by, deleted)
VALUES 
(2, 2, 'system', 'system', 0),
(3, 3, 'system', 'system', 0),
(4, 4, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- Additional Groups & Brands & Properties / 额外集团、品牌、民宿
INSERT INTO hotel_group (id, group_code, group_name, status, created_by, updated_by, deleted)
VALUES 
(2, 'G002', '连锁民宿集团', 1, 'system', 'system', 0),
(3, 'G003', '精品酒店集团', 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO hotel_brand (id, group_id, brand_code, brand_name, status, created_by, updated_by, deleted)
VALUES 
(2, 2, 'B002', '温馨民宿', 1, 'system', 'system', 0),
(3, 3, 'B003', '豪华酒店', 1, 'system', 'system', 0),
(4, 2, 'B004', '独立品牌', 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO hotel_property (id, group_id, brand_id, property_code, property_name, business_mode, contact_phone, province, city, district, address, status, created_by, updated_by, deleted)
VALUES 
(2, 2, 2, 'P002', '海景民宿', 'HOMESTAY', '0755-88888888', '广东省', '深圳市', '南山区', '海滨路88号', 1, 'system', 'system', 0),
(3, 3, 3, 'P003', '商务酒店', 'HOTEL', '010-66666666', '北京市', '北京市', '朝阳区', '建国门外大街1号', 1, 'system', 'system', 0),
(4, 2, 4, 'P004', '山景客栈', 'HOMESTAY', '0571-99999999', '浙江省', '杭州市', '西湖区', '西湖景区100号', 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- Additional Room Types / 额外房型
INSERT INTO hotel_room_type (id, property_id, room_type_code, room_type_name, max_guest_count, bed_type, base_price, status, created_by, updated_by, deleted)
VALUES 
(2, 1, 'RT002', '豪华双床房', 2, 'Twin', 428.00, 1, 'system', 'system', 0),
(3, 1, 'RT003', '家庭套房', 4, 'King+Twin', 688.00, 1, 'system', 'system', 0),
(4, 2, 'RT004', '海景大床房', 2, 'King', 588.00, 1, 'system', 'system', 0),
(5, 3, 'RT005', '商务单间', 1, 'Single', 298.00, 1, 'system', 'system', 0),
(6, 4, 'RT006', '山景标间', 2, 'Twin', 368.00, 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- Additional Rooms / 额外房间
INSERT INTO hotel_room (id, property_id, room_type_id, room_no, floor_no, status, created_by, updated_by, deleted)
VALUES 
(3, 1, 2, '1803', '18', 'VACANT_DIRTY', 'system', 'system', 0),
(4, 1, 3, '1804', '18', 'MAINTENANCE', 'system', 'system', 0),
(5, 1, 1, '1701', '17', 'VACANT_CLEAN', 'system', 'system', 0),
(6, 1, 2, '1702', '17', 'OCCUPIED', 'system', 'system', 0),
(7, 2, 4, '2001', '20', 'VACANT_CLEAN', 'system', 'system', 0),
(8, 2, 4, '2002', '20', 'OCCUPIED', 'system', 'system', 0),
(9, 3, 5, '1001', '10', 'VACANT_CLEAN', 'system', 'system', 0),
(10, 3, 5, '1002', '10', 'LOCKED', 'system', 'system', 0),
(11, 4, 6, '3001', '3', 'VACANT_CLEAN', 'system', 'system', 0),
(12, 4, 6, '3002', '3', 'VACANT_DIRTY', 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- Room Status Logs / 房态日志
INSERT INTO hotel_room_status_log (room_id, old_status, new_status, reason, operator)
VALUES 
(3, 'VACANT_CLEAN', 'VACANT_DIRTY', '客人退房后待清扫', 'system'),
(4, 'VACANT_CLEAN', 'MAINTENANCE', '空调维修', 'system'),
(6, 'VACANT_CLEAN', 'OCCUPIED', '客人入住', 'system'),
(8, 'VACANT_CLEAN', 'OCCUPIED', '客人入住', 'system'),
(10, 'VACANT_CLEAN', 'LOCKED', '预留房间', 'system');

-- Reservations / 预订记录
INSERT INTO hotel_reservation (reservation_no, property_id, room_type_id, channel_code, contact_name, contact_mobile, guest_count, check_in_date, check_out_date, reservation_status, estimated_amount, remark, created_by, updated_by, deleted)
VALUES 
('RSV202403001', 1, 1, 'DIRECT', '张三', '13900000011', 2, CURRENT_DATE + INTERVAL 2 DAY, CURRENT_DATE + INTERVAL 3 DAY, 'CONFIRMED', 388.00, '电话预订', 'system', 'system', 0),
('RSV202403002', 2, 4, 'OTA', '李四', '13900000012', 2, CURRENT_DATE + INTERVAL 3 DAY, CURRENT_DATE + INTERVAL 5 DAY, 'CONFIRMED', 1176.00, '携程预订', 'system', 'system', 0),
('RSV202403003', 3, 5, 'DIRECT', '王五', '13900000013', 1, CURRENT_DATE + INTERVAL 1 DAY, CURRENT_DATE + INTERVAL 2 DAY, 'PENDING', 298.00, '待确认预订', 'system', 'system', 0),
('RSV202403004', 4, 6, 'WECHAT', '赵六', '13900000014', 2, CURRENT_DATE + INTERVAL 4 DAY, CURRENT_DATE + INTERVAL 6 DAY, 'CONFIRMED', 736.00, '微信预订', 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- Orders / 订单记录
INSERT INTO hotel_order (order_no, property_id, room_type_id, source_channel, guest_name, guest_mobile, check_in_date, check_out_date, total_amount, order_status, channel_order_no, created_by, updated_by, deleted)
VALUES 
('ORD202403001', 1, 1, 'DIRECT', '张三', '13900000011', CURRENT_DATE + INTERVAL 2 DAY, CURRENT_DATE + INTERVAL 3 DAY, 388.00, 'CONFIRMED', NULL, 'system', 'system', 0),
('ORD202403002', 2, 4, 'OTA', '李四', '13900000012', CURRENT_DATE + INTERVAL 3 DAY, CURRENT_DATE + INTERVAL 5 DAY, 1176.00, 'PAID', 'CT202403002', 'system', 'system', 0),
('ORD202403003', 1, 2, 'DIRECT', '钱七', '13900000015', CURRENT_DATE, CURRENT_DATE + INTERVAL 2 DAY, 856.00, 'CHECKED_IN', NULL, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- Members / 会员信息
INSERT INTO member_profile (id, property_id, member_no, member_name, mobile, gender, level_code, point_balance, status, created_by, updated_by, deleted)
VALUES 
(1, 1, 'M000001', '张三', '13900000011', 'MALE', 3, 800, 'ACTIVE', 'system', 'system', 0),
(2, 1, 'M000002', '李四', '13900000012', 'FEMALE', 2, 380, 'ACTIVE', 'system', 'system', 0),
(3, 1, 'M000003', '王五', '13900000013', 'MALE', 1, 180, 'ACTIVE', 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- Pricing Rules / 价格规则 (使用实际的价格规则表结构)
INSERT INTO hotel_price_rule (id, property_id, rule_name, rule_type, rule_value, delta_amount, enabled, created_by, updated_by, deleted)
VALUES 
(1, 1, '标准房价', 'BASE_PRICE', 'STANDARD', 0.00, 1, 'system', 'system', 0),
(2, 2, '海景房价', 'BASE_PRICE', 'OCEAN_VIEW', 200.00, 1, 'system', 'system', 0),
(3, 3, '商务房价', 'BASE_PRICE', 'BUSINESS', -90.00, 1, 'system', 'system', 0),
(4, 4, '山景房价', 'BASE_PRICE', 'MOUNTAIN_VIEW', -20.00, 1, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- Channel Config / 渠道配置
INSERT INTO channel_account (id, property_id, channel_code, app_key, app_secret, auth_status, created_by, updated_by, deleted)
VALUES 
(1, 1, 'CTrip', 'ctrip_key_001', 'ctrip_secret_001', 1, 'system', 'system', 0),
(2, 2, 'Fliggy', 'fliggy_key_001', 'fliggy_secret_001', 1, 'system', 'system', 0),
(3, 1, 'Booking', 'booking_key_001', 'booking_secret_001', 0, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

CREATE TABLE IF NOT EXISTS channel_callback_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '回调日志ID / Callback Log ID',
  idempotent_key VARCHAR(255) NULL COMMENT '幂等键 / Idempotent Key',
  channel_code VARCHAR(32) NOT NULL COMMENT '渠道编码 / Channel Code',
  event_type VARCHAR(64) NULL COMMENT '事件类型 / Event Type',
  external_request_no VARCHAR(128) NULL COMMENT '外部请求号 / External Request No',
  signature VARCHAR(255) DEFAULT NULL COMMENT '签名值 / Signature',
  callback_status VARCHAR(32) NOT NULL COMMENT '回调状态 / Callback Status',
  payload LONGTEXT COMMENT '回调报文 / Callback Payload',
  message VARCHAR(255) DEFAULT NULL COMMENT '处理消息 / Message',
  processed_flag CHAR(1) NOT NULL DEFAULT 'N' COMMENT '是否已消费(Y/N) / Processed Flag (Y/N)',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  UNIQUE KEY uk_channel_callback_log_idempotent (idempotent_key),
  KEY idx_channel_callback_log_code_time (channel_code, created_at),
  KEY idx_channel_callback_log_event (channel_code, event_type, external_request_no)
) COMMENT='渠道回调日志表 / Channel Callback Log Table';

-- Channel Callback Logs / 渠道回调日志
INSERT INTO channel_callback_log (idempotent_key, channel_code, event_type, external_request_no, signature, callback_status, payload, message, processed_flag)
VALUES 
('callback_001', 'CTrip', 'RESERVATION_CONFIRM', 'CT202403001', 'sig_abc123', 'SUCCESS', '{"reservationNo":"RSV202403001","status":"confirmed"}', '预订确认成功', 'Y'),
('callback_002', 'Fliggy', 'RESERVATION_CANCEL', 'FG202403002', 'sig_def456', 'FAILED', '{"reservationNo":"RSV202403002","status":"cancelled"}', '预订取消失败', 'N'),
('callback_003', 'CTrip', 'RATE_UPDATE', 'CT202403003', 'sig_ghi789', 'SUCCESS', '{"roomType":"RT001","rate":388.00}', '房价更新成功', 'Y')
ON DUPLICATE KEY UPDATE
  channel_code = VALUES(channel_code),
  event_type = VALUES(event_type),
  external_request_no = VALUES(external_request_no),
  signature = VALUES(signature),
  callback_status = VALUES(callback_status),
  payload = VALUES(payload),
  message = VALUES(message),
  processed_flag = VALUES(processed_flag);

-- Operation Logs / 操作日志
INSERT INTO operation_log_record (module_code, operation, operator, success_flag, message, property_id, created_at)
VALUES 
('RESERVATION', '创建预订记录: RSV202403001', 'admin', 'Y', '预订创建成功', 1, NOW()),
('ORDER', '更新订单状态: ORD202403002', 'manager', 'Y', '订单状态更新为已付款', 2, NOW()),
('MEMBER', '删除会员记录: M000001', 'system', 'Y', '会员记录删除成功', 1, NOW());

-- Finance Records / 财务记录 (使用实际的支付记录表结构)
INSERT INTO hotel_payment_record (id, order_id, payment_type, payment_method, amount, payment_status, created_by, updated_by, deleted)
VALUES 
(1, 1, 'PAYMENT', 'CASH', 388.00, 'PAID', 'system', 'system', 0),
(2, 2, 'PAYMENT', 'ONLINE', 1176.00, 'PAID', 'system', 'system', 0),
(3, 3, 'PAYMENT', 'WECHAT', 856.00, 'PAID', 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO finance_refund_record (id, payment_id, order_id, refund_amount, refund_reason, refund_status, created_by, updated_by, deleted)
VALUES 
(1, 2, 2, 588.00, '客人申请退款', 'PROCESSING', 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- Additional Dictionary Items / 额外字典项
INSERT INTO sys_dict_item (dict_code, item_code, item_name, item_value, status, sort_no, remark, created_by, updated_by, deleted)
VALUES 
('BUSINESS_MODE', 'GUESTHOUSE', '客栈 / Guesthouse', 'GUESTHOUSE', 1, 30, '客栈经营模式 / guesthouse mode', 'system', 'system', 0),
('GENDER', 'OTHER', '其他 / Other', 'OTHER', 1, 40, '其他性别 / other gender', 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- Additional System Parameters / 额外系统参数
INSERT INTO sys_param (param_key, param_value, remark, created_by, updated_by, deleted)
VALUES 
('hms.checkin.autoCheckoutTime', '12:00', '自动退房时间 / auto checkout time', 'system', 'system', 0),
('hms.pricing.weekendRate', '1.2', '周末房价倍率 / weekend rate multiplier', 'system', 'system', 0),
('hms.notification.sms.enabled', 'true', '短信通知开关 / SMS notification switch', 'system', 'system', 0),
('hms.member.points.rate', '0.1', '积分获取比例 / points earning rate', 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO hotel_reservation (reservation_no, property_id, room_type_id, channel_code, contact_name, contact_mobile, guest_count, check_in_date, check_out_date, reservation_status, estimated_amount, remark, created_by, updated_by, deleted)
VALUES ('RSV_INIT_001', 1, 1, 'DIRECT', '测试住客', '13900000001', 2, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), 'CONFIRMED', 388.00, '初始化预订', 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

INSERT INTO hotel_order (order_no, property_id, room_type_id, source_channel, guest_name, guest_mobile, check_in_date, check_out_date, total_amount, order_status, channel_order_no, created_by, updated_by, deleted)
VALUES ('ORD_INIT_001', 1, 1, 'DIRECT', '测试住客', '13900000001', CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), 388.00, 'CONFIRMED', NULL, 'system', 'system', 0)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- NOTE:
-- order:export and log center permissions are already seeded in menu ids 2213 and 2704.
-- 说明：
-- 订单导出与日志中心权限已在菜单ID 2213、2704中初始化。

-- =========================
-- Enhancement: OTA callback retry task / 增强：OTA回调失败重试任务
-- =========================

CREATE TABLE IF NOT EXISTS ota_callback_retry_task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '重试任务ID / Retry Task ID',
  idempotent_key VARCHAR(255) NOT NULL COMMENT '幂等键 / Idempotent Key',
  channel_code VARCHAR(32) NOT NULL COMMENT '渠道编码 / Channel Code',
  event_type VARCHAR(64) DEFAULT NULL COMMENT '事件类型 / Event Type',
  external_request_no VARCHAR(128) DEFAULT NULL COMMENT '外部请求号 / External Request No',
  signature VARCHAR(255) DEFAULT NULL COMMENT '签名值 / Signature',
  headers_json TEXT COMMENT '请求头JSON / Headers JSON',
  payload LONGTEXT COMMENT '回调报文 / Callback Payload',
  task_status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态(PENDING/RETRYING/SUCCESS/FINAL_FAILED) / Task Status',
  retry_count INT NOT NULL DEFAULT 0 COMMENT '当前重试次数 / Current Retry Count',
  max_retry_count INT NOT NULL DEFAULT 5 COMMENT '最大重试次数 / Max Retry Count',
  last_error VARCHAR(500) DEFAULT NULL COMMENT '最近失败原因 / Last Error Message',
  next_retry_time DATETIME DEFAULT NULL COMMENT '下次重试时间 / Next Retry Time',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间 / Created At',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间 / Updated At',
  KEY idx_ota_retry_status_next (task_status, next_retry_time),
  KEY idx_ota_retry_channel_event (channel_code, event_type, external_request_no),
  UNIQUE KEY uk_ota_retry_idempotent (idempotent_key)
) COMMENT='OTA回调重试任务表 / OTA Callback Retry Task Table';
