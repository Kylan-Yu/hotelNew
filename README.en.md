# Hotel/Homestay Multi-Property Management System

## 1. Project Positioning
This project has been refocused from a hotel-group admin panel to a system tailored to homestay operators managing multiple properties. The default information architecture de-emphasizes group and brand hierarchy and instead highlights homestays, inventory, orders, customers, operations, and system governance.

## 2. Tech Stack
- Frontend: React 18 + TypeScript + Vite + Ant Design + Redux Toolkit
- Backend: Spring Boot 3 + MyBatis + MySQL + Redis + RabbitMQ
- Authentication: JWT + RBAC with button-level permission codes
- Database: MySQL 8.x with bilingual table, column, and index comments

## 3. Menu Information Architecture
The sidebar, routes, permission points, and default role menus have been adjusted to the following structure:

1. Workbench
- Dashboard `/workbench/dashboard`
- Today Check-in `/workbench/today-checkin`
- Today Check-out `/workbench/today-checkout`
- Pending Tasks `/workbench/todos`

2. Asset Management
- Homestay Management `/assets/homestays`
- Room Type Management `/assets/room-types`
- Room Management `/assets/rooms`
- Room Status Management `/assets/room-status`

3. Order Center
- Reservation Orders `/orders/reservations`
- Check-in Registration `/orders/checkin`
- In-house Management `/orders/inhouse`
- Check-out Management `/orders/checkout`
- Order Details `/orders/detail`, `/orders/detail/:id`

4. Pricing & Inventory
- Rate Calendar `/pricing/calendar`
- Pricing Plans `/pricing/plans`
- Sellable Inventory `/pricing/inventory`
- Overbooking Alerts `/pricing/overbook-warning`

5. Channel Management
- Channel Configuration `/channels/config`
- Channel Mapping `/channels/mapping`
- Sync Logs `/channels/sync-logs`
- Callback Logs `/channels/callback-logs`

6. CRM & Membership
- Customer Profiles `/crm/customers`
- Member Management `/crm/members`
- Points Management `/crm/points`
- Coupon Management `/crm/coupons`

7. Finance
- Receipt Records `/finance/receipts`
- Refund Records `/finance/refunds`
- Billing Details `/finance/bills`
- Invoice Management `/finance/invoices`
- Business Reports `/finance/reports`

8. Operations
- Housekeeping `/operations/housekeeping`
- Maintenance Work Orders `/operations/maintenance`
- Room Status Records `/operations/room-status-record`
- Log Center `/operations/log-center`

9. System
- User Management `/system/users`
- Role Management `/system/roles`
- Permission Management `/system/permissions`
- Menu Management `/system/menus`
- Dictionary Management `/system/dicts`
- Parameter Configuration `/system/params`

Note: group and brand capabilities are retained as compatibility extensions, but they are no longer exposed as default top-level menus.

## 4. Unified Visual Style
A consistent gradient-based visual system has been implemented through global layout, theme tokens, and CSS variables:
- Sider background:
  - `linear-gradient(180deg, #0F172A 0%, #1E293B 45%, #334155 100%)`
- Selected menu state:
  - `linear-gradient(90deg, #2563EB 0%, #38BDF8 100%)`
- Main page background:
  - `linear-gradient(180deg, #F8FAFC 0%, #EEF2FF 100%)`

The style applies across the workbench, list pages, detail pages, and form pages, with a unified card hierarchy and background tone.

## 5. Permission Points and Button-Level RBAC
Menu and button permissions have been completed and wired into both frontend and backend flows. Core examples include:
- Orders: `order:read`, `order:write`, `order:export`
- Check-in: `checkin:read`, `checkin:write`
- Pricing and inventory: `pricing:read`, `pricing:write`, `inventory:read`, `inventory:write`
- OTA channels: `ota:read`, `ota:write`
- Members: `member:read`, `member:write`
- Finance and reports: `finance:read`, `finance:write`, `report:read`, `report:export`
- Operations: `ops:read`, `ops:write`
- System management: `sys:user:read|write`, `sys:role:read|write`, `sys:permission:read|write`, `sys:menu:read|write`, `sys:dict:read|write`, `sys:param:read|write`
- Global data scope: `scope:all`

The frontend uses `PermissionButton` to control button visibility, while the backend uses `@PreAuthorize` for endpoint authorization.

## 6. currentProperty-First Strategy
Log filters and organization dropdowns support a current-property-first narrowing strategy:
- APIs:
  - `GET /api/logs/options/context`
  - `GET /api/logs/options/groups`
  - `GET /api/logs/options/brands?groupId=`
  - `GET /api/logs/options/properties?groupId=&brandId=`
