const withFallback = (value?: string, map?: Record<string, string>) => {
  if (!value) {
    return '-'
  }
  if (!map) {
    return value
  }
  return map[value] || value
}

const RESERVATION_STATUS: Record<string, string> = {
  PENDING_CONFIRM: '待确认',
  CONFIRMED: '已确认',
  ORDER_GENERATED: '已生成订单',
  CANCELED: '已取消',
}

const ORDER_STATUS: Record<string, string> = {
  CREATED: '已创建',
  CONFIRMED: '已确认',
  CHECKED_IN: '已入住',
  CHECKED_OUT: '已退房',
  CANCELED: '已取消',
}

const STAY_STATUS: Record<string, string> = {
  IN_HOUSE: '在住',
  PENDING_CHECKOUT: '待退房',
  CHECKED_OUT: '已退房',
  CANCELED: '已取消',
}

const STAY_TYPE: Record<string, string> = {
  INDIVIDUAL: '散客',
  TEAM: '团队',
}

const CHANNEL_CODE: Record<string, string> = {
  DIRECT: '直销',
  DOUYIN: '抖音',
  MEITUAN: '美团',
  CTRIP: '携程',
}

const PAYMENT_TYPE: Record<string, string> = {
  DEPOSIT: '押金',
  ROOM_FEE: '房费',
  MISC: '杂费',
}

const PAYMENT_METHOD: Record<string, string> = {
  CASH: '现金',
  WECHAT: '微信',
  ALIPAY: '支付宝',
  CARD: '银行卡',
}

const PAYMENT_STATUS: Record<string, string> = {
  SUCCESS: '成功',
  PENDING: '处理中',
  FAILED: '失败',
}

const PRICE_RULE_TYPE: Record<string, string> = {
  HOLIDAY: '节假日',
  WEEKEND: '周末',
  STAY_DISCOUNT: '连住优惠',
  HOURLY_ROOM: '钟点房',
  HOMESTAY_SCENE: '民宿场景',
}

const ADJUST_TYPE: Record<string, string> = {
  PERCENT: '比例',
  AMOUNT: '固定金额',
}

const SYNC_STATUS: Record<string, string> = {
  SUCCESS: '成功',
  FAILED: '失败',
  PENDING: '处理中',
}

const CALLBACK_STATUS: Record<string, string> = {
  SUCCESS: '成功',
  FAILED: '失败',
  PENDING: '待处理',
}

const OTA_EVENT_TYPE: Record<string, string> = {
  ORDER_CREATED: '订单创建',
  ORDER_CANCELED: '订单取消',
  INVENTORY_CHANGED: '库存变更',
  PRICE_CHANGED: '价格变更',
}

const MEMBER_STATUS: Record<string, string> = {
  ACTIVE: '有效',
  DISABLED: '停用',
}

const COUPON_STATUS: Record<string, string> = {
  ACTIVE: '有效',
  DISABLED: '停用',
  EXPIRED: '已过期',
}

const CAMPAIGN_STATUS: Record<string, string> = {
  ACTIVE: '进行中',
  DISABLED: '停用',
  ENDED: '已结束',
}

const INVOICE_STATUS: Record<string, string> = {
  PENDING: '待开票',
  ISSUED: '已开票',
  CANCELED: '已作废',
}

const REFUND_STATUS: Record<string, string> = {
  PENDING: '待退款',
  SUCCESS: '退款成功',
  FAILED: '退款失败',
}

const HOUSEKEEPING_STATUS: Record<string, string> = {
  TODO: '待处理',
  DOING: '进行中',
  DONE: '已完成',
}

const MAINTENANCE_STATUS: Record<string, string> = {
  OPEN: '待处理',
  IN_PROGRESS: '处理中',
  CLOSED: '已关闭',
}

const ROOM_STATUS: Record<string, string> = {
  VACANT_CLEAN: '空净',
  OCCUPIED: '在住',
  VACANT_DIRTY: '待清扫',
  MAINTENANCE: '维修',
  LOCKED: '锁房',
}

const BILL_TYPE: Record<string, string> = {
  ROOM_FEE: '房费',
  DEPOSIT: '押金',
  SERVICE_FEE: '服务费',
  OTHER: '其他',
}

const CERTIFICATE_TYPE: Record<string, string> = {
  ID_CARD: '身份证',
  PASSPORT: '护照',
  OTHER: '其他',
}

export const dictText = {
  reservationStatus: (value?: string) => withFallback(value, RESERVATION_STATUS),
  orderStatus: (value?: string) => withFallback(value, ORDER_STATUS),
  stayStatus: (value?: string) => withFallback(value, STAY_STATUS),
  stayType: (value?: string) => withFallback(value, STAY_TYPE),
  channelCode: (value?: string) => withFallback(value, CHANNEL_CODE),
  paymentType: (value?: string) => withFallback(value, PAYMENT_TYPE),
  paymentMethod: (value?: string) => withFallback(value, PAYMENT_METHOD),
  paymentStatus: (value?: string) => withFallback(value, PAYMENT_STATUS),
  priceRuleType: (value?: string) => withFallback(value, PRICE_RULE_TYPE),
  adjustType: (value?: string) => withFallback(value, ADJUST_TYPE),
  syncStatus: (value?: string) => withFallback(value, SYNC_STATUS),
  callbackStatus: (value?: string) => withFallback(value, CALLBACK_STATUS),
  otaEventType: (value?: string) => withFallback(value, OTA_EVENT_TYPE),
  memberStatus: (value?: string) => withFallback(value, MEMBER_STATUS),
  couponStatus: (value?: string) => withFallback(value, COUPON_STATUS),
  campaignStatus: (value?: string) => withFallback(value, CAMPAIGN_STATUS),
  invoiceStatus: (value?: string) => withFallback(value, INVOICE_STATUS),
  refundStatus: (value?: string) => withFallback(value, REFUND_STATUS),
  housekeepingStatus: (value?: string) => withFallback(value, HOUSEKEEPING_STATUS),
  maintenanceStatus: (value?: string) => withFallback(value, MAINTENANCE_STATUS),
  roomStatus: (value?: string) => withFallback(value, ROOM_STATUS),
  billType: (value?: string) => withFallback(value, BILL_TYPE),
  certificateType: (value?: string) => withFallback(value, CERTIFICATE_TYPE),
}
