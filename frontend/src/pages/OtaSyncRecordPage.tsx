import { Card, Select, Space, Table } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { dictText } from '../constants/businessDict'
import { fetchOtaSyncLogs } from '../api/otaApi'

export function OtaSyncRecordPage() {
  const [data, setData] = useState<any[]>([])
  const [channelCode, setChannelCode] = useState<string | undefined>()
  const [bizType, setBizType] = useState<string | undefined>()
  const [status, setStatus] = useState<string | undefined>()

  useEffect(() => {
    const load = async () => {
      const logs = await fetchOtaSyncLogs()
      setData(logs || [])
    }
    load()
  }, [])

  const filteredData = useMemo(() => {
    return data.filter((item) => {
      if (channelCode && item.channelCode !== channelCode) return false
      if (bizType && item.bizType !== bizType) return false
      if (status && item.syncStatus !== status) return false
      return true
    })
  }, [data, channelCode, bizType, status])

  return (
    <Card title="渠道同步记录">
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
          style={{ width: 140 }}
          placeholder="业务类型"
          value={bizType}
          options={[
            { label: '库存', value: 'INVENTORY' },
            { label: '价格', value: 'PRICE' },
            { label: '订单', value: 'ORDER' },
            { label: '回调', value: 'CALLBACK' },
          ]}
          onChange={setBizType}
        />
        <Select
          allowClear
          style={{ width: 140 }}
          placeholder="状态"
          value={status}
          options={[
            { label: '成功', value: 'SUCCESS' },
            { label: '失败', value: 'FAILED' },
            { label: '处理中', value: 'PENDING' },
          ]}
          onChange={setStatus}
        />
      </Space>

      <Table
        rowKey="id"
        dataSource={filteredData}
        columns={[
          { title: 'ID', dataIndex: 'id', width: 80 },
          { title: '民宿ID', dataIndex: 'propertyId', width: 100 },
          { title: '渠道', dataIndex: 'channelCode', width: 120, render: (value: string) => dictText.channelCode(value) },
          { title: '业务类型', dataIndex: 'bizType', width: 120 },
          { title: '业务标识', dataIndex: 'bizId', width: 200 },
          { title: '同步状态', dataIndex: 'syncStatus', width: 120, render: (value: string) => dictText.syncStatus(value) },
          { title: '创建时间', dataIndex: 'createdAt' },
        ]}
      />
    </Card>
  )
}
