# 酒店/民宿多店管理系统

## 1. 项目定位
本项目已从“酒店集团后台”调整为更贴合“民宿多店运营者”的管理系统，默认信息架构弱化集团/品牌主视角，强化民宿、房源、订单、客户、运营与系统治理。

## 2. 技术栈
- 前端：React 18 + TypeScript + Vite + Ant Design + Redux Toolkit
- 后端：Spring Boot 3 + MyBatis + MySQL + Redis + RabbitMQ
- 鉴权：JWT + RBAC（按钮级权限码）
- 数据库：MySQL 8.x（表/字段/索引注释保持中英双语）

## 3. 菜单信息架构
前端侧边栏、路由、权限点、默认角色菜单均按以下结构调整：

1. 工作台
- 经营看板 `/workbench/dashboard`
- 今日入住 `/workbench/today-checkin`
- 今日退房 `/workbench/today-checkout`
- 待处理事项 `/workbench/todos`

2. 房源管理
- 民宿管理 `/assets/homestays`
- 房型管理 `/assets/room-types`
- 房间管理 `/assets/rooms`
- 房态管理 `/assets/room-status`

3. 订单中心
- 预订订单 `/orders/reservations`
- 入住登记 `/orders/checkin`
- 在住管理 `/orders/inhouse`
- 退房管理 `/orders/checkout`
- 订单详情 `/orders/detail`、`/orders/detail/:id`

4. 价格与库存
- 房价日历 `/pricing/calendar`
- 价格计划 `/pricing/plans`
- 可售库存 `/pricing/inventory`
- 超售预警 `/pricing/overbook-warning`

5. 渠道管理
- 渠道配置 `/channels/config`
- 渠道映射 `/channels/mapping`
- 同步记录 `/channels/sync-logs`
- 回调记录 `/channels/callback-logs`

6. 客户与会员
- 客户档案 `/crm/customers`
- 会员管理 `/crm/members`
- 积分管理 `/crm/points`
- 优惠券管理 `/crm/coupons`

7. 财务管理
- 收款记录 `/finance/receipts`
- 退款记录 `/finance/refunds`
- 账单明细 `/finance/bills`
- 发票管理 `/finance/invoices`
- 经营报表 `/finance/reports`

8. 运营管理
- 保洁管理 `/operations/housekeeping`
- 维修工单 `/operations/maintenance`
- 房态记录 `/operations/room-status-record`
- 日志中心 `/operations/log-center`

9. 系统管理
- 用户管理 `/system/users`
- 角色管理 `/system/roles`
- 权限管理 `/system/permissions`
- 菜单管理 `/system/menus`
- 字典管理 `/system/dicts`
- 参数配置 `/system/params`

说明：集团/品牌能力作为兼容扩展保留，不作为默认主菜单入口。

## 4. 视觉风格统一
已通过全局 layout + token + CSS 变量统一实现渐变风格：
- 侧边栏背景：
  - `linear-gradient(180deg, #0F172A 0%, #1E293B 45%, #334155 100%)`
- 菜单选中态：
  - `linear-gradient(90deg, #2563EB 0%, #38BDF8 100%)`
- 页面主背景：
  - `linear-gradient(180deg, #F8FAFC 0%, #EEF2FF 100%)`

作用范围覆盖工作台、列表页、详情页、表单页，统一卡片层次与背景氛围。

## 5. 权限点与按钮级控制
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

## 6. currentProperty 优先策略
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

## 7. 订单详情房态时间线国际化
- 后端返回标准码：`nodeCode`, `actionCode`, `remarkCode`, `remarkText`
- 前端基于字典渲染（`zh-CN` / `en-US`）
- 节点名称、动作描述、备注类型均通过字典映射
- 接口：`GET /api/orders/{id}/timeline`
- 页面：订单详情页展示房态时间线、入住人、房间信息、支付流水、操作记录

## 8. OTA 回调幂等与失败重试
### 8.1 幂等防重
- 幂等键：`channelCode + eventType + externalRequestNo`（无外部号时回退为 payload digest）
- 防重机制：Redis `SETNX` + 数据库唯一键（`channel_callback_log.idempotent_key`）

