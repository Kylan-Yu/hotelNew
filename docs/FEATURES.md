# Features | 功能清单

## Product Positioning | 产品定位
- Homestay-first, multi-property operations platform.
- Supports one operator managing multiple homestays/properties.
- Keeps group/brand dimensions as compatibility extension, not default IA.

- 以民宿多店运营为核心的平台。
- 支持一个运营者管理多家民宿/多套房源。
- 集团/品牌能力保留为扩展，不作为默认主视角。

## Core Modules | 核心模块

### 1. Auth & Security | 认证与安全
- JWT access/refresh token.
- RBAC with menu + button-level permissions.
- Property-scoped data access with `currentProperty` first strategy.
- SQL-level data scope placeholder injection.

- JWT 双令牌机制。
- 菜单级与按钮级 RBAC。
- currentProperty 优先的数据收敛。
- SQL 级数据权限注入。

### 2. Asset Management | 房源管理
- Property (homestay), room type, room, room status management.
- Dictionary-driven options (business mode, room status, gender).
- Property switch from top-right context selector.

- 民宿、房型、房间、房态管理。
- 字典驱动取值（经营模式、房态、性别）。
- 顶部民宿切换联动。

### 3. Order & Stay | 订单与入住
- Reservation/order lifecycle.
- Check-in, in-house stay management, extend stay, change room, checkout, cancel.
- Order detail page with payment/deposit flows and room-status timeline.

- 预订/订单状态流转。
- 入住、在住、续住、换房、退房、取消。
- 订单详情页含支付流水与房态时间线。

### 4. Pricing & Inventory | 价格与库存
- Pricing calendar, pricing plan, inventory control, overbooking warning.
- Extension points for long-stay/hourly/homestay pricing variants.

- 房价日历、价格计划、库存控制、超售预警。
- 预留连住价/钟点房/民宿扩展位。

### 5. OTA Integration Skeleton | OTA 对接骨架
- Channel adapter abstraction (Douyin / Meituan / Ctrip reserved).
- Callback DTOs, signature verification skeleton.
- Idempotency key and retry task model.

- 渠道抽象层（预留抖音/美团/携程）。
- 回调 DTO 与验签骨架。
- 幂等键与失败重试任务模型。

### 6. CRM, Finance, Operations | 客户会员、财务、运营
- Customer/member profile, points, coupon base structures.
- Receipt/refund/bill/invoice/report base APIs.
- Housekeeping and maintenance work-order support.
- Audit log and operation log center.

- 客户会员、积分、优惠券基础结构。
- 收退款、账单、发票、经营报表基础接口。
- 保洁与维修工单。
- 审计日志与操作日志中心。

## Engineering Readiness | 工程化能力
- React + TypeScript + Ant Design frontend.
- Spring Boot + MyBatis + MySQL + Redis + RabbitMQ backend.
- i18n frontend baseline: English default + Chinese switch.
- CI workflow: backend test + frontend test/build.
- Bilingual schema comments (Chinese/English) in SQL.

- 前端：React + TS + AntD。
- 后端：Spring Boot + MyBatis + MySQL + Redis + RabbitMQ。
- 前端国际化：默认英文，可切中文。
- CI：后端测试 + 前端测试构建。
- SQL 注释保持中英双语。
