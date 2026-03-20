import {
  App,
  Button,
  DatePicker,
  Form,
  Input,
  InputNumber,
  Modal,
  Select,
  Space,
  Table,
  Typography,
} from 'antd'
import dayjs from 'dayjs'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { PermissionButton } from '../components/PermissionButton'
import { dictText } from '../constants/businessDict'
import { DEFAULT_TABLE_PAGINATION } from '../constants/tablePagination'
import {
  cancelOrder,
  changeRoom,
  checkIn,
  checkout,
  createPayment,
  createReservation,
  extendStay,
  fetchOrders,
  fetchReservations,
  fetchStays,
  generateOrder,
  orderExportUrl,
  registerGuest,
} from '../api/orderApi'
import { fetchProperties } from '../api/propertyApi'
import { fetchRooms } from '../api/roomApi'
import { fetchRoomTypes } from '../api/roomTypeApi'

interface OrderPageProps {
  view: 'reservations' | 'checkin' | 'inhouse' | 'checkout'
}

export function OrderPage({ view }: OrderPageProps) {
  const [reservations, setReservations] = useState<any[]>([])
  const [orders, setOrders] = useState<any[]>([])
  const [stays, setStays] = useState<any[]>([])
  const [properties, setProperties] = useState<any[]>([])
  const [roomTypes, setRoomTypes] = useState<any[]>([])
  const [rooms, setRooms] = useState<any[]>([])
  const [reservationKeyword, setReservationKeyword] = useState('')
  const [reservationPropertyId, setReservationPropertyId] = useState<number | undefined>()
  const [reservationRoomTypeId, setReservationRoomTypeId] = useState<number | undefined>()

  const [reservationOpen, setReservationOpen] = useState(false)
  const [checkInOpen, setCheckInOpen] = useState(false)
  const [extendOpen, setExtendOpen] = useState(false)
  const [changeRoomOpen, setChangeRoomOpen] = useState(false)
  const [guestOpen, setGuestOpen] = useState(false)
  const [paymentOpen, setPaymentOpen] = useState(false)

  const [currentOrder, setCurrentOrder] = useState<any>(null)
  const [currentStay, setCurrentStay] = useState<any>(null)

  const [reservationForm] = Form.useForm<any>()
  const [checkInForm] = Form.useForm<any>()
  const [extendForm] = Form.useForm<any>()
  const [changeRoomForm] = Form.useForm<any>()
  const [guestForm] = Form.useForm<any>()
  const [paymentForm] = Form.useForm<any>()

  const navigate = useNavigate()
  const { message } = App.useApp()

  const loadData = async () => {
    const [r, o, s, p, rt, rm] = await Promise.all([
      fetchReservations(),
      fetchOrders(),
      fetchStays(),
      fetchProperties(),
      fetchRoomTypes(),
      fetchRooms(),
    ])
    setReservations(r || [])
    setOrders(o || [])
    setStays(s || [])
    setProperties(p || [])
    setRoomTypes(rt || [])
    setRooms(rm || [])
  }

  useEffect(() => {
    loadData()
  }, [])

  const filteredOrders = useMemo(() => {
    if (view === 'checkin') {
      return orders.filter((item) => !['CANCELED', 'CHECKED_OUT'].includes(item.orderStatus))
    }
    return orders
  }, [orders, view])

  const filteredStays = useMemo(() => {
    if (view === 'inhouse') {
      return stays.filter((item) => ['IN_HOUSE', 'PENDING_CHECKOUT'].includes(item.stayStatus))
    }
    if (view === 'checkout') {
      return stays.filter((item) => item.stayStatus !== 'CHECKED_OUT')
    }
    return stays
  }, [stays, view])

  const filteredReservations = useMemo(() => {
    const keyword = reservationKeyword.trim().toLowerCase()
    return reservations.filter((item) => {
      if (reservationPropertyId && item.propertyId !== reservationPropertyId) {
        return false
      }
      if (reservationRoomTypeId && item.roomTypeId !== reservationRoomTypeId) {
        return false
      }
      if (
        keyword &&
        !(
          String(item.propertyName || '').toLowerCase().includes(keyword) ||
          String(item.roomTypeName || '').toLowerCase().includes(keyword) ||
          String(item.contactMobile || '').toLowerCase().includes(keyword) ||
          String(item.contactName || '').toLowerCase().includes(keyword)
        )
      ) {
        return false
      }
      return true
    })
  }, [reservationKeyword, reservationPropertyId, reservationRoomTypeId, reservations])

  const viewTitle = useMemo(() => {
    if (view === 'reservations') return '预订订单'
    if (view === 'checkin') return '入住登记'
    if (view === 'inhouse') return '在住管理'
    return '退房管理'
  }, [view])

  const downloadOrders = async () => {
    const token = localStorage.getItem('hms_access_token')
    const response = await fetch(orderExportUrl(), { headers: { Authorization: `Bearer ${token}` } })
    const blob = await response.blob()
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '订单列表.xlsx'
    link.click()
    window.URL.revokeObjectURL(url)
  }

  const openCheckIn = (order: any) => {
    setCurrentOrder(order)
    checkInForm.setFieldsValue({
      orderId: order.id,
      stayType: 'INDIVIDUAL',
      checkInDate: dayjs(order.checkInDate),
      checkOutDate: dayjs(order.checkOutDate),
    })
    setCheckInOpen(true)
  }

  const openStayActions = (stay: any) => {
    setCurrentStay(stay)
    extendForm.setFieldsValue({
      stayRecordId: stay.id,
      newCheckOutDate: dayjs(stay.checkOutDate).add(1, 'day'),
    })
    changeRoomForm.setFieldsValue({ stayRecordId: stay.id })
    guestForm.setFieldsValue({ orderId: stay.orderId, primaryGuest: false })
    paymentForm.setFieldsValue({ orderId: stay.orderId, stayRecordId: stay.id, paymentType: 'DEPOSIT', paymentMethod: 'CASH' })
  }

  return (
    <div>
      <Typography.Title level={4} style={{ marginTop: 0 }}>
        {viewTitle}
      </Typography.Title>

      <Space style={{ marginBottom: 16 }} wrap>
        {view === 'reservations' && (
          <PermissionButton permission="order:write" type="primary" onClick={() => setReservationOpen(true)}>
            新建预订
          </PermissionButton>
        )}
        {view === 'reservations' && (
          <>
            <Input
              allowClear
              placeholder="搜索民宿 / 房型 / 电话 / 联系人"
              style={{ width: 320 }}
              value={reservationKeyword}
              onChange={(event) => setReservationKeyword(event.target.value)}
            />
            <Select
              allowClear
              placeholder="筛选民宿"
              style={{ width: 180 }}
              value={reservationPropertyId}
              onChange={(value) => setReservationPropertyId(value)}
              options={properties.map((item) => ({ label: item.propertyName, value: item.id }))}
            />
            <Select
              allowClear
              placeholder="筛选房型"
              style={{ width: 180 }}
              value={reservationRoomTypeId}
              onChange={(value) => setReservationRoomTypeId(value)}
              options={roomTypes.map((item) => ({ label: item.roomTypeName, value: item.id }))}
            />
          </>
        )}
        <PermissionButton permission="order:export" onClick={downloadOrders}>
          导出订单
        </PermissionButton>
      </Space>

      {view === 'reservations' && (
        <Table
          rowKey="id"
          dataSource={filteredReservations}
          pagination={DEFAULT_TABLE_PAGINATION}
          columns={[
            { title: '预订号', dataIndex: 'reservationNo', width: 170 },
            { title: '民宿', dataIndex: 'propertyName', width: 160 },
            { title: '房型', dataIndex: 'roomTypeName', width: 140 },
            { title: '联系人', dataIndex: 'contactName', width: 120 },
            { title: '电话', dataIndex: 'contactMobile', width: 130 },
            { title: '入住', dataIndex: 'checkInDate', width: 110 },
            { title: '离店', dataIndex: 'checkOutDate', width: 110 },
            {
              title: '状态',
              dataIndex: 'reservationStatus',
              width: 120,
              render: (value: string) => dictText.reservationStatus(value),
            },
            {
              title: '操作',
              width: 160,
              render: (_, row) => (
                <PermissionButton
                  permission="order:write"
                  type="link"
                  onClick={async () => {
                    await generateOrder(row.id)
                    message.success('已生成订单')
                    loadData()
                  }}
                >
                  生成订单
                </PermissionButton>
              ),
            },
          ]}
        />
      )}

      {view === 'checkin' && (
        <Table
          rowKey="id"
          dataSource={filteredOrders}
          pagination={DEFAULT_TABLE_PAGINATION}
          columns={[
            { title: '订单号', dataIndex: 'orderNo', width: 170 },
            { title: '民宿', dataIndex: 'propertyName', width: 150 },
            { title: '住客', dataIndex: 'guestName', width: 120 },
            { title: '手机号', dataIndex: 'guestMobile', width: 130 },
            { title: '入住日期', dataIndex: 'checkInDate', width: 120 },
            { title: '离店日期', dataIndex: 'checkOutDate', width: 120 },
            {
              title: '状态',
              dataIndex: 'orderStatus',
              width: 120,
              render: (value: string) => dictText.orderStatus(value),
            },
            {
              title: '操作',
              width: 260,
              render: (_, row) => (
                <Space>
                  <PermissionButton permission="checkin:write" type="link" onClick={() => openCheckIn(row)}>
                    办理入住
                  </PermissionButton>
                  <PermissionButton
                    permission="order:write"
                    type="link"
                    onClick={async () => {
                      await cancelOrder({ orderId: row.id })
                      message.success('订单已取消')
                      loadData()
                    }}
                  >
                    取消订单
                  </PermissionButton>
                  <Button type="link" onClick={() => navigate(`/orders/detail/${row.id}`)}>
                    详情
                  </Button>
                </Space>
              ),
            },
          ]}
        />
      )}

      {view === 'inhouse' && (
        <Table
          rowKey="id"
          dataSource={filteredStays}
          pagination={DEFAULT_TABLE_PAGINATION}
          columns={[
            { title: '入住单号', dataIndex: 'stayNo', width: 170 },
            { title: '订单号', dataIndex: 'orderNo', width: 170 },
            { title: '民宿', dataIndex: 'propertyName', width: 150 },
            { title: '房间号', dataIndex: 'roomNo', width: 100 },
            {
              title: '入住类型',
              dataIndex: 'stayType',
              width: 120,
              render: (value: string) => dictText.stayType(value),
            },
            {
              title: '状态',
              dataIndex: 'stayStatus',
              width: 130,
              render: (value: string) => dictText.stayStatus(value),
            },
            {
              title: '操作',
              width: 380,
              render: (_, row) => {
                return (
                  <Space size={4} wrap>
                    <PermissionButton
                      permission="checkin:write"
                      type="link"
                      onClick={() => {
                        openStayActions(row)
                        setExtendOpen(true)
                      }}
                    >
                      续住
                    </PermissionButton>
                    <PermissionButton
                      permission="checkin:write"
                      type="link"
                      onClick={() => {
                        openStayActions(row)
                        setChangeRoomOpen(true)
                      }}
                    >
                      换房
                    </PermissionButton>
                    <PermissionButton
                      permission="checkin:write"
                      type="link"
                      onClick={() => {
                        openStayActions(row)
                        setGuestOpen(true)
                      }}
                    >
                      登记入住人
                    </PermissionButton>
                    <PermissionButton
                      permission="finance:write"
                      type="link"
                      onClick={() => {
                        openStayActions(row)
                        setPaymentOpen(true)
                      }}
                    >
                      登记押金/收款
                    </PermissionButton>
                    <Button type="link" onClick={() => navigate(`/orders/detail/${row.orderId}`)}>
                      详情
                    </Button>
                  </Space>
                )
              },
            },
          ]}
        />
      )}

      {view === 'checkout' && (
        <Table
          rowKey="id"
          dataSource={filteredStays}
          pagination={DEFAULT_TABLE_PAGINATION}
          columns={[
            { title: '入住单号', dataIndex: 'stayNo', width: 170 },
            { title: '订单号', dataIndex: 'orderNo', width: 170 },
            { title: '民宿', dataIndex: 'propertyName', width: 150 },
            { title: '房间号', dataIndex: 'roomNo', width: 100 },
            {
              title: '状态',
              dataIndex: 'stayStatus',
              width: 120,
              render: (value: string) => dictText.stayStatus(value),
            },
            { title: '计划离店', dataIndex: 'checkOutDate', width: 120 },
            {
              title: '操作',
              width: 180,
              render: (_, row) => (
                <PermissionButton
                  permission="checkin:write"
                  type="link"
                  onClick={async () => {
                    await checkout({ stayRecordId: row.id, remark: '前台退房' })
                    message.success('退房完成')
                    loadData()
                  }}
                >
                  办理退房
                </PermissionButton>
              ),
            },
          ]}
        />
      )}

      <Modal
        title="新建预订"
        open={reservationOpen}
        onOk={async () => {
          const values = await reservationForm.validateFields()
          await createReservation({
            ...values,
            checkInDate: dayjs(values.checkInDate).format('YYYY-MM-DD'),
            checkOutDate: dayjs(values.checkOutDate).format('YYYY-MM-DD'),
          })
          message.success('预订已创建')
          setReservationOpen(false)
          reservationForm.resetFields()
          loadData()
        }}
        onCancel={() => setReservationOpen(false)}
      >
        <Form form={reservationForm} layout="vertical" initialValues={{ guestCount: 1, channelCode: 'DIRECT' }}>
          <Form.Item name="propertyId" label="民宿" rules={[{ required: true }]}>
            <Select options={properties.map((item) => ({ label: item.propertyName, value: item.id }))} />
          </Form.Item>
          <Form.Item name="roomTypeId" label="房型" rules={[{ required: true }]}>
            <Select options={roomTypes.map((item) => ({ label: `${item.roomTypeName} (${item.propertyName})`, value: item.id }))} />
          </Form.Item>
          <Form.Item name="contactName" label="联系人" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="contactMobile" label="联系电话" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="guestCount" label="入住人数" rules={[{ required: true }]}>
            <InputNumber min={1} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="checkInDate" label="入住日期" rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="checkOutDate" label="离店日期" rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="estimatedAmount" label="预估金额">
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="channelCode" label="来源渠道">
            <Select
              options={[
                { label: '直销', value: 'DIRECT' },
                { label: '抖音', value: 'DOUYIN' },
                { label: '美团', value: 'MEITUAN' },
                { label: '携程', value: 'CTRIP' },
              ]}
            />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`办理入住 ${currentOrder?.orderNo || ''}`}
        open={checkInOpen}
        onOk={async () => {
          const values = await checkInForm.validateFields()
          await checkIn({
            ...values,
            checkInDate: dayjs(values.checkInDate).format('YYYY-MM-DD'),
            checkOutDate: dayjs(values.checkOutDate).format('YYYY-MM-DD'),
          })
          message.success('入住办理完成')
          setCheckInOpen(false)
          checkInForm.resetFields()
          loadData()
        }}
        onCancel={() => setCheckInOpen(false)}
      >
        <Form form={checkInForm} layout="vertical">
          <Form.Item name="orderId" label="订单ID" rules={[{ required: true }]}>
            <InputNumber disabled style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="roomId" label="房间" rules={[{ required: true }]}>
            <Select options={rooms.map((item) => ({ label: `${item.roomNo} (${item.propertyName})`, value: item.id }))} />
          </Form.Item>
          <Form.Item name="stayType" label="入住类型" rules={[{ required: true }]}>
            <Select
              options={[
                { label: '散客', value: 'INDIVIDUAL' },
                { label: '团队', value: 'TEAM' },
              ]}
            />
          </Form.Item>
          <Form.Item name="checkInDate" label="入住日期" rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="checkOutDate" label="离店日期" rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`续住办理 ${currentStay?.stayNo || ''}`}
        open={extendOpen}
        onOk={async () => {
          const values = await extendForm.validateFields()
          await extendStay({
            stayRecordId: values.stayRecordId,
            newCheckOutDate: dayjs(values.newCheckOutDate).format('YYYY-MM-DD'),
          })
          message.success('续住成功')
          setExtendOpen(false)
          loadData()
        }}
        onCancel={() => setExtendOpen(false)}
      >
        <Form form={extendForm} layout="vertical">
          <Form.Item name="stayRecordId" label="入住记录ID" rules={[{ required: true }]}>
            <InputNumber disabled style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="newCheckOutDate" label="新离店日期" rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`换房办理 ${currentStay?.stayNo || ''}`}
        open={changeRoomOpen}
        onOk={async () => {
          const values = await changeRoomForm.validateFields()
          await changeRoom(values)
          message.success('换房成功')
          setChangeRoomOpen(false)
          loadData()
        }}
        onCancel={() => setChangeRoomOpen(false)}
      >
        <Form form={changeRoomForm} layout="vertical">
          <Form.Item name="stayRecordId" label="入住记录ID" rules={[{ required: true }]}>
            <InputNumber disabled style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="newRoomId" label="目标房间" rules={[{ required: true }]}>
            <Select options={rooms.map((item) => ({ label: `${item.roomNo} (${item.propertyName})`, value: item.id }))} />
          </Form.Item>
          <Form.Item name="reason" label="换房原因">
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`登记入住人 ${currentStay?.stayNo || ''}`}
        open={guestOpen}
        onOk={async () => {
          const values = await guestForm.validateFields()
          await registerGuest(values)
          message.success('入住人登记完成')
          setGuestOpen(false)
        }}
        onCancel={() => setGuestOpen(false)}
      >
        <Form form={guestForm} layout="vertical" initialValues={{ certificateType: 'ID_CARD' }}>
          <Form.Item name="orderId" label="订单ID" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="guestName" label="入住人姓名" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="guestMobile" label="手机号" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="certificateType" label="证件类型">
            <Select
              options={[
                { label: '身份证', value: 'ID_CARD' },
                { label: '护照', value: 'PASSPORT' },
                { label: '其他', value: 'OTHER' },
              ]}
            />
          </Form.Item>
          <Form.Item name="certificateNo" label="证件号">
            <Input />
          </Form.Item>
          <Form.Item name="primaryGuest" label="是否主入住人">
            <Select
              options={[
                { label: '是', value: true },
                { label: '否', value: false },
              ]}
            />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`登记押金/收款 ${currentStay?.stayNo || ''}`}
        open={paymentOpen}
        onOk={async () => {
          const values = await paymentForm.validateFields()
          await createPayment(values)
          message.success('支付流水已登记')
          setPaymentOpen(false)
        }}
        onCancel={() => setPaymentOpen(false)}
      >
        <Form form={paymentForm} layout="vertical">
          <Form.Item name="orderId" label="订单ID" rules={[{ required: true }]}>
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="stayRecordId" label="入住记录ID">
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="paymentType" label="支付类型" rules={[{ required: true }]}>
            <Select
              options={[
                { label: '押金', value: 'DEPOSIT' },
                { label: '房费', value: 'ROOM_FEE' },
                { label: '杂费', value: 'MISC' },
              ]}
            />
          </Form.Item>
          <Form.Item name="paymentMethod" label="支付方式" rules={[{ required: true }]}>
            <Select
              options={[
                { label: '现金', value: 'CASH' },
                { label: '微信', value: 'WECHAT' },
                { label: '支付宝', value: 'ALIPAY' },
                { label: '银行卡', value: 'CARD' },
              ]}
            />
          </Form.Item>
          <Form.Item name="amount" label="金额" rules={[{ required: true }]}>
            <InputNumber min={0.01} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="externalTradeNo" label="外部流水号">
            <Input />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
