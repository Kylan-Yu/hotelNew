import { App, Button, Layout, Menu, Select, Space, Typography } from 'antd'
import type { MenuProps } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import { logout, switchProperty } from '../api/authApi'
import { PropertyItem, fetchProperties, fetchPropertyScopeOptions } from '../api/propertyApi'
import { clearAuth, setAuth } from '../features/auth/authSlice'
import { languageOptions } from '../i18n'
import { useAppDispatch, useAppSelector } from '../store/hooks'

const { Header, Sider, Content } = Layout

type RawMenuItem = {
  key: string
  labelKey: string
  permission?: string
  children?: RawMenuItem[]
}

const rawMenuItems: RawMenuItem[] = [
  {
    key: 'workbench',
    labelKey: 'menu.workbench',
    children: [
      { key: '/workbench/dashboard', labelKey: 'menu.dashboard', permission: 'report:read' },
      { key: '/workbench/today-checkin', labelKey: 'menu.todayCheckin', permission: 'checkin:read' },
      { key: '/workbench/today-checkout', labelKey: 'menu.todayCheckout', permission: 'checkin:read' },
      { key: '/workbench/todos', labelKey: 'menu.todos', permission: 'ops:read' },
    ],
  },
  {
    key: 'assets',
    labelKey: 'menu.assets',
    children: [
      { key: '/assets/homestays', labelKey: 'menu.homestays', permission: 'property:read' },
      { key: '/assets/room-types', labelKey: 'menu.roomTypes', permission: 'roomType:read' },
      { key: '/assets/rooms', labelKey: 'menu.rooms', permission: 'room:read' },
      { key: '/assets/room-status', labelKey: 'menu.roomStatus', permission: 'room:read' },
    ],
  },
  {
    key: 'orders',
    labelKey: 'menu.orders',
    children: [
      { key: '/orders/reservations', labelKey: 'menu.reservations', permission: 'order:read' },
      { key: '/orders/checkin', labelKey: 'menu.checkin', permission: 'checkin:read' },
      { key: '/orders/inhouse', labelKey: 'menu.inhouse', permission: 'checkin:read' },
      { key: '/orders/checkout', labelKey: 'menu.checkout', permission: 'checkin:read' },
      { key: '/orders/detail', labelKey: 'menu.orderDetail', permission: 'order:read' },
    ],
  },
  {
    key: 'pricing',
    labelKey: 'menu.pricing',
    children: [
      { key: '/pricing/calendar', labelKey: 'menu.calendar', permission: 'pricing:read' },
      { key: '/pricing/plans', labelKey: 'menu.plans', permission: 'pricing:read' },
      { key: '/pricing/inventory', labelKey: 'menu.inventory', permission: 'inventory:read' },
      { key: '/pricing/overbook-warning', labelKey: 'menu.overbookWarning', permission: 'inventory:read' },
    ],
  },
  {
    key: 'channels',
    labelKey: 'menu.channels',
    children: [
      { key: '/channels/config', labelKey: 'menu.config', permission: 'ota:read' },
      { key: '/channels/mapping', labelKey: 'menu.mapping', permission: 'ota:read' },
      { key: '/channels/sync-logs', labelKey: 'menu.syncLogs', permission: 'ota:read' },
      { key: '/channels/callback-logs', labelKey: 'menu.callbackLogs', permission: 'ota:read' },
    ],
  },
  {
    key: 'crm',
    labelKey: 'menu.crm',
    children: [
      { key: '/crm/customers', labelKey: 'menu.customers', permission: 'member:read' },
      { key: '/crm/members', labelKey: 'menu.members', permission: 'member:read' },
      { key: '/crm/points', labelKey: 'menu.points', permission: 'member:read' },
      { key: '/crm/coupons', labelKey: 'menu.coupons', permission: 'member:read' },
    ],
  },
  {
    key: 'finance',
    labelKey: 'menu.finance',
    children: [
      { key: '/finance/receipts', labelKey: 'menu.receipts', permission: 'finance:read' },
      { key: '/finance/refunds', labelKey: 'menu.refunds', permission: 'finance:read' },
      { key: '/finance/bills', labelKey: 'menu.bills', permission: 'finance:read' },
      { key: '/finance/invoices', labelKey: 'menu.invoices', permission: 'finance:read' },
      { key: '/finance/reports', labelKey: 'menu.reports', permission: 'report:read' },
    ],
  },
  {
    key: 'operations',
    labelKey: 'menu.operations',
    children: [
      { key: '/operations/housekeeping', labelKey: 'menu.housekeeping', permission: 'ops:read' },
      { key: '/operations/maintenance', labelKey: 'menu.maintenance', permission: 'ops:read' },
      { key: '/operations/room-status-record', labelKey: 'menu.roomStatusRecord', permission: 'room:read' },
      { key: '/operations/log-center', labelKey: 'menu.logCenter', permission: 'report:read' },
    ],
  },
  {
    key: 'system',
    labelKey: 'menu.system',
    children: [
      { key: '/system/users', labelKey: 'menu.users', permission: 'sys:user:read' },
      { key: '/system/roles', labelKey: 'menu.roles', permission: 'sys:role:read' },
      { key: '/system/permissions', labelKey: 'menu.permissions', permission: 'sys:permission:read' },
      { key: '/system/menus', labelKey: 'menu.menus', permission: 'sys:menu:read' },
      { key: '/system/dicts', labelKey: 'menu.dicts', permission: 'sys:dict:read' },
      { key: '/system/params', labelKey: 'menu.params', permission: 'sys:param:read' },
    ],
  },
]

