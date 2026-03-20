import { Card, Descriptions, Empty, Table, Tabs, Timeline, Typography } from 'antd'
import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { fetchOrderDetail, fetchOrderTimeline } from '../api/orderApi'
import { dictText } from '../constants/businessDict'
import { resolveTimelineLang, timelineText } from '../constants/roomTimelineI18n'
import { DEFAULT_TABLE_PAGINATION } from '../constants/tablePagination'

export function OrderDetailPage() {
  const { id } = useParams()
  const [detail, setDetail] = useState<any>(null)
  const lang = resolveTimelineLang()

  useEffect(() => {
    const load = async () => {
      if (!id) return
      const orderId = Number(id)
      const data = await fetchOrderDetail(orderId)
      const timeline = await fetchOrderTimeline(orderId)
      data.roomStatusTimeline = timeline || data.roomStatusTimeline || []
      setDetail(data)
    }
    load()
  }, [id])

  if (!detail || !detail.order) {
    return <Empty description="未找到订单" />
  }

  return (
    <div>
      <Card title={`订单详情 - ${detail.order.orderNo}`} style={{ marginBottom: 16 }}>
        <Descriptions bordered column={2}>
          <Descriptions.Item label="订单号">{detail.order.orderNo}</Descriptions.Item>
          <Descriptions.Item label="订单状态">{dictText.orderStatus(detail.order.orderStatus)}</Descriptions.Item>
          <Descriptions.Item label="民宿">{detail.order.propertyName}</Descriptions.Item>
          <Descriptions.Item label="房型">{detail.order.roomTypeName}</Descriptions.Item>
          <Descriptions.Item label="主住客">{detail.order.guestName}</Descriptions.Item>
          <Descriptions.Item label="联系电话">{detail.order.guestMobile}</Descriptions.Item>
          <Descriptions.Item label="入住日期">{detail.order.checkInDate}</Descriptions.Item>
          <Descriptions.Item label="离店日期">{detail.order.checkOutDate}</Descriptions.Item>
          <Descriptions.Item label="订单金额">{detail.order.totalAmount}</Descriptions.Item>
        </Descriptions>
      </Card>

      <Tabs
        items={[
          {
            key: 'timeline',
            label: '房态变更时间线',
            children: (
              <Card size="small" title="房态节点">
                <Timeline
                  items={(detail.roomStatusTimeline || []).map((item: any) => ({
                    color:
                      item.nodeCode === 'CHECKED_OUT'
                        ? 'green'
                        : item.nodeCode === 'CANCELED'
                          ? 'red'
                          : 'blue',
                    children: (
                      <div>
                        <Typography.Text strong>{timelineText(item.nodeCode, 'nodes', lang)}</Typography.Text>
                        <div>操作类型: {timelineText(item.actionCode, 'actions', lang)}</div>
                        <div>操作时间: {item.operationTime || '-'}</div>
                        <div>操作人: {item.operator || '-'}</div>
                        <div>关联房间: {item.roomNo || '-'}</div>
                        <div>备注类型: {timelineText(item.remarkCode, 'remarks', lang)}</div>
                        <div>备注内容: {item.remarkText || '-'}</div>
                      </div>
                    ),
                  }))}
                />
              </Card>
            ),
          },
          {
            key: 'guest',
            label: '入住人信息',
            children: (
              <Table
                rowKey="id"
                dataSource={detail.guests || []}
                pagination={DEFAULT_TABLE_PAGINATION}
                columns={[
                  { title: '姓名', dataIndex: 'guestName' },
                  { title: '手机号', dataIndex: 'guestMobile' },
                  {
                    title: '证件类型',
                    dataIndex: 'certificateType',
                    render: (value: string) => dictText.certificateType(value),
                  },
                  { title: '证件号', dataIndex: 'certificateNo' },
                  {
                    title: '主入住人',
                    dataIndex: 'isPrimary',
                    render: (value: any) => (value ? '是' : '否'),
                  },
                ]}
              />
            ),
          },
          {
            key: 'stay',
            label: '房间入住记录',
            children: (
              <Table
                rowKey="id"
                dataSource={detail.stays || []}
                pagination={DEFAULT_TABLE_PAGINATION}
                columns={[
                  { title: '入住单号', dataIndex: 'stayNo' },
                  { title: '房间号', dataIndex: 'roomNo' },
                  {
                    title: '入住类型',
                    dataIndex: 'stayType',
                    render: (value: string) => dictText.stayType(value),
                  },
                  {
                    title: '状态',
                    dataIndex: 'stayStatus',
                    render: (value: string) => dictText.stayStatus(value),
                  },
                  { title: '实际入住时间', dataIndex: 'actualCheckInTime' },
                  { title: '实际退房时间', dataIndex: 'actualCheckOutTime' },
                ]}
              />
            ),
          },
          {
            key: 'payment',
            label: '支付与押金流水',
            children: (
              <Table
                rowKey="id"
                dataSource={detail.payments || []}
                pagination={DEFAULT_TABLE_PAGINATION}
                columns={[
                  { title: '类型', dataIndex: 'paymentType', render: (value: string) => dictText.paymentType(value) },
                  { title: '方式', dataIndex: 'paymentMethod', render: (value: string) => dictText.paymentMethod(value) },
                  { title: '金额', dataIndex: 'amount' },
                  { title: '状态', dataIndex: 'paymentStatus', render: (value: string) => dictText.paymentStatus(value) },
                  { title: '外部流水号', dataIndex: 'externalTradeNo' },
                ]}
              />
            ),
          },
          {
            key: 'audit',
            label: '操作记录',
            children: (
              <>
                <Card size="small" title="审计日志" style={{ marginBottom: 12 }}>
                  <Table
                    rowKey="id"
                    dataSource={detail.auditLogs || []}
                    pagination={DEFAULT_TABLE_PAGINATION}
                    columns={[
                      { title: '动作', dataIndex: 'actionType' },
                      { title: '内容', dataIndex: 'content' },
                      { title: '操作人', dataIndex: 'operator' },
                      { title: '时间', dataIndex: 'createdAt' },
                    ]}
                  />
                </Card>
                <Card size="small" title="系统操作日志">
                  <Table
                    rowKey="id"
                    dataSource={detail.operationLogs || []}
                    pagination={DEFAULT_TABLE_PAGINATION}
                    columns={[
                      { title: '操作', dataIndex: 'operation' },
                      { title: '请求路径', dataIndex: 'requestUri' },
                      { title: '操作人', dataIndex: 'operator' },
                      {
                        title: '结果',
                        dataIndex: 'successFlag',
                        render: (value: string) => (value === 'Y' ? '成功' : '失败'),
                      },
                      { title: '时间', dataIndex: 'createdAt' },
                    ]}
                  />
                </Card>
              </>
            ),
          },
        ]}
      />
    </div>
  )
}




