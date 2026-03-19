export type TimelineLang = 'zh-CN' | 'en-US'

const zhCN = {
  nodes: {
    RESERVED: '\u9884\u7559',
    PENDING_CHECKIN: '\u5f85\u5165\u4f4f',
    CHECKED_IN: '\u5df2\u5165\u4f4f',
    EXTENDED: '\u7eed\u4f4f',
    CHANGE_ROOM: '\u6362\u623f',
    PENDING_CHECKOUT: '\u5f85\u9000\u623f',
    CHECKED_OUT: '\u5df2\u9000\u623f',
    CANCELED: '\u5df2\u53d6\u6d88',
  },
  actions: {
    CREATE: '\u521b\u5efa',
    WAITING: '\u7b49\u5f85',
    CHECK_IN: '\u5165\u4f4f',
    EXTEND_STAY: '\u7eed\u4f4f',
    CHANGE_ROOM: '\u6362\u623f',
    CHECKOUT: '\u9000\u623f',
    CANCEL: '\u53d6\u6d88',
  },
  remarks: {
    ORDER_CREATED: '\u8ba2\u5355\u521b\u5efa',
    WAITING_CHECKIN: '\u7b49\u5f85\u5230\u5e97\u529e\u7406\u5165\u4f4f',
    CHECKIN_COMPLETED: '\u529e\u7406\u5165\u4f4f\u5b8c\u6210',
    STAY_EXTENDED: '\u5df2\u529e\u7406\u7eed\u4f4f',
    ROOM_CHANGED: '\u5df2\u5b8c\u6210\u6362\u623f',
    PENDING_CHECKOUT: '\u5f53\u524d\u5904\u4e8e\u5f85\u9000\u623f',
    CHECKOUT_COMPLETED: '\u5df2\u5b8c\u6210\u9000\u623f',
    ORDER_CANCELED: '\u8ba2\u5355\u5df2\u53d6\u6d88',
  },
}

const enUS = {
  nodes: {
    RESERVED: 'Reserved',
    PENDING_CHECKIN: 'Pending Check-in',
    CHECKED_IN: 'Checked-in',
    EXTENDED: 'Extended',
    CHANGE_ROOM: 'Changed Room',
    PENDING_CHECKOUT: 'Pending Check-out',
    CHECKED_OUT: 'Checked-out',
    CANCELED: 'Canceled',
  },
  actions: {
    CREATE: 'Create',
    WAITING: 'Waiting',
    CHECK_IN: 'Check-in',
    EXTEND_STAY: 'Extend Stay',
    CHANGE_ROOM: 'Change Room',
    CHECKOUT: 'Check-out',
    CANCEL: 'Cancel',
  },
  remarks: {
    ORDER_CREATED: 'Order created',
    WAITING_CHECKIN: 'Waiting for check-in',
    CHECKIN_COMPLETED: 'Check-in completed',
    STAY_EXTENDED: 'Stay extended',
    ROOM_CHANGED: 'Room changed',
    PENDING_CHECKOUT: 'Waiting for checkout',
    CHECKOUT_COMPLETED: 'Checkout completed',
    ORDER_CANCELED: 'Order canceled',
  },
}

export function resolveTimelineLang(): TimelineLang {
  const lang = (navigator.language || '').toLowerCase()
  return lang.startsWith('zh') ? 'zh-CN' : 'en-US'
}

export function timelineText(code: string | undefined, type: 'nodes' | 'actions' | 'remarks', lang?: TimelineLang): string {
  if (!code) {
    return '-'
  }
  const dict = (lang || resolveTimelineLang()) === 'zh-CN' ? zhCN : enUS
  return (dict[type] as Record<string, string>)[code] || code
}
