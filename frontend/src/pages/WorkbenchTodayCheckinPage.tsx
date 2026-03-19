import { Card, Table } from 'antd'
import dayjs from 'dayjs'
import { useEffect, useState } from 'react'
import { fetchOrders } from '../api/orderApi'
import { dictText } from '../constants/businessDict'

export function WorkbenchTodayCheckinPage() {
  const [data, setData] = useState<any[]>([])

  useEffect(() => {
    const load = async () => {
      const orders = await fetchOrders()
      const today = dayjs().format('YYYY-MM-DD')
      setData((orders || []).filter((item) => item.checkInDate === today))
    }
    load()
  }, [])

  return (
    <Card title="今日入住">
      <Table
        rowKey="id"
        dataSource={data}
        pagination={false}
        columns={[
          { title: '订单号', dataIndex: 'orderNo' },
          { title: '民宿', dataIndex: 'propertyName' },
          { title: '住客', dataIndex: 'guestName' },
          { title: '入住日期', dataIndex: 'checkInDate' },
          { title: '订单状态', dataIndex: 'orderStatus', render: (value: string) => dictText.orderStatus(value) },
        ]}
      />
    </Card>
  )
}
