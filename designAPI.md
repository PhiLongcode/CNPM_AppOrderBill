# API Design - AppOrderBill

## 1) Scope

- Base URL: `/api/v1`
- Controllers in scope:
  - `MenuController`
  - `OrdersController`
  - `BillingController`
  - `KitchenController`
  - `TableController`
  - `ReportingController`
  - `PrinterController`
  - `SystemController`
  - `CustomerController`
  - `IdentityController`

## 2) API Conventions

- Content type: `application/json`
- Authorization header (required for protected endpoints): `X-Username: <username>`
- Date-time format: ISO-8601 (`yyyy-MM-dd'T'HH:mm:ss`)
- Error format: Spring `ProblemDetail` (from `ApiExceptionHandler`)
  - `status`, `title`, `detail`, `type`
  - custom props: `code`, `field` (when validation fails)
- Status code conventions:
  - `200 OK`: successful read/write command result
  - `201 Created`: resource created (tables)
  - `204 No Content`: successful delete/clear
  - `400 Bad Request`: validation/domain input issues
  - 401 UnAuthorization: 
  - `404 Not Found`: missing resource
  - `409 Conflict`: business conflict
  - `500 Internal Server Error`: unexpected error

## 3) Versioning

- Current version: `v1`
- Version strategy: URL-based (`/api/v1/...`)
- Backward-incompatible change => new prefix `/api/v2`

## 4) Endpoint Design

## 4.1 Menu


| Method | Path                  | Request                    | Response                    |
| ------ | --------------------- | -------------------------- | --------------------------- |
| GET    | `/api/v1/menu`        | -                          | `List<MenuItemOutput>`      |
| GET    | `/api/v1/menu/active` | -                          | `List<MenuItemOutput>`      |
| POST   | `/api/v1/menu`        | `CreateMenuItemInput`      | `MenuItemOutput`            |
| PUT    | `/api/v1/menu`        | `UpdateMenuItemInput`      | `MenuItemOutput`            |
| DELETE | `/api/v1/menu`        | `DeleteMenuItemInput`      | `204`                       |
| POST   | `/api/v1/menu/import` | `ImportMenuFromExcelInput` | `ImportMenuFromExcelOutput` |


## 4.2 Orders


| Method | Path                                    | Request                        | Response                        |
| ------ | --------------------------------------- | ------------------------------ | ------------------------------- |
| POST   | `/api/v1/orders/open`                   | `OpenOrCreateOrderInput`       | `OpenOrCreateOrderOutput`       |
| POST   | `/api/v1/orders/{orderId}/details`      | -                              | `GetOrderDetailsOutput`         |
| POST   | `/api/v1/orders/{orderId}/items/menu`   | `AddMenuItemInput`             | `AddCustomItemOutput`           |
| POST   | `/api/v1/orders/{orderId}/items/custom` | `AddCustomItemInput`           | `AddCustomItemOutput`           |
| PUT    | `/api/v1/orders/items/quantity`         | `UpdateOrderItemQuantityInput` | `UpdateOrderItemQuantityOutput` |
| PUT    | `/api/v1/orders/items/note`             | `UpdateOrderItemNoteInput`     | `UpdateOrderItemNoteOutput`     |
| PUT    | `/api/v1/orders/items/discount`         | `UpdateOrderItemDiscountInput` | `UpdateOrderItemDiscountOutput` |
| DELETE | `/api/v1/orders/items/delete`           | `DeleteOrderItemInput`         | `DeleteOrderItemOutput`         |
| POST   | `/api/v1/orders/items/remove`           | `RemoveOrderItemInput`         | `RemoveOrderItemOutput`         |
| POST   | `/api/v1/orders/total`                  | `CalculateOrderTotalInput`     | `CalculateOrderTotalOutput`     |
| POST   | `/api/v1/orders/checkout`               | `CheckoutOrderInput`           | `CheckoutOrderOutput`           |
| POST   | `/api/v1/orders/cancel`                 | `CancelOrderInput`             | `CancelOrderOutput`             |


## 4.3 Billing


