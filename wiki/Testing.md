# Testing

## Build & test

```bash
mvn -q -DskipTests package
mvn test
```

## Smoke check nhanh

```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/swagger-ui/index.html
curl -s -o /dev/null -w "%{http_code}" -H "X-Username: admin" http://localhost:8080/api/v1/customers
```

Quy ước:

- `401`: thiếu `X-Username` hoặc user không tồn tại
- `403`: user tồn tại nhưng thiếu quyền
