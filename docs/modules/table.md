# Table — Quản lý bàn

## Chức năng

- Quản lý danh sách bàn
- Thêm / xoá / clear bàn
- Hỗ trợ transfer order giữa các bàn

## Thành phần chính (code map)

- Package: `src/main/java/com/giadinh/apporderbill/table/`
- Repository: `TableRepository`
- Use case tiêu biểu: `AddTableUseCase`, `DeleteTableUseCase`, `ClearTableUseCase`, ...

## API liên quan

- `GET /api/v1/tables` — danh sách bàn
- `POST /api/v1/tables` — thêm bàn
- `DELETE /api/v1/tables` — xoá bàn
- `POST /api/v1/tables/clear` — clear bàn

## Quyền (Authorization)

- `Manage Tables`

## Dữ liệu & persistence

- SQLite: bảng `tables`
- MySQL (profile `api-mysql`): bảng `tables`

## Luồng chính (tóm tắt)

- (1) Thao tác danh mục bàn (add/delete/clear)
- (2) Khi mở order, bàn liên kết `current_order_id`

## Troubleshooting

| Triệu chứng | Nguyên nhân | Cách xử |
|---|---|---|
| Không add được bàn | Trùng tên bàn | Kiểm tra rule duplicate trong use case/repo |
| Bàn không clear được | Có order active | Kiểm tra logic clear/reservation |

## Liên kết

- Module Orders: `docs/modules/orders.md`
