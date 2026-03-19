import { Card, Select, Space, Table, Tag } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { dictText } from '../constants/businessDict'
import { fetchOtaCallbackLogs } from '../api/otaApi'

export function OtaCallbackRecordPage() {
  const [data, setData] = useState<any[]>([])
  const [channelCode, setChannelCode] = useState<string | undefined>()
  const [eventType, setEventType] = useState<string | undefined>()
  const [status, setStatus] = useState<string | undefined>()

  useEffect(() => {
    const load = async () => {
      const logs = await fetchOtaCallbackLogs()
      setData(logs || [])
    }
    load()
  }, [])

  const filteredData = useMemo(() => {
    return data.filter((item) => {
      if (channelCode && item.channelCode !== channelCode) return false
      if (eventType && item.eventType !== eventType) return false
      if (status && item.callbackStatus !== status) return false
      return true
    })
  }, [data, channelCode, eventType, status])

  return (
    <Card title="渠道回调记录">
      <Space style={{ marginBottom: 16 }} wrap>
        <Select
          allowClear
          style={{ width: 140 }}
          placeholder="渠道"
          value={channelCode}
          options={[
            { label: '抖音', value: 'DOUYIN' },
            { label: '美团', value: 'MEITUAN' },
            { label: '携程', value: 'CTRIP' },
          ]}
          onChange={setChannelCode}
        />
        <Select
          allowClear
          style={{ width: 200 }}
          placeholder="事件类型"
          value={eventType}
          options={[
            { label: '订单创建', value: 'ORDER_CREATED' },
            { label: '订单取消', value: 'ORDER_CANCELED' },
            { label: '库存变更', value: 'INVENTORY_CHANGED' },
            { label: '价格变更', value: 'PRICE_CHANGED' },
          ]}
          onChange={setEventType}
        />
        <Select
          allowClear
          style={{ width: 140 }}
          placeholder="状态"
          value={status}
          options={[
            { label: '成功', value: 'SUCCESS' },
            { label: '失败', value: 'FAILED' },
            { label: '待处理', value: 'PENDING' },
          ]}
          onChange={setStatus}
        />
      </Space>

      <Table
        rowKey="id"
        dataSource={filteredData}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 80 },
          { title: '渠道', dataIndex: 'channelCode', width: 120, render: (value: string) => dictText.channelCode(value) },
          { title: '事件', dataIndex: 'eventType', width: 180, render: (value: string) => dictText.otaEventType(value) },
          { title: '外部流水号', dataIndex: 'externalRequestNo', width: 180 },
          { title: '回调状态', dataIndex: 'callbackStatus', width: 120, render: (value: string) => dictText.callbackStatus(value) },
          {
            title: '是否已消费',
            dataIndex: 'processedFlag',
            width: 120,
            render: (value: string) => (value === 'Y' ? <Tag color="green">已消费</Tag> : <Tag color="orange">未消费</Tag>),
          },
          { title: '处理消息', dataIndex: 'message', width: 220 },
          { title: '创建时间', dataIndex: 'createdAt' },
        ]}
      />
    </Card>
  )
}
