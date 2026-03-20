import { Card, Input, Table, Tag } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { fetchInventories } from '../api/pricingApi'
import { DEFAULT_TABLE_PAGINATION } from '../constants/tablePagination'

export function OverbookWarningPage() {
  const [data, setData] = useState<any[]>([])
  const [keyword, setKeyword] = useState('')

  useEffect(() => {
    const load = async () => {
      const inventories = await fetchInventories()
      setData((inventories || []).filter((item) => item.warning))
    }
    load()
  }, [])

  const filteredData = useMemo(() => {
    const text = keyword.trim().toLowerCase()
    if (!text) return data
    return data.filter((item) => JSON.stringify(item).toLowerCase().includes(text))
  }, [data, keyword])

  return (
    <Card title="超售预警" extra={(
      <Input.Search
        allowClear
        placeholder="搜索民宿/房型/日期"
        value={keyword}
        onChange={(e) => setKeyword(e.target.value)}
        style={{ width: 260 }}
      />
    )}>
      <Table
        rowKey="id"
        dataSource={filteredData}
        pagination={DEFAULT_TABLE_PAGINATION}
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
