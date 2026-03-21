import { App, Button, Card, Form, Input, InputNumber, Modal, Select, Space, Table, Typography } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { usePermission } from '../hooks/usePermission'
import {
  createSystemDict,
  createSystemDictItem,
  createSystemParam,
  createSystemRole,
  createSystemUser,
  fetchSystemDictItems,
  fetchSystemDicts,
  fetchSystemMenus,
  fetchSystemParams,
  fetchSystemPermissions,
  fetchSystemRoles,
  fetchSystemUsers,
  updateSystemDict,
  updateSystemDictItem,
  updateSystemParam,
  updateSystemRole,
  updateSystemUser,
} from '../api/systemApi'
import { DEFAULT_TABLE_PAGINATION } from '../constants/tablePagination'

interface SystemDataPageProps {
  type: 'users' | 'roles' | 'permissions' | 'menus' | 'dicts' | 'params'
  title?: string
}

export function SystemDataPage({ type, title }: SystemDataPageProps) {
  const [data, setData] = useState<any[]>([])
  const [keyword, setKeyword] = useState('')
  const [itemKeyword, setItemKeyword] = useState('')
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<any>(null)
  const [dictItems, setDictItems] = useState<any[]>([])
  const [selectedDictCode, setSelectedDictCode] = useState<string | undefined>()
  const [itemOpen, setItemOpen] = useState(false)
  const [editingItem, setEditingItem] = useState<any>(null)
  const [form] = Form.useForm<any>()
  const [itemForm] = Form.useForm<any>()
  const { message } = App.useApp()
  const { hasPermission } = usePermission()
  const { t } = useTranslation()

  const pageTitle = t(`systemData.${type}Title`, { defaultValue: title || type })

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

  const statusText = (value: number) => (value === 1 ? t('common.enabled') : t('common.disabled'))

  const load = async () => {
    const fetcherMap: Record<SystemDataPageProps['type'], () => Promise<any[]>> = {
      users: fetchSystemUsers,
      roles: fetchSystemRoles,
      permissions: fetchSystemPermissions,
      menus: fetchSystemMenus,
      dicts: fetchSystemDicts,
      params: fetchSystemParams,
    }
    try {
      const list = await fetcherMap[type]()
      setData(list || [])
      if (type === 'dicts') {
        const firstCode = selectedDictCode || (list?.[0]?.dictCode as string | undefined)
        if (firstCode) {
          setSelectedDictCode(firstCode)
          const items = await fetchSystemDictItems(firstCode)
          setDictItems(items || [])
        } else {
          setDictItems([])
        }
      }
    } catch (error: any) {
      const msg = error?.response?.data?.message || error?.message || t('systemData.loadFailed')
      message.error(msg)
    }
  }

  useEffect(() => {
    setData([])
    setKeyword('')
    setDictItems([])
    setSelectedDictCode(undefined)
    load()
  }, [type])

  const loadDictItems = async (dictCode?: string) => {
    if (!dictCode) {
      setDictItems([])
      return
    }
    try {
      const list = await fetchSystemDictItems(dictCode)
      setDictItems(list || [])
    } catch (error: any) {
      const msg = error?.response?.data?.message || error?.message || t('systemData.dicts.loadItemsFailed')
      message.error(msg)
    }
  }

  const openCreate = () => {
    setEditing(null)
    form.resetFields()
    if (type === 'users' || type === 'roles') {
      form.setFieldValue('status', 1)
    }
    if (type === 'dicts') {
      form.setFieldsValue({ status: 1, sortNo: 100 })
    }
    setOpen(true)
  }

  const openEdit = (row: any) => {
    setEditing(row)
    form.setFieldsValue(row)
    setOpen(true)
  }

  const save = async () => {
    try {
      const values = await form.validateFields()
      if (type === 'users') {
        if (editing?.id) {
          await updateSystemUser(editing.id, values)
          message.success(t('systemData.users.updateSuccess'))
        } else {
          await createSystemUser(values)
          message.success(t('systemData.users.createSuccess'))
        }
      }

      if (type === 'roles') {
        if (editing?.id) {
          await updateSystemRole(editing.id, values)
          message.success(t('systemData.roles.updateSuccess'))
        } else {
          await createSystemRole(values)
          message.success(t('systemData.roles.createSuccess'))
        }
      }

      if (type === 'dicts') {
        if (editing?.dictCode) {
          await updateSystemDict(editing.dictCode, values)
          message.success(t('systemData.dicts.updateSuccess'))
          window.dispatchEvent(new Event('hms:dict-updated'))
        } else {
          await createSystemDict(values)
          message.success(t('systemData.dicts.createSuccess'))
          window.dispatchEvent(new Event('hms:dict-updated'))
          if (!selectedDictCode && values?.dictCode) {
            setSelectedDictCode(values.dictCode)
          }
        }
      }

      if (type === 'params') {
        if (editing?.paramKey) {
          await updateSystemParam(editing.paramKey, values)
          message.success(t('systemData.params.updateSuccess'))
        } else {
          await createSystemParam(values)
          message.success(t('systemData.params.createSuccess'))
        }
      }

      setOpen(false)
      form.resetFields()
      load()
    } catch (error: any) {
      const msg = error?.response?.data?.message || error?.message || t('systemData.saveFailed')
      message.error(msg)
    }
  }

  const openCreateItem = () => {
    setEditingItem(null)
    itemForm.resetFields()
    itemForm.setFieldsValue({ status: 1, sortNo: 100 })
    setItemOpen(true)
  }

  const openEditItem = (row: any) => {
    setEditingItem(row)
    itemForm.setFieldsValue(row)
    setItemOpen(true)
  }

  const saveItem = async () => {
    try {
      const values = await itemForm.validateFields()
      if (!selectedDictCode) {
        message.warning(t('systemData.dicts.selectDictWarning'))
        return
      }
      if (editingItem?.id) {
        await updateSystemDictItem(editingItem.id, values)
        message.success(t('systemData.dicts.itemUpdateSuccess'))
        window.dispatchEvent(new Event('hms:dict-updated'))
      } else {
        await createSystemDictItem(selectedDictCode, values)
        message.success(t('systemData.dicts.itemCreateSuccess'))
        window.dispatchEvent(new Event('hms:dict-updated'))
      }
      setItemOpen(false)
      itemForm.resetFields()
      loadDictItems(selectedDictCode)
      load()
    } catch (error: any) {
      const msg = error?.response?.data?.message || error?.message || t('systemData.dicts.saveItemFailed')
      message.error(msg)
    }
  }

  const columns = useMemo(() => {
    if (type === 'users') {
      return [
        { title: t('systemData.users.id'), dataIndex: 'id', width: 90 },
        { title: t('systemData.users.username'), dataIndex: 'username', width: 150 },
        { title: t('systemData.users.nickname'), dataIndex: 'nickname', width: 140 },
        { title: t('systemData.users.mobile'), dataIndex: 'mobile', width: 140 },
        { title: t('systemData.users.email'), dataIndex: 'email', width: 200 },
        { title: t('systemData.users.status'), dataIndex: 'status', width: 100, render: (value: number) => statusText(value) },
        ...(canEdit
          ? [{
              title: t('property.colActions'),
              width: 120,
              render: (_: any, row: any) => (
                <Button type="link" onClick={() => openEdit(row)}>
                  {t('systemData.actions.edit')}
                </Button>
              ),
            }]
          : []),
      ]
    }
    if (type === 'roles') {
      return [
        { title: t('systemData.roles.id'), dataIndex: 'id', width: 90 },
        { title: t('systemData.roles.code'), dataIndex: 'roleCode', width: 180 },
        { title: t('systemData.roles.name'), dataIndex: 'roleName', width: 180 },
        { title: t('systemData.roles.status'), dataIndex: 'status', width: 100, render: (value: number) => statusText(value) },
        ...(canEdit
          ? [{
              title: t('property.colActions'),
              width: 120,
              render: (_: any, row: any) => (
                <Button type="link" onClick={() => openEdit(row)}>
                  {t('systemData.actions.edit')}
                </Button>
              ),
            }]
          : []),
      ]
    }
    if (type === 'permissions') {
      return [
        { title: t('systemData.permissions.code'), dataIndex: 'permissionCode', width: 260 },
        { title: t('systemData.permissions.menuName'), dataIndex: 'menuName', width: 220 },
        { title: t('systemData.permissions.menuType'), dataIndex: 'menuType', width: 120 },
      ]
    }
    if (type === 'menus') {
      return [
        { title: t('systemData.menus.id'), dataIndex: 'id', width: 90 },
        { title: t('systemData.menus.parentId'), dataIndex: 'parentId', width: 100 },
        { title: t('systemData.menus.name'), dataIndex: 'menuName', width: 180 },
        { title: t('systemData.menus.path'), dataIndex: 'path', width: 240 },
        { title: t('systemData.menus.permissionCode'), dataIndex: 'permissionCode' },
      ]
    }
    if (type === 'dicts') {
      return [
        { title: t('systemData.dicts.code'), dataIndex: 'dictCode', width: 220 },
        { title: t('systemData.dicts.name'), dataIndex: 'dictName', width: 260 },
        { title: t('systemData.dicts.status'), dataIndex: 'status', width: 100, render: (value: number) => statusText(value) },
        { title: t('systemData.dicts.sortNo'), dataIndex: 'sortNo', width: 90 },
        { title: t('systemData.dicts.itemCount'), dataIndex: 'itemCount', width: 90 },
        { title: t('systemData.dicts.remark'), dataIndex: 'remark' },
        ...(canEdit
          ? [{
              title: t('property.colActions'),
              width: 220,
              render: (_: any, row: any) => (
                <Space size={4}>
                  <Button type="link" onClick={() => openEdit(row)}>
                    {t('systemData.actions.edit')}
                  </Button>
                  <Button
                    type="link"
                    onClick={async () => {
                      setSelectedDictCode(row.dictCode)
                      await loadDictItems(row.dictCode)
                    }}
                  >
                    {t('systemData.dicts.viewItems')}
                  </Button>
                </Space>
              ),
            }]
          : []),
      ]
    }
    return [
      { title: t('systemData.params.key'), dataIndex: 'paramKey', width: 260 },
      { title: t('systemData.params.value'), dataIndex: 'paramValue', width: 180 },
      { title: t('systemData.params.remark'), dataIndex: 'remark' },
      ...(canEdit
        ? [{
            title: t('property.colActions'),
            width: 120,
            render: (_: any, row: any) => (
              <Button type="link" onClick={() => openEdit(row)}>
                {t('systemData.actions.edit')}
              </Button>
            ),
          }]
        : []),
    ]
  }, [type, canEdit, t])

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
          <Typography.Text strong>{pageTitle}</Typography.Text>
          <Typography.Text type="secondary">{t('systemData.subtitle')}</Typography.Text>
        </Space>
      }
      extra={
        <Space>
          <Input.Search
            allowClear
            placeholder={t('systemData.searchPlaceholder')}
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            style={{ width: 260 }}
          />
          {canEdit && (
            <Button type="primary" onClick={openCreate}>
              {t('systemData.newButton')}
            </Button>
          )}
        </Space>
      }
    >
      <Table
        rowKey={(row) => `${type}-${row.id || row.permissionCode || row.dictCode || row.paramKey}`}
        dataSource={filteredData}
        columns={columns}
        pagination={DEFAULT_TABLE_PAGINATION}
      />

      {type === 'dicts' && (
        <Card
          size="small"
          style={{ marginTop: 16 }}
          title={`${t('systemData.dicts.itemsTitle')}${selectedDictCode ? ` - ${selectedDictCode}` : ''}`}
          extra={(
            <Space>
              <Select
                style={{ width: 220 }}
                placeholder={t('systemData.dicts.selectDictPlaceholder')}
                value={selectedDictCode}
                options={data.map((item: any) => ({ label: `${item.dictName}(${item.dictCode})`, value: item.dictCode }))}
                onChange={async (value) => {
                  setSelectedDictCode(value)
                  await loadDictItems(value)
                }}
              />
              {canEdit && (
                <Button type="primary" onClick={openCreateItem} disabled={!selectedDictCode}>
                  {t('systemData.dicts.newItemButton')}
                </Button>
              )}
              <Input.Search
                allowClear
                style={{ width: 220 }}
                placeholder={t('systemData.dicts.itemSearchPlaceholder')}
                value={itemKeyword}
                onChange={(e) => setItemKeyword(e.target.value)}
              />
            </Space>
          )}
        >
          <Table
            rowKey={(row) => `dict-item-${row.id}`}
            dataSource={dictItems.filter((row) => !itemKeyword.trim() || JSON.stringify(row).toLowerCase().includes(itemKeyword.toLowerCase()))}
            pagination={DEFAULT_TABLE_PAGINATION}
            columns={[
              { title: t('systemData.dicts.itemCode'), dataIndex: 'itemCode', width: 180 },
              { title: t('systemData.dicts.itemName'), dataIndex: 'itemName', width: 220 },
              { title: t('systemData.dicts.itemValue'), dataIndex: 'itemValue', width: 180 },
              { title: t('systemData.dicts.itemStatus'), dataIndex: 'status', width: 100, render: (value: number) => statusText(value) },
              { title: t('systemData.dicts.itemSortNo'), dataIndex: 'sortNo', width: 90 },
              { title: t('systemData.dicts.itemRemark'), dataIndex: 'remark' },
              ...(canEdit
                ? [{
                    title: t('property.colActions'),
                    width: 120,
                    render: (_: any, row: any) => (
                      <Button type="link" onClick={() => openEditItem(row)}>
                        {t('systemData.actions.edit')}
                      </Button>
                    ),
                  }]
                : []),
            ]}
          />
        </Card>
      )}

      <Modal title={editing ? `${t('common.edit')} ${pageTitle}` : `${t('systemData.newButton')} ${pageTitle}`} open={open} onOk={save} onCancel={() => setOpen(false)} destroyOnClose>
        {(type === 'users' || type === 'roles' || type === 'dicts' || type === 'params') && (
          <Form form={form} layout="vertical">
            {type === 'users' && (
              <>
                <Form.Item name="username" label={t('systemData.users.formUsername')} rules={[{ required: true }]}>
                  <Input disabled={!!editing} />
                </Form.Item>
                <Form.Item name="nickname" label={t('systemData.users.formNickname')} rules={[{ required: true }]}>
                  <Input />
                </Form.Item>
                <Form.Item name="mobile" label={t('systemData.users.formMobile')}>
                  <Input />
                </Form.Item>
                <Form.Item name="email" label={t('systemData.users.formEmail')}>
                  <Input />
                </Form.Item>
                <Form.Item name="status" label={t('systemData.users.formStatus')} rules={[{ required: true }]}>
                  <Select options={[{ label: t('common.enabled'), value: 1 }, { label: t('common.disabled'), value: 0 }]} />
                </Form.Item>
              </>
            )}

            {type === 'roles' && (
              <>
                <Form.Item name="roleCode" label={t('systemData.roles.formCode')} rules={[{ required: true }]}>
                  <Input disabled={!!editing} />
                </Form.Item>
                <Form.Item name="roleName" label={t('systemData.roles.formName')} rules={[{ required: true }]}>
                  <Input />
                </Form.Item>
                <Form.Item name="status" label={t('systemData.roles.formStatus')} rules={[{ required: true }]}>
                  <Select options={[{ label: t('common.enabled'), value: 1 }, { label: t('common.disabled'), value: 0 }]} />
                </Form.Item>
              </>
            )}

            {type === 'dicts' && (
              <>
                <Form.Item name="dictCode" label={t('systemData.dicts.formCode')} rules={[{ required: true }]}>
                  <Input disabled={!!editing} />
                </Form.Item>
                <Form.Item name="dictName" label={t('systemData.dicts.formName')} rules={[{ required: true }]}>
                  <Input />
                </Form.Item>
                <Form.Item name="status" label={t('systemData.dicts.formStatus')} rules={[{ required: true }]}>
                  <Select options={[{ label: t('common.enabled'), value: 1 }, { label: t('common.disabled'), value: 0 }]} />
                </Form.Item>
                <Form.Item name="sortNo" label={t('systemData.dicts.formSortNo')}>
                  <InputNumber min={0} style={{ width: '100%' }} />
                </Form.Item>
                <Form.Item name="remark" label={t('systemData.dicts.formRemark')}>
                  <Input.TextArea rows={3} />
                </Form.Item>
              </>
            )}

            {type === 'params' && (
              <>
                <Form.Item name="paramKey" label={t('systemData.params.formKey')} rules={[{ required: true }]}>
                  <Input disabled={!!editing} />
                </Form.Item>
                <Form.Item name="paramValue" label={t('systemData.params.formValue')} rules={[{ required: true }]}>
                  <Input />
                </Form.Item>
                <Form.Item name="remark" label={t('systemData.params.formRemark')}>
                  <Input.TextArea rows={3} />
                </Form.Item>
              </>
            )}
          </Form>
        )}
      </Modal>

      <Modal
        title={editingItem ? t('systemData.dicts.itemModalEditTitle') : t('systemData.dicts.itemModalAddTitle')}
        open={itemOpen}
        onOk={saveItem}
        onCancel={() => setItemOpen(false)}
        destroyOnClose
      >
        <Form form={itemForm} layout="vertical">
          <Form.Item name="itemCode" label={t('systemData.dicts.itemCode')} rules={[{ required: !editingItem }]}>
            <Input disabled={!!editingItem} />
          </Form.Item>
          <Form.Item name="itemName" label={t('systemData.dicts.itemName')} rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="itemValue" label={t('systemData.dicts.itemValue')}>
            <Input />
          </Form.Item>
          <Form.Item name="status" label={t('systemData.dicts.itemStatus')} rules={[{ required: true }]}>
            <Select options={[{ label: t('common.enabled'), value: 1 }, { label: t('common.disabled'), value: 0 }]} />
          </Form.Item>
          <Form.Item name="sortNo" label={t('systemData.dicts.itemSortNo')}>
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="remark" label={t('systemData.dicts.itemRemark')}>
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </Card>
  )
}
