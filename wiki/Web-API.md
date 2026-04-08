# Web API

## Vai trò

Layer `web/` là adapter REST:

- Nhận request HTTP
- Kiểm tra quyền (Authorization)
- Gọi use case/component
- Trả response và map lỗi qua `ProblemDetail`

## Swagger / OpenAPI

- Swagger UI: `/swagger-ui/index.html`
- OpenAPI: `/v3/api-docs`

## Authorization

- Header: `X-Username: <username>`
- `401`: thiếu header hoặc user không tồn tại
- `403`: user tồn tại nhưng không đủ quyền

## Link

- Contract API: xem file `designAPI.md` trong repo code
