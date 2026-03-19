# 酒店/民宿多店管理系统 | Hotel/Homestay Multi-Property Management System

## 1. 项目定位 | Positioning
本项目已从“酒店集团后台”调整为更贴合“民宿多店运营者”的管理系统，默认信息架构弱化集团/品牌主视角，强化民宿、房源、订单、客户、运营与系统治理。

This project has been refocused from a hotel-group admin panel to a homestay multi-property operations system. The default IA now emphasizes homestay assets, orders, CRM, operations, and governance.

## 2. 技术栈 | Tech Stack
- 前端 Frontend: React 18 + TypeScript + Vite + Ant Design + Redux Toolkit
- 后端 Backend: Spring Boot 3 + MyBatis + MySQL + Redis + RabbitMQ
- 鉴权 Auth: JWT + RBAC (button-level permission codes)
- 数据库 Database: MySQL 8.x（表/字段/索引注释保持中英双语）

## 3. 新菜单信息架构 | New Menu IA
前端侧边栏、路由、权限点、默认角色菜单均已按以下结构调整：

1. 工作台 Workbench
- 经营看板 `/workbench/dashboard`
- 今日入住 `/workbench/today-checkin`
- 今日退房 `/workbench/today-checkout`
- 待处理事项 `/workbench/todos`

2. 房源管理 Asset Management
- 民宿管理 `/assets/homestays`
- 房型管理 `/assets/room-types`
- 房间管理 `/assets/rooms`
- 房态管理 `/assets/room-status`

3. 订单中心 Order Center
- 预订订单 `/orders/reservations`
- 入住登记 `/orders/checkin`
- 在住管理 `/orders/inhouse`
- 退房管理 `/orders/checkout`
- 订单详情 `/orders/detail`、`/orders/detail/:id`

4. 价格与库存 Pricing & Inventory
- 房价日历 `/pricing/calendar`
- 价格计划 `/pricing/plans`
- 可售库存 `/pricing/inventory`
- 超售预警 `/pricing/overbook-warning`

5. 渠道管理 Channel Management
- 渠道配置 `/channels/config`
- 渠道映射 `/channels/mapping`
- 同步记录 `/channels/sync-logs`
- 回调记录 `/channels/callback-logs`

6. 客户与会员 CRM & Membership
- 客户档案 `/crm/customers`
- 会员管理 `/crm/members`
- 积分管理 `/crm/points`
- 优惠券管理 `/crm/coupons`

7. 财务管理 Finance
- 收款记录 `/finance/receipts`
- 退款记录 `/finance/refunds`
- 账单明细 `/finance/bills`
- 发票管理 `/finance/invoices`
- 经营报表 `/finance/reports`

8. 运营管理 Operations
- 保洁管理 `/operations/housekeeping`
- 维修工单 `/operations/maintenance`
- 房态记录 `/operations/room-status-record`
- 日志中心 `/operations/log-center`

9. 系统管理 System
- 用户管理 `/system/users`
- 角色管理 `/system/roles`
- 权限管理 `/system/permissions`
- 菜单管理 `/system/menus`
- 字典管理 `/system/dicts`
- 参数配置 `/system/params`

说明：集团/品牌能力作为兼容扩展保留，不作为默认主菜单入口。

## 4. 视觉风格统一 | Unified Visual Style
已通过全局 layout + token + CSS 变量统一实现渐变风格：
- 侧边栏背景 Sider:
  - `linear-gradient(180deg, #0F172A 0%, #1E293B 45%, #334155 100%)`
- 菜单选中态 Menu selected:
  - `linear-gradient(90deg, #2563EB 0%, #38BDF8 100%)`
- 页面主背景 Page background:
  - `linear-gradient(180deg, #F8FAFC 0%, #EEF2FF 100%)`

作用范围覆盖工作台、列表页、详情页、表单页（统一卡片层次、背景氛围）。

