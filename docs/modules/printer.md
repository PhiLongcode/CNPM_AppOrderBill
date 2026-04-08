# Printer — Cấu hình máy in & template

## Chức năng

- Quản lý cấu hình máy in
- Quản lý template in (hoá đơn / phiếu bếp)

## Thành phần chính (code map)

- Package: `src/main/java/com/giadinh/apporderbill/printer/`
- Repository: `PrinterConfigRepository`, `PrintTemplateRepository`

## API liên quan

- `GET /api/v1/printer/configs` — lấy cấu hình máy in
- `PUT /api/v1/printer/configs` — cập nhật cấu hình
- `GET /api/v1/printer/templates/{type}` — lấy template theo type
- `PUT /api/v1/printer/templates` — cập nhật template

## Quyền (Authorization)

- `Print Receipt`

## Dữ liệu & persistence

- SQLite: bảng `printer_configs`, `print_templates`
- MySQL (profile `api-mysql`): bảng `printer_configs`, `print_templates`

## Luồng chính (tóm tắt)

- (1) Lấy/cập nhật cấu hình & template
- (2) Billing/Kitchen sử dụng template để in

## Troubleshooting

| Triệu chứng | Nguyên nhân | Cách xử |
|---|---|---|
| Không thấy template | Chưa init defaults | Kiểm tra init repo template |
| In lỗi | Printer config sai | Cập nhật config rồi thử lại |

## Liên kết

- Module Billing: `docs/modules/billing.md`
- Module Kitchen: `docs/modules/kitchen.md`
