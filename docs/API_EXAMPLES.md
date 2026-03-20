# API Examples | 接口示例

> Base URL / 基础地址: `http://localhost:8080/api`

## 1) Login | 登录
```bash
curl -X POST 'http://localhost:8080/api/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "admin",
    "password": "Admin@123"
  }'
```

## 2) Create Homestay Property | 新增民宿
```bash
curl -X POST 'http://localhost:8080/api/properties' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <access_token>' \
  -d '{
    "propertyCode": "WESTLAKE-001",
    "propertyName": "西湖云舍",
    "businessMode": "HOMESTAY",
    "contactPhone": "13800138000",
    "city": "Hangzhou",
    "address": "West Lake Street"
  }'
```

## 3) Query Property List | 查询民宿列表
```bash
curl 'http://localhost:8080/api/properties' \
  -H 'Authorization: Bearer <access_token>'
```

## 4) Query Order Detail with Timeline | 查询订单详情（含房态时间线）
```bash
curl 'http://localhost:8080/api/orders/1' \
  -H 'Authorization: Bearer <access_token>'
```

```bash
curl 'http://localhost:8080/api/orders/1/timeline' \
  -H 'Authorization: Bearer <access_token>'
```

## 5) Export Daily Report | 导出日报
```bash
curl -L 'http://localhost:8080/api/finance-ops/reports/daily/export' \
  -H 'Authorization: Bearer <access_token>' \
  -o daily-report.xlsx
```

## 6) Dictionary Items | 字典子项查询
```bash
curl 'http://localhost:8080/api/system/dicts/items?dictCode=ROOM_STATUS' \
  -H 'Authorization: Bearer <access_token>'
```
