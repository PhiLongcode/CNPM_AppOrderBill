# Mã lỗi và mã thành công — Quy ước bảo trì (AppOrderBill)

Tài liệu này ghi nhận **quy ước** và **lộ trình refactor** dùng mã ổn định (machine-readable) thay cho chuỗi tiếng Việt rải rác trong domain/use case, phục vụ REST API (RFC 7807) và giao diện JavaFX (dịch theo key).

**Phạm vi:** backend Spring Boot + domain chung với ứng dụng POS JavaFX. Không thay đổi hành vi nghiệp vụ; chỉ chuẩn hóa cách báo lỗi / thành công có mã để tra cứu và i18n.

---

## 1. Mục tiêu

- Client (web/mobile) và log server có **mã lỗi cố định** để lọc, cảnh báo, tài liệu hóa.
- Thông điệp hiển thị cho người dùng lấy từ **MessageSource** (VI/EN), không hard-code trong entity.
- API REST trả về **Problem Details** (Spring `ProblemDetail`), có thêm trường **`code`** (mã domain).
- **Mã đã công bố** (đã tài liệu / đã dùng ở client) được coi như **hợp đồng công khai**: chỉ bổ sung mã mới hoặc deprecate có thời hạn; không đổi ý nghĩa mã cũ.

---

## 2. Định dạng mã lỗi (domain)

**Pattern:** `MODULE.SNAKE_CASE_IDENTIFIER` (ASCII, không dấu).

| Tiền tố gợi ý | Module |
|----------------|--------|
| `ORDER.*` | Đơn hàng, dòng order, in bếp |
| `MENU.*` | Thực đơn, món, import Excel |
| `TABLE.*` | Bàn, chuyển bàn, đặt trước, đổi tên |
| `KITCHEN.*` | Bếp, trạng thái in |
| `BILLING.*` | Thanh toán, hóa đơn |
| `AUTH.*` | Đăng nhập / phiên (nếu có) |
| `COMMON.*` | Lỗi dùng chung (ví dụ validation tổng quát) |

Ví dụ (minh họa, đặt tên theo nghiệp vụ thực tế khi implement):

- `ORDER.NOT_FOUND`
- `ORDER.ALREADY_PRINTED`
- `TABLE.NAME_DUPLICATE`
- `MENU.ITEM_INACTIVE`

Mã gắn với một **HTTP status** khi expose REST (xem mục 4).

---

## 3. Kiểu triển khai trong code

- **`DomainException`**: ngoại lệ nghiệp vụ mang **`ErrorCode`**, tùy chọn **`Object[] args`** cho placeholder trong bundle (`messages.properties` / `MessageSource`).
- **`ErrorCode`**: enum hoặc interface có hằng số chuỗi trùng giá trị mã domain; tránh typo khi throw.
- **Entity / use case:** chỉ `throw new DomainException(ErrorCode.ORDER_NOT_FOUND, …)` — không nhúng câu tiếng Việt trong `IllegalArgumentException` nếu lỗi cần hiển thị cho user hoặc client.

**JavaFX:** presenter/controller bắt `DomainException`; text hiển thị lấy từ bundle với key **`error.<ENUM_NAME>`** (trùng tên hằng trong `ErrorCode`), hoặc `DomainMessages.format(ex)` đọc `messages.properties` UTF-8.

---

## 4. REST: ProblemDetail và HTTP status

Spring Boot 3.2+ dùng `org.springframework.http.ProblemDetail`. Quy ước bổ sung:

| Trường | Nguồn |
|--------|--------|
| `type` | URI cố định theo loại lỗi (có thể dùng namespace nội bộ, ví dụ `https://apporderbill.local/problems/validation`) |
| `title` | Tiêu đề ngắn (có thể lấy từ message bundle theo locale) |
| `status` | HTTP status |
| `detail` | Chi tiết (có thể là message đã dịch hoặc thông điệp an toàn cho log) |
| **`code`** (property tùy chỉnh) | **Mã domain** (`ORDER.NOT_FOUND`, …) — bắt buộc cho lỗi nghiệp vụ |

**Ánh xạ HTTP gợi ý:**

