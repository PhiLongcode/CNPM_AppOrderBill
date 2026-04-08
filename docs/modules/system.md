# System — Thông tin vận hành

## Chức năng

- Cung cấp thông tin hệ thống phục vụ vận hành
- Hiện tại tập trung vào kiểm tra dung lượng lưu trữ

## API liên quan

- `GET /api/v1/system/storage` — kiểm tra dung lượng

## Quyền (Authorization)

- `Manage Users` (hiện tại đang dùng cho endpoint system)

## Dữ liệu & persistence

- Không phụ thuộc DB cụ thể; dùng `CheckStorageUsageUseCase`

## Luồng chính (tóm tắt)

- (1) Gọi endpoint system
- (2) Check quyền
- (3) Trả thông tin dung lượng

## Troubleshooting

| Triệu chứng | Nguyên nhân | Cách xử |
|---|---|---|
| `403` | User thiếu quyền | Gán quyền phù hợp cho role group |

## Liên kết

- Web layer: `docs/modules/web-api.md`
