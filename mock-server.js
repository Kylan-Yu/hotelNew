
const express = require('express');
const cors = require('cors');

const app = express();
const port = 8080;
app.use(cors({
  origin: ['http://localhost:5173', 'http://127.0.0.1:5173'],
  methods: ['GET', 'POST', 'PUT', 'PATCH', 'DELETE', 'OPTIONS'],
  allowedHeaders: ['Content-Type', 'Authorization'],
  credentials: true,
}));
app.use(express.json());

const now = () => new Date().toISOString().slice(0, 19).replace('T', ' ');
const today = () => new Date().toISOString().slice(0, 10);
const plusDays = (n) => {
  const d = new Date();
  d.setDate(d.getDate() + n);
  return d.toISOString().slice(0, 10);
};
const ok = (res, data = null) => res.json({ code: 200, message: 'success', data });
const clone = (obj) => JSON.parse(JSON.stringify(obj));
const idGen = { property: 2, roomType: 2, room: 2, reservation: 1, order: 1, stay: 1, payment: 1, refund: 1, bill: 1, invoice: 1, task: 1, ticket: 1, map: 1, sync: 1, member: 1, point: 1, pref: 1, coupon: 1, campaign: 1, op: 2, audit: 2, timeline: 2, rule: 1, plan: 1, inventory: 1, sysUser: 1, sysRole: 1 };
const next = (k) => ++idGen[k];

const groups = [{ id: 1, groupCode: 'ORG001', groupName: '自在旅居运营中心', status: 1 }];
const brands = [{ id: 1, groupId: 1, brandCode: 'BR001', brandName: '静栖里', status: 1 }];
const properties = [
  { id: 1, groupId: 1, groupName: '自在旅居运营中心', brandId: 1, brandName: '静栖里', propertyCode: 'HS_SH_001', propertyName: '静栖里·外滩店', businessMode: 'HOMESTAY', contactPhone: '021-12345678', city: '上海', address: '黄浦区中山东二路188号', status: 1 },
  { id: 2, groupId: null, groupName: null, brandId: null, brandName: null, propertyCode: 'HS_HZ_001', propertyName: '西湖云舍', businessMode: 'HOMESTAY', contactPhone: '0571-88886666', city: '杭州', address: '西湖区龙井路66号', status: 1 },
];
const roomTypes = [
  { id: 1, propertyId: 1, propertyName: '静栖里·外滩店', roomTypeCode: 'DLX', roomTypeName: '豪华大床房', maxGuestCount: 2, bedType: '大床', basePrice: 388, status: 1 },
  { id: 2, propertyId: 2, propertyName: '西湖云舍', roomTypeCode: 'VIEW', roomTypeName: '湖景大床房', maxGuestCount: 2, bedType: '大床', basePrice: 458, status: 1 },
];
const rooms = [
  { id: 1, propertyId: 1, propertyName: '静栖里·外滩店', roomTypeId: 1, roomTypeName: '豪华大床房', roomNo: '1201', floorNo: '12', status: 'VACANT_CLEAN' },
  { id: 2, propertyId: 2, propertyName: '西湖云舍', roomTypeId: 2, roomTypeName: '湖景大床房', roomNo: '801', floorNo: '8', status: 'OCCUPIED' },
];
const roomStatusLogs = [{ id: 1, roomId: 2, oldStatus: 'VACANT_CLEAN', newStatus: 'OCCUPIED', reason: '订单入住', operator: 'admin', createdAt: now() }];

const reservations = [{ id: 1, reservationNo: 'RSV20260319001', propertyId: 1, propertyName: '静栖里·外滩店', roomTypeId: 1, roomTypeName: '豪华大床房', contactName: '王小明', contactMobile: '13900000001', guestCount: 2, checkInDate: today(), checkOutDate: plusDays(1), reservationStatus: 'CONFIRMED', estimatedAmount: 388 }];
const orders = [{ id: 1, orderNo: 'ORD20260319001', propertyId: 1, propertyName: '静栖里·外滩店', roomTypeName: '豪华大床房', guestName: '王小明', guestMobile: '13900000001', checkInDate: today(), checkOutDate: plusDays(1), totalAmount: 388, orderStatus: 'CONFIRMED' }];
const stays = [{ id: 1, stayNo: 'ST20260319001', orderId: 1, orderNo: 'ORD20260319001', propertyId: 1, propertyName: '静栖里·外滩店', roomId: 1, roomNo: '1201', stayType: 'INDIVIDUAL', stayStatus: 'IN_HOUSE', checkInDate: today(), checkOutDate: plusDays(1), actualCheckInTime: now(), actualCheckOutTime: null }];
const guests = [{ id: 1, orderId: 1, guestName: '王小明', guestMobile: '13900000001', certificateType: 'ID_CARD', certificateNo: '3101************', isPrimary: true }];
const payments = [{ id: 1, propertyId: 1, orderId: 1, stayRecordId: 1, paymentType: 'DEPOSIT', paymentMethod: 'WECHAT', amount: 200, paymentStatus: 'SUCCESS', externalTradeNo: 'WX-001', remark: '押金', createdAt: now() }];
const timelines = [{ id: 1, orderId: 1, nodeCode: 'RESERVED', actionCode: 'CREATE', remarkCode: 'ORDER_CREATED', remarkText: '预订创建', operationTime: now(), operator: 'admin', roomId: 1, roomNo: '1201' }, { id: 2, orderId: 1, nodeCode: 'CHECKED_IN', actionCode: 'CHECK_IN', remarkCode: 'CHECKIN_COMPLETED', remarkText: '办理入住', operationTime: now(), operator: 'admin', roomId: 1, roomNo: '1201' }];

const pricePlans = [{ id: 1, propertyId: 1, propertyName: '静栖里·外滩店', roomTypeId: 1, roomTypeName: '豪华大床房', bizDate: today(), salePrice: 388, sellableInventory: 5, overbookLimit: 1, priceTag: '平日价' }];
const priceRules = [{ id: 1, propertyId: 1, ruleType: 'WEEKEND', ruleName: '周末上浮10%', dateScope: '', weekdayMask: 'SAT,SUN', adjustType: 'PERCENT', adjustValue: 10, priorityNo: 100, enabledFlag: 1 }];
const inventories = [{ id: 1, propertyId: 1, propertyName: '静栖里·外滩店', roomTypeId: 1, roomTypeName: '豪华大床房', bizDate: today(), totalInventory: 6, occupiedInventory: 5, availableInventory: 1, warning: true }];

