import { App, Button, Card, Form, Input, Modal, Select, Space, Table, Typography } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { usePermission } from '../hooks/usePermission'
import {
  createSystemDict,
  createSystemParam,
  createSystemRole,
  createSystemUser,
  fetchSystemDicts,
  fetchSystemMenus,
  fetchSystemParams,
  fetchSystemPermissions,
  fetchSystemRoles,
  fetchSystemUsers,
  updateSystemDict,
  updateSystemParam,
  updateSystemRole,
  updateSystemUser,
} from '../api/systemApi'

interface SystemDataPageProps {
  type: 'users' | 'roles' | 'permissions' | 'menus' | 'dicts' | 'params'
  title: string
}

export function SystemDataPage({ type, title }: SystemDataPageProps) {
  const [data, setData] = useState<any[]>([])
  const [keyword, setKeyword] = useState('')
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<any>(null)
  const [form] = Form.useForm<any>()
  const { message } = App.useApp()
  const { hasPermission } = usePermission()

  const writePermissionMap: Record<SystemDataPageProps['type'], string | null> = {
    users: 'sys:user:write',
    roles: 'sys:role:write',
    permissions: null,
    menus: null,
    dicts: 'sys:dict:write',
    params: 'sys:param:write',
  }

  const canEdit = (type === 'users' || type === 'roles' || type === 'dicts' || type === 'params')
    && !!writePermissionMap[type]
    && hasPermission(writePermissionMap[type] as string)

  const load = async () => {
    const fetcherMap = {
      users: fetchSystemUsers,
      roles: fetchSystemRoles,
      permissions: fetchSystemPermissions,
      menus: fetchSystemMenus,
      dicts: fetchSystemDicts,
      params: fetchSystemParams,
    }
    const list = await fetcherMap[type]()
    setData(list || [])
  }

  useEffect(() => {
    load()
  }, [type])

  const openCreate = () => {
    setEditing(null)
    form.resetFields()
    if (type === 'users' || type === 'roles') {
      form.setFieldValue('status', 1)
    }
    setOpen(true)
  }

  const openEdit = (row: any) => {
    setEditing(row)
    form.setFieldsValue(row)
    setOpen(true)
  }

  const save = async () => {
    const values = await form.validateFields()
    if (type === 'users') {
      if (editing?.id) {
        await updateSystemUser(editing.id, values)
        message.success('用户更新成功')
      } else {
        await createSystemUser(values)
        message.success('用户创建成功')
      }
    }

    if (type === 'roles') {
      if (editing?.id) {
        await updateSystemRole(editing.id, values)
        message.success('角色更新成功')
      } else {
        await createSystemRole(values)
        message.success('角色创建成功')
      }
    }

    if (type === 'dicts') {
      if (editing?.dictCode) {
        await updateSystemDict(editing.dictCode, values)
        message.success('字典更新成功')
      } else {
        await createSystemDict(values)
        message.success('字典创建成功')
      }
    }

    if (type === 'params') {
      if (editing?.paramKey) {
        await updateSystemParam(editing.paramKey, values)
        message.success('参数更新成功')
      } else {
        await createSystemParam(values)
        message.success('参数创建成功')
      }
    }

    setOpen(false)
    form.resetFields()
    load()
  }

  const columns = useMemo(() => {
    if (type === 'users') {
      return [
        { title: '用户ID', dataIndex: 'id', width: 90 },
        { title: '用户名', dataIndex: 'username', width: 150 },
        { title: '昵称', dataIndex: 'nickname', width: 140 },
        { title: '手机号', dataIndex: 'mobile', width: 140 },
        { title: '邮箱', dataIndex: 'email', width: 200 },
        { title: '状态', dataIndex: 'status', width: 100, render: (value: number) => (value === 1 ? '启用' : '停用') },
        ...(canEdit
          ? [{
              title: '操作',
              width: 120,
              render: (_: any, row: any) => (
                <Button type="link" onClick={() => openEdit(row)}>
                  编辑
                </Button>
              ),
            }]
          : []),
      ]
    }
    if (type === 'roles') {
      return [
        { title: '角色ID', dataIndex: 'id', width: 90 },
        { title: '角色编码', dataIndex: 'roleCode', width: 180 },
        { title: '角色名称', dataIndex: 'roleName', width: 180 },
        { title: '状态', dataIndex: 'status', width: 100, render: (value: number) => (value === 1 ? '启用' : '停用') },
        ...(canEdit
          ? [{
              title: '操作',
              width: 120,
              render: (_: any, row: any) => (
                <Button type="link" onClick={() => openEdit(row)}>
                  编辑
                </Button>
              ),
            }]
          : []),
      ]
    }
    if (type === 'permissions') {
      return [
        { title: '权限编码', dataIndex: 'permissionCode', width: 260 },
        { title: '菜单名称', dataIndex: 'menuName', width: 220 },
        { title: '类型', dataIndex: 'menuType', width: 120 },
      ]
    }
    if (type === 'menus') {
      return [
        { title: '菜单ID', dataIndex: 'id', width: 90 },
        { title: '父级ID', dataIndex: 'parentId', width: 100 },
        { title: '菜单名称', dataIndex: 'menuName', width: 180 },
        { title: '路由路径', dataIndex: 'path', width: 240 },
        { title: '权限编码', dataIndex: 'permissionCode' },
      ]
    }
    if (type === 'dicts') {
      return [
        { title: '字典编码', dataIndex: 'dictCode', width: 220 },
        { title: '字典名称', dataIndex: 'dictName', width: 260 },
        { title: '说明', dataIndex: 'remark' },
        ...(canEdit
          ? [{
              title: '操作',
              width: 120,
              render: (_: any, row: any) => (
                <Button type="link" onClick={() => openEdit(row)}>
                  编辑
                </Button>
              ),
            }]
          : []),
      ]
    }
    return [
      { title: '参数键', dataIndex: 'paramKey', width: 260 },
      { title: '参数值', dataIndex: 'paramValue', width: 180 },
      { title: '说明', dataIndex: 'remark' },
      ...(canEdit
        ? [{
            title: '操作',
            width: 120,
            render: (_: any, row: any) => (
              <Button type="link" onClick={() => openEdit(row)}>
                编辑
              </Button>
            ),
          }]
        : []),
    ]
  }, [type, canEdit])

  const filteredData = useMemo(() => {
    if (!keyword.trim()) {
      return data
    }
    const text = keyword.toLowerCase()
    return data.filter((row) => JSON.stringify(row).toLowerCase().includes(text))
  }, [data, keyword])

  return (
    <Card
      title={
        <Space direction="vertical" size={0}>
          <Typography.Text strong>{title}</Typography.Text>
          <Typography.Text type="secondary">支持关键字检索，便于排查权限与配置问题。</Typography.Text>
        </Space>
      }
      extra={
        <Space>
          <Input.Search
            allowClear
            placeholder="请输入关键字搜索"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            style={{ width: 260 }}
          />
          {canEdit && (
            <Button type="primary" onClick={openCreate}>
              新增
            </Button>
          )}
        </Space>
      }
    >
      <Table
        rowKey={(row) => `${type}-${row.id || row.permissionCode || row.dictCode || row.paramKey}`}
        dataSource={filteredData}
        columns={columns}
        pagination={{ pageSize: 10 }}
      />

      <Modal title={editing ? `编辑${title}` : `新增${title}`} open={open} onOk={save} onCancel={() => setOpen(false)} destroyOnClose>
        {(type === 'users' || type === 'roles' || type === 'dicts' || type === 'params') && (
          <Form form={form} layout="vertical">
            {type === 'users' && (
              <>
                <Form.Item name="username" label="用户名" rules={[{ required: true }]}>
                  <Input disabled={!!editing} />
                </Form.Item>
                <Form.Item name="nickname" label="昵称" rules={[{ required: true }]}>
                  <Input />
                </Form.Item>
                <Form.Item name="mobile" label="手机号">
                  <Input />
                </Form.Item>
                <Form.Item name="email" label="邮箱">
                  <Input />
                </Form.Item>
                <Form.Item name="status" label="状态" rules={[{ required: true }]}>
                  <Select options={[{ label: '启用', value: 1 }, { label: '停用', value: 0 }]} />
                </Form.Item>
              </>
            )}

            {type === 'roles' && (
              <>
                <Form.Item name="roleCode" label="角色编码" rules={[{ required: true }]}>
                  <Input disabled={!!editing} />
                </Form.Item>
                <Form.Item name="roleName" label="角色名称" rules={[{ required: true }]}>
                  <Input />
                </Form.Item>
                <Form.Item name="status" label="状态" rules={[{ required: true }]}>
                  <Select options={[{ label: '启用', value: 1 }, { label: '停用', value: 0 }]} />
                </Form.Item>
              </>
            )}

            {type === 'dicts' && (
              <>
                <Form.Item name="dictCode" label="字典编码" rules={[{ required: true }]}>
                  <Input disabled={!!editing} />
                </Form.Item>
                <Form.Item name="dictName" label="字典名称" rules={[{ required: true }]}>
                  <Input />
                </Form.Item>
                <Form.Item name="remark" label="说明">
                  <Input.TextArea rows={3} />
                </Form.Item>
              </>
            )}

            {type === 'params' && (
              <>
                <Form.Item name="paramKey" label="参数键" rules={[{ required: true }]}>
                  <Input disabled={!!editing} />
                </Form.Item>
                <Form.Item name="paramValue" label="参数值" rules={[{ required: true }]}>
                  <Input />
                </Form.Item>
                <Form.Item name="remark" label="说明">
                  <Input.TextArea rows={3} />
                </Form.Item>
              </>
            )}
          </Form>
        )}
      </Modal>
    </Card>
  )
}
