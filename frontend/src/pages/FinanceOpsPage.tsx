import { App, Form, Input, InputNumber, Modal, Select, Space, Table, Typography } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import {
  createBill,
  createInvoice,
  createRefund,
  dailyReportExportUrl,
  fetchBillDetails,
  fetchDailyReports,
  fetchInvoices,
  fetchPropertyStats,
  fetchRefunds,
  propertyStatsExportUrl,
} from '../api/financeApi'
import { fetchPayments } from '../api/orderApi'
import { fetchProperties } from '../api/propertyApi'
import { dictText } from '../constants/businessDict'
import { PermissionButton } from '../components/PermissionButton'

interface FinanceOpsPageProps {
  view: 'receipts' | 'refunds' | 'bills' | 'invoices' | 'reports'
}

export function FinanceOpsPage({ view }: FinanceOpsPageProps) {
  const [payments, setPayments] = useState<any[]>([])
  const [refunds, setRefunds] = useState<any[]>([])
  const [bills, setBills] = useState<any[]>([])
  const [invoices, setInvoices] = useState<any[]>([])
  const [dailyReports, setDailyReports] = useState<any[]>([])
  const [propertyStats, setPropertyStats] = useState<any[]>([])
  const [properties, setProperties] = useState<any[]>([])

  const [filterPropertyId, setFilterPropertyId] = useState<number | undefined>()

  const [refundOpen, setRefundOpen] = useState(false)
  const [billOpen, setBillOpen] = useState(false)
  const [invoiceOpen, setInvoiceOpen] = useState(false)
  const [refundForm] = Form.useForm<any>()
  const [billForm] = Form.useForm<any>()
  const [invoiceForm] = Form.useForm<any>()

  const { message } = App.useApp()

  const title = useMemo(() => {
    if (view === 'receipts') return '收款记录'
    if (view === 'refunds') return '退款记录'
    if (view === 'bills') return '账单明细'
    if (view === 'invoices') return '发票管理'
    return '经营报表'
  }, [view])

  const loadData = async () => {
    const [paymentList, refundList, billList, invoiceList, dailyList, statList, propertyList] = await Promise.all([
      fetchPayments(),
      fetchRefunds(),
      fetchBillDetails(),
      fetchInvoices(),
      fetchDailyReports(),
      fetchPropertyStats(),
      fetchProperties(),
    ])

    setPayments(paymentList || [])
    setRefunds(refundList || [])
    setBills(billList || [])
    setInvoices(invoiceList || [])
    setDailyReports(dailyList || [])
    setPropertyStats(statList || [])
    setProperties(propertyList || [])
  }

  useEffect(() => {
    loadData()
  }, [])

  const propertyNameById = useMemo(() => {
    const map: Record<number, string> = {}
    ;(properties || []).forEach((item) => {
      map[item.id] = item.propertyName
    })
    return map
  }, [properties])

  const getPropertyName = (propertyId?: number) => {
    if (!propertyId) return '-'
    return propertyNameById[propertyId] || `民宿${propertyId}`
  }

  const filteredPayments = useMemo(() => {
    if (!filterPropertyId) return payments
    return payments.filter((item) => item.propertyId === filterPropertyId)
  }, [payments, filterPropertyId])

  const filteredRefunds = useMemo(() => {
    if (!filterPropertyId) return refunds
    return refunds.filter((item) => item.propertyId === filterPropertyId)
  }, [refunds, filterPropertyId])

  const filteredBills = useMemo(() => {
    if (!filterPropertyId) return bills
    return bills.filter((item) => item.propertyId === filterPropertyId)
  }, [bills, filterPropertyId])

  const filteredInvoices = useMemo(() => {
    if (!filterPropertyId) return invoices
    return invoices.filter((item) => item.propertyId === filterPropertyId)
  }, [invoices, filterPropertyId])

  const filteredDailyReports = useMemo(() => {
    if (!filterPropertyId) return dailyReports
    return dailyReports.filter((item) => item.propertyId === filterPropertyId)
  }, [dailyReports, filterPropertyId])

  const filteredPropertyStats = useMemo(() => {
    if (!filterPropertyId) return propertyStats
    return propertyStats.filter((item) => item.propertyId === filterPropertyId)
  }, [propertyStats, filterPropertyId])

  const download = async (url: string, filename: string) => {
    const token = localStorage.getItem('hms_access_token')
    const response = await fetch(url, {
      headers: { Authorization: `Bearer ${token}` },
    })
    const blob = await response.blob()
    const targetUrl = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = targetUrl
    link.download = filename
    link.click()
    window.URL.revokeObjectURL(targetUrl)
  }

  return (
    <div>
      <Typography.Title level={4} style={{ marginTop: 0 }}>
        {title}
      </Typography.Title>

      <Space style={{ marginBottom: 16 }} wrap>
        <Select
          allowClear
          style={{ width: 240 }}
          placeholder="筛选民宿"
          value={filterPropertyId}
          options={properties.map((item) => ({ label: item.propertyName, value: item.id }))}
          onChange={setFilterPropertyId}
        />
        {view === 'refunds' && (
          <PermissionButton permission="finance:write" type="primary" onClick={() => setRefundOpen(true)}>
            登记退款
          </PermissionButton>
        )}
        {view === 'bills' && (
          <PermissionButton permission="finance:write" type="primary" onClick={() => setBillOpen(true)}>
            新增账单
          </PermissionButton>
        )}
        {view === 'invoices' && (
          <PermissionButton permission="finance:write" type="primary" onClick={() => setInvoiceOpen(true)}>
            申请发票
          </PermissionButton>
        )}
        {view === 'reports' && (
          <>
            <PermissionButton permission="report:export" onClick={() => download(dailyReportExportUrl(), 'daily-report.xlsx')}>
              导出日报
            </PermissionButton>
            <PermissionButton permission="report:export" onClick={() => download(propertyStatsExportUrl(), 'property-stats.xlsx')}>
              导出经营统计
            </PermissionButton>
          </>
        )}
      </Space>

      {view === 'receipts' && (
        <Table
          rowKey="id"
          dataSource={filteredPayments}
          columns={[
            { title: '流水ID', dataIndex: 'id', width: 90 },
            { title: '民宿', dataIndex: 'propertyId', width: 140, render: (value: number) => getPropertyName(value) },
            { title: '订单ID', dataIndex: 'orderId', width: 100 },
            { title: '入住记录ID', dataIndex: 'stayRecordId', width: 120 },
            { title: '支付类型', dataIndex: 'paymentType', width: 120, render: (value: string) => dictText.paymentType(value) },
            { title: '支付方式', dataIndex: 'paymentMethod', width: 120, render: (value: string) => dictText.paymentMethod(value) },
            { title: '金额', dataIndex: 'amount', width: 100 },
            { title: '状态', dataIndex: 'paymentStatus', width: 120, render: (value: string) => dictText.paymentStatus(value) },
            { title: '外部流水号', dataIndex: 'externalTradeNo' },
          ]}
        />
      )}

      {view === 'refunds' && (
        <Table
          rowKey="id"
          dataSource={filteredRefunds}
          columns={[
            { title: '退款单ID', dataIndex: 'id', width: 100 },
            { title: '民宿', dataIndex: 'propertyId', width: 140, render: (value: number) => getPropertyName(value) },
            { title: '支付ID', dataIndex: 'paymentId', width: 110 },
            { title: '订单ID', dataIndex: 'orderId', width: 110 },
            { title: '退款金额', dataIndex: 'refundAmount', width: 120 },
            { title: '退款原因', dataIndex: 'refundReason' },
            { title: '退款状态', dataIndex: 'refundStatus', width: 120, render: (value: string) => dictText.refundStatus(value) },
            { title: '创建时间', dataIndex: 'createdAt', width: 180 },
          ]}
        />
      )}

      {view === 'bills' && (
        <Table
          rowKey="id"
          dataSource={filteredBills}
          columns={[
            { title: '账单ID', dataIndex: 'id', width: 90 },
            { title: '民宿', dataIndex: 'propertyId', width: 140, render: (value: number) => getPropertyName(value) },
            { title: '订单ID', dataIndex: 'orderId', width: 100 },
            { title: '账单类型', dataIndex: 'billType', width: 130, render: (value: string) => dictText.billType(value) },
            { title: '账单项', dataIndex: 'billItem', width: 180 },
            { title: '金额', dataIndex: 'amount', width: 100 },
            { title: '创建时间', dataIndex: 'createdAt', width: 180 },
          ]}
        />
      )}

      {view === 'invoices' && (
        <Table
          rowKey="id"
          dataSource={filteredInvoices}
          columns={[
            { title: '发票ID', dataIndex: 'id', width: 90 },
            { title: '订单ID', dataIndex: 'orderId', width: 100 },
            { title: '民宿', dataIndex: 'propertyId', width: 140, render: (value: number) => getPropertyName(value) },
            { title: '发票类型', dataIndex: 'invoiceType', width: 120, render: (value: string) => (value === 'ELECTRONIC' ? '电子发票' : '纸质发票') },
            { title: '发票抬头', dataIndex: 'invoiceTitle', width: 220 },
            { title: '税号', dataIndex: 'taxNo', width: 160 },
            { title: '金额', dataIndex: 'amount', width: 100 },
            { title: '状态', dataIndex: 'invoiceStatus', width: 120, render: (value: string) => dictText.invoiceStatus(value) },
            { title: '创建时间', dataIndex: 'createdAt', width: 180 },
          ]}
        />
      )}

      {view === 'reports' && (
        <>
          <Table
            rowKey="propertyId"
            dataSource={filteredDailyReports}
            style={{ marginBottom: 16 }}
            columns={[
              { title: '民宿', dataIndex: 'propertyName' },
              { title: '订单数', dataIndex: 'orderCount' },
              { title: '收款金额', dataIndex: 'paymentAmount' },
              { title: '退款金额', dataIndex: 'refundAmount' },
              { title: '净收入', dataIndex: 'netRevenue' },
            ]}
          />
          <Table
            rowKey="propertyId"
            dataSource={filteredPropertyStats}
            columns={[
              { title: '民宿', dataIndex: 'propertyName' },
              { title: '房间总数', dataIndex: 'roomCount' },
              { title: '在住房间', dataIndex: 'occupiedRoomCount' },
              { title: '入住率', dataIndex: 'occupancyRate' },
            ]}
          />
        </>
      )}

      <Modal
        title="登记退款"
        open={refundOpen}
        onOk={async () => {
          const values = await refundForm.validateFields()
          await createRefund(values)
          message.success('退款记录已创建')
          setRefundOpen(false)
          refundForm.resetFields()
          loadData()
        }}
        onCancel={() => setRefundOpen(false)}
      >
        <Form form={refundForm} layout="vertical">
          <Form.Item name="paymentId" label="支付ID" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="orderId" label="订单ID" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="refundAmount" label="退款金额" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} min={0.01} />
          </Form.Item>
          <Form.Item name="refundReason" label="退款原因">
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="新增账单"
        open={billOpen}
        onOk={async () => {
          const values = await billForm.validateFields()
          await createBill(values)
          message.success('账单明细已创建')
          setBillOpen(false)
          billForm.resetFields()
          loadData()
        }}
        onCancel={() => setBillOpen(false)}
      >
        <Form form={billForm} layout="vertical" initialValues={{ billType: 'ROOM_FEE' }}>
          <Form.Item name="propertyId" label="民宿" rules={[{ required: true }]}>
            <Select options={properties.map((item) => ({ label: item.propertyName, value: item.id }))} />
          </Form.Item>
          <Form.Item name="orderId" label="订单ID" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="billType" label="账单类型" rules={[{ required: true }]}>
            <Select
              options={[
                { label: '房费', value: 'ROOM_FEE' },
                { label: '押金', value: 'DEPOSIT' },
                { label: '服务费', value: 'SERVICE_FEE' },
                { label: '其他', value: 'OTHER' },
              ]}
            />
          </Form.Item>
          <Form.Item name="billItem" label="账单项" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="amount" label="金额" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} min={0.01} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="申请发票"
        open={invoiceOpen}
        onOk={async () => {
          const values = await invoiceForm.validateFields()
          await createInvoice(values)
          message.success('发票申请已提交')
          setInvoiceOpen(false)
          invoiceForm.resetFields()
          loadData()
        }}
        onCancel={() => setInvoiceOpen(false)}
      >
        <Form form={invoiceForm} layout="vertical" initialValues={{ invoiceType: 'ELECTRONIC' }}>
          <Form.Item name="orderId" label="订单ID" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="propertyId" label="民宿" rules={[{ required: true }]}>
            <Select options={properties.map((item) => ({ label: item.propertyName, value: item.id }))} />
          </Form.Item>
          <Form.Item name="invoiceType" label="发票类型" rules={[{ required: true }]}>
            <Select options={[{ label: '电子发票', value: 'ELECTRONIC' }, { label: '纸质发票', value: 'PAPER' }]} />
          </Form.Item>
          <Form.Item name="invoiceTitle" label="发票抬头" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="taxNo" label="税号">
            <Input />
          </Form.Item>
          <Form.Item name="amount" label="开票金额" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} min={0.01} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