- 400 — đầu vào / rule nghiệp vụ từ client (`COMMON.VALIDATION`, `TABLE.INVALID_NAME`, …)
- 404 — thực thể không tồn tại (`ORDER.NOT_FOUND`, …)
- 409 — xung đột trạng thái (`ORDER.ALREADY_PRINTED`, …)
- 422 — semantically invalid (nếu team muốn tách với 400)
- 500 — lỗi không mong đợi (không expose chi tiết nội bộ)

Handler tập trung: `@RestControllerAdvice` map `DomainException` → `ProblemDetail` + `code`; map `MethodArgumentNotValidException` → 400 với `code` thống nhất (ví dụ `COMMON.VALIDATION`).

---

## 5. i18n (`messages.properties`)

- Key theo mã: ví dụ `error.ORDER_NOT_FOUND=Đơn hàng không tồn tại.` và tương ứng trong `messages_en.properties`.
- Placeholder dùng `{0}`, `{1}` nếu `DomainException` truyền `args`.
- Khi thêm mã mới: **luôn** thêm cặp VI/EN trong cùng PR.

---

## 6. Mã thành công

**REST (HTTP):** Thành công mặc định là **mã trạng thái HTTP 2xx** (`200`, `201`, `204`, …). Không bắt buộc thêm trường `success: true` trong body trừ khi API cần **payload thống nhất** (ví dụ envelope cho tích hợp cũ).

**Tùy chọn envelope (khi cần):**

```json
{
  "ok": true,
  "data": { }
}
```

- **`ok: true`** chỉ nên dùng khi toàn bộ API dùng một schema response; tránh trộn lẫn endpoint có/không có envelope.
- Không dùng “mã thành công” dạng số tùy tiện trùng với mã HTTP; nếu cần mã nghiệp vụ cho báo cáo/async, dùng **`resultCode`** rõ ràng (ví dụ `EXPORT_JOB_STARTED`) trong tài liệu OpenAPI, tách biệt với HTTP.

**JavaFX:** Thành công thường là cập nhật UI + toast/snackbar; nếu cần log/telemetry, có thể dùng hằng số `ActionResult.SUCCESS` trong presenter — không nhất thiết phải trùng với REST.

**Chuỗi thành công có tham số (POS / refactor bàn):** các key `success.*` trong `messages.properties` / `messages_en.properties`, ví dụ `success.transferOrderBetweenTables`, `success.renameTable`, `success.setTableReserved`, `success.clearTableReservation`, `success.addTable`, `success.transferTableById` — dùng với `DomainMessages.formatKey("success.xxx", args)` hoặc `MessageSource` tương đương.

**Chuỗi UI JavaFX:** chuẩn key `ui.<module>.*` (ví dụ `ui.order.*`, `ui.dashboard.*`, `ui.customer.*`, `ui.menu.*`, `ui.table.*`, `ui.admin.*`) và **không hard-code literal user-facing** trực tiếp trong controller/presenter.

---

## 7. Lộ trình refactor (đề xuất)

Thực hiện **theo từng package** để dễ review và giảm rủi ro:

1. **Hạ tầng:** `DomainException`, `ErrorCode`, `RestControllerAdvice` + `ProblemDetail`, cặp `messages*.properties` mẫu.
2. **Orders** — thay `IllegalArgumentException` / `IllegalStateException` mang ý nghĩa user-visible bằng `DomainException` + mã.
3. **Menu** → **Table** → **Kitchen** → **Billing** → các phần còn lại.
4. **Cầu nối:** Tạm thời có thể map một số `IllegalArgumentException` (message cố định) sang mã generic trong advice cho đến khi migration xong; sau đó **gỡ** map đó.

Sau mỗi phase: cập nhật bảng mã dưới đây (hoặc file con `docs/error-codes-catalog.md` nếu danh sách dài).

---

## 8. Catalog mã (`ErrorCode`)

Nguồn chân lý: enum `ErrorCode` tại `src/main/java/com/giadinh/apporderbill/shared/error/ErrorCode.java`. REST: `ProblemDetail` + property **`code`** = `ErrorCode.name()`. i18n lỗi: **`error.<ENUM_NAME>`** trong `src/main/resources/messages.properties` và `messages_en.properties` (bản VI trong `messages.properties` dùng cho `DomainMessages` trên desktop).

