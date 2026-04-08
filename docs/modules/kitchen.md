# Kitchen — In phiếu bếp

## Chức năng

- In phiếu bếp cho toàn bộ đơn
- In phiếu bếp theo danh sách món được chọn

## Thành phần chính (code map)

- Package: `src/main/java/com/giadinh/apporderbill/kitchen/`
- Use case: `PrintKitchenTicketUseCase`, `PrintSelectedItemsUseCase`

## API liên quan

- `POST /api/v1/kitchen/ticket` — in phiếu bếp toàn bộ order
- `POST /api/v1/kitchen/ticket/selected` — in theo selection

## Quyền (Authorization)

- `Print Kitchen Ticket`

## Dữ liệu & persistence

- Có thể dùng `KitchenTicketRepository` (hiện interface tối giản) + printer service

## Luồng chính (tóm tắt)

- (1) Gọi endpoint in phiếu bếp
- (2) Kiểm tra quyền `Print Kitchen Ticket`
- (3) Lấy order items và format ticket
- (4) Gửi lệnh in

## Troubleshooting

| Triệu chứng | Nguyên nhân | Cách xử |
|---|---|---|
| Không in được | Printer service lỗi | Kiểm tra config printer + template |
| Báo “không có món để in” | Order không có items hoặc đã huỷ | Kiểm tra trạng thái order/items |

## Liên kết

- Module Orders: `docs/modules/orders.md`
