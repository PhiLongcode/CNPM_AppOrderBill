# Orders — Đơn hàng & order items

## Chức năng

- Mở/tạo đơn theo bàn
- Thêm món, sửa số lượng, ghi chú, giảm giá
- Tính tổng đơn, checkout, huỷ đơn

## Thành phần chính (code map)

- Package: `src/main/java/com/giadinh/apporderbill/orders/`
- Repository: `OrderRepository`
- Use case tiêu biểu: `OpenOrCreateOrderUseCase`, `AddMenuItemToOrderUseCase`, `CheckoutOrderUseCase`, ...

## API liên quan

- `POST /api/v1/orders/open` — mở/tạo order theo bàn
- `POST /api/v1/orders/{orderId}/details` — lấy chi tiết order
- `POST /api/v1/orders/{orderId}/items/menu` — thêm món từ menu
- `POST /api/v1/orders/{orderId}/items/custom` — thêm món custom
- `PUT /api/v1/orders/items/quantity` — cập nhật số lượng
- `PUT /api/v1/orders/items/note` — cập nhật ghi chú
- `PUT /api/v1/orders/items/discount` — cập nhật giảm giá item
- `POST /api/v1/orders/total` — tính tổng
- `POST /api/v1/orders/checkout` — thanh toán
- `POST /api/v1/orders/cancel` — huỷ order

## Quyền (Authorization)

- `Create Order`
- `Checkout Order`

## Dữ liệu & persistence

- SQLite: bảng `orders`, `order_items`
- MySQL (profile `api-mysql`): bảng `orders`, `order_items`

## Luồng chính (tóm tắt)

- (1) Mở/tạo order cho bàn
- (2) Thêm món / cập nhật item (số lượng, note, discount)
- (3) Tính tổng / checkout
- (4) Lưu order + cập nhật bàn (table) theo trạng thái

## Troubleshooting

| Triệu chứng | Nguyên nhân | Cách xử |
|---|---|---|
| `CHECKOUT_*` lỗi | Paid amount/ trạng thái order không hợp lệ | Xem `ErrorCode` + `ApiExceptionHandler` |
| `MENU_ITEM_STOCK_INSUFFICIENT` | Không đủ tồn kho | Kiểm tra stock và logic giảm tồn |

## Liên kết

- Thiết kế API: `designAPI.md`
- Module Billing: `docs/modules/billing.md`
