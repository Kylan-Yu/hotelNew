import { App, Form, Input, InputNumber, Modal, Select, Space, Table, Tabs, Typography } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { dictText } from '../constants/businessDict'
import { PermissionButton } from '../components/PermissionButton'
import { useDictOptions } from '../hooks/useDictOptions'
import {
  adjustMemberPoint,
  createCampaign,
  createCoupon,
  createMember,
  fetchCampaigns,
  fetchCoupons,
  fetchMemberPointLedgers,
  fetchMembers,
} from '../api/memberApi'
import { fetchProperties } from '../api/propertyApi'
import { DEFAULT_TABLE_PAGINATION } from '../constants/tablePagination'

interface MemberPageProps {
  view: 'members' | 'points' | 'coupons'
}

export function MemberPage({ view }: MemberPageProps) {
  const [members, setMembers] = useState<any[]>([])
  const [coupons, setCoupons] = useState<any[]>([])
  const [campaigns, setCampaigns] = useState<any[]>([])
  const [pointLedgers, setPointLedgers] = useState<any[]>([])
  const [properties, setProperties] = useState<any[]>([])
  const [keyword, setKeyword] = useState('')

  const [memberOpen, setMemberOpen] = useState(false)
  const [couponOpen, setCouponOpen] = useState(false)
  const [campaignOpen, setCampaignOpen] = useState(false)
  const [pointOpen, setPointOpen] = useState(false)

  const [memberForm] = Form.useForm<any>()
  const [couponForm] = Form.useForm<any>()
  const [campaignForm] = Form.useForm<any>()
  const [pointForm] = Form.useForm<any>()

  const { message } = App.useApp()
  const { options: genderOptions, labelMap: genderLabelMap } = useDictOptions('GENDER')

  const loadData = async () => {
    const [m, c, cp, pl, ps] = await Promise.all([
      fetchMembers(),
      fetchCoupons(),
      fetchCampaigns(),
      fetchMemberPointLedgers(),
      fetchProperties(),
    ])
    setMembers(m || [])
    setCoupons(c || [])
    setCampaigns(cp || [])
    setPointLedgers(pl || [])
    setProperties(ps || [])
  }

  useEffect(() => {
    loadData()
  }, [])

  const title = useMemo(() => {
    if (view === 'members') return '会员管理'
    if (view === 'points') return '积分管理'
    return '优惠券管理'
  }, [view])

  const keywordText = keyword.trim().toLowerCase()
  const matchKeyword = (item: any) => {
    if (!keywordText) return true
    return JSON.stringify(item).toLowerCase().includes(keywordText)
  }

  const filteredMembers = useMemo(() => members.filter(matchKeyword), [members, keywordText])
  const filteredPointLedgers = useMemo(() => pointLedgers.filter(matchKeyword), [pointLedgers, keywordText])
  const filteredCoupons = useMemo(() => coupons.filter(matchKeyword), [coupons, keywordText])
  const filteredCampaigns = useMemo(() => campaigns.filter(matchKeyword), [campaigns, keywordText])

  return (
    <div>
      <Typography.Title level={4} style={{ marginTop: 0 }}>
        {title}
      </Typography.Title>

      <Space style={{ marginBottom: 16 }} wrap>
        <Input.Search
          allowClear
          style={{ width: 320 }}
          placeholder="模糊搜索（会员/手机号/券编码/活动名称）"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />
        {view === 'members' && (
          <PermissionButton permission="member:write" type="primary" onClick={() => setMemberOpen(true)}>
            新建会员
          </PermissionButton>
        )}
        {view === 'points' && (
          <PermissionButton permission="member:write" type="primary" onClick={() => setPointOpen(true)}>
            积分调整
          </PermissionButton>
        )}
        {view === 'coupons' && (
          <>
            <PermissionButton permission="member:write" type="primary" onClick={() => setCouponOpen(true)}>
              新建优惠券
            </PermissionButton>
            <PermissionButton permission="member:write" onClick={() => setCampaignOpen(true)}>
              新建营销活动
            </PermissionButton>
          </>
        )}
      </Space>

      {view === 'members' && (
        <Table
          rowKey="id"
          dataSource={filteredMembers}
          pagination={DEFAULT_TABLE_PAGINATION}
          columns={[
            { title: '会员编号', dataIndex: 'memberNo', width: 150 },
            { title: '姓名', dataIndex: 'memberName', width: 120 },
            { title: '性别', dataIndex: 'gender', width: 120, render: (value: string) => genderLabelMap[value] || value || '-' },
            { title: '手机号', dataIndex: 'mobile', width: 140 },
            { title: '等级', dataIndex: 'levelCode', width: 90 },
            { title: '积分', dataIndex: 'pointBalance', width: 100 },
            { title: '状态', dataIndex: 'status', width: 100, render: (value: string) => dictText.memberStatus(value) },
            { title: '归属民宿', dataIndex: 'propertyName' },
          ]}
        />
      )}

      {view === 'points' && (
        <Table
          rowKey="id"
          dataSource={filteredPointLedgers}
          pagination={DEFAULT_TABLE_PAGINATION}
          columns={[
            { title: '流水ID', dataIndex: 'id', width: 90 },
            { title: '会员ID', dataIndex: 'memberId', width: 100 },
            { title: '积分变动', dataIndex: 'pointDelta', width: 100 },
            { title: '业务类型', dataIndex: 'bizType', width: 120 },
            { title: '业务单号', dataIndex: 'bizNo', width: 160 },
            { title: '备注', dataIndex: 'remark' },
            { title: '创建时间', dataIndex: 'createdAt', width: 180 },
          ]}
        />
      )}

      {view === 'coupons' && (
        <Tabs
          items={[
            {
              key: 'coupon',
              label: '优惠券模板',
              children: (
                <Table
                  rowKey="id"
                  dataSource={filteredCoupons}
                  pagination={DEFAULT_TABLE_PAGINATION}
                  columns={[
                    { title: '券编码', dataIndex: 'couponCode', width: 140 },
                    { title: '券名称', dataIndex: 'couponName', width: 180 },
                    { title: '券面值', dataIndex: 'amount', width: 120 },
                    { title: '使用门槛', dataIndex: 'threshold', width: 120 },
                    { title: '状态', dataIndex: 'status', width: 100, render: (value: string) => dictText.couponStatus(value) },
                    { title: '归属民宿', dataIndex: 'propertyName' },
                  ]}
                />
              ),
            },
            {
              key: 'campaign',
              label: '营销活动',
              children: (
                <Table
                  rowKey="id"
                  dataSource={filteredCampaigns}
                  pagination={DEFAULT_TABLE_PAGINATION}
                  columns={[
                    { title: '活动编码', dataIndex: 'campaignCode', width: 150 },
                    { title: '活动名称', dataIndex: 'campaignName', width: 180 },
                    { title: '活动类型', dataIndex: 'campaignType', width: 140 },
                    { title: '开始日期', dataIndex: 'startDate', width: 120 },
                    { title: '结束日期', dataIndex: 'endDate', width: 120 },
                    { title: '状态', dataIndex: 'status', width: 100, render: (value: string) => dictText.campaignStatus(value) },
                    { title: '归属民宿', dataIndex: 'propertyName' },
                  ]}
                />
              ),
            },
          ]}
        />
      )}

      <Modal
        title="新建会员"
        open={memberOpen}
        onOk={async () => {
          const values = await memberForm.validateFields()
          await createMember(values)
          message.success('会员创建成功')
          setMemberOpen(false)
          memberForm.resetFields()
          loadData()
        }}
        onCancel={() => setMemberOpen(false)}
      >
        <Form form={memberForm} layout="vertical" initialValues={{ levelCode: 1, gender: 'UNKNOWN' }}>
          <Form.Item name="propertyId" label="归属民宿" rules={[{ required: true }]}>
            <Select options={properties.map((item) => ({ label: item.propertyName, value: item.id }))} />
          </Form.Item>
          <Form.Item name="memberName" label="会员姓名" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="gender" label="性别">
            <Select options={genderOptions} />
          </Form.Item>
          <Form.Item name="mobile" label="手机号" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="levelCode" label="会员等级">
            <InputNumber min={1} style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="积分调整"
        open={pointOpen}
        onOk={async () => {
          const values = await pointForm.validateFields()
          await adjustMemberPoint(values)
          message.success('积分调整成功')
          setPointOpen(false)
          pointForm.resetFields()
          loadData()
        }}
        onCancel={() => setPointOpen(false)}
      >
        <Form form={pointForm} layout="vertical">
          <Form.Item name="memberId" label="会员" rules={[{ required: true }]}>
            <Select options={members.map((item) => ({ label: `${item.memberName}(${item.mobile})`, value: item.id }))} />
          </Form.Item>
          <Form.Item name="pointDelta" label="积分变动值" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="bizType" label="业务类型">
            <Input placeholder="MANUAL / ORDER / PROMOTION" />
          </Form.Item>
          <Form.Item name="bizNo" label="业务单号">
            <Input />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="新建优惠券"
        open={couponOpen}
        onOk={async () => {
          const values = await couponForm.validateFields()
          await createCoupon(values)
          message.success('优惠券创建成功')
          setCouponOpen(false)
          couponForm.resetFields()
          loadData()
        }}
        onCancel={() => setCouponOpen(false)}
      >
        <Form form={couponForm} layout="vertical">
          <Form.Item name="propertyId" label="归属民宿" rules={[{ required: true }]}>
            <Select options={properties.map((item) => ({ label: item.propertyName, value: item.id }))} />
          </Form.Item>
          <Form.Item name="couponCode" label="券编码" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="couponName" label="券名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="amount" label="券面值" rules={[{ required: true }]}>
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="threshold" label="使用门槛">
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="新建营销活动"
        open={campaignOpen}
        onOk={async () => {
          const values = await campaignForm.validateFields()
          await createCampaign(values)
          message.success('营销活动创建成功')
          setCampaignOpen(false)
          campaignForm.resetFields()
          loadData()
        }}
        onCancel={() => setCampaignOpen(false)}
      >
        <Form form={campaignForm} layout="vertical">
          <Form.Item name="propertyId" label="归属民宿" rules={[{ required: true }]}>
            <Select options={properties.map((item) => ({ label: item.propertyName, value: item.id }))} />
          </Form.Item>
          <Form.Item name="campaignCode" label="活动编码" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="campaignName" label="活动名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="campaignType" label="活动类型" rules={[{ required: true }]}>
            <Input placeholder="如：节日促销 / 连住优惠" />
          </Form.Item>
          <Form.Item name="startDate" label="开始日期">
            <Input placeholder="YYYY-MM-DD" />
          </Form.Item>
          <Form.Item name="endDate" label="结束日期">
            <Input placeholder="YYYY-MM-DD" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}