## 5. 权限点与按钮级控制 | Permission Points & Button-Level RBAC
菜单与按钮权限点已补齐并用于前后端联动，核心示例：
- 订单：`order:read`, `order:write`, `order:export`
- 入住：`checkin:read`, `checkin:write`
- 价格库存：`pricing:read`, `pricing:write`, `inventory:read`, `inventory:write`
- 渠道：`ota:read`, `ota:write`
- 会员：`member:read`, `member:write`
- 财务报表：`finance:read`, `finance:write`, `report:read`, `report:export`
- 运营：`ops:read`, `ops:write`
- 系统管理：`sys:user:read|write`, `sys:role:read|write`, `sys:permission:read|write`, `sys:menu:read|write`, `sys:dict:read|write`, `sys:param:read|write`
- 全量数据范围：`scope:all`

前端通过 `PermissionButton` 做按钮显隐；后端通过 `@PreAuthorize` 做接口鉴权。

## 6. currentProperty 优先策略 | currentProperty-First Strategy
日志筛选与组织下拉支持“当前门店优先收敛”：
- 接口：
  - `GET /api/logs/options/context`
  - `GET /api/logs/options/groups`
  - `GET /api/logs/options/brands?groupId=`
  - `GET /api/logs/options/properties?groupId=&brandId=`
- 规则：
  1. 默认按 `currentProperty` 对 group/brand/property 收敛。
  2. 若用户具备 `scope:all`，允许主动放宽筛选范围。
  3. 门店切换后，前端筛选条件实时刷新并重置到当前上下文。

## 7. 订单详情房态时间线国际化 | Timeline i18n
- 后端返回标准码：`nodeCode`, `actionCode`, `remarkCode`, `remarkText`。
- 前端基于字典渲染（`zh-CN` / `en-US`）：
  - 节点名称、动作描述、备注类型均通过字典映射。
- 接口：`GET /api/orders/{id}/timeline`
- 页面：订单详情页展示房态时间线 + 入住人 + 房间信息 + 支付流水 + 操作记录。

## 8. OTA 回调幂等与失败重试 | OTA Idempotency and Retry
### 8.1 幂等防重
- 幂等键：`channelCode + eventType + externalRequestNo`（无外部号时回退为 payload digest）
- 防重机制：Redis `SETNX` + 数据库唯一键（`channel_callback_log.idempotent_key`）

### 8.2 失败重试骨架（RabbitMQ）
- 表：`ota_callback_retry_task`
- 已包含：实体、Mapper、Service、消息模型、重试发布器、重试消费者、死信流转预留
- 支持字段：失败原因、重试次数、下次重试时间、最终失败状态

## 9. SQL 初始化与测试数据 | SQL Initialization & Seed Data
初始化脚本：`sql/schema.sql`

已包含：
- 核心表结构 + 第二/三阶段扩展表结构
- OTA 回调日志与重试任务表结构
- 新菜单树、权限点、默认角色菜单绑定
- 管理员、示例民宿/房型/房间、预订测试数据

注意：脚本已移除 `ADD COLUMN IF NOT EXISTS` 语法以提升 MySQL 8 兼容性。

## 10. 关键接口分组 | Key API Groups
- 鉴权与门店切换：`/api/auth/**`
- 房源：`/api/properties`, `/api/room-types`, `/api/rooms`
- 订单入住：`/api/orders/**`
- 价格库存：`/api/pricing/**`
- 渠道 OTA：`/api/ota/**`, `/api/ota/callback/**`
- 客户会员：`/api/member-marketing/**`
- 财务运营：`/api/finance-ops/**`
- 日志中心：`/api/logs/**`
- 系统管理：`/api/system/**`

## 11. 本轮功能增强 | Latest Enhancement
### 11.1 民宿管理去集团强依赖
- 前端“新增/编辑民宿”已移除集团/品牌输入项，运营者可直接管理多家民宿。
- 后端仍保留组织字段兼容能力：当请求不传组织信息时，按当前门店上下文自动继承（用于兼容旧库约束）。
- SQL `hotel_property.group_id`、`hotel_property.brand_id` 设计为可空字段；旧库可通过脚本 `ALTER TABLE ... MODIFY COLUMN ... NULL` 迁移。

