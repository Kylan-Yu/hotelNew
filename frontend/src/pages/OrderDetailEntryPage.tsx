import { Button, Card, Input, Space, Table } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { dictText } from '../constants/businessDict'
import { fetchOrders } from '../api/orderApi'
import { DEFAULT_TABLE_PAGINATION } from '../constants/tablePagination'

export function OrderDetailEntryPage() {
  const [data, setData] = useState<any[]>([])
  const [keyword, setKeyword] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    const load = async () => {
      const orders = await fetchOrders()
      setData(orders || [])
    }
    load()
  }, [])

  const filteredData = useMemo(() => {
    if (!keyword.trim()) {
      return data
    }
    const searchText = keyword.toLowerCase()
    return data.filter((item) => {
      return (
        String(item.orderNo || '').toLowerCase().includes(searchText) ||
        String(item.propertyName || '').toLowerCase().includes(searchText) ||
        String(item.guestName || '').toLowerCase().includes(searchText) ||
        String(item.guestMobile || '').toLowerCase().includes(searchText)
      )
    })
  }, [data, keyword])

  return (
    <Card title="订单详情入口">
      <Space style={{ marginBottom: 16 }}>
        <Input.Search
          allowClear
          placeholder="按订单号/民宿/住客/手机号搜索"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          style={{ width: 360 }}
        />
      </Space>
      <Table
        rowKey="id"
        dataSource={filteredData}
        pagination={DEFAULT_TABLE_PAGINATION}
        columns={[
          { title: '订单号', dataIndex: 'orderNo', width: 170 },
          { title: '民宿', dataIndex: 'propertyName', width: 170 },
          { title: '住客', dataIndex: 'guestName', width: 120 },
          { title: '手机号', dataIndex: 'guestMobile', width: 130 },
          { title: '入住日期', dataIndex: 'checkInDate', width: 120 },
          { title: '离店日期', dataIndex: 'checkOutDate', width: 120 },
          { title: '状态', dataIndex: 'orderStatus', width: 120, render: (value: string) => dictText.orderStatus(value) },
          {
            title: '操作',
            width: 120,
            render: (_, row) => (
              <Button type="link" onClick={() => navigate(`/orders/detail/${row.id}`)}>
                查看详情
              </Button>
            ),
          },
        ]}
      />
    </Card>
  )
}