const otaMappings = [{ id: 1, propertyId: 1, propertyName: '静栖里·外滩店', channelCode: 'DOUYIN', mappingType: 'ROOM_TYPE', localBizId: 1, channelBizId: 'DY_RT_1001', remark: '默认映射' }];
const otaSyncLogs = [{ id: 1, propertyId: 1, channelCode: 'DOUYIN', bizType: 'INVENTORY', bizId: 'INV-1', syncStatus: 'SUCCESS', createdAt: now() }];
const otaCallbackLogs = [{ id: 1, channelCode: 'DOUYIN', eventType: 'ORDER_CREATED', externalRequestNo: 'DYCB-001', callbackStatus: 'SUCCESS', processedFlag: 'Y', message: '处理完成', createdAt: now() }];

const members = [{ id: 1, propertyId: 1, propertyName: '静栖里·外滩店', memberNo: 'M20260001', memberName: '周女士', mobile: '13800000001', levelCode: 2, pointBalance: 3200, status: 'ACTIVE' }];
const pointLedgers = [{ id: 1, memberId: 1, propertyId: 1, pointDelta: 300, bizType: 'ORDER', bizNo: 'ORD20260319001', remark: '入住赠送积分', createdAt: now() }];
const preferences = [{ id: 1, memberId: 1, propertyId: 1, preferenceType: 'QUIET_NEED', preferenceValue: '远离电梯', createdAt: now() }];
const coupons = [{ id: 1, propertyId: 1, propertyName: '静栖里·外滩店', couponCode: 'CPN1001', couponName: '周末立减券', amount: 80, threshold: 500, status: 'ACTIVE' }];
const campaigns = [{ id: 1, propertyId: 1, propertyName: '静栖里·外滩店', campaignCode: 'CMP001', campaignName: '春季促销', campaignType: 'SEASONAL', startDate: plusDays(-1), endDate: plusDays(30), status: 'ACTIVE' }];

const refunds = [{ id: 1, propertyId: 1, paymentId: 1, orderId: 1, refundAmount: 50, refundReason: '服务补偿', refundStatus: 'SUCCESS', createdAt: now() }];
const bills = [{ id: 1, propertyId: 1, orderId: 1, billType: 'ROOM_FEE', billItem: '房费', amount: 388, createdAt: now() }];
const invoices = [{ id: 1, orderId: 1, propertyId: 1, invoiceType: 'ELECTRONIC', invoiceTitle: '上海星云科技有限公司', taxNo: '9131*********', amount: 388, invoiceStatus: 'PENDING', createdAt: now() }];
const housekeepingTasks = [{ id: 1, propertyId: 1, roomId: 1, bizDate: today(), taskStatus: 'DOING', assignee: '保洁阿姨A', remark: '处理中', createdAt: now() }];
const maintenanceTickets = [{ id: 1, propertyId: 2, roomId: 2, issueType: '空调故障', issueDescription: '空调不制冷', ticketStatus: 'OPEN', assignee: '维修师傅张', createdAt: now() }];

const operationLogs = [{ id: 1, groupId: 1, brandId: 1, propertyId: 1, moduleCode: 'ORDER', operation: '创建订单', operator: 'admin', successFlag: 'Y', createdAt: now() }, { id: 2, groupId: null, brandId: null, propertyId: 2, moduleCode: 'OPS', operation: '创建维修工单', operator: 'admin', successFlag: 'Y', createdAt: now() }];
const auditLogs = [{ id: 1, groupId: 1, brandId: 1, propertyId: 1, moduleCode: 'ORDER', actionType: 'CREATE', content: '创建预订单', operator: 'admin', createdAt: now() }, { id: 2, groupId: null, brandId: null, propertyId: 2, moduleCode: 'OPS', actionType: 'CREATE', content: '创建维修工单', operator: 'admin', createdAt: now() }];

const sysUsers = [{ id: 1, username: 'admin', nickname: '系统管理员', mobile: '13800000000', email: 'admin@example.com', status: 1 }];
const sysRoles = [{ id: 1, roleCode: 'SUPER_ADMIN', roleName: '超级管理员', status: 1 }];
const sysPermissions = [{ permissionCode: 'property:read', menuName: '民宿管理', menuType: 'MENU' }, { permissionCode: 'order:read', menuName: '订单中心', menuType: 'MENU' }, { permissionCode: 'report:export', menuName: '报表导出', menuType: 'BUTTON' }];
const sysMenus = [{ id: 1, parentId: 0, menuName: '工作台', path: '/workbench', permissionCode: null }, { id: 2, parentId: 0, menuName: '房源管理', path: '/assets', permissionCode: null }];
const sysDicts = [{ dictCode: 'ROOM_TIMELINE_NODE', dictName: '房态时间线节点', remark: '订单详情时间线字典' }];
const sysParams = [{ paramKey: 'hms.currentProperty.prefer', paramValue: 'true', remark: '日志筛选优先当前门店' }];

let currentPropertyId = 1;
const currentOrg = () => {
  const p = properties.find((x) => x.id === currentPropertyId);
  return { currentPropertyId, currentBrandId: p?.brandId || null, currentGroupId: p?.groupId || null, allowWideRangeSwitch: true };
};

