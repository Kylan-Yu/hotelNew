import { Card, Col, Row, Space, Statistic, Table } from 'antd'
import { useEffect, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { dailyReportExportUrl, fetchDailyReports, fetchDashboard, fetchPropertyStats, propertyStatsExportUrl } from '../api/financeApi'
import { PermissionButton } from '../components/PermissionButton'
import { DEFAULT_TABLE_PAGINATION } from '../constants/tablePagination'

export function DashboardPage() {
  const [dashboard, setDashboard] = useState<any>({})
  const [dailyReports, setDailyReports] = useState<any[]>([])
  const [propertyStats, setPropertyStats] = useState<any[]>([])
  const { t, i18n } = useTranslation()

  const download = async (url: string, filename: string) => {
    const token = localStorage.getItem('hms_access_token')
    const response = await fetch(url, { headers: { Authorization: `Bearer ${token}` } })
    const blob = await response.blob()
    const obj = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = obj
    link.download = filename
    link.click()
    window.URL.revokeObjectURL(obj)
  }

  useEffect(() => {
    const load = async () => {
      const [d, dr, ps] = await Promise.all([fetchDashboard(), fetchDailyReports(), fetchPropertyStats()])
      setDashboard(d || {})
      setDailyReports(dr || [])
      setPropertyStats(ps || [])
    }
    load()
  }, [])

  const dailyFilename = i18n.language === 'zh-CN' ? 'daily-report.xlsx' : 'daily-report.xlsx'
  const propertyFilename = i18n.language === 'zh-CN' ? 'property-stats.xlsx' : 'property-stats.xlsx'

  return (
    <div>
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}><Card><Statistic title={t('dashboard.metricTodayOrders')} value={dashboard.todayOrderCount || 0} /></Card></Col>
        <Col span={6}><Card><Statistic title={t('dashboard.metricTodayRevenue')} value={dashboard.todayRevenue || 0} precision={2} /></Card></Col>
        <Col span={6}><Card><Statistic title={t('dashboard.metricInHouse')} value={dashboard.inHouseCount || 0} /></Card></Col>
        <Col span={6}><Card><Statistic title={t('dashboard.metricWarnings')} value={dashboard.warningInventoryCount || 0} /></Card></Col>
      </Row>

      <Card
        title={t('dashboard.dailyReportTitle')}
        style={{ marginBottom: 16 }}
        extra={(
          <Space>
            <PermissionButton permission="report:export" onClick={() => download(dailyReportExportUrl(), dailyFilename)}>
              {t('dashboard.dailyReportExport')}
            </PermissionButton>
          </Space>
        )}
      >
        <Table
          rowKey="propertyId"
          dataSource={dailyReports}
          pagination={DEFAULT_TABLE_PAGINATION}
          columns={[
            { title: t('dashboard.colProperty'), dataIndex: 'propertyName' },
            { title: t('dashboard.colOrderCount'), dataIndex: 'orderCount' },
            { title: t('dashboard.colPaymentAmount'), dataIndex: 'paymentAmount' },
            { title: t('dashboard.colRefundAmount'), dataIndex: 'refundAmount' },
            { title: t('dashboard.colNetRevenue'), dataIndex: 'netRevenue' },
          ]}
        />
      </Card>

      <Card
        title={t('dashboard.propertyStatsTitle')}
        extra={(
          <PermissionButton permission="report:export" onClick={() => download(propertyStatsExportUrl(), propertyFilename)}>
            {t('dashboard.propertyStatsExport')}
          </PermissionButton>
        )}
      >
        <Table
          rowKey="propertyId"
          dataSource={propertyStats}
          pagination={DEFAULT_TABLE_PAGINATION}
          columns={[
            { title: t('dashboard.colProperty'), dataIndex: 'propertyName' },
            { title: t('dashboard.colRoomCount'), dataIndex: 'roomCount' },
            { title: t('dashboard.colOccupiedRoomCount'), dataIndex: 'occupiedRoomCount' },
            { title: t('dashboard.colOccupancyRate'), dataIndex: 'occupancyRate' },
          ]}
        />
      </Card>
    </div>
  )
}