Bảng dưới đây liệt kê **toàn bộ** mã hiện có (đồng bộ với enum). Mô tả đầy đủ theo locale xem `messages.properties` / `messages_en.properties`.

### 8.1 Đơn hàng & dòng order

| Mã (`code`) | HTTP |
|-------------|------|
| `ORDER_NOT_FOUND` | 404 |
| `ORDER_MENU_ITEM_NOT_FOUND` | 404 |
| `ORDER_ITEM_NOT_FOUND` | 404 |
| `ORDER_NOT_MODIFIABLE` | 409 |
| `ORDER_NEW_TABLE_ID_REQUIRED` | 400 |
| `ORDER_ITEM_QUANTITY_INVALID` | 400 |

### 8.2 Bàn, chuyển bàn, đặt trước, đổi tên

| Mã (`code`) | HTTP |
|-------------|------|
| `TABLE_NOT_AVAILABLE_FOR_ASSIGN` | 409 |
| `TABLE_ID_REQUIRED` | 400 |
| `TABLE_NOT_FOUND` | 404 |
| `SOURCE_TABLE_NOT_FOUND` | 404 |
| `TARGET_TABLE_NOT_FOUND` | 404 |
| `SOURCE_TABLE_NO_ACTIVE_ORDER` | 409 |
| `TARGET_TABLE_NOT_EMPTY` | 409 |
| `TABLE_NUMBER_REQUIRED` | 400 |
| `NO_ACTIVE_ORDER_FOR_TABLE` | 404 |
| `ORDER_HAS_ITEMS_CANNOT_RELEASE` | 409 |
| `TRANSFER_SOURCE_TABLE_NAME_REQUIRED` | 400 |
| `TRANSFER_TARGET_TABLE_NAME_REQUIRED` | 400 |
| `TRANSFER_SAME_SOURCE_AND_TARGET_TABLE` | 400 |
| `TABLE_DISPLAY_NAME_REQUIRED` | 400 |
| `RENAME_TABLE_ACTIVE_ORDER_EXISTS` | 409 |
| `TABLE_NAME_DUPLICATE` | 409 |
| `TABLE_ADD_NAME_INVALID` | 400 |
| `TABLE_RESERVATION_BLOCKED_BY_ACTIVE_ORDER` | 409 |
| `TABLE_RESERVATION_BLOCKED_BY_LINKED_ORDER` | 409 |
| `TABLE_RESERVATION_OCCUPIED_CANNOT_RESERVE` | 409 |
| `TABLE_NOT_IN_RESERVED_STATE` | 400 |

### 8.3 Thanh toán (checkout)

| Mã (`code`) | HTTP | Ghi chú |
|-------------|------|---------|
| `CHECKOUT_ORDER_ID_REQUIRED` | 400 | |
| `CHECKOUT_ORDER_NOT_PAYABLE_STATE` | 409 | |
| `CHECKOUT_PAID_AMOUNT_INSUFFICIENT` | 400 | args: `{0}` = số tiền cần (VNĐ) |

### 8.4 Bếp, in phiếu bếp, in món chọn

| Mã (`code`) | HTTP |
|-------------|------|
| `KITCHEN_ORDER_ID_REQUIRED` | 400 |
| `KITCHEN_SELECTED_ITEMS_REQUIRED` | 400 |
| `KITCHEN_NO_ITEMS_TO_PRINT` | 400 |
| `KITCHEN_ORDER_NO_ITEMS_FOR_TICKET` | 400 |
| `PRINTER_KITCHEN_SEND_FAILED` | 503 |

### 8.5 In hóa đơn (receipt)

| Mã (`code`) | HTTP |
|-------------|------|
| `PRINT_RECEIPT_PAYMENT_ID_REQUIRED` | 400 |
| `PRINTER_RECEIPT_SEND_FAILED` | 503 |

### 8.6 Hóa đơn / thanh toán (bill domain)

