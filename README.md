# Homestay Multi-Property Management System | 民宿多店管理系统

[English](./README.en.md) | [简体中文](./README.zh-CN.md)

This project is a full-stack demo for a homestay-first multi-property operations platform.
该项目是一个面向民宿多店运营场景的全栈演示系统。

## Quick Links | 快速入口
- Features | 功能清单: `docs/FEATURES.md`
- API Examples | 接口示例: `docs/API_EXAMPLES.md`
- Architecture | 架构说明: `docs/ARCHITECTURE.md`
- SQL Schema & Seeds | 表结构与测试数据: `sql/schema.sql`
- CI Workflow: `.github/workflows/ci.yml`

## Quick Start | 快速启动
Prerequisites | 前置环境: `Java 17`, `Maven 3.9+`, `Node 20+`, `MySQL 8`, `Redis 7`, `RabbitMQ 3.13` or Docker Desktop.

```bash
# docker (optional)
docker compose up --build

# backend
# import sql/schema.sql first and inject env vars from .env.example manually
cd backend
mvn spring-boot:run

# frontend
# put VITE_API_BASE_URL in frontend/.env.local for local dev
cd frontend
npm install
npm run dev
```

Default account | 默认账号:
- username: `admin`
- password: `Admin@123`

## Environment Variables | 环境变量
- Template file | 模板文件: `.env.example` (reference only, not auto-loaded by Maven/Vite)
- Frontend API base URL: `VITE_API_BASE_URL=http://localhost:8080/api`

## Project Status | 当前状态
- Core modules and IA for homestay scenario are implemented.
- Frontend supports i18n (English default + Chinese switch).
- Backend/Frontend baseline tests and CI workflow are included.
- Runtime config has been switched to environment-driven style (no hardcoded infra credentials).
- Business dictionaries are database-driven (sys_dict / sys_dict_item) with startup bootstrap.
- Login and system parameters are now database-backed only (mock login fallback removed, `sys_param` persisted).
- Root-level mock API server entry has been removed to avoid accidental fake-data runtime.

For full details, please open `README.en.md` or `README.zh-CN.md`.
详细说明请查看 `README.en.md` 或 `README.zh-CN.md`。