### 11.2 菜单页面功能补齐（中文化）
- 订单中心：预订、入住、在住、退房、详情入口与时间线均已中文化并可操作。
- 价格与库存：拆分房价日历、价格计划、库存调整、预警查询。
- 渠道管理：拆分渠道配置、渠道映射、同步记录、回调记录。
- 客户与会员：客户档案、偏好、会员、积分、优惠券、营销活动均可操作。
- 财务管理：收款、退款、账单、发票、经营报表与导出。
- 运营管理：保洁任务、维修工单、房态记录、日志中心。
- 系统管理：用户/角色/权限/菜单/字典/参数页面支持中文检索浏览。

### 11.3 Mock 联调接口补齐
- `mock-server.js` 已扩展为完整业务 Mock，覆盖上述菜单关键接口，支持基础增改查。
- 适配前端全部二级菜单页面，避免“空白页/纯占位页/英文未本地化”问题。

### 11.4 本轮继续增强（民宿多店场景）
- 登录链路增强：
  - 前端登录接口增加“包裹数据/直接数据”双格式兼容，修复 `accessToken` 读取异常。
  - 登录页文案乱码已修复为中英文正常显示。
- 民宿管理增强：
  - 新增民宿默认不要求集团/品牌，表单已改为纯民宿维度信息。
  - 保留集团/品牌扩展能力作为后端兼容字段，但不再作为默认录入项。
- 订单中心中文化增强：
  - 订单、预订、入住、支付、证件等状态码统一采用前端字典映射，不再直接显示英文状态码。
  - 今日入住/今日退房工作台列表同步中文状态显示。
- 价格/渠道/会员/财务/运营增强：
  - 价格规则类型、调价方式、渠道状态、会员状态、发票/退款状态、保洁/维修状态统一中文渲染。
  - 财务页增加民宿筛选能力，列表页显示更贴合业务。
- 系统管理增强：
  - 用户、角色、字典、参数页支持新增与编辑（前后端接口与 Mock 同步补齐）。
  - 权限/菜单页保持只读查询，便于权限排查。

Latest incremental updates:
- Login flow now supports both wrapped and flat response payloads to avoid null-token parsing errors.
- Homestay creation no longer requires group/brand by default, while keeping optional organization extension fields.
- Order center and related pages now render status codes via Chinese dictionaries instead of raw English codes.
- Pricing/channel/member/finance/operations pages now apply unified Chinese business-code mapping.
- System management now supports create/update for users, roles, dictionaries, and parameters (backend + mock APIs).
- Startup compatibility initializer now auto-ensures critical legacy schema gaps (`hotel_order`, audit/operation log org-dimension columns).

## 12.1 兼容初始化说明 | Compatibility Bootstrap
后端启动时会自动执行最小兼容建表/补列（不覆盖业务数据）：
- `hotel_order` 缺表自动创建
- `audit_log_record.group_id/brand_id/property_id` 缺列自动补齐
- `operation_log_record.group_id/brand_id/property_id` 缺列自动补齐

This startup bootstrap only fills missing critical structures for legacy databases and keeps existing data unchanged.

## 13. 启动说明 | Quick Start
### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

默认测试账号（seed data）：
- username: `admin`
- password: `Admin@123`

## 14. 常见问题 | Troubleshooting
- 前端报 `ERR_CONNECTION_REFUSED`（如 `POST http://localhost:8080/api/properties`）：
  - 原因通常是后端未启动或进程被中断（例如终端 `Ctrl+C`）。
  - 处理：先确认 `8080` 端口监听，再刷新前端页面重试。
- 旧库执行新功能时报 “Unknown column / Table doesn't exist”：
  - 先运行 `sql/schema.sql` 完整脚本；
  - 或直接重启后端，利用兼容初始化自动补齐关键缺失结构。
