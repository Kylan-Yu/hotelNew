# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project follows [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
- Added frontend internationalization baseline with `react-i18next` (`en` default, `zh-CN` switch).
- Added OTA callback idempotency and retry task data model skeleton.
- Added CI workflow for backend tests and frontend test/build.
- Added backend unit tests for JWT auth, property scope permission checks, and SQL data scope injection.
- Added `.env.example` templates for backend and frontend runtime configuration.

### Changed
- Refactored menu IA from hotel-group perspective to homestay multi-property perspective.
- Unified frontend API base URL through `VITE_API_BASE_URL`.
- Replaced backend hardcoded infrastructure credentials with environment-driven configuration.
- Standardized export URL generation to avoid hardcoded local addresses.

### Security
- Removed hardcoded database, Redis, RabbitMQ, and JWT sensitive defaults from repository config.
