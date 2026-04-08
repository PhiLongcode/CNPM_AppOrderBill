# Billing — Thanh toán, hoá đơn, lịch sử

## Chức năng

- Quản lý giao dịch thanh toán (payments)
- In / in lại hoá đơn (receipt)
- Tra cứu lịch sử thanh toán theo ngày / khoảng ngày

## Thành phần chính (code map)

- Package: `src/main/java/com/giadinh/apporderbill/billing/`
- Repository: `PaymentRepository`
- Use case tiêu biểu: `PrintReceiptUseCase`, `GetPaymentDetailUseCase`, ...

## API liên quan

- `GET /api/v1/billing/payments/today` — danh sách payment hôm nay
- `GET /api/v1/billing/payments/date/{date}` — theo ngày
- `GET /api/v1/billing/payments/range?start=&end=` — theo khoảng datetime
- `GET /api/v1/billing/payments/{paymentId}` — chi tiết payment
- `DELETE /api/v1/billing/payments/range?start=&end=` — xoá theo khoảng
- `POST /api/v1/billing/receipt/print` — in hoá đơn
- `POST /api/v1/billing/receipt/reprint/{paymentId}` — in lại

## Quyền (Authorization)

- `Checkout Order`
- `Print Receipt`

## Dữ liệu & persistence

- SQLite: (hiện repo payment có nhánh in-memory/SQLite tuỳ cấu hình)
- MySQL (profile `api-mysql`): bảng `payments`

## Luồng chính (tóm tắt)

- (1) Checkout tạo `Payment`
- (2) Billing API tra cứu / xoá lịch sử
- (3) Print/reprint gửi lệnh in theo printer service

## Troubleshooting

| Triệu chứng | Nguyên nhân | Cách xử |
|---|---|---|
| Không thấy payment | Repo payment đang in-memory | Chạy profile đúng hoặc hoàn thiện persistence tuỳ mục tiêu |
| In hoá đơn lỗi | Printer service fail | Kiểm tra cấu hình printer/template |

## Liên kết

- Module Orders: `docs/modules/orders.md`
- Module Printer: `docs/modules/printer.md`
