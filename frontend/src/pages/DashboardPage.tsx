import { Card, Col, Row, Statistic, Table, Space } from 'antd'
import { useEffect, useState } from 'react'
import { dailyReportExportUrl } from '../api/financeApi'
import { PermissionButton } from '../components/PermissionButton'
import { fetchDashboard, fetchDailyReports, fetchPropertyStats } from '../api/financeApi'

export function DashboardPage() {
  const [dashboard, setDashboard] = useState<any>({})
  const [dailyReports, setDailyReports] = useState<any[]>([])
  const [propertyStats, setPropertyStats] = useState<any[]>([])

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

  return (
    <div>
      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col span={6}><Card><Statistic title="今日订单" value={dashboard.todayOrderCount || 0} /></Card></Col>
        <Col span={6}><Card><Statistic title="今日净收入" value={dashboard.todayRevenue || 0} precision={2} /></Card></Col>
        <Col span={6}><Card><Statistic title="在住间数" value={dashboard.inHouseCount || 0} /></Card></Col>
        <Col span={6}><Card><Statistic title="库存预警门店" value={dashboard.warningInventoryCount || 0} /></Card></Col>
      </Row>

      <Card title="每日经营报表" style={{ marginBottom: 16 }} extra={<Space><PermissionButton permission="report:export" onClick={() => download(dailyReportExportUrl(), 'daily-report.xlsx')}>导出日报</PermissionButton></Space>}>
        <Table rowKey="propertyId" dataSource={dailyReports} pagination={false}
          columns={[
            { title: '门店', dataIndex: 'propertyName' },
            { title: '订单数', dataIndex: 'orderCount' },
            { title: '收款', dataIndex: 'paymentAmount' },
            { title: '退款', dataIndex: 'refundAmount' },
            { title: '净收入', dataIndex: 'netRevenue' },
          ]}
        />
      </Card>

      <Card title="门店经营统计" extra={<PermissionButton permission="report:export" onClick={() => download('http://localhost:8080/api/finance-ops/reports/property-stats/export', 'property-stats.xlsx')}>导出统计</PermissionButton>}>
        <Table rowKey="propertyId" dataSource={propertyStats} pagination={false}
          columns={[
            { title: '门店', dataIndex: 'propertyName' },
            { title: '房间数', dataIndex: 'roomCount' },
            { title: '在住房间', dataIndex: 'occupiedRoomCount' },
            { title: '入住率', dataIndex: 'occupancyRate' },
          ]}
        />
      </Card>
    </div>
  )
}
