# 民宿多店管理系统

[English](./README.en.md) | 简体中文

本仓库是一个用于面试展示的全栈项目，定位为**民宿优先的多店运营平台**。

## 项目价值
- 面向“一个运营者管理多家民宿”的真实业务模型。
- 覆盖房源、订单入住、价格库存、OTA、会员、财务、运营、系统治理等核心链路。
- 代码分层清晰，可从当前单体逐步演进到微服务。

## 技术栈
- 前端：React 18 + TypeScript + Vite + Ant Design + Redux Toolkit + react-i18next
- 后端：Spring Boot 3 + MyBatis + MySQL + Redis + RabbitMQ + JWT + RBAC
- 工程化：GitHub Actions CI、单元测试、SQL 注释中英双语

## 信息架构（民宿场景）
1. 工作台
2. 房源管理
3. 订单中心
4. 价格与库存
5. 渠道管理
6. 客户与会员
7. 财务管理
8. 运营管理
9. 系统管理

## 快速启动
### 1）准备环境变量
参考根目录 `.env.example`：
- 后端：数据库/Redis/RabbitMQ/JWT 等配置
- 前端：`VITE_API_BASE_URL`

### 2）Docker 一键启动（可选）
```bash
docker compose up --build
```

服务地址：
- 前端：`http://localhost:5173`
- 后端：`http://localhost:8080`
- RabbitMQ 管理台：`http://localhost:15672`（guest/guest）

### 3）启动后端（本地）
```bash
cd backend
mvn spring-boot:run
```

### 4）启动前端（本地）
```bash
cd frontend
npm install
npm run dev
```

### 5）默认账号
- 用户名：`admin`
- 密码：`Admin@123`

## 测试
### 后端
```bash
cd backend
mvn test
```

### 前端
```bash
cd frontend
npm run test
npm run build
```

## CI 持续集成
- 工作流文件：`.github/workflows/ci.yml`
- 自动执行：
  - 后端测试（Java 17）
  - 前端测试与构建（Node 20）

## 接口基地址策略
- 前端不再写死 `localhost`。
- 通过 `VITE_API_BASE_URL` 统一配置（示例：`http://localhost:8080/api`）。
- 导出/下载接口复用同一基地址生成函数。

## 字典数据来源
- 业务字典（`GENDER`、`BUSINESS_MODE`、`ROOM_STATUS`）统一从后端数据库表 `sys_dict`、`sys_dict_item` 读取。
- 前端字典下拉与状态展示统一走 `/api/dicts/{dictCode}/items`，数据库为空时不再自动使用前端静态兜底值。
- 内置字典初始化只在后端启动时执行一次，不再在每次字典查询时触发。

## 假数据清理与真数据化
- 已移除前端登录对 `/final-auth/login` 的回退调用，登录统一走 `/api/auth/login`。
- 已移除后端硬编码登录返回（`/api/auth/test-login`、`/api/health/test-login`、`/api/simple/login`）。
- 系统参数改为数据库表 `sys_param` 持久化，不再使用内存列表假数据。
- 兼容初始化改为“仅补齐缺失字典/参数（INSERT IGNORE）”，不再覆盖已维护的数据库真实数据。
- 已移除仓库根目录 mock-server 假数据服务入口，避免误用本地假接口。

## 安全与配置
- 已移除运行配置中的明文基础设施地址与口令。
- `backend/src/main/resources/application.yml` 改为环境变量读取。
- 提供 `backend/src/main/resources/application.example.yml` 作为干净模板。

## 文档入口
- 功能清单：`docs/FEATURES.md`
- 接口示例：`docs/API_EXAMPLES.md`
- 架构说明：`docs/ARCHITECTURE.md`
- 表结构与初始化：`sql/schema.sql`

## 下一步可增强
- Testcontainers 集成测试
- 可观测性（结构化日志、监控指标、告警）

---
面试演示建议：先讲架构和分层，再演示“民宿切换数据收敛、订单详情时间线、OTA 回调幂等与重试骨架”三条核心链路。