- Rules:
  1. The default scope is narrowed by `currentProperty` across group, brand, and property dimensions.
  2. Users with `scope:all` may explicitly widen the filter scope.
  3. After switching the active property, frontend filters refresh in real time and reset to the current context.

## 7. Internationalized Room-Status Timeline in Order Details
- The backend returns normalized codes: `nodeCode`, `actionCode`, `remarkCode`, `remarkText`
- The frontend renders timeline content through dictionaries (`zh-CN` / `en-US`)
- Node names, action descriptions, and remark types are all mapped through dictionaries
- API: `GET /api/orders/{id}/timeline`
- The order detail page displays the room-status timeline, guests, room data, payment records, and operation history

## 8. OTA Callback Idempotency and Retry
### 8.1 Idempotency
- Idempotency key: `channelCode + eventType + externalRequestNo`, with payload digest as the fallback when no external request number is available
- Duplicate prevention: Redis `SETNX` plus a database unique key on `channel_callback_log.idempotent_key`

### 8.2 Failure Retry Skeleton (RabbitMQ)
- Table: `ota_callback_retry_task`
- Included components: entity, Mapper, Service, message model, retry publisher, retry consumer, and dead-letter extension points
- Supported fields: failure reason, retry count, next retry time, and final failure status

## 9. SQL Initialization and Seed Data
Initialization script: `sql/schema.sql`

Included content:
- Core tables and Phase 2/3 extension tables
- OTA callback log and retry task tables
- New menu tree, permission points, and default role-menu bindings
- Seed data for admin, sample homestays, room types, rooms, and reservation records

Note: `ADD COLUMN IF NOT EXISTS` has been removed to improve MySQL 8 compatibility.

## 10. Key API Groups
- Authentication and property switching: `/api/auth/**`
- Assets: `/api/properties`, `/api/room-types`, `/api/rooms`
- Orders and check-in: `/api/orders/**`
- Pricing and inventory: `/api/pricing/**`
- OTA channels: `/api/ota/**`, `/api/ota/callback/**`
- CRM and membership: `/api/member-marketing/**`
- Finance and operations: `/api/finance-ops/**`
- Log center: `/api/logs/**`
- System management: `/api/system/**`

## 11. Latest Enhancements
### 11.1 Reduced Group Dependency in Homestay Management
- `groupId` and `brandId` are now optional when creating or editing a homestay, enabling independent property onboarding.
- Backend DTO and Service validation have been relaxed accordingly. If only `brandId` is provided, the related `groupId` is filled automatically.
- `hotel_property.group_id` and `hotel_property.brand_id` are now nullable in SQL.

### 11.2 Functional Completion of Menu Pages
- Order center: reservation, check-in, in-house, check-out, detail entry, and timeline views are localized and operational.
- Pricing and inventory: rate calendar, pricing plans, inventory adjustments, and warning queries are separated into dedicated pages.
- Channel management: channel configuration, channel mapping, sync logs, and callback logs are split into dedicated modules.
- CRM and membership: customer profiles, preferences, members, points, coupons, and campaigns are operable.
- Finance: receipts, refunds, bills, invoices, business reports, and exports are available.
- Operations: housekeeping tasks, maintenance work orders, room-status records, and the log center are included.
- System management: user, role, permission, menu, dictionary, and parameter pages support localized browsing and search.

### 11.3 Completed Mock APIs for Integration
- `mock-server.js` has been expanded into a full business mock server covering the key APIs behind the menus above and supports basic create, update, query, and delete flows.
- This supports all secondary frontend menu pages and helps avoid blank pages, placeholder-only pages, or incomplete localization.

### 11.4 Continued Enhancements for the Multi-Property Homestay Scenario
- Login flow enhancements:
  - The frontend login handler now supports both wrapped and flat payload formats, fixing `accessToken` parsing issues.
  - Garbled login-page copy has been corrected to display properly in Chinese and English.
- Homestay management enhancements:
  - Group and brand are no longer required by default when creating a homestay. Organization fields are now treated as advanced optional metadata.
  - Group and brand extension capability is preserved without blocking independent homestay onboarding.
- Order center localization:
  - Order, reservation, check-in, payment, and ID-document status codes are rendered through frontend dictionaries instead of raw English values.
  - Today check-in and today check-out widgets also display localized Chinese status labels.
- Pricing, channel, membership, finance, and operations:
  - Pricing rule types, adjustment methods, channel status, member status, invoice/refund status, and housekeeping/maintenance states now use unified localized rendering.
  - Finance pages support homestay-level filtering for more realistic business views.
- System management:
  - User, role, dictionary, and parameter pages now support create and update actions with both backend and mock APIs aligned.
  - Permission and menu pages remain read-only to simplify permission auditing.

## 12. Quick Start
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

Default test account (seed data):
- username: `admin`
- password: `Admin@123`
