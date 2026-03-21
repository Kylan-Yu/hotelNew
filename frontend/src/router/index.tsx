import { Navigate, Route, Routes } from 'react-router-dom'
import { MainLayout } from '../layouts/MainLayout'
import { CustomerArchivePage } from '../pages/CustomerArchivePage'
import { DashboardPage } from '../pages/DashboardPage'
import { FinanceOpsPage } from '../pages/FinanceOpsPage'
import { LogPage } from '../pages/LogPage'
import { LoginPage } from '../pages/LoginPage'
import { MemberPage } from '../pages/MemberPage'
import { OperationsPage } from '../pages/OperationsPage'
import { OrderDetailEntryPage } from '../pages/OrderDetailEntryPage'
import { OrderDetailPage } from '../pages/OrderDetailPage'
import { OrderPage } from '../pages/OrderPage'
import { OtaCallbackRecordPage } from '../pages/OtaCallbackRecordPage'
import { OtaPage } from '../pages/OtaPage'
import { OtaSyncRecordPage } from '../pages/OtaSyncRecordPage'
import { OverbookWarningPage } from '../pages/OverbookWarningPage'
import { PricingPage } from '../pages/PricingPage'
import { PropertyDetailPage } from '../pages/PropertyDetailPage'
import { PropertyListPage } from '../pages/PropertyListPage'
import { RoomListPage } from '../pages/RoomListPage'
import { RoomStatusPage } from '../pages/RoomStatusPage'
import { RoomTypeListPage } from '../pages/RoomTypeListPage'
import { SystemDataPage } from '../pages/SystemDataPage'
import { WorkbenchTodayCheckinPage } from '../pages/WorkbenchTodayCheckinPage'
import { WorkbenchTodayCheckoutPage } from '../pages/WorkbenchTodayCheckoutPage'
import { WorkbenchTodoPage } from '../pages/WorkbenchTodoPage'
import { ProtectedRoute } from './ProtectedRoute'

export function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route path="/" element={<MainLayout />}>
          <Route index element={<Navigate to="/workbench/dashboard" replace />} />

          <Route path="/workbench/dashboard" element={<DashboardPage />} />
          <Route path="/workbench/today-checkin" element={<WorkbenchTodayCheckinPage />} />
          <Route path="/workbench/today-checkout" element={<WorkbenchTodayCheckoutPage />} />
          <Route path="/workbench/todos" element={<WorkbenchTodoPage />} />

          <Route path="/assets/homestays" element={<PropertyListPage />} />
          <Route path="/assets/homestays/:id" element={<PropertyDetailPage />} />
          <Route path="/assets/room-types" element={<RoomTypeListPage />} />
          <Route path="/assets/rooms" element={<RoomListPage />} />
          <Route path="/assets/room-status" element={<RoomStatusPage />} />

          <Route path="/orders/reservations" element={<OrderPage view="reservations" />} />
          <Route path="/orders/checkin" element={<OrderPage view="checkin" />} />
          <Route path="/orders/inhouse" element={<OrderPage view="inhouse" />} />
          <Route path="/orders/checkout" element={<OrderPage view="checkout" />} />
          <Route path="/orders/detail" element={<OrderDetailEntryPage />} />
          <Route path="/orders/detail/:id" element={<OrderDetailPage />} />

          <Route path="/pricing/calendar" element={<PricingPage view="calendar" />} />
          <Route path="/pricing/plans" element={<PricingPage view="plans" />} />
          <Route path="/pricing/inventory" element={<PricingPage view="inventory" />} />
          <Route path="/pricing/overbook-warning" element={<OverbookWarningPage />} />

          <Route path="/channels/config" element={<OtaPage view="config" />} />
          <Route path="/channels/mapping" element={<OtaPage view="mapping" />} />
          <Route path="/channels/sync-logs" element={<OtaSyncRecordPage />} />
          <Route path="/channels/callback-logs" element={<OtaCallbackRecordPage />} />

          <Route path="/crm/customers" element={<CustomerArchivePage />} />
          <Route path="/crm/members" element={<MemberPage view="members" />} />
          <Route path="/crm/points" element={<MemberPage view="points" />} />
          <Route path="/crm/coupons" element={<MemberPage view="coupons" />} />

          <Route path="/finance/receipts" element={<FinanceOpsPage view="receipts" />} />
          <Route path="/finance/refunds" element={<FinanceOpsPage view="refunds" />} />
          <Route path="/finance/bills" element={<FinanceOpsPage view="bills" />} />
          <Route path="/finance/invoices" element={<FinanceOpsPage view="invoices" />} />
          <Route path="/finance/reports" element={<FinanceOpsPage view="reports" />} />

          <Route path="/operations/housekeeping" element={<OperationsPage view="housekeeping" />} />
          <Route path="/operations/maintenance" element={<OperationsPage view="maintenance" />} />
          <Route path="/operations/room-status-record" element={<RoomStatusPage />} />
          <Route path="/operations/log-center" element={<LogPage />} />

          <Route path="/system/users" element={<SystemDataPage key="users" type="users" title="用户管理" />} />
          <Route path="/system/roles" element={<SystemDataPage key="roles" type="roles" title="角色管理" />} />
          <Route path="/system/permissions" element={<SystemDataPage key="permissions" type="permissions" title="权限管理" />} />
          <Route path="/system/menus" element={<SystemDataPage key="menus" type="menus" title="菜单管理" />} />
          <Route path="/system/dicts" element={<SystemDataPage key="dicts" type="dicts" title="字典管理" />} />
          <Route path="/system/params" element={<SystemDataPage key="params" type="params" title="参数配置" />} />

          <Route path="/dashboard" element={<Navigate to="/workbench/dashboard" replace />} />
          <Route path="/groups" element={<Navigate to="/assets/homestays" replace />} />
          <Route path="/brands" element={<Navigate to="/assets/homestays" replace />} />
          <Route path="/properties" element={<Navigate to="/assets/homestays" replace />} />
          <Route path="/properties/:id" element={<PropertyDetailPage />} />
          <Route path="/room-types" element={<Navigate to="/assets/room-types" replace />} />
          <Route path="/rooms" element={<Navigate to="/assets/rooms" replace />} />
          <Route path="/orders" element={<Navigate to="/orders/reservations" replace />} />
          <Route path="/orders/:id" element={<Navigate to="/orders/detail" replace />} />
          <Route path="/pricing" element={<Navigate to="/pricing/calendar" replace />} />
          <Route path="/ota" element={<Navigate to="/channels/config" replace />} />
          <Route path="/member" element={<Navigate to="/crm/members" replace />} />
          <Route path="/finance-ops" element={<Navigate to="/finance/receipts" replace />} />
          <Route path="/logs" element={<Navigate to="/operations/log-center" replace />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