function hasPermission(permissions: string[], permission?: string) {
  if (!permission) {
    return true
  }
  if (permissions.includes('*')) {
    return true
  }
  return permissions.includes(permission)
}

function toAntMenuItems(
  rawItems: RawMenuItem[],
  permissions: string[],
  translate: (key: string) => string,
): MenuProps['items'] {
  return rawItems
    .map((item) => {
      if (item.children && item.children.length > 0) {
        const children = item.children.filter((child) => hasPermission(permissions, child.permission))
        if (children.length === 0) {
          return null
        }
        return {
          key: item.key,
          label: translate(item.labelKey),
          children: children.map((child) => ({ key: child.key, label: translate(child.labelKey) })),
        }
      }
      if (!hasPermission(permissions, item.permission)) {
        return null
      }
      return { key: item.key, label: translate(item.labelKey) }
    })
    .filter(Boolean) as MenuProps['items']
}

function flattenMenuKeys(items: MenuProps['items']): string[] {
  const keys: string[] = []
  for (const item of items || []) {
    if (!item) continue
    if ('children' in item && item.children) {
      keys.push(...flattenMenuKeys(item.children))
    } else if (item.key && String(item.key).startsWith('/')) {
      keys.push(String(item.key))
    }
  }
  return keys
}

function buildLeafParentMap(items: MenuProps['items'], parentKey?: string, map: Record<string, string> = {}) {
  for (const item of items || []) {
    if (!item || !item.key) continue
    const key = String(item.key)
    if ('children' in item && item.children) {
      buildLeafParentMap(item.children, key, map)
    } else if (key.startsWith('/')) {
      map[key] = parentKey || ''
    }
  }
  return map
}

