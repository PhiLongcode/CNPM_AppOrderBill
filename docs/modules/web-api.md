# Web API — REST controllers, Swagger, config, phân quyền

## Vai trò

Layer `web/` la adapter REST:

- Nhận request HTTP
- Validate input / parse param
- Gọi use case/component bên dưới
- Trả response và xử lý exception thông qua `ApiExceptionHandler`

## Thành phần chính (code map)

- Controllers trong `src/main/java/com/giadinh/apporderbill/web/`
- Config:
  - `OrderApiConfig` (default profile)
  - `OrderApiMySqlConfig` (profile `api-mysql`)
  - `OpenApiConfig`
- Authorization:
  - `web/security/ApiAuthorizationService` (require view/operate)
 - Exception handler:
  - `web/ApiExceptionHandler` (ProblemDetail)

## Chuẩn auth hiện tại

- Header: `X-Username`
- `401`: thiếu header hoặc user không tồn tại
- `403`: user tồn tại nhưng không đủ quyền

## Swagger / OpenAPI

- Swagger UI: `/swagger-ui/index.html`
- OpenAPI: `/v3/api-docs`

## Luồng xử lý (tóm tắt)

- (1) Request vào controller `/api/v1/*`
- (2) Authorization kiểm tra function theo endpoint
- (3) Controller gọi use case/component
- (4) Nếu lỗi domain -> `ApiExceptionHandler` map sang `ProblemDetail`

## Troubleshooting

| Triệu chứng | Nguyên nhân | Cách xử |
|---|---|---|
| `401` | Thiếu `X-Username` hoặc user không tồn tại | Dùng user seed `admin` hoặc tạo user mới trong Identity |
| `403` | Thiếu permission | Gán permission assignment theo function |

## Liên kết

- Thiết kế API: `designAPI.md`
- Module Identity: `docs/modules/identity.md`
