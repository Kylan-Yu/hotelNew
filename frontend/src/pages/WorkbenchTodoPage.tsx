import { Card, Col, Row, Statistic } from 'antd'
import { useEffect, useState } from 'react'
import { fetchOrders, fetchReservations, fetchStays } from '../api/orderApi'

export function WorkbenchTodoPage() {
  const [stats, setStats] = useState({ pendingReservations: 0, pendingCheckin: 0, inHouse: 0, pendingCheckout: 0 })

  useEffect(() => {
    const load = async () => {
      const [reservations, orders, stays] = await Promise.all([fetchReservations(), fetchOrders(), fetchStays()])
      setStats({
        pendingReservations: (reservations || []).filter((item) => item.reservationStatus === 'PENDING_CONFIRM').length,
        pendingCheckin: (orders || []).filter((item) => ['CREATED', 'CONFIRMED'].includes(item.orderStatus)).length,
        inHouse: (stays || []).filter((item) => item.stayStatus === 'IN_HOUSE').length,
        pendingCheckout: (stays || []).filter((item) => item.stayStatus === 'PENDING_CHECKOUT').length,
      })
    }
    load()
  }, [])

  return (
    <Row gutter={16}>
      <Col span={6}>
        <Card>
          <Statistic title="待确认预订" value={stats.pendingReservations} />
        </Card>
      </Col>
      <Col span={6}>
        <Card>
          <Statistic title="待办理入住" value={stats.pendingCheckin} />
        </Card>
      </Col>
      <Col span={6}>
        <Card>
          <Statistic title="当前在住" value={stats.inHouse} />
        </Card>
      </Col>
      <Col span={6}>
        <Card>
          <Statistic title="待办理退房" value={stats.pendingCheckout} />
        </Card>
      </Col>
    </Row>
  )
}