### 8.2 失败重试骨架（RabbitMQ）
- 表：`ota_callback_retry_task`
- 已包含：实体、Mapper、Service、消息模型、重试发布器、重试消费者、死信流转预留
- 支持字段：失败原因、重试次数、下次重试时间、最终失败状态

## 9. SQL 初始化与测试数据
初始化脚本：`sql/schema.sql`

已包含：
- 核心表结构 + 第二/三阶段扩展表结构
- OTA 回调日志与重试任务表结构
- 新菜单树、权限点、默认角色菜单绑定
- 管理员、示例民宿/房型/房间、预订测试数据

注意：脚本已移除 `ADD COLUMN IF NOT EXISTS` 语法以提升 MySQL 8 兼容性。

## 10. 关键接口分组
- 鉴权与门店切换：`/api/auth/**`
- 房源：`/api/properties`, `/api/room-types`, `/api/rooms`
- 订单入住：`/api/orders/**`
- 价格库存：`/api/pricing/**`
- 渠道 OTA：`/api/ota/**`, `/api/ota/callback/**`
- 客户会员：`/api/member-marketing/**`
- 财务运营：`/api/finance-ops/**`
- 日志中心：`/api/logs/**`
- 系统管理：`/api/system/**`

## 11. 本轮功能增强
### 11.1 民宿管理去集团强依赖
- 新增/编辑民宿时，`groupId`、`brandId` 均为可选，支持独立民宿直接建档。
- 后端 DTO 与 Service 校验已同步放宽，若只传 `brandId` 将自动回填所属 `groupId`。
- SQL `hotel_property.group_id`、`hotel_property.brand_id` 已调整为可空字段。

### 11.2 菜单页面功能补齐
- 订单中心：预订、入住、在住、退房、详情入口与时间线均已中文化并可操作。
- 价格与库存：拆分房价日历、价格计划、库存调整、预警查询。
- 渠道管理：拆分渠道配置、渠道映射、同步记录、回调记录。
- 客户与会员：客户档案、偏好、会员、积分、优惠券、营销活动均可操作。
- 财务管理：收款、退款、账单、发票、经营报表与导出。
- 运营管理：保洁任务、维修工单、房态记录、日志中心。
- 系统管理：用户、角色、权限、菜单、字典、参数页面支持中文检索浏览。

### 11.3 Mock 联调接口补齐
- `mock-server.js` 已扩展为完整业务 Mock，覆盖上述菜单关键接口，支持基础增改查。
- 适配前端全部二级菜单页面，避免空白页、纯占位页与未本地化问题。

### 11.4 本轮继续增强（民宿多店场景）
- 登录链路增强：
  - 前端登录接口增加“包裹数据/直接数据”双格式兼容，修复 `accessToken` 读取异常。
  - 登录页文案乱码已修复为中英文正常显示。
- 民宿管理增强：
  - 新增民宿默认不要求集团/品牌，组织字段调整为“高级组织信息（选填）”。
  - 保留集团/品牌扩展能力，但不影响独立民宿建档。
- 订单中心中文化增强：
  - 订单、预订、入住、支付、证件等状态码统一采用前端字典映射，不再直接显示英文状态码。
  - 今日入住、今日退房工作台列表同步中文状态显示。
- 价格/渠道/会员/财务/运营增强：
  - 价格规则类型、调价方式、渠道状态、会员状态、发票/退款状态、保洁/维修状态统一中文渲染。
  - 财务页增加民宿筛选能力，列表页显示更贴合业务。
- 系统管理增强：
  - 用户、角色、字典、参数页支持新增与编辑（前后端接口与 Mock 同步补齐）。
  - 权限、菜单页保持只读查询，便于权限排查。

## 12. 启动说明
### 后端
```bash
cd backend
mvn spring-boot:run
```

### 前端
```bash
cd frontend
npm install
npm run dev
```

默认测试账号（seed data）：
- username: `admin`
- password: `Admin@123`
