import { App, Button, Layout, Menu, Select, Space, Typography } from 'antd'
import type { MenuProps } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { Outlet, useLocation, useNavigate } from 'react-router-dom'
import { logout, switchProperty } from '../api/authApi'
import { fetchProperties } from '../api/propertyApi'
import { clearAuth, setAuth } from '../features/auth/authSlice'
import { useAppDispatch, useAppSelector } from '../store/hooks'

const { Header, Sider, Content } = Layout

type RawMenuItem = {
  key: string
  label: string
  permission?: string
  children?: RawMenuItem[]
}

const rawMenuItems: RawMenuItem[] = [
  {
    key: 'workbench',
    label: '工作台',
    children: [
      { key: '/workbench/dashboard', label: '经营看板', permission: 'report:read' },
      { key: '/workbench/today-checkin', label: '今日入住', permission: 'checkin:read' },
      { key: '/workbench/today-checkout', label: '今日退房', permission: 'checkin:read' },
      { key: '/workbench/todos', label: '待处理事项', permission: 'ops:read' },
    ],
  },
  {
    key: 'assets',
    label: '房源管理',
    children: [
      { key: '/assets/homestays', label: '民宿管理', permission: 'property:read' },
      { key: '/assets/room-types', label: '房型管理', permission: 'roomType:read' },
      { key: '/assets/rooms', label: '房间管理', permission: 'room:read' },
      { key: '/assets/room-status', label: '房态管理', permission: 'room:read' },
    ],
  },
  {
    key: 'orders',
    label: '订单中心',
    children: [
      { key: '/orders/reservations', label: '预订订单', permission: 'order:read' },
      { key: '/orders/checkin', label: '入住登记', permission: 'checkin:read' },
      { key: '/orders/inhouse', label: '在住管理', permission: 'checkin:read' },
      { key: '/orders/checkout', label: '退房管理', permission: 'checkin:read' },
      { key: '/orders/detail', label: '订单详情', permission: 'order:read' },
    ],
  },
  {
    key: 'pricing',
    label: '价格与库存',
    children: [
      { key: '/pricing/calendar', label: '房价日历', permission: 'pricing:read' },
      { key: '/pricing/plans', label: '价格计划', permission: 'pricing:read' },
      { key: '/pricing/inventory', label: '可售库存', permission: 'inventory:read' },
      { key: '/pricing/overbook-warning', label: '超售预警', permission: 'inventory:read' },
    ],
  },
  {
    key: 'channels',
    label: '渠道管理',
    children: [
      { key: '/channels/config', label: '渠道配置', permission: 'ota:read' },
      { key: '/channels/mapping', label: '渠道映射', permission: 'ota:read' },
      { key: '/channels/sync-logs', label: '同步记录', permission: 'ota:read' },
      { key: '/channels/callback-logs', label: '回调记录', permission: 'ota:read' },
    ],
  },
  {
    key: 'crm',
    label: '客户与会员',
    children: [
      { key: '/crm/customers', label: '客户档案', permission: 'member:read' },
      { key: '/crm/members', label: '会员管理', permission: 'member:read' },
      { key: '/crm/points', label: '积分管理', permission: 'member:read' },
      { key: '/crm/coupons', label: '优惠券管理', permission: 'member:read' },
    ],
  },
  {
    key: 'finance',
    label: '财务管理',
    children: [
      { key: '/finance/receipts', label: '收款记录', permission: 'finance:read' },
      { key: '/finance/refunds', label: '退款记录', permission: 'finance:read' },
      { key: '/finance/bills', label: '账单明细', permission: 'finance:read' },
      { key: '/finance/invoices', label: '发票管理', permission: 'finance:read' },
      { key: '/finance/reports', label: '经营报表', permission: 'report:read' },
    ],
  },
  {
    key: 'operations',
    label: '运营管理',
    children: [
      { key: '/operations/housekeeping', label: '保洁管理', permission: 'ops:read' },
      { key: '/operations/maintenance', label: '维修工单', permission: 'ops:read' },
      { key: '/operations/room-status-record', label: '房态记录', permission: 'room:read' },
      { key: '/operations/log-center', label: '日志中心', permission: 'report:read' },
    ],
  },
  {
    key: 'system',
    label: '系统管理',
    children: [
      { key: '/system/users', label: '用户管理', permission: 'sys:user:read' },
      { key: '/system/roles', label: '角色管理', permission: 'sys:role:read' },
      { key: '/system/permissions', label: '权限管理', permission: 'sys:permission:read' },
      { key: '/system/menus', label: '菜单管理', permission: 'sys:menu:read' },
      { key: '/system/dicts', label: '字典管理', permission: 'sys:dict:read' },
      { key: '/system/params', label: '参数配置', permission: 'sys:param:read' },
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

function toAntMenuItems(rawItems: RawMenuItem[], permissions: string[]): MenuProps['items'] {
  return rawItems
    .map((item) => {
      if (item.children && item.children.length > 0) {
        const children = item.children.filter((child) => hasPermission(permissions, child.permission))
        if (children.length === 0) {
          return null
        }
        return {
          key: item.key,
          label: item.label,
          children: children.map((child) => ({ key: child.key, label: child.label })),
        }
      }
      if (!hasPermission(permissions, item.permission)) {
        return null
      }
      return { key: item.key, label: item.label }
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
  const navigate = useNavigate()
  const location = useLocation()
  const dispatch = useAppDispatch()
  const auth = useAppSelector((state) => state.auth)
  const permissions = auth.permissions || []
  const { message } = App.useApp()
  const [propertyMap, setPropertyMap] = useState<Record<number, string>>({})

  useEffect(() => {
    const loadProperties = async () => {
      try {
        const list = await fetchProperties()
        const map: Record<number, string> = {}
        ;(list || []).forEach((item) => {
          map[item.id] = item.propertyName
        })
        setPropertyMap(map)
      } catch {
        setPropertyMap({})
      }
    }
    loadProperties()
  }, [auth.accessToken])

  const menuItems = useMemo(() => toAntMenuItems(rawMenuItems, permissions), [permissions])
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
    message.success('已退出登录')
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
      message.success('门店切换成功')
    } catch {
      message.error('门店切换失败')
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
            民宿多店管理系统
          </Typography.Title>
          <Space>
            <span>当前用户: {auth.nickname || auth.username || '-'}</span>
            <Select
              style={{ width: 220 }}
              placeholder="切换门店"
              value={auth.currentPropertyId}
              options={(auth.propertyScopes || []).map((id) => ({ label: propertyMap[id] || `门店 ${id}`, value: id }))}
              onChange={handleSwitchProperty}
            />
            <Button onClick={handleLogout}>退出</Button>
          </Space>
        </Header>
        <Content className="hms-content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
}
