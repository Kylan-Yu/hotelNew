# Hotel / Homestay Management System (Portfolio Project)

This repository is a personal full-stack project for a business-oriented hotel/homestay management system.
It is built to demonstrate backend-focused engineering capability (with a working admin frontend) for interview evaluation.

## 1. Project Overview
- Domain: multi-property homestay/hotel operations
- Style: modular monolith (`Spring Boot + MyBatis`) with React admin UI
- Focus: property operations, order/stay workflow, pricing/inventory, member/finance/system modules
- Evidence sources: `backend/`, `frontend/`, `sql/schema.sql`, `docker-compose.yml`, `.github/workflows/ci.yml`

## 2. Why I Built This Project
I built this project to practice and demonstrate implementation of a realistic business system, not just isolated CRUD demos.
The goal is to show domain modeling, workflow handling, permission control, and deployable local setup in one coherent repository.

## 3. My Role
I built this as an individual portfolio project, including:
- backend API and business services
- frontend admin pages and API integration
- database schema and seed data
- local development/deployment setup (Docker + local run)
- baseline tests and CI workflow

## 4. Business Scope
The current scope targets day-to-day operations for a small/mid-size multi-property operation team:
- property/room asset management
- reservation, order, and stay lifecycle operations
- pricing plan and inventory management
- member/customer and marketing basics
- finance/operations records and reporting
- system admin (users, roles, permissions, dictionary, params)

## 5. Current Implementation Status
### Implemented
- Auth: JWT login/refresh/logout, current-user endpoint, property scope switch
- Access control: method-level RBAC via `@PreAuthorize`
- Asset module: property, room type, room, room status logs
- Order/stay module: reservation creation, order generation, check-in, extend stay, change room, checkout, cancel, payment records, order detail timeline, export
- Pricing/inventory: price plans, rules, inventory adjustment, overbook warning events
- Member/marketing: member profile, points ledger query, coupon template, preference, campaign
- Finance/ops: refund/bill/invoice records, housekeeping and maintenance tasks, daily/property report APIs and export
- System admin: users, roles, permissions, menus, dictionaries, params
- Logging/query: audit log and operation log search APIs
- Frontend admin: routed pages for the main modules above with API integration

### Partially Implemented
- OTA integration:
  - callback idempotency/retry pipeline exists (Redis + DB + RabbitMQ)
  - channel adapters currently log actions; real platform API integration is not implemented
  - signature verification currently checks only non-empty signature
- i18n:
  - i18n framework exists (`react-i18next`, EN/ZH resources)
  - many UI texts are still hardcoded in Chinese
- Group/brand management:
  - backend and frontend pages exist
  - not integrated into the main navigation IA in current UI flow

### Planned / Roadmap
- real OTA platform API integration (inventory/price/order sync)
- broader automated tests (integration and end-to-end)
- full i18n coverage for page-level labels/messages
- tighter frontend/backend contract alignment for some advanced form fields

## 6. System Architecture
- Backend: layered Spring Boot structure
  - `controller` -> API + permission guard
  - `service` -> business workflow/state transition
  - `mapper` -> SQL persistence (MyBatis annotations)
  - `domain` -> DTO/entity/VO contracts
- Frontend: React + TypeScript admin app
  - route-based module pages
  - Redux auth state with token refresh flow
  - Ant Design data-entry/table-heavy business UI
- Data and infrastructure:
  - MySQL for core business data
  - Redis for token/temporary idempotency keys
  - RabbitMQ for asynchronous retry/event handling

## 7. Implemented Modules
- **Auth & Scope**: `/api/auth/*`, JWT token flow, property scope switching
- **Asset Management**: `/api/properties`, `/api/room-types`, `/api/rooms`
- **Order & Stay**: `/api/orders/*`, including reservation/order/stay and timeline/export
- **Pricing & Inventory**: `/api/pricing/*`, inventory updates and warning events
- **OTA (Skeleton + Callback Pipeline)**: `/api/ota/*`, `/api/ota/callback/*`
- **CRM / Membership**: `/api/member-marketing/*`
- **Finance / Operations**: `/api/finance-ops/*`
- **System Admin**: `/api/system/*`, `/api/dicts/*`
- **Log Center**: `/api/logs/*`

## 8. Tech Stack
- Backend: Java 17, Spring Boot 3.3, Spring Security, MyBatis, JWT, Redis, RabbitMQ, MySQL
- Frontend: React 18, TypeScript, Vite, Ant Design, Redux Toolkit, React Router
- Engineering: Docker, Docker Compose, GitHub Actions CI, Maven, npm/vitest

## 9. Local Setup
### Prerequisites
- Java 17
- Maven 3.9+
- Node.js 20+
- MySQL 8, Redis 7, RabbitMQ 3.13
- Or Docker Desktop for one-command startup

### Option A: Docker Compose
```bash
docker compose up --build
```

Services:
- frontend: `http://localhost:5173`
- backend: `http://localhost:8080`
- rabbitmq console: `http://localhost:15672` (`guest/guest`)

### Option B: Run locally
1. Initialize database with `sql/schema.sql`.
2. Configure backend runtime variables (see `.env.example`).
3. Start backend:
```bash
cd backend
mvn spring-boot:run
```
4. Configure frontend API base URL in `frontend/.env.local`:
```bash
VITE_API_BASE_URL=http://localhost:8080/api
```
5. Start frontend:
```bash
cd frontend
npm install
npm run dev
```

### Tests / CI
- Backend tests: `cd backend && mvn test`
- Frontend tests: `cd frontend && npm run test`
- CI workflow: `.github/workflows/ci.yml`

## 10. Key Engineering Decisions
- Property-first scope model: data visibility is constrained by current property scope for business operations.
- Domain-oriented module split inside a monolith: keeps boundaries explicit while reducing operational overhead for a portfolio project.
- OTA callback safety baseline: idempotent key + retry task model + queue consumer for failed callback handling.
- DB-centered master data: dictionaries/params and core business entities are persisted in MySQL rather than in-memory mocks.
- Environment-driven runtime config: key infra settings are externalized via environment variables.

## 11. Current Limitations
- OTA channel adapters are skeleton implementations (logging placeholders, no real external API calls).
- i18n is incomplete at page level.
- Automated tests are currently limited (few backend unit tests + one frontend helper test).
- There is an operation log filter class, but request-level automatic operation logging wiring is incomplete in current runtime configuration.

## 12. Roadmap
- Complete OTA platform integrations and channel-specific signing/verification logic.
- Add integration tests for core workflows (auth, order/stay, pricing, finance).
- Improve frontend/backend API contract consistency in advanced forms.
- Expand observability and operational diagnostics for troubleshooting.

## Additional Docs
- Features: `docs/FEATURES.md`
- API examples: `docs/API_EXAMPLES.md`
- Architecture notes: `docs/ARCHITECTURE.md`
- SQL schema and seed data: `sql/schema.sql`