const reportDaily = () => properties.map((p) => {
  const orderIds = orders.filter((o) => o.propertyId === p.id).map((o) => o.id);
  const paymentAmount = payments.filter((x) => orderIds.includes(x.orderId)).reduce((s, x) => s + Number(x.amount || 0), 0);
  const refundAmount = refunds.filter((x) => orderIds.includes(x.orderId)).reduce((s, x) => s + Number(x.refundAmount || 0), 0);
  return { propertyId: p.id, propertyName: p.propertyName, orderCount: orderIds.length, paymentAmount, refundAmount, netRevenue: Number((paymentAmount - refundAmount).toFixed(2)) };
});
const reportProperty = () => properties.map((p) => {
  const r = rooms.filter((x) => x.propertyId === p.id);
  const occ = r.filter((x) => x.status === 'OCCUPIED').length;
  const rate = r.length ? Number((occ / r.length).toFixed(4)) : 0;
  return { propertyId: p.id, propertyName: p.propertyName, roomCount: r.length, occupiedRoomCount: occ, occupancyRate: rate };
});
const reportDashboard = () => {
  const daily = reportDaily();
  return { todayOrderCount: daily.reduce((s, x) => s + x.orderCount, 0), todayRevenue: daily.reduce((s, x) => s + x.netRevenue, 0), inHouseCount: stays.filter((x) => x.stayStatus === 'IN_HOUSE').length, warningInventoryCount: inventories.filter((x) => x.warning).length };
};
const sendCsv = (res, filename, rows) => {
  const csv = rows.map((row) => row.map((v) => `"${String(v ?? '')}"`).join(',')).join('\n');
  res.setHeader('Content-Type', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
  res.setHeader('Content-Disposition', `attachment; filename="${filename}"`);
  res.send(Buffer.from(csv, 'utf8'));
};
app.get('/api/simple/ping', (req, res) => ok(res, { status: 'ok', message: 'pong', timestamp: Date.now() }));

const loginPayload = (username) => ({
  accessToken: `mock-token-${Date.now()}`,
  refreshToken: `mock-refresh-${Date.now()}`,
  tokenType: 'Bearer',
  expiresIn: 7200,
  userId: 1,
  username,
  nickname: 'Mock Admin',
  permissions: ['*'],
  propertyScopes: properties.map((p) => p.id),
  currentPropertyId,
});

const loginHandler = (req, res) => {
  const { username, password } = req.body || {};
  if (username === 'admin' && password === 'Admin@123') return ok(res, loginPayload(username));
  return res.status(401).json({ code: 401, message: 'invalid credentials', data: null });
};
app.post('/api/auth/login', loginHandler);
app.post('/api/final-auth/login', loginHandler);
app.post('/api/auth/refresh', (req, res) => {
  if (!req.body?.refreshToken) return res.status(401).json({ code: 401, message: 'invalid refresh token', data: null });
  return ok(res, loginPayload('admin'));
});
app.post('/api/auth/logout', (req, res) => ok(res));
app.post('/api/auth/switch-property', (req, res) => {
  const pid = Number(req.body?.propertyId || currentPropertyId);
  if (!properties.find((p) => p.id === pid)) return res.status(400).json({ code: 400, message: 'property not found', data: null });
  currentPropertyId = pid;
  return ok(res, loginPayload('admin'));
});

app.get('/api/groups', (req, res) => ok(res, clone(groups)));
app.get('/api/brands', (req, res) => ok(res, clone(brands)));

app.get('/api/properties', (req, res) => ok(res, clone(properties)));
app.post('/api/properties', (req, res) => {
  const b = req.body || {};
  const id = next('property');
  const brand = brands.find((x) => x.id === Number(b.brandId));
  const group = groups.find((x) => x.id === Number(b.groupId)) || groups.find((x) => x.id === brand?.groupId);
  properties.unshift({ id, groupId: group?.id || null, groupName: group?.groupName || null, brandId: brand?.id || null, brandName: brand?.brandName || null, propertyCode: b.propertyCode || `HS_${id}`, propertyName: b.propertyName, businessMode: b.businessMode || 'HOMESTAY', contactPhone: b.contactPhone || '', city: b.city || '', address: b.address || '', status: 1 });
  return ok(res, id);
});
app.put('/api/properties/:id', (req, res) => {
  const p = properties.find((x) => x.id === Number(req.params.id));
  if (!p) return res.status(404).json({ code: 404, message: 'property not found', data: null });
  const b = req.body || {};
  const brand = brands.find((x) => x.id === Number(b.brandId));
  const group = groups.find((x) => x.id === Number(b.groupId)) || groups.find((x) => x.id === brand?.groupId);
  Object.assign(p, { groupId: group?.id || null, groupName: group?.groupName || null, brandId: brand?.id || null, brandName: brand?.brandName || null, propertyName: b.propertyName || p.propertyName, businessMode: b.businessMode || p.businessMode, contactPhone: b.contactPhone || '', city: b.city || '', address: b.address || '' });
  return ok(res);
});
app.patch('/api/properties/:id/status', (req, res) => {
  const p = properties.find((x) => x.id === Number(req.params.id));
  if (!p) return res.status(404).json({ code: 404, message: 'property not found', data: null });
  p.status = Number(req.body?.status || 0);
  return ok(res);
});

app.get('/api/room-types', (req, res) => ok(res, clone(roomTypes)));
app.post('/api/room-types', (req, res) => {
  const b = req.body || {};
  const p = properties.find((x) => x.id === Number(b.propertyId));
  const id = next('roomType');
  roomTypes.unshift({ id, propertyId: Number(b.propertyId), propertyName: p?.propertyName || `民宿${b.propertyId}`, roomTypeCode: b.roomTypeCode, roomTypeName: b.roomTypeName, maxGuestCount: Number(b.maxGuestCount || 2), bedType: b.bedType || '', basePrice: Number(b.basePrice || 0), status: 1 });
  return ok(res, id);
});
app.put('/api/room-types/:id', (req, res) => {
  const t = roomTypes.find((x) => x.id === Number(req.params.id));
  if (!t) return res.status(404).json({ code: 404, message: 'room type not found', data: null });
  const b = req.body || {};
  const p = properties.find((x) => x.id === Number(b.propertyId || t.propertyId));
  Object.assign(t, { propertyId: Number(b.propertyId || t.propertyId), propertyName: p?.propertyName || t.propertyName, roomTypeName: b.roomTypeName || t.roomTypeName, maxGuestCount: Number(b.maxGuestCount || t.maxGuestCount), bedType: b.bedType || '', basePrice: Number(b.basePrice || t.basePrice) });
  return ok(res);
});
app.patch('/api/room-types/:id/status', (req, res) => {
  const t = roomTypes.find((x) => x.id === Number(req.params.id));
  if (!t) return res.status(404).json({ code: 404, message: 'room type not found', data: null });
  t.status = Number(req.body?.status || 0);
  return ok(res);
});

app.get('/api/rooms', (req, res) => ok(res, clone(rooms)));
app.post('/api/rooms', (req, res) => {
  const b = req.body || {};
  const p = properties.find((x) => x.id === Number(b.propertyId));
  const t = roomTypes.find((x) => x.id === Number(b.roomTypeId));
  const id = next('room');
  rooms.unshift({ id, propertyId: Number(b.propertyId), propertyName: p?.propertyName || `民宿${b.propertyId}`, roomTypeId: Number(b.roomTypeId), roomTypeName: t?.roomTypeName || `房型${b.roomTypeId}`, roomNo: b.roomNo, floorNo: b.floorNo || '', status: 'VACANT_CLEAN' });
  return ok(res, id);
});
app.put('/api/rooms/:id', (req, res) => {
  const r = rooms.find((x) => x.id === Number(req.params.id));
  if (!r) return res.status(404).json({ code: 404, message: 'room not found', data: null });
  const t = roomTypes.find((x) => x.id === Number(req.body?.roomTypeId));
  r.roomTypeId = Number(req.body?.roomTypeId || r.roomTypeId);
  r.roomTypeName = t?.roomTypeName || r.roomTypeName;
  r.floorNo = req.body?.floorNo || r.floorNo;
  return ok(res);
});
app.patch('/api/rooms/:id/status', (req, res) => {
  const r = rooms.find((x) => x.id === Number(req.params.id));
  if (!r) return res.status(404).json({ code: 404, message: 'room not found', data: null });
  const oldStatus = r.status;
  r.status = req.body?.status;
  roomStatusLogs.unshift({ id: next('roomStatusLog'), roomId: r.id, oldStatus, newStatus: r.status, reason: req.body?.reason || '', operator: 'admin', createdAt: now() });
  return ok(res);
});
app.get('/api/rooms/:id/status-logs', (req, res) => ok(res, clone(roomStatusLogs.filter((x) => x.roomId === Number(req.params.id)))));

app.get('/api/orders/reservations', (req, res) => ok(res, clone(reservations)));
app.post('/api/orders/reservations', (req, res) => {
  const b = req.body || {};
  const p = properties.find((x) => x.id === Number(b.propertyId));
  const t = roomTypes.find((x) => x.id === Number(b.roomTypeId));
  const id = next('reservation');
  reservations.unshift({ id, reservationNo: `RSV${Date.now()}`, propertyId: Number(b.propertyId), propertyName: p?.propertyName || `民宿${b.propertyId}`, roomTypeId: Number(b.roomTypeId), roomTypeName: t?.roomTypeName || `房型${b.roomTypeId}`, contactName: b.contactName, contactMobile: b.contactMobile, guestCount: Number(b.guestCount || 1), checkInDate: b.checkInDate, checkOutDate: b.checkOutDate, reservationStatus: 'CONFIRMED', estimatedAmount: Number(b.estimatedAmount || 0) });
  return ok(res, id);
});
app.post('/api/orders/reservations/:id/generate-order', (req, res) => {
  const rv = reservations.find((x) => x.id === Number(req.params.id));
  if (!rv) return res.status(404).json({ code: 404, message: 'reservation not found', data: null });
  const id = next('order');
  orders.unshift({ id, orderNo: `ORD${Date.now()}`, propertyId: rv.propertyId, propertyName: rv.propertyName, roomTypeName: rv.roomTypeName, guestName: rv.contactName, guestMobile: rv.contactMobile, checkInDate: rv.checkInDate, checkOutDate: rv.checkOutDate, totalAmount: rv.estimatedAmount, orderStatus: 'CONFIRMED' });
  rv.reservationStatus = 'ORDER_GENERATED';
  timelines.unshift({ id: next('timeline'), orderId: id, nodeCode: 'RESERVED', actionCode: 'CREATE', remarkCode: 'ORDER_CREATED', remarkText: '由预订生成订单', operationTime: now(), operator: 'admin', roomId: null, roomNo: null });
  return ok(res, id);
});
app.get('/api/orders', (req, res) => ok(res, clone(orders)));
app.get('/api/orders/stays', (req, res) => ok(res, clone(stays)));
app.post('/api/orders/stays/check-in', (req, res) => {
  const b = req.body || {};
  const order = orders.find((x) => x.id === Number(b.orderId));
  const room = rooms.find((x) => x.id === Number(b.roomId));
  if (!order || !room) return res.status(400).json({ code: 400, message: 'invalid order/room', data: null });
  const id = next('stay');
  stays.unshift({ id, stayNo: `ST${Date.now()}`, orderId: order.id, orderNo: order.orderNo, propertyId: order.propertyId, propertyName: order.propertyName, roomId: room.id, roomNo: room.roomNo, stayType: b.stayType || 'INDIVIDUAL', stayStatus: 'IN_HOUSE', checkInDate: b.checkInDate, checkOutDate: b.checkOutDate, actualCheckInTime: now(), actualCheckOutTime: null });
  order.orderStatus = 'CHECKED_IN';
  room.status = 'OCCUPIED';
  timelines.unshift({ id: next('timeline'), orderId: order.id, nodeCode: 'CHECKED_IN', actionCode: 'CHECK_IN', remarkCode: 'CHECKIN_COMPLETED', remarkText: `入住房间 ${room.roomNo}`, operationTime: now(), operator: 'admin', roomId: room.id, roomNo: room.roomNo });
  return ok(res, id);
});
app.patch('/api/orders/stays/extend', (req, res) => {
  const s = stays.find((x) => x.id === Number(req.body?.stayRecordId));
  if (!s) return res.status(404).json({ code: 404, message: 'stay not found', data: null });
  s.checkOutDate = req.body?.newCheckOutDate;
  timelines.unshift({ id: next('timeline'), orderId: s.orderId, nodeCode: 'EXTENDED', actionCode: 'EXTEND_STAY', remarkCode: 'STAY_EXTENDED', remarkText: `续住至 ${s.checkOutDate}`, operationTime: now(), operator: 'admin', roomId: s.roomId, roomNo: s.roomNo });
  return ok(res);
});
app.patch('/api/orders/stays/change-room', (req, res) => {
  const s = stays.find((x) => x.id === Number(req.body?.stayRecordId));
  const r = rooms.find((x) => x.id === Number(req.body?.newRoomId));
  if (!s || !r) return res.status(404).json({ code: 404, message: 'stay/room not found', data: null });
  const old = s.roomNo;
  s.roomId = r.id;
  s.roomNo = r.roomNo;
  timelines.unshift({ id: next('timeline'), orderId: s.orderId, nodeCode: 'CHANGE_ROOM', actionCode: 'CHANGE_ROOM', remarkCode: 'ROOM_CHANGED', remarkText: req.body?.reason || `${old} -> ${s.roomNo}`, operationTime: now(), operator: 'admin', roomId: s.roomId, roomNo: s.roomNo });
  return ok(res);
});
app.patch('/api/orders/stays/checkout', (req, res) => {
  const s = stays.find((x) => x.id === Number(req.body?.stayRecordId));
  if (!s) return res.status(404).json({ code: 404, message: 'stay not found', data: null });
  s.stayStatus = 'CHECKED_OUT';
  s.actualCheckOutTime = now();
  const o = orders.find((x) => x.id === s.orderId);
  if (o) o.orderStatus = 'CHECKED_OUT';
  timelines.unshift({ id: next('timeline'), orderId: s.orderId, nodeCode: 'CHECKED_OUT', actionCode: 'CHECKOUT', remarkCode: 'CHECKOUT_COMPLETED', remarkText: req.body?.remark || '完成退房', operationTime: now(), operator: 'admin', roomId: s.roomId, roomNo: s.roomNo });
  return ok(res);
});
app.patch('/api/orders/cancel', (req, res) => {
  const o = orders.find((x) => x.id === Number(req.body?.orderId));
  if (!o) return res.status(404).json({ code: 404, message: 'order not found', data: null });
  o.orderStatus = 'CANCELED';
  timelines.unshift({ id: next('timeline'), orderId: o.id, nodeCode: 'CANCELED', actionCode: 'CANCEL', remarkCode: 'ORDER_CANCELED', remarkText: '订单取消', operationTime: now(), operator: 'admin', roomId: null, roomNo: null });
  return ok(res);
});
app.post('/api/orders/guests', (req, res) => {
  const b = req.body || {};
  const id = next('guest');
  guests.unshift({ id, orderId: Number(b.orderId), guestName: b.guestName, guestMobile: b.guestMobile, certificateType: b.certificateType || 'ID_CARD', certificateNo: b.certificateNo || '', isPrimary: !!b.primaryGuest });
  return ok(res, id);
});
app.get('/api/orders/payments', (req, res) => ok(res, clone(payments)));
app.post('/api/orders/payments', (req, res) => {
  const b = req.body || {};
  const id = next('payment');
  const order = orders.find((x) => x.id === Number(b.orderId));
  payments.unshift({ id, propertyId: order?.propertyId || null, orderId: Number(b.orderId), stayRecordId: b.stayRecordId ? Number(b.stayRecordId) : null, paymentType: b.paymentType, paymentMethod: b.paymentMethod, amount: Number(b.amount), paymentStatus: 'SUCCESS', externalTradeNo: b.externalTradeNo || '', remark: b.remark || '', createdAt: now() });
  return ok(res, id);
});
app.get('/api/orders/:id/timeline', (req, res) => ok(res, clone(timelines.filter((x) => x.orderId === Number(req.params.id)))));
app.get('/api/orders/:id', (req, res) => {
  const id = Number(req.params.id);
  const order = orders.find((x) => x.id === id);
  if (!order) return res.status(404).json({ code: 404, message: 'order not found', data: null });
  return ok(res, { order, stays: stays.filter((x) => x.orderId === id), guests: guests.filter((x) => x.orderId === id), payments: payments.filter((x) => x.orderId === id), auditLogs: auditLogs.filter((x) => x.propertyId === order.propertyId), operationLogs: operationLogs.filter((x) => x.propertyId === order.propertyId), roomStatusTimeline: timelines.filter((x) => x.orderId === id) });
});
app.get('/api/orders/export', (req, res) =>
  sendCsv(res, 'orders.xlsx', [
    ['订单号', '民宿', '住客', '手机号', '入住日期', '离店日期', '金额', '状态'],
    ...orders.map((x) => [x.orderNo, x.propertyName, x.guestName, x.guestMobile, x.checkInDate, x.checkOutDate, x.totalAmount, x.orderStatus]),
  ]),
);

app.get('/api/pricing/plans', (req, res) => ok(res, clone(pricePlans)));
app.post('/api/pricing/plans', (req, res) => {
  const b = req.body || {};
  const existing = pricePlans.find((x) => x.propertyId === Number(b.propertyId) && x.roomTypeId === Number(b.roomTypeId) && x.bizDate === b.bizDate);
  if (existing) Object.assign(existing, { salePrice: Number(b.salePrice), sellableInventory: Number(b.sellableInventory), overbookLimit: Number(b.overbookLimit || 0), priceTag: b.priceTag || '' });
  else {
    const p = properties.find((x) => x.id === Number(b.propertyId));
    const t = roomTypes.find((x) => x.id === Number(b.roomTypeId));
    pricePlans.unshift({ id: next('plan'), propertyId: Number(b.propertyId), propertyName: p?.propertyName || `民宿${b.propertyId}`, roomTypeId: Number(b.roomTypeId), roomTypeName: t?.roomTypeName || `房型${b.roomTypeId}`, bizDate: b.bizDate, salePrice: Number(b.salePrice), sellableInventory: Number(b.sellableInventory), overbookLimit: Number(b.overbookLimit || 0), priceTag: b.priceTag || '' });
  }
  return ok(res);
});
app.get('/api/pricing/rules', (req, res) => ok(res, clone(priceRules)));
app.post('/api/pricing/rules', (req, res) => {
  const b = req.body || {};
  const id = next('rule');
  priceRules.unshift({ id, propertyId: Number(b.propertyId), ruleType: b.ruleType, ruleName: b.ruleName, dateScope: b.dateScope || '', weekdayMask: b.weekdayMask || '', adjustType: b.adjustType, adjustValue: Number(b.adjustValue), priorityNo: Number(b.priorityNo || 100), enabledFlag: Number(b.enabledFlag || 1) });
  return ok(res, id);
});
app.get('/api/pricing/inventories', (req, res) => ok(res, clone(inventories)));
app.patch('/api/pricing/inventories/adjust', (req, res) => {
  const b = req.body || {};
  const i = inventories.find((x) => x.propertyId === Number(b.propertyId) && x.roomTypeId === Number(b.roomTypeId) && x.bizDate === b.bizDate);
  if (!i) {
    const p = properties.find((x) => x.id === Number(b.propertyId));
    const t = roomTypes.find((x) => x.id === Number(b.roomTypeId));
    const occupied = Math.max(0, Number(b.occupiedDelta || 0));
    inventories.unshift({ id: next('inventory'), propertyId: Number(b.propertyId), propertyName: p?.propertyName || `民宿${b.propertyId}`, roomTypeId: Number(b.roomTypeId), roomTypeName: t?.roomTypeName || `房型${b.roomTypeId}`, bizDate: b.bizDate, totalInventory: 5, occupiedInventory: occupied, availableInventory: Math.max(0, 5 - occupied), warning: 5 - occupied <= 1 });
  } else {
    i.occupiedInventory = Math.max(0, i.occupiedInventory + Number(b.occupiedDelta || 0));
    i.availableInventory = Math.max(0, i.totalInventory - i.occupiedInventory);
    i.warning = i.availableInventory <= 1;
  }
  return ok(res);
});

app.get('/api/ota/mappings', (req, res) => ok(res, clone(otaMappings)));
app.post('/api/ota/mappings', (req, res) => {
  const b = req.body || {};
  const p = properties.find((x) => x.id === Number(b.propertyId));
  const id = next('map');
  otaMappings.unshift({ id, propertyId: Number(b.propertyId), propertyName: p?.propertyName || `民宿${b.propertyId}`, channelCode: b.channelCode, mappingType: b.mappingType, localBizId: Number(b.localBizId), channelBizId: b.channelBizId, remark: b.remark || '' });
  return ok(res, id);
});
const syncLog = (b, bizType) => otaSyncLogs.unshift({ id: next('sync'), propertyId: Number(b.propertyId || currentPropertyId), channelCode: b.channelCode || 'DOUYIN', bizType, bizId: `${bizType}-${Date.now()}`, syncStatus: 'SUCCESS', createdAt: now() });
app.post('/api/ota/sync/inventory', (req, res) => { syncLog(req.body || {}, 'INVENTORY'); return ok(res); });
app.post('/api/ota/sync/price', (req, res) => { syncLog(req.body || {}, 'PRICE'); return ok(res); });
app.post('/api/ota/sync/orders/pull', (req, res) => { syncLog(req.body || {}, 'ORDER'); return ok(res); });
app.get('/api/ota/sync/logs', (req, res) => ok(res, clone(otaSyncLogs)));
app.get('/api/ota/callback/logs', (req, res) => ok(res, clone(otaCallbackLogs)));
app.get('/api/member-marketing/members', (req, res) => ok(res, clone(members)));
app.post('/api/member-marketing/members', (req, res) => {
  const b = req.body || {};
  const p = properties.find((x) => x.id === Number(b.propertyId));
  const id = next('member');
  members.unshift({ id, propertyId: Number(b.propertyId), propertyName: p?.propertyName || `民宿${b.propertyId}`, memberNo: `M${Date.now()}`, memberName: b.memberName, mobile: b.mobile, levelCode: Number(b.levelCode || 1), pointBalance: 0, status: 'ACTIVE' });
  return ok(res, id);
});
app.patch('/api/member-marketing/members/points', (req, res) => {
  const b = req.body || {};
  const m = members.find((x) => x.id === Number(b.memberId));
  if (!m) return res.status(404).json({ code: 404, message: 'member not found', data: null });
  m.pointBalance = Number(m.pointBalance || 0) + Number(b.pointDelta || 0);
  pointLedgers.unshift({ id: next('point'), memberId: m.id, propertyId: m.propertyId, pointDelta: Number(b.pointDelta || 0), bizType: b.bizType || 'MANUAL', bizNo: b.bizNo || '', remark: b.remark || '', createdAt: now() });
  return ok(res);
});
app.get('/api/member-marketing/points', (req, res) => ok(res, clone(pointLedgers)));
app.get('/api/member-marketing/preferences', (req, res) => {
  const memberId = req.query.memberId ? Number(req.query.memberId) : null;
  const result = memberId ? preferences.filter((x) => x.memberId === memberId) : preferences;
  return ok(res, clone(result));
});
app.post('/api/member-marketing/preferences', (req, res) => {
  const b = req.body || {};
  const m = members.find((x) => x.id === Number(b.memberId));
  if (!m) return res.status(404).json({ code: 404, message: 'member not found', data: null });
  const id = next('pref');
  preferences.unshift({ id, memberId: m.id, propertyId: m.propertyId, preferenceType: b.preferenceType, preferenceValue: b.preferenceValue, createdAt: now() });
  return ok(res, id);
});
app.get('/api/member-marketing/coupons', (req, res) => ok(res, clone(coupons)));
app.post('/api/member-marketing/coupons', (req, res) => {
  const b = req.body || {};
  const p = properties.find((x) => x.id === Number(b.propertyId));
  const id = next('coupon');
  coupons.unshift({ id, propertyId: Number(b.propertyId), propertyName: p?.propertyName || `民宿${b.propertyId}`, couponCode: b.couponCode, couponName: b.couponName, amount: Number(b.amount), threshold: Number(b.threshold || 0), status: 'ACTIVE' });
  return ok(res, id);
});
app.get('/api/member-marketing/campaigns', (req, res) => ok(res, clone(campaigns)));
app.post('/api/member-marketing/campaigns', (req, res) => {
  const b = req.body || {};
  const p = properties.find((x) => x.id === Number(b.propertyId));
  const id = next('campaign');
  campaigns.unshift({ id, propertyId: Number(b.propertyId), propertyName: p?.propertyName || `民宿${b.propertyId}`, campaignCode: b.campaignCode, campaignName: b.campaignName, campaignType: b.campaignType, startDate: b.startDate || null, endDate: b.endDate || null, status: 'ACTIVE' });
  return ok(res, id);
});

app.get('/api/finance-ops/refunds', (req, res) => ok(res, clone(refunds)));
app.post('/api/finance-ops/refunds', (req, res) => {
  const b = req.body || {};
  const id = next('refund');
  const order = orders.find((x) => x.id === Number(b.orderId));
  refunds.unshift({ id, propertyId: order?.propertyId || null, paymentId: Number(b.paymentId), orderId: Number(b.orderId), refundAmount: Number(b.refundAmount), refundReason: b.refundReason || '', refundStatus: 'SUCCESS', createdAt: now() });
  return ok(res, id);
});
app.get('/api/finance-ops/bills', (req, res) => ok(res, clone(bills)));
app.post('/api/finance-ops/bills', (req, res) => {
  const b = req.body || {};
  const id = next('bill');
  bills.unshift({ id, propertyId: Number(b.propertyId), orderId: Number(b.orderId), billType: b.billType, billItem: b.billItem, amount: Number(b.amount), createdAt: now() });
  return ok(res, id);
});
app.get('/api/finance-ops/invoices', (req, res) => ok(res, clone(invoices)));
app.post('/api/finance-ops/invoices', (req, res) => {
  const b = req.body || {};
  const id = next('invoice');
  invoices.unshift({ id, orderId: Number(b.orderId), propertyId: Number(b.propertyId), invoiceType: b.invoiceType || 'ELECTRONIC', invoiceTitle: b.invoiceTitle || '', taxNo: b.taxNo || '', amount: Number(b.amount || 0), invoiceStatus: 'PENDING', createdAt: now() });
  return ok(res, id);
});
app.get('/api/finance-ops/housekeeping/tasks', (req, res) => ok(res, clone(housekeepingTasks)));
app.post('/api/finance-ops/housekeeping/tasks', (req, res) => {
  const b = req.body || {};
  const id = next('task');
  housekeepingTasks.unshift({ id, propertyId: Number(b.propertyId), roomId: Number(b.roomId), bizDate: b.bizDate, taskStatus: 'TODO', assignee: b.assignee || '', remark: b.remark || '', createdAt: now() });
  return ok(res, id);
});
app.patch('/api/finance-ops/housekeeping/tasks/status', (req, res) => {
  const t = housekeepingTasks.find((x) => x.id === Number(req.body?.id));
  if (!t) return res.status(404).json({ code: 404, message: 'task not found', data: null });
  t.taskStatus = req.body?.status;
  return ok(res);
});
app.get('/api/finance-ops/maintenance/tickets', (req, res) => ok(res, clone(maintenanceTickets)));
app.post('/api/finance-ops/maintenance/tickets', (req, res) => {
  const b = req.body || {};
  const id = next('ticket');
  maintenanceTickets.unshift({ id, propertyId: Number(b.propertyId), roomId: Number(b.roomId), issueType: b.issueType, issueDescription: b.issueDescription, ticketStatus: 'OPEN', assignee: b.assignee || '', createdAt: now() });
  return ok(res, id);
});
app.patch('/api/finance-ops/maintenance/tickets/status', (req, res) => {
  const t = maintenanceTickets.find((x) => x.id === Number(req.body?.id));
  if (!t) return res.status(404).json({ code: 404, message: 'ticket not found', data: null });
  t.ticketStatus = req.body?.status;
  return ok(res);
});
app.get('/api/finance-ops/reports/daily', (req, res) => ok(res, reportDaily()));
app.get('/api/finance-ops/reports/property-stats', (req, res) => ok(res, reportProperty()));
app.get('/api/finance-ops/dashboard', (req, res) => ok(res, reportDashboard()));
app.get('/api/finance-ops/reports/daily/export', (req, res) =>
  sendCsv(res, 'daily-report.xlsx', [
    ['民宿ID', '民宿', '订单数', '收款', '退款', '净收入'],
    ...reportDaily().map((x) => [x.propertyId, x.propertyName, x.orderCount, x.paymentAmount, x.refundAmount, x.netRevenue]),
  ]),
);
app.get('/api/finance-ops/reports/property-stats/export', (req, res) =>
  sendCsv(res, 'property-stats.xlsx', [
    ['民宿ID', '民宿', '房间数', '在住房间', '入住率'],
    ...reportProperty().map((x) => [x.propertyId, x.propertyName, x.roomCount, x.occupiedRoomCount, x.occupancyRate]),
  ]),
);

app.post('/api/logs/operation/search', (req, res) => {
  const b = req.body || {};
  const pageNo = Number(b.pageNo || 1);
  const pageSize = Number(b.pageSize || 20);
  const records = operationLogs.filter((x) => (!b.groupId || Number(b.groupId) === Number(x.groupId)) && (!b.brandId || Number(b.brandId) === Number(x.brandId)) && (!b.propertyId || Number(b.propertyId) === Number(x.propertyId)) && (!b.moduleCode || String(x.moduleCode).toLowerCase().includes(String(b.moduleCode).toLowerCase())) && (!b.operator || String(x.operator).toLowerCase().includes(String(b.operator).toLowerCase())) && (!b.startTime || x.createdAt >= b.startTime) && (!b.endTime || x.createdAt <= b.endTime));
  const start = (pageNo - 1) * pageSize;
  return ok(res, { pageNo, pageSize, total: records.length, records: clone(records.slice(start, start + pageSize)) });
});
app.post('/api/logs/audit/search', (req, res) => {
  const b = req.body || {};
  const pageNo = Number(b.pageNo || 1);
  const pageSize = Number(b.pageSize || 20);
  const records = auditLogs.filter((x) => (!b.groupId || Number(b.groupId) === Number(x.groupId)) && (!b.brandId || Number(b.brandId) === Number(x.brandId)) && (!b.propertyId || Number(b.propertyId) === Number(x.propertyId)) && (!b.moduleCode || String(x.moduleCode).toLowerCase().includes(String(b.moduleCode).toLowerCase())) && (!b.operator || String(x.operator).toLowerCase().includes(String(b.operator).toLowerCase())) && (!b.startTime || x.createdAt >= b.startTime) && (!b.endTime || x.createdAt <= b.endTime));
  const start = (pageNo - 1) * pageSize;
  return ok(res, { pageNo, pageSize, total: records.length, records: clone(records.slice(start, start + pageSize)) });
});
app.get('/api/logs/options/context', (req, res) => ok(res, currentOrg()));
app.get('/api/logs/options/groups', (req, res) => ok(res, clone(groups)));
app.get('/api/logs/options/brands', (req, res) => {
  const gid = req.query.groupId ? Number(req.query.groupId) : null;
  let list = brands;
  if (gid) list = list.filter((x) => x.groupId === gid);
  return ok(res, clone(list.map((x) => ({ id: x.id, groupId: x.groupId, brandName: x.brandName }))));
});
app.get('/api/logs/options/properties', (req, res) => {
  const gid = req.query.groupId ? Number(req.query.groupId) : null;
  const bid = req.query.brandId ? Number(req.query.brandId) : null;
  let list = properties;
  if (gid) list = list.filter((x) => Number(x.groupId || 0) === gid);
  if (bid) list = list.filter((x) => Number(x.brandId || 0) === bid);
  return ok(res, clone(list.map((x) => ({ id: x.id, groupId: x.groupId, brandId: x.brandId, propertyName: x.propertyName }))));
});

app.get('/api/system/users', (req, res) => ok(res, clone(sysUsers)));
app.get('/api/system/roles', (req, res) => ok(res, clone(sysRoles)));
app.get('/api/system/permissions', (req, res) => ok(res, clone(sysPermissions)));
app.get('/api/system/menus', (req, res) => ok(res, clone(sysMenus)));
app.get('/api/system/dicts', (req, res) => ok(res, clone(sysDicts)));
app.get('/api/system/params', (req, res) => ok(res, clone(sysParams)));
app.post('/api/system/users', (req, res) => {
  const b = req.body || {};
  const id = next('sysUser');
  sysUsers.unshift({ id, username: b.username, nickname: b.nickname, mobile: b.mobile || '', email: b.email || '', status: Number(b.status || 1) });
  return ok(res, id);
});
app.put('/api/system/users/:id', (req, res) => {
  const u = sysUsers.find((x) => x.id === Number(req.params.id));
  if (!u) return res.status(404).json({ code: 404, message: 'user not found', data: null });
  const b = req.body || {};
  u.nickname = b.nickname ?? u.nickname;
  u.mobile = b.mobile ?? u.mobile;
  u.email = b.email ?? u.email;
  u.status = b.status != null ? Number(b.status) : u.status;
  return ok(res);
});
app.post('/api/system/roles', (req, res) => {
  const b = req.body || {};
  const id = next('sysRole');
  sysRoles.unshift({ id, roleCode: b.roleCode, roleName: b.roleName, status: Number(b.status || 1) });
  return ok(res, id);
});
app.put('/api/system/roles/:id', (req, res) => {
  const r = sysRoles.find((x) => x.id === Number(req.params.id));
  if (!r) return res.status(404).json({ code: 404, message: 'role not found', data: null });
  const b = req.body || {};
  r.roleName = b.roleName ?? r.roleName;
  r.status = b.status != null ? Number(b.status) : r.status;
  return ok(res);
});
app.post('/api/system/dicts', (req, res) => {
  const b = req.body || {};
  const existed = sysDicts.find((x) => x.dictCode === b.dictCode);
  if (existed) return res.status(400).json({ code: 400, message: 'dict code exists', data: null });
  sysDicts.unshift({ dictCode: b.dictCode, dictName: b.dictName, remark: b.remark || '' });
  return ok(res, sysDicts.length);
});
app.put('/api/system/dicts/:dictCode', (req, res) => {
  const dictCode = decodeURIComponent(req.params.dictCode);
  const d = sysDicts.find((x) => x.dictCode === dictCode);
  if (!d) return res.status(404).json({ code: 404, message: 'dict not found', data: null });
  const b = req.body || {};
  d.dictName = b.dictName ?? d.dictName;
  d.remark = b.remark ?? d.remark;
  return ok(res);
});
app.post('/api/system/params', (req, res) => {
  const b = req.body || {};
  const existed = sysParams.find((x) => x.paramKey === b.paramKey);
  if (existed) return res.status(400).json({ code: 400, message: 'param key exists', data: null });
  sysParams.unshift({ paramKey: b.paramKey, paramValue: b.paramValue, remark: b.remark || '' });
  return ok(res, sysParams.length);
});
app.put('/api/system/params/:paramKey', (req, res) => {
  const paramKey = decodeURIComponent(req.params.paramKey);
  const p = sysParams.find((x) => x.paramKey === paramKey);
  if (!p) return res.status(404).json({ code: 404, message: 'param not found', data: null });
  const b = req.body || {};
  p.paramValue = b.paramValue ?? p.paramValue;
  p.remark = b.remark ?? p.remark;
  return ok(res);
});

app.get('/api/*', (req, res) => ok(res, []));
app.post('/api/*', (req, res) => ok(res, null));
app.put('/api/*', (req, res) => ok(res, null));
app.patch('/api/*', (req, res) => ok(res, null));

app.listen(port, () => {
  console.log(`Mock server running at http://localhost:${port}`);
  console.log('Login endpoints: POST /api/auth/login and /api/final-auth/login');
});
