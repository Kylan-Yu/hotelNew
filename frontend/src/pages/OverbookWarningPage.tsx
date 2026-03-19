import { Card, Table, Tag } from 'antd'
import { useEffect, useState } from 'react'
import { fetchInventories } from '../api/pricingApi'

export function OverbookWarningPage() {
  const [data, setData] = useState<any[]>([])

  useEffect(() => {
    const load = async () => {
      const inventories = await fetchInventories()
      setData((inventories || []).filter((item) => item.warning))
    }
    load()
  }, [])

  return (
    <Card title="超售预警">
      <Table
        rowKey="id"
        dataSource={data}
        columns={[
          { title: '民宿', dataIndex: 'propertyName' },
          { title: '房型', dataIndex: 'roomTypeName' },
          { title: '营业日', dataIndex: 'bizDate' },
          { title: '可售库存', dataIndex: 'availableInventory' },
          { title: '预警', render: () => <Tag color="red">超售风险</Tag> },
        ]}
      />
    </Card>
  )
}
