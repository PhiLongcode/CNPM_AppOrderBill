# Identity — Đăng nhập & phân quyền

## Chức năng

- Đăng nhập
- Quản lý user, role group, permission assignment
- Kiểm tra quyền truy cập theo function

## Thành phần chính (code map)

- Package: `src/main/java/com/giadinh/apporderbill/identity/`
- Core component: `IdentityComponent`
- Seed dữ liệu mặc định: `IdentityDataInitializer`

## API liên quan

- `POST /api/v1/identity/login` — login
- `GET/POST/PUT/DELETE /api/v1/identity/users` — quản lý user
- `GET/POST/PUT/DELETE /api/v1/identity/role-groups` — quản lý role group
- `GET/POST/PUT/DELETE /api/v1/identity/permission-assignments` — gán quyền
- `GET /api/v1/identity/modules` — module list
- `GET /api/v1/identity/functions` — function list

## Quyền (Authorization) — function tiêu biểu

- `Manage Users`
- `Manage Roles`
- `Manage Permissions`

## Dữ liệu & persistence

- Hiện tại Identity dùng SQLite DB chung `output/pos.db` (xem `identity/config/IdentityConfig`)
- Bảng liên quan: users/role_groups/functions/modules/permission_assignments (theo repo sqlite impl)

## Luồng chính (tóm tắt)

- (1) Seed dữ liệu quyền mặc định khi app API start (`IdentityDataInitializer`)
- (2) Login trả `LoginOutput`
- (3) Các endpoint protected kiểm tra `X-Username` và function tương ứng

## Troubleshooting

| Triệu chứng | Nguyên nhân | Cách xử |
|---|---|---|
| User hợp lệ nhưng bị `403` | Chưa gán quyền function | Thêm permission assignment cho role group |
| User không tồn tại `401` | Chưa seed hoặc DB khác | Kiểm tra `output/pos.db` và init identity |

## Liên kết

- Web layer auth: `docs/modules/web-api.md`
