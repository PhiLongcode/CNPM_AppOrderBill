# Catalog — Menu, danh mục, tồn kho, Excel

## Chức năng

- Quản lý menu item, category
- Quản lý tồn kho (stock tracked)
- Import/Export Excel menu

## Thành phần chính (code map)

- Package: `src/main/java/com/giadinh/apporderbill/catalog/`
- Repository: `MenuItemRepository`, `CategoryRepository`
- Use case tiêu biểu: create/update/delete/get/import/export

## API liên quan

- `GET /api/v1/menu` — lấy toàn bộ menu
- `GET /api/v1/menu/active` — lấy menu đang active
- `POST /api/v1/menu` — tạo món
- `PUT /api/v1/menu` — cập nhật món
- `DELETE /api/v1/menu` — xoá món
- `POST /api/v1/menu/import` — import từ Excel

## Quyền (Authorization)

- `Manage Menu Items` (view/operate)

## Dữ liệu & persistence

- SQLite: bảng `menu_items` (mặc định)
- MySQL (profile `api-mysql`): bảng `menu_items`

## Luồng chính (tóm tắt)

- (1) Client gọi API `menu/*` kèm `X-Username`
- (2) Web layer kiểm tra quyền `Manage Menu Items`
- (3) Use case thao tác `MenuItemRepository`
- (4) Repository đọc/ghi SQLite hoặc MySQL theo profile

## Troubleshooting

| Triệu chứng | Nguyên nhân | Cách xử |
|---|---|---|
| `401/403` khi gọi menu | Thiếu header hoặc thiếu quyền | Xem `docs/modules/web-api.md` và `docs/modules/identity.md` |
| Import Excel lỗi | File/format sheet không đúng | Kiểm tra `ExcelService` và input import |

## Liên kết

- Thiết kế API: `designAPI.md`
- Web layer: `docs/modules/web-api.md`
