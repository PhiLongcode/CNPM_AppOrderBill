# Testing guide — Build, test, smoke check

## Mục tiêu

Hướng dẫn build/test và smoke check nhanh các endpoint quan trọng (đặc biệt là Swagger và phân quyền).

## Điều kiện tiên quyết

- API đang chạy local (xem `docs/guides/getting-started.md`)

## Build & test cơ bản

```bash
mvn -q -DskipTests package
mvn test
```

## Smoke test API

```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/swagger-ui/index.html
curl -s -o /dev/null -w "%{http_code}" -H "X-Username: admin" http://localhost:8080/api/v1/customers
```

## Quy ước auth status

- `401`: thiếu `X-Username` hoặc user không tồn tại
- `403`: user tồn tại nhưng thiếu quyền

## Xác minh (verification)

- Swagger UI trả `200`
- Các endpoint protected trả đúng `401/403` theo rule ở trên

## Troubleshooting

| Triệu chứng | Nguyên nhân có thể | Cách xử |
|---|---|---|
| `curl` trả `401` dù dùng user hợp lệ | Sai header hoặc user chưa seed | Dùng `X-Username: admin` (mặc định seed), kiểm tra Identity data init |
| `curl` trả `403` | User thiếu quyền | Xem `docs/modules/identity.md` để gán quyền |

## Tài liệu bổ trợ

- `unit_test.md`
- `unit_test_scenarios.md`
