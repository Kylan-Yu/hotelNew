你现在是一个资深企业级全栈工程师，请基于以下要求生成一个“酒店 / 民宿 / 连锁门店管理系统”的可运行代码。

# 一、硬性要求
1. 前端必须使用 React 18 + TypeScript + Vite + Redux Toolkit + React Router + Ant Design。
2. 后端必须使用 Spring Boot 3 + Java 17 + Spring Security + JWT + MyBatis + MySQL + Redis + RabbitMQ。
3. 数据库为 MySQL 8，所有表和字段的注释必须是中英双语。
4. 项目代码风格要接近企业级生产项目，而不是 demo。
5. 代码目录要清晰，按 controller / service / mapper / entity / dto / vo / common 分层。
6. 先按模块化单体生成，但要求具备后续拆分为微服务的边界。
7. 必须支持多集团、多品牌、多门店，并支持门店切换“酒店 / 民宿”模式。
8. 必须设计对接抖音、美团、携程的渠道适配层，统一抽象 ChannelAdapter。
9. 需要生成 README.md（中英双语）、SQL 初始化脚本、接口示例、基础单元测试。
10. 代码必须优先贴合以下技术亮点：
   - Spring Boot / React + TypeScript
   - Redis 缓存
   - RabbitMQ 事件驱动
   - MyBatis
   - JWT 鉴权
   - 企业级 RBAC 权限

# 二、请优先生成的模块顺序
第一阶段：
- 认证与权限模块
- 集团 / 品牌 / 门店模块
- 房型 / 房间 / 房态模块

第二阶段：
- 订单 / 入住 / 退房模块
- 价格 / 库存模块
- OTA 渠道对接抽象层

第三阶段：
- 财务 / 对账 / 报表模块
- 会员营销模块
- 审计日志与运维能力

# 三、代码生成要求
1. 每生成一个模块，请同时输出：
   - 后端代码
   - 前端页面与 API 调用
   - 对应 SQL
   - 简要说明
2. 不要省略 import。
3. 不要使用伪代码。
4. 尽量补充合理的校验、异常处理、统一响应结构。
5. 每个核心实体都要有 DTO、VO、Mapper、Service、Controller。
6. 列出 curl 或 http 请求示例。
7. 前端页面优先生成管理后台风格：列表页 + 新增编辑弹窗 + 详情页。

# 四、数据库设计重点
至少生成这些核心表：
- sys_user
- sys_role
- sys_menu
- sys_user_role
- hotel_group
- hotel_brand
- hotel_property
- hotel_room_type
- hotel_room
- hotel_room_status_log
- hotel_reservation
- hotel_order
- channel_account
- channel_mapping
- channel_sync_log

# 五、架构要求
1. 提供统一 BaseEntity。
2. 提供统一 ApiResponse。
3. 提供全局异常处理器。
4. 提供 Spring Security + JWT 基础配置。
5. 提供 RedisKey 规范。
6. 提供 RabbitMQ 事件示例（订单创建后触发库存同步事件）。
7. 提供 OTA 适配接口：
   - pushInventory
   - pushPrice
   - pullOrders
   - handleCallback

# 六、输出方式
请先输出完整项目目录树，然后从“认证与权限模块”开始生成代码。