| Method | Path                                          | Request                        | Response                     |
| ------ | --------------------------------------------- | ------------------------------ | ---------------------------- |
| GET    | `/api/v1/billing/payments/today`              | -                              | `List<PaymentSummaryOutput>` |
| GET    | `/api/v1/billing/payments/date/{date}`        | path `date: yyyy-MM-dd`        | `List<PaymentSummaryOutput>` |
| GET    | `/api/v1/billing/payments/range`              | query `start,end` ISO datetime | `List<PaymentSummaryOutput>` |
| GET    | `/api/v1/billing/payments/{paymentId}`        | -                              | `PaymentDetailOutput`        |
| DELETE | `/api/v1/billing/payments/range`              | query `start,end` ISO datetime | `204`                        |
| POST   | `/api/v1/billing/receipt/print`               | `PrintReceiptInput`            | `PrintReceiptOutput`         |
| POST   | `/api/v1/billing/receipt/reprint/{paymentId}` | -                              | `PrintReceiptOutput`         |


## 4.4 Kitchen


| Method | Path                              | Request                   | Response                   |
| ------ | --------------------------------- | ------------------------- | -------------------------- |
| POST   | `/api/v1/kitchen/ticket`          | `PrintKitchenTicketInput` | `PrintKitchenTicketOutput` |
| POST   | `/api/v1/kitchen/ticket/selected` | `PrintSelectedItemsInput` | `PrintKitchenTicketOutput` |


## 4.5 Tables


| Method | Path                   | Request            | Response                 |
| ------ | ---------------------- | ------------------ | ------------------------ |
| GET    | `/api/v1/tables`       | -                  | `List<TableOutput>`      |
| POST   | `/api/v1/tables`       | `AddTableInput`    | `AddTableOutput` (`201`) |
| DELETE | `/api/v1/tables`       | `DeleteTableInput` | `204`                    |
| POST   | `/api/v1/tables/clear` | `ClearTableInput`  | `204`                    |


## 4.6 Reporting


| Method | Path                                | Request                              | Response               |
| ------ | ----------------------------------- | ------------------------------------ | ---------------------- |
| GET    | `/api/v1/reporting/revenue/daily`   | query `date` optional (`yyyy-MM-dd`) | `RevenueOutput`        |
| GET    | `/api/v1/reporting/revenue/weekly`  | -                                    | `RevenueOutput`        |
| GET    | `/api/v1/reporting/revenue/monthly` | -                                    | `RevenueOutput`        |
| GET    | `/api/v1/reporting/revenue/range`   | query `start,end` (`yyyy-MM-dd`)     | `RevenueSummaryOutput` |


## 4.7 Printer


| Method | Path                               | Request                    | Response                    |
| ------ | ---------------------------------- | -------------------------- | --------------------------- |
| GET    | `/api/v1/printer/configs`          | -                          | `List<PrinterConfigOutput>` |
| PUT    | `/api/v1/printer/configs`          | `UpdatePrinterConfigInput` | `PrinterConfigOutput`       |
| GET    | `/api/v1/printer/templates/{type}` | -                          | `PrintTemplateOutput`       |
| PUT    | `/api/v1/printer/templates`        | `UpdatePrintTemplateInput` | `PrintTemplateOutput`       |


## 4.8 System


| Method | Path                     | Request | Response             |
| ------ | ------------------------ | ------- | -------------------- |
| GET    | `/api/v1/system/storage` | -       | `StorageUsageOutput` |


## 4.9 Customers


| Method | Path                       | Request                  | Response           |
| ------ | -------------------------- | ------------------------ | ------------------ |
| GET    | `/api/v1/customers`        | query `keyword` optional | `List<Customer>`   |
| POST   | `/api/v1/customers`        | `{name, phone, points}`  | `Customer` (`201`) |
| PUT    | `/api/v1/customers/{id}`   | `{name, phone, points}`  | `Customer`         |
| POST   | `/api/v1/customers/points` | `{phone, pointsToAdd}`   | `Customer`         |
| GET    | `/api/v1/customers/loyalty-config` | -                | `{earnUnitAmount, pointsPerUnit, redeemPointsRequired, redeemValue}` |
| PUT    | `/api/v1/customers/loyalty-config` | same as response | same as response |
| DELETE | `/api/v1/customers/{id}`   | -                        | `204`              |


