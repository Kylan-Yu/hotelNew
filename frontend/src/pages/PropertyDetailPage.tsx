import { Card, Descriptions, Empty } from 'antd'
import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { PropertyItem, fetchProperties } from '../api/propertyApi'

export function PropertyDetailPage() {
  const { id } = useParams()
  const [property, setProperty] = useState<PropertyItem | null>(null)

  useEffect(() => {
    const loadData = async () => {
      const list = await fetchProperties()
      const matched = list.find((item) => item.id === Number(id)) || null
      setProperty(matched)
    }
    loadData()
  }, [id])

  if (!property) {
    return <Empty description="未找到民宿" />
  }

  return (
    <Card title={`民宿详情 - ${property.propertyName}`}>
      <Descriptions bordered column={2}>
        <Descriptions.Item label="民宿编码">{property.propertyCode}</Descriptions.Item>
        <Descriptions.Item label="民宿名称">{property.propertyName}</Descriptions.Item>
        <Descriptions.Item label="集团">{property.groupName || '未设置'}</Descriptions.Item>
        <Descriptions.Item label="品牌">{property.brandName || '未设置'}</Descriptions.Item>
        <Descriptions.Item label="经营模式">{property.businessMode === 'HOMESTAY' ? '民宿模式' : '酒店模式'}</Descriptions.Item>
        <Descriptions.Item label="联系电话">{property.contactPhone || '-'}</Descriptions.Item>
        <Descriptions.Item label="城市">{property.city || '-'}</Descriptions.Item>
        <Descriptions.Item label="地址" span={2}>
          {property.address || '-'}
        </Descriptions.Item>
      </Descriptions>
    </Card>
  )
}