export function MainLayout() {
  const { t, i18n } = useTranslation()
  const navigate = useNavigate()
  const location = useLocation()
  const dispatch = useAppDispatch()
  const auth = useAppSelector((state) => state.auth)
  const permissions = auth.permissions || []
  const { message } = App.useApp()
  const [propertyOptions, setPropertyOptions] = useState<PropertyItem[]>([])

  useEffect(() => {
    const loadScopeProperties = async () => {
      try {
        const list = await fetchPropertyScopeOptions()
        if (list && list.length > 0) {
          setPropertyOptions(list)
          return
        }
        const fallbackList = await fetchProperties()
        setPropertyOptions((fallbackList || []).filter((item) => item.status === 1))
      } catch {
        try {
          const fallbackList = await fetchProperties()
          setPropertyOptions((fallbackList || []).filter((item) => item.status === 1))
        } catch {
          setPropertyOptions([])
        }
      }
    }
    loadScopeProperties()
    const onPropertyOptionsUpdated = () => {
      loadScopeProperties()
    }
    window.addEventListener('hms:property-options-updated', onPropertyOptionsUpdated)
    return () => {
      window.removeEventListener('hms:property-options-updated', onPropertyOptionsUpdated)
    }
  }, [auth.accessToken])

  const menuItems = useMemo(() => toAntMenuItems(rawMenuItems, permissions, t), [permissions, t, i18n.language])
  const leafKeys = useMemo(() => flattenMenuKeys(menuItems), [menuItems])
  const leafParentMap = useMemo(() => buildLeafParentMap(menuItems), [menuItems])

  const selectedKey = useMemo(() => {
    const matched = [...leafKeys]
      .sort((a, b) => b.length - a.length)
      .find((key) => location.pathname === key || location.pathname.startsWith(`${key}/`))
    return matched || leafKeys[0] || '/workbench/dashboard'
  }, [leafKeys, location.pathname])

  const [openKeys, setOpenKeys] = useState<string[]>(() => {
    const parent = leafParentMap[selectedKey]
    return parent ? [parent] : []
  })

  useEffect(() => {
    const parent = leafParentMap[selectedKey]
    setOpenKeys(parent ? [parent] : [])
  }, [leafParentMap, selectedKey])

  const handleLogout = async () => {
    try {
      await logout(auth.refreshToken)
    } catch {
      // ignore network errors
    }
    dispatch(clearAuth())
    message.success(t('layout.logoutSuccess'))
    navigate('/login', { replace: true })
  }

  const handleSwitchProperty = async (propertyId: number) => {
    try {
      const data = await switchProperty(propertyId)
      dispatch(
        setAuth({
          accessToken: data.accessToken,
          refreshToken: data.refreshToken,
          username: data.username,
          nickname: data.nickname || data.username,
          permissions: data.permissions || [],
          propertyScopes: data.propertyScopes || [],
          currentPropertyId: data.currentPropertyId,
        }),
      )
      message.success(t('layout.switchPropertySuccess'))
      window.dispatchEvent(new Event('hms:property-options-updated'))
    } catch {
      message.error(t('layout.switchPropertyFailed'))
    }
  }

  return (
    <Layout className="hms-layout">
      <Sider className="hms-sider" width={252}>
        <div className="hms-sider-logo">Homestay Hub</div>
        <Menu
          className="hms-menu"
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          openKeys={openKeys}
          onOpenChange={(keys) => setOpenKeys(keys as string[])}
          items={menuItems}
          onClick={({ key }) => {
            if (String(key).startsWith('/')) {
              navigate(String(key))
            }
          }}
        />
      </Sider>
      <Layout className="hms-main">
        <Header className="hms-header">
          <Typography.Title level={5} style={{ margin: 0 }}>
            {t('layout.systemTitle')}
          </Typography.Title>
          <Space>
            <Select
              style={{ width: 130 }}
              value={i18n.language === 'zh-CN' ? 'zh-CN' : 'en'}
              options={languageOptions}
              onChange={(lang) => i18n.changeLanguage(lang)}
            />
            <Select
              style={{ width: 260 }}
              placeholder={t('layout.switchPropertyPlaceholder')}
              value={auth.currentPropertyId}
              options={propertyOptions.map((item) => ({ label: `${item.propertyCode} - ${item.propertyName}`, value: item.id }))}
              onChange={handleSwitchProperty}
            />
            <Button onClick={handleLogout}>{t('layout.logout')}</Button>
          </Space>
        </Header>
        <Content className="hms-content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}
