# Identity (Authorization)

## Chức năng

- Login
- Quản lý user / role group / permission assignment
- Kiểm tra quyền theo function

## Các function quyền tiêu biểu

- `Manage Users`
- `Manage Roles`
- `Manage Permissions`
- `Create Order`
- `Checkout Order`
- `Manage Customers`
- `View Reports`

## Cách gọi API đúng quyền

- Dùng header `X-Username: <username>`
- Nếu user không tồn tại: `401`
- Nếu user thiếu quyền: `403`
