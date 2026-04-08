# Customer — Khách hàng & tích điểm

## Chức năng

- CRUD khách hàng
- Tích điểm theo số điện thoại

## Thành phần chính (code map)

- Package: `src/main/java/com/giadinh/apporderbill/customer/`
- Repository: `CustomerRepository`
- Use case: `CustomerUseCases`

## API liên quan

- `GET /api/v1/customers` — danh sách + search keyword
- `POST /api/v1/customers` — tạo khách hàng
- `PUT /api/v1/customers/{id}` — cập nhật
- `POST /api/v1/customers/points` — cộng điểm theo phone
- `DELETE /api/v1/customers/{id}` — xoá

## Quyền (Authorization)

- `Manage Customers`

## Dữ liệu & persistence

- SQLite: bảng `customers`
- MySQL (profile `api-mysql`): bảng `customers`

## Luồng chính (tóm tắt)

- (1) CRUD customer
- (2) Khi checkout có thể cộng điểm theo phone (tuỳ use case)

## Troubleshooting

| Triệu chứng | Nguyên nhân | Cách xử |
|---|---|---|
| Trùng số điện thoại | Unique constraint | Kiểm tra `CUSTOMER_PHONE_DUPLICATE` |
| `401/403` | Thiếu header hoặc thiếu quyền | Xem `docs/modules/web-api.md` |

## Liên kết

- Module Orders (checkout): `docs/modules/orders.md`
