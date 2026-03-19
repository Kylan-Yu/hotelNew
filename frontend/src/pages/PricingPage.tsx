import { App, Button, DatePicker, Form, Input, InputNumber, Modal, Select, Space, Table, Tag, Typography } from 'antd'
import dayjs from 'dayjs'
import { useEffect, useMemo, useState } from 'react'
import { dictText } from '../constants/businessDict'
import { PermissionButton } from '../components/PermissionButton'
import {
  adjustInventory,
  createPriceRule,
  fetchInventories,
  fetchPricePlans,
  fetchPriceRules,
  upsertPricePlan,
} from '../api/pricingApi'
import { fetchProperties } from '../api/propertyApi'
import { fetchRoomTypes } from '../api/roomTypeApi'

interface PricingPageProps {
  view: 'calendar' | 'plans' | 'inventory'
}

export function PricingPage({ view }: PricingPageProps) {
  const [plans, setPlans] = useState<any[]>([])
  const [rules, setRules] = useState<any[]>([])
  const [inventories, setInventories] = useState<any[]>([])
  const [properties, setProperties] = useState<any[]>([])
  const [roomTypes, setRoomTypes] = useState<any[]>([])

  const [filterPropertyId, setFilterPropertyId] = useState<number | undefined>()
  const [filterRoomTypeId, setFilterRoomTypeId] = useState<number | undefined>()
  const [filterDate, setFilterDate] = useState<string | undefined>()

  const [planOpen, setPlanOpen] = useState(false)
  const [ruleOpen, setRuleOpen] = useState(false)
  const [inventoryOpen, setInventoryOpen] = useState(false)
  const [planForm] = Form.useForm<any>()
  const [ruleForm] = Form.useForm<any>()
  const [inventoryForm] = Form.useForm<any>()

  const { message } = App.useApp()

  const loadData = async () => {
    const [p, r, i, ps, rt] = await Promise.all([
      fetchPricePlans(),
      fetchPriceRules(),
      fetchInventories(),
      fetchProperties(),
      fetchRoomTypes(),
    ])
    setPlans(p || [])
    setRules(r || [])
    setInventories(i || [])
    setProperties(ps || [])
    setRoomTypes(rt || [])
  }

  useEffect(() => {
    loadData()
  }, [])

  const title = useMemo(() => {
    if (view === 'calendar') return '房价日历'
    if (view === 'plans') return '价格计划'
    return '可售库存'
  }, [view])

  const filteredPlans = useMemo(() => {
    return plans.filter((item) => {
      if (filterPropertyId && item.propertyId !== filterPropertyId) return false
      if (filterRoomTypeId && item.roomTypeId !== filterRoomTypeId) return false
      if (filterDate && item.bizDate !== filterDate) return false
      return true
    })
  }, [plans, filterPropertyId, filterRoomTypeId, filterDate])

  const filteredInventories = useMemo(() => {
    return inventories.filter((item) => {
      if (filterPropertyId && item.propertyId !== filterPropertyId) return false
      if (filterRoomTypeId && item.roomTypeId !== filterRoomTypeId) return false
      if (filterDate && item.bizDate !== filterDate) return false
      return true
    })
  }, [inventories, filterPropertyId, filterRoomTypeId, filterDate])

  const filteredRules = useMemo(() => {
    return rules.filter((item) => {
      if (filterPropertyId && item.propertyId !== filterPropertyId) return false
      return true
    })
  }, [rules, filterPropertyId])

  return (
    <div>
      <Typography.Title level={4} style={{ marginTop: 0 }}>
        {title}
      </Typography.Title>

      <Space style={{ marginBottom: 16 }} wrap>
        <Select
          allowClear
          style={{ width: 200 }}
          placeholder="筛选民宿"
          value={filterPropertyId}
          options={properties.map((item) => ({ label: item.propertyName, value: item.id }))}
          onChange={setFilterPropertyId}
        />
        <Select
          allowClear
          style={{ width: 200 }}
          placeholder="筛选房型"
          value={filterRoomTypeId}
          options={roomTypes.map((item) => ({ label: `${item.roomTypeName} (${item.propertyName})`, value: item.id }))}
          onChange={setFilterRoomTypeId}
        />
        <DatePicker
          allowClear
          placeholder="营业日期"
          value={filterDate ? dayjs(filterDate) : undefined}
          onChange={(value) => setFilterDate(value ? value.format('YYYY-MM-DD') : undefined)}
        />
        <Button
          onClick={() => {
            setFilterPropertyId(undefined)
            setFilterRoomTypeId(undefined)
            setFilterDate(undefined)
          }}
        >
          重置
        </Button>
      </Space>

      {view !== 'inventory' && (
        <Space style={{ marginBottom: 16 }}>
          <PermissionButton permission="pricing:write" type="primary" onClick={() => setPlanOpen(true)}>
            新增价格计划
          </PermissionButton>
          <PermissionButton permission="pricing:write" onClick={() => setRuleOpen(true)}>
            新增价格规则
          </PermissionButton>
        </Space>
      )}

      {view === 'inventory' && (
        <Space style={{ marginBottom: 16 }}>
          <PermissionButton permission="inventory:write" type="primary" onClick={() => setInventoryOpen(true)}>
            调整库存
          </PermissionButton>
        </Space>
      )}

      {view === 'calendar' && (
        <Table
          rowKey="id"
          dataSource={filteredPlans}
          columns={[
            { title: '民宿', dataIndex: 'propertyName' },
            { title: '房型', dataIndex: 'roomTypeName' },
            { title: '营业日', dataIndex: 'bizDate' },
            { title: '销售价', dataIndex: 'salePrice' },
            { title: '可售库存', dataIndex: 'sellableInventory' },
            { title: '超售上限', dataIndex: 'overbookLimit' },
            {
              title: '价格标签',
              dataIndex: 'priceTag',
              render: (value: string) => (value ? <Tag color="blue">{value}</Tag> : '-'),
            },
          ]}
        />
      )}

      {view === 'plans' && (
        <>
          <Table
            rowKey="id"
            dataSource={filteredPlans}
            style={{ marginBottom: 16 }}
            columns={[
              { title: '民宿', dataIndex: 'propertyName' },
              { title: '房型', dataIndex: 'roomTypeName' },
              { title: '营业日', dataIndex: 'bizDate' },
              { title: '销售价', dataIndex: 'salePrice' },
              { title: '可售库存', dataIndex: 'sellableInventory' },
              { title: '超售上限', dataIndex: 'overbookLimit' },
            ]}
          />
          <Table
            rowKey="id"
            dataSource={filteredRules}
            columns={[
              { title: '规则名称', dataIndex: 'ruleName' },
              { title: '规则类型', dataIndex: 'ruleType', render: (value: string) => dictText.priceRuleType(value) },
              { title: '日期范围', dataIndex: 'dateScope' },
              { title: '周掩码', dataIndex: 'weekdayMask' },
              { title: '调价方式', dataIndex: 'adjustType', render: (value: string) => dictText.adjustType(value) },
              { title: '调价值', dataIndex: 'adjustValue' },
              { title: '优先级', dataIndex: 'priorityNo' },
              {
                title: '启用',
                dataIndex: 'enabledFlag',
                render: (value: number) => (value === 1 ? '是' : '否'),
              },
            ]}
          />
        </>
      )}

      {view === 'inventory' && (
        <Table
          rowKey="id"
          dataSource={filteredInventories}
          columns={[
            { title: '民宿', dataIndex: 'propertyName' },
            { title: '房型', dataIndex: 'roomTypeName' },
            { title: '营业日', dataIndex: 'bizDate' },
            { title: '总库存', dataIndex: 'totalInventory' },
            { title: '占用库存', dataIndex: 'occupiedInventory' },
            {
              title: '可售库存',
              dataIndex: 'availableInventory',
              render: (value: number, row) => {
                if (row.warning) {
                  return <Tag color="red">{value} (预警)</Tag>
                }
                return value
              },
            },
          ]}
        />
      )}

      <Modal
        title="新增价格计划"
        open={planOpen}
        onOk={async () => {
          const values = await planForm.validateFields()
          await upsertPricePlan({
            ...values,
            bizDate: dayjs(values.bizDate).format('YYYY-MM-DD'),
          })
          message.success('价格计划已保存')
          setPlanOpen(false)
          planForm.resetFields()
          loadData()
        }}
        onCancel={() => setPlanOpen(false)}
      >
        <Form form={planForm} layout="vertical" initialValues={{ sellableInventory: 5, overbookLimit: 0 }}>
          <Form.Item name="propertyId" label="民宿" rules={[{ required: true }]}> 
            <Select options={properties.map((item) => ({ label: item.propertyName, value: item.id }))} />
          </Form.Item>
          <Form.Item name="roomTypeId" label="房型" rules={[{ required: true }]}> 
            <Select options={roomTypes.map((item) => ({ label: `${item.roomTypeName} (${item.propertyName})`, value: item.id }))} />
          </Form.Item>
          <Form.Item name="bizDate" label="营业日期" rules={[{ required: true }]}> 
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="salePrice" label="销售价" rules={[{ required: true }]}> 
            <InputNumber style={{ width: '100%' }} min={0} />
          </Form.Item>
          <Form.Item name="sellableInventory" label="可售库存" rules={[{ required: true }]}> 
            <InputNumber style={{ width: '100%' }} min={0} />
          </Form.Item>
          <Form.Item name="overbookLimit" label="超售上限">
            <InputNumber style={{ width: '100%' }} min={0} />
          </Form.Item>
          <Form.Item name="priceTag" label="价格标签">
            <Input placeholder="连住价 / 周末价 / 节假日价" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="新增价格规则"
        open={ruleOpen}
        onOk={async () => {
          const values = await ruleForm.validateFields()
          await createPriceRule(values)
          message.success('价格规则已创建')
          setRuleOpen(false)
          ruleForm.resetFields()
          loadData()
        }}
        onCancel={() => setRuleOpen(false)}
      >
        <Form form={ruleForm} layout="vertical" initialValues={{ adjustType: 'PERCENT', enabledFlag: 1, priorityNo: 100 }}>
          <Form.Item name="propertyId" label="民宿" rules={[{ required: true }]}>
            <Select options={properties.map((item) => ({ label: item.propertyName, value: item.id }))} />
          </Form.Item>
          <Form.Item name="ruleType" label="规则类型" rules={[{ required: true }]}>
            <Select
              options={[
                { label: '节假日', value: 'HOLIDAY' },
                { label: '周末', value: 'WEEKEND' },
                { label: '连住优惠', value: 'STAY_DISCOUNT' },
                { label: '钟点房', value: 'HOURLY_ROOM' },
                { label: '民宿场景', value: 'HOMESTAY_SCENE' },
              ]}
            />
          </Form.Item>
          <Form.Item name="ruleName" label="规则名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="dateScope" label="日期范围描述">
            <Input placeholder="如：2026-05-01~2026-05-05" />
          </Form.Item>
          <Form.Item name="weekdayMask" label="周掩码">
            <Input placeholder="如：SAT,SUN" />
          </Form.Item>
          <Form.Item name="adjustType" label="调价方式" rules={[{ required: true }]}>
            <Select options={[{ label: '比例%', value: 'PERCENT' }, { label: '固定金额', value: 'AMOUNT' }]} />
          </Form.Item>
          <Form.Item name="adjustValue" label="调价值" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="priorityNo" label="优先级" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} min={1} />
          </Form.Item>
          <Form.Item name="enabledFlag" label="是否启用" rules={[{ required: true }]}>
            <Select options={[{ label: '启用', value: 1 }, { label: '停用', value: 0 }]} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="库存调整"
        open={inventoryOpen}
        onOk={async () => {
          const values = await inventoryForm.validateFields()
          await adjustInventory({
            ...values,
            bizDate: dayjs(values.bizDate).format('YYYY-MM-DD'),
          })
          message.success('库存已调整')
          setInventoryOpen(false)
          inventoryForm.resetFields()
          loadData()
        }}
        onCancel={() => setInventoryOpen(false)}
      >
        <Form form={inventoryForm} layout="vertical" initialValues={{ occupiedDelta: 1 }}>
          <Form.Item name="propertyId" label="民宿" rules={[{ required: true }]}>
            <Select options={properties.map((item) => ({ label: item.propertyName, value: item.id }))} />
          </Form.Item>
          <Form.Item name="roomTypeId" label="房型" rules={[{ required: true }]}>
            <Select options={roomTypes.map((item) => ({ label: `${item.roomTypeName} (${item.propertyName})`, value: item.id }))} />
          </Form.Item>
          <Form.Item name="bizDate" label="营业日期" rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="occupiedDelta" label="占用变化（+增加占用，-释放占用）" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
