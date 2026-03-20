import { Card, Descriptions, Empty } from 'antd'
import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { useParams } from 'react-router-dom'
import { PropertyItem, fetchProperties } from '../api/propertyApi'
import { useDictOptions } from '../hooks/useDictOptions'

export function PropertyDetailPage() {
  const { id } = useParams()
  const [property, setProperty] = useState<PropertyItem | null>(null)
  const { t } = useTranslation()
  const { labelMap: businessModeLabelMap } = useDictOptions('BUSINESS_MODE')

  useEffect(() => {
    const loadData = async () => {
      const list = await fetchProperties()
      const matched = list.find((item) => item.id === Number(id)) || null
      setProperty(matched)
    }
    loadData()
  }, [id])

  if (!property) {
    return <Empty description={t('property.notFound')} />
  }

  return (
    <Card title={t('property.detailTitle', { name: property.propertyName })}>
      <Descriptions bordered column={2}>
        <Descriptions.Item label={t('property.detailCode')}>{property.propertyCode}</Descriptions.Item>
        <Descriptions.Item label={t('property.detailName')}>{property.propertyName}</Descriptions.Item>
        <Descriptions.Item label={t('property.detailBusinessMode')}>
          {businessModeLabelMap[property.businessMode] || property.businessMode}
        </Descriptions.Item>
        <Descriptions.Item label={t('property.detailPhone')}>{property.contactPhone || '-'}</Descriptions.Item>
        <Descriptions.Item label={t('property.detailCity')}>{property.city || '-'}</Descriptions.Item>
        <Descriptions.Item label={t('property.detailAddress')} span={2}>{property.address || '-'}</Descriptions.Item>
      </Descriptions>
    </Card>
  )
}

