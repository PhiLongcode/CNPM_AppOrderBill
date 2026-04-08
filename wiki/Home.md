# AppOrderBill Wiki

> Wiki nội bộ cho dev: chạy dự án, đọc module, gọi API, phân quyền, và vận hành.

## Tổng quan nhanh

AppOrderBill gồm 2 luồng:

- **POS Desktop (JavaFX + SQLite)**: gọi món, in phiếu bếp/hoá đơn, quản lý bàn/menu/khách hàng.
- **REST API (Spring Boot + Swagger)**: API cho menu, orders, billing, kitchen, tables, reporting, printer, system; có phân quyền (Identity).

## Hình ảnh minh hoạ

### POS Desktop

| Màn hình | Ảnh |
|---|---|
| Order/Thu ngân | ![Order](images/ui-order-screen.png) |
| Quản lý Menu | ![Menu](images/ui-menu-management.png) |
| Phân quyền | ![Permissions](images/ui-permissions.png) |
| Cấu hình máy in | ![Printer config](images/ui-printer-config.png) |

### Swagger UI / API

- Tổng quan Swagger: ![Swagger overview](images/swagger-overview.png)
- Danh sách endpoint: ![Swagger endpoints](images/swagger-endpoints.png)

### Mẫu in

- Phiếu bếp: ![Kitchen ticket](images/kitchen-ticket-sample.png)
- Hoá đơn: ![Receipt](images/receipt-sample.png)

## Quick start

### Chạy API (mặc định, SQLite)

```bash
mvn -DskipTests spring-boot:run
```

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`

### Chạy POS Desktop (JavaFX)

```bash
mvn javafx:run
```

## Authorization (API)

- Header: `X-Username: <username>`
- `401`: thiếu header hoặc user không tồn tại
- `403`: user tồn tại nhưng không đủ quyền

## Sơ đồ luồng chính (tóm tắt)

```mermaid
flowchart TD
  PosDesktop[POS_Desktop(JavaFX)] -->|Order_actions| Orders[Orders_Module]
  Orders -->|Checkout| Billing[Billing_Module]
  Orders -->|Print_kitchen| Kitchen[Kitchen_Module]
  Billing -->|Print_receipt| Printer[Printer_Module]
  Billing --> Reporting[Reporting_Module]
  WebApi[REST_API(Web_layer)] --> Orders
  WebApi --> Billing
  WebApi --> Kitchen
  WebApi --> Reporting
  WebApi --> Catalog[Catalog_Module]
  WebApi --> Customer[Customer_Module]
  WebApi --> Printer
  WebApi --> Table[Table_Module]
  WebApi --> System[System_Module]
  WebApi -->|AuthZ| Identity[Identity_Module]
```

## Link nhanh

- Getting started: `Getting-started`
- Testing: `Testing`
- Web API: `Web-API`
- Identity (phân quyền): `Identity`
- Orders/Checkout: `Orders`, `Billing`
