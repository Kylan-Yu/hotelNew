# Contributing Guide

Thanks for your interest in contributing to this project.

## Branch and Commit
- Create feature branches with prefix: `codex/` (for example: `codex/fix-login-env`).
- Keep commits focused and descriptive.
- Prefer conventional-style commit messages (for example: `feat(order): add timeline i18n mapping`).

## Local Setup
1. Copy environment template:
   - root: `.env.example`
   - frontend: `frontend/.env.example`
2. Start dependencies (MySQL, Redis, RabbitMQ).
3. Run backend:
   - `cd backend`
   - `mvn spring-boot:run`
4. Run frontend:
   - `cd frontend`
   - `npm install`
   - `npm run dev`

## Quality Gate
- Backend tests:
  - `cd backend && mvn test`
- Frontend tests:
  - `cd frontend && npm run test`
- Frontend build:
  - `cd frontend && npm run build`

## Pull Request Checklist
- Keep API compatibility (no breaking rename for existing fields).
- Keep RBAC permission code semantics stable.
- Keep MySQL comments bilingual when adding or changing schema.
- Update docs (`README.md`, `README.en.md`, `README.zh-CN.md`, `sql/schema.sql`) when behavior changes.