| Mã (`code`) | HTTP |
|-------------|------|
| `BILL_NOT_FOUND` | 404 |
| `BILL_TOTAL_INVALID` | 400 |
| `BILL_PAYMENT_AMOUNT_INVALID` | 400 |
| `BILL_ALREADY_PAID` | 409 |
| `BILL_PAYMENT_STATE_INVALID` | 409 |

### 8.7 Thực đơn — validation tồn kho / giá / tên

| Mã (`code`) | HTTP |
|-------------|------|
| `MENU_ITEM_NAME_REQUIRED` | 400 |
| `MENU_ITEM_PRICE_INVALID` | 400 |
| `MENU_ITEM_CATEGORY_REQUIRED` | 400 |
| `MENU_ITEM_STOCK_LEVELS_INVALID` | 400 |
| `MENU_ITEM_STOCK_CURRENT_INVALID` | 400 |
| `MENU_ITEM_CANNOT_ACTIVATE_OUT_OF_STOCK` | 409 |
| `MENU_ITEM_STOCK_NOT_TRACKED` | 409 |
| `MENU_ITEM_STOCK_DECREASE_INVALID` | 400 |
| `MENU_ITEM_STOCK_INSUFFICIENT` | 409 |
| `MENU_ITEM_STOCK_INCREASE_INVALID` | 400 |
| `CATEGORY_NAME_REQUIRED` | 400 |

### 8.8 Catalog / use case quản lý món & danh mục

| Mã (`code`) | HTTP |
|-------------|------|
| `CATEGORY_NOT_FOUND` | 404 |
| `CATEGORY_NAME_DUPLICATE` | 409 |
| `MENU_ITEM_NOT_FOUND` | 404 |
| `MENU_ITEM_NAME_DUPLICATE` | 409 |
| `MENU_ITEM_STOCK_OPERATION_INVALID` | 400 |

### 8.9 Import menu (Excel)

| Mã (`code`) | HTTP |
|-------------|------|
| `MENU_IMPORT_EXCEL_NOT_CONFIGURED` | 500 |

### 8.10 Khách hàng

| Mã (`code`) | HTTP |
|-------------|------|
| `CUSTOMER_PHONE_REQUIRED` | 400 |
| `CUSTOMER_PHONE_DUPLICATE` | 409 |

### 8.11 Validation tên vai trò / quyền (biểu mẫu)

| Mã (`code`) | HTTP |
|-------------|------|
| `ROLE_NAME_REQUIRED` | 400 |
| `PERMISSION_NAME_REQUIRED` | 400 |

### 8.12 Đăng nhập & quản trị identity

| Mã (`code`) | HTTP |
|-------------|------|
| `AUTH_INVALID_CREDENTIALS` | 401 |
| `USER_ROLE_GROUP_NOT_FOUND` | 404 |
| `USER_USERNAME_DUPLICATE` | 409 |
| `USER_NOT_FOUND` | 404 |
| `ROLE_GROUP_NOT_FOUND` | 404 |
| `ROLE_GROUP_NAME_DUPLICATE` | 409 |
| `IDENTITY_FUNCTION_NOT_FOUND` | 404 |
| `PERMISSION_ASSIGNMENT_NOT_FOUND` | 404 |
| `PERMISSION_ASSIGNMENT_DUPLICATE` | 409 |

### 8.13 Hạ tầng & lỗi chung

| Mã (`code`) | HTTP |
|-------------|------|
| `SQLITE_CONNECTION_NOT_CONFIGURED` | 500 |
| `COMMON_VALIDATION_FAILED` | 400 |
| `RESOURCE_NOT_FOUND` | 404 |
| `INTERNAL_ERROR` | 500 |

---

## 9. Bảo trì sau này

- PR thêm lỗi nghiệp vụ mới **bắt buộc** có: mã trong catalog, key i18n, (nếu REST) entry trong OpenAPI mô tả `ProblemDetail`.
- **Breaking change:** đổi tên hoặc đổi ý nghĩa mã đã public tạo issue release note; ưu tiên thêm mã mới và deprecate mã cũ.

---

_Mục 8 đồng bộ với toàn bộ hằng trong `ErrorCode`; khi thêm mã mới vào enum cần cập nhật bảng này và cặp key `messages*.properties`._