## 4.10 Identity (Authorization)


| Method | Path                                                     | Request                                             | Response                                   |
| ------ | -------------------------------------------------------- | --------------------------------------------------- | ------------------------------------------ |
| POST   | `/api/v1/identity/login`                                 | `{username, password}`                              | `LoginOutput`                              |
| POST   | `/api/v1/identity/check-access`                          | `{username,functionName,requiresOperatePermission}` | `boolean`                                  |
| GET    | `/api/v1/identity/users`                                 | -                                                   | `List<User>`                               |
| POST   | `/api/v1/identity/users`                                 | `{username,password,roleGroupId}`                   | `ManageUserOutput` (`201`)                 |
| PUT    | `/api/v1/identity/users/{userId}`                        | `{username,password,roleGroupId}`                   | `ManageUserOutput`                         |
| DELETE | `/api/v1/identity/users/{userId}`                        | -                                                   | `204`                                      |
| GET    | `/api/v1/identity/role-groups`                           | -                                                   | `List<RoleGroup>`                          |
| POST   | `/api/v1/identity/role-groups`                           | `{name,description,functionIds}`                    | `ManageRoleGroupOutput` (`201`)            |
| PUT    | `/api/v1/identity/role-groups/{roleGroupId}`             | `{name,description,functionIds}`                    | `ManageRoleGroupOutput`                    |
| DELETE | `/api/v1/identity/role-groups/{roleGroupId}`             | -                                                   | `204`                                      |
| GET    | `/api/v1/identity/role-groups/{roleGroupId}/functions`   | -                                                   | `Set<Function>`                            |
| GET    | `/api/v1/identity/permission-assignments`                | -                                                   | `List<PermissionAssignment>`               |
| POST   | `/api/v1/identity/permission-assignments`                | `{roleGroupId,functionId,canView,canOperate}`       | `ManagePermissionAssignmentOutput` (`201`) |
| PUT    | `/api/v1/identity/permission-assignments/{assignmentId}` | `{roleGroupId,functionId,canView,canOperate}`       | `ManagePermissionAssignmentOutput`         |
| DELETE | `/api/v1/identity/permission-assignments/{assignmentId}` | -                                                   | `204`                                      |
| GET    | `/api/v1/identity/modules`                               | -                                                   | `List<Module>`                             |
| GET    | `/api/v1/identity/functions`                             | -                                                   | `List<Function>`                           |
| GET    | `/api/v1/identity/modules/{moduleId}/functions`          | -                                                   | `List<Function>`                           |


## 5) DTO Boundary

- API only exposes DTOs from `usecase.dto` packages.
- Domain models (`Order`, `MenuItem`, `Payment`) are not exposed directly.
- Future extension:
  - add dedicated request/response DTOs in `web.dto` to decouple public API from use-case contracts.

## 6) Error Examples

```json
{
  "type": "about:blank#domain",
  "title": "Conflict",
  "status": 409,
  "detail": "Không đủ tồn kho cho món",
  "code": "MENU_ITEM_STOCK_INSUFFICIENT"
}
```

```json
{
  "type": "about:blank#validation",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "code": "COMMON_VALIDATION_FAILED",
  "field": "quantity"
}
```

## 7) Database Target

- API profile uses MySQL (`api-mysql`) with schema `apporderbill`.
- POS desktop flow keeps SQLite untouched.
- API write/read tables:
  - `orders`, `order_items`, `payments`, `menu_items`, `tables`
  - `printer_configs`, `print_templates`, `categories`
  - `customers`, `point_transactions`, `settings` (loyalty config)

## 8) Docker Runtime

- Services:
  - `apporder-api` (Spring Boot)
  - `mysql` (8.4+)
- App depends on MySQL health before startup.
- Persist MySQL via named volume.

