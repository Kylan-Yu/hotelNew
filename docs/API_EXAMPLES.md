# API 示例 | API Examples

## 登录 | Login
```bash
curl -X POST 'http://localhost:8080/api/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

## 创建门店 | Create Property
```bash
curl -X POST 'http://localhost:8080/api/properties' \
  -H 'Content-Type: application/json' \
  -d '{
    "groupId": 1,
    "brandId": 1,
    "propertyCode": "KM001",
    "propertyName": "昆明南屏街门店",
    "businessMode": "HOTEL",
    "city": "Kunming",
    "address": "Nanping Street"
  }'
```

## 查询门店列表 | List Properties
```bash
curl 'http://localhost:8080/api/properties'
```
