# Homestay Multi-Property Management System

English | [简体中文](./README.zh-CN.md)

This repository is a full-stack demo project for a **homestay-first multi-property operations platform**.

## Why this project
- Built for operators managing multiple homestays/properties.
- Covers core business modules: asset, order/stay, pricing, OTA, CRM, finance, operations, and system governance.
- Designed with clear boundaries so it can evolve from monolith to microservices later.

## Tech Stack
- Frontend: React 18 + TypeScript + Vite + Ant Design + Redux Toolkit + react-i18next
- Backend: Spring Boot 3 + MyBatis + MySQL + Redis + RabbitMQ + JWT + RBAC
- Infra/Engineering: GitHub Actions CI, unit tests, bilingual SQL comments

## Menu IA (Homestay-Oriented)
1. Workbench
2. Asset Management
3. Order Center
4. Pricing & Inventory
5. Channel Management
6. CRM & Membership
7. Finance
8. Operations
9. System

## Quick Start
### 1) Prepare env
Copy `.env.example` and adjust values:
- backend runtime vars: DB/Redis/RabbitMQ/JWT
- frontend runtime var: `VITE_API_BASE_URL`

### 2) One-command startup with Docker (optional)
```bash
docker compose up --build
```

Services:
- frontend: `http://localhost:5173`
- backend: `http://localhost:8080`
- rabbitmq console: `http://localhost:15672` (guest/guest)

### 3) Backend (local)
```bash
cd backend
mvn spring-boot:run
```

### 4) Frontend (local)
```bash
cd frontend
npm install
npm run dev
```

### 5) Default account
- username: `admin`
- password: `Admin@123`

## Testing
### Backend
```bash
cd backend
mvn test
```

### Frontend
```bash
cd frontend
npm run test
npm run build
```

## CI
- GitHub Actions workflow: `.github/workflows/ci.yml`
- Runs:
  - backend tests (Java 17)
  - frontend tests + build (Node 20)

## API Base URL Strategy
- Frontend no longer hardcodes localhost endpoints.
- Configure with `VITE_API_BASE_URL` (example: `http://localhost:8080/api`).
- Export/report download links reuse the same base URL helper.

## Dictionary Data Source
- Business dictionaries (`GENDER`, `BUSINESS_MODE`, `ROOM_STATUS`) are loaded from backend database tables `sys_dict` and `sys_dict_item`.
- Frontend dictionary dropdowns/tags now read `/api/dicts/{dictCode}/items` directly and no longer use static fallback values when database is empty.
- Built-in dictionary bootstrap is executed once at backend startup, not on every dictionary query.

## Mock Data Cleanup
- Frontend login fallback to `/final-auth/login` was removed; login now uses `/api/auth/login` only.
- Backend hardcoded login endpoints were removed (`/api/auth/test-login`, `/api/health/test-login`, `/api/simple/login`).
- System parameters are now persisted in database table `sys_param` instead of in-memory collections.
- Compatibility bootstrap now inserts missing dictionary/parameter rows only (`INSERT IGNORE`) and will not overwrite maintained DB values.
- The root-level mock server entry was removed to prevent accidental use of non-database fake APIs.

## Security Notes
- No hardcoded infrastructure credentials in source-controlled runtime config.
- `backend/src/main/resources/application.yml` now reads from environment variables.
- `backend/src/main/resources/application.example.yml` is provided as a clean template.

## Docs
- Feature overview: `docs/FEATURES.md`
- API examples: `docs/API_EXAMPLES.md`
- Architecture: `docs/ARCHITECTURE.md`
- SQL schema & seed data: `sql/schema.sql`

## Roadmap
- Add API integration test suite (Testcontainers)
- Add observability baseline (structured logging + metrics dashboards)

---
If this project is used in interviews, start with the architecture and core modules, then demo order detail timeline, property isolation, and OTA callback idempotency/retry skeleton.


