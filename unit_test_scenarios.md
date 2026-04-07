# UNIT TEST SCENARIOS (TABLE FORMAT)

Tài liệu này dùng cho bước tiếp theo: viết unit test toàn bộ logic hệ thống theo từng kịch bản.

## 0. Nguyên tắc chung áp dụng cho mọi kịch bản

- Mỗi ID trong bảng tương ứng ít nhất 1 test method độc lập.
- Mỗi test method chỉ verify 1 behavior chính.
- Ưu tiên assert cả output và side-effect (status, stock, persisted state).
- Với lỗi nghiệp vụ phải assert đúng `DomainException` và `ErrorCode`.
- Không dùng hạ tầng ngoài phạm vi unit test (network/UI/external process).
- Ưu tiên triển khai theo mức P0 -> P1 -> P2.

## 1. Catalog / Menu

| ID | Class/Use case | Scenario | Expected |
|---|---|---|---|
| UT-M01 | `CreateMenuItemUseCase` | Tạo món mới với dữ liệu hợp lệ | Món được lưu thành công, trạng thái mặc định đúng. |
| UT-M02 | `CreateMenuItemUseCase` | Tạo món với giá âm hoặc bằng 0 | Ném `DomainException` với `ErrorCode` tương ứng. |
| UT-M03 | `UpdateMenuItemUseCase` | Cập nhật món đang hoạt động | Dữ liệu món cập nhật đúng, không đổi ID. |
| UT-M04 | `UpdateMenuItemUseCase` | Cập nhật món không tồn tại | Ném lỗi `MENU_ITEM_NOT_FOUND` (hoặc code tương đương). |
| UT-M05 | `UpdateMenuItemStockUseCase` | Tăng tồn kho hợp lệ | `currentStockQuantity` tăng đúng giá trị. |
| UT-M06 | `UpdateMenuItemStockUseCase` | Giảm tồn kho vượt quá tồn hiện tại | Ném lỗi `MENU_ITEM_STOCK_INSUFFICIENT`. |
| UT-M07 | `SqliteMenuItemRepository` | `findById` với ID tồn tại | Trả về `Optional` có dữ liệu đúng cột. |
| UT-M08 | `SqliteMenuItemRepository` | `decreaseStockAtomic` khi đủ kho | Trả về `true`, tồn kho giảm đúng. |
| UT-M09 | `SqliteMenuItemRepository` | `decreaseStockAtomic` khi thiếu kho | Trả về `false`, tồn kho giữ nguyên. |

## 2. Orders / Use Cases

| ID | Class/Use case | Scenario | Expected |
|---|---|---|---|
| UT-O01 | `OpenOrCreateOrderUseCase` | Mở bàn chưa có order | Tạo order mới trạng thái `PENDING`, có `orderCode`. |
| UT-O02 | `OpenOrCreateOrderUseCase` | Mở bàn đang có order active | Không tạo mới, trả về order hiện có. |
| UT-O03 | `AddCustomItemToOrderUseCase` | Thêm món custom hợp lệ | Item được thêm, tổng tiền tăng đúng. |
| UT-O04 | `AddCustomItemToOrderUseCase` | Thêm custom item với quantity <= 0 | Ném lỗi validation. |
| UT-O05 | `AddMenuItemToOrderUseCase` | Thêm món có quản lý kho và kho đủ | Thêm item thành công, kho bị trừ đúng. |
| UT-O06 | `AddMenuItemToOrderUseCase` | Thêm món khi không đủ kho | Ném `MENU_ITEM_STOCK_INSUFFICIENT`, không đổi dữ liệu. |
| UT-O07 | `UpdateOrderItemQuantityUseCase` | Tăng số lượng item | Quantity và tổng tiền cập nhật đúng, kho cập nhật tương ứng. |
| UT-O08 | `UpdateOrderItemQuantityUseCase` | Đặt quantity = 0 hoặc âm | Ném lỗi validation, dữ liệu không đổi. |
| UT-O09 | `DeleteOrderItemUseCase` | Xóa món chưa in bếp | Item bị xóa, tổng tiền giảm, kho hoàn lại. |
| UT-O10 | `RemoveOrderItemUseCase` | Hủy món (cancel) | Item còn trong order nhưng không tính tiền, kho hoàn lại. |
| UT-O11 | `CancelOrderUseCase` | Hủy order có nhiều món | Status chuyển `CANCELLED`, kho hoàn toàn bộ. |
| UT-O12 | `CalculateOrderTotalUseCase` | Tính tổng với `discountAmount` | `subtotal/finalAmount` đúng công thức. |
| UT-O13 | `CalculateOrderTotalUseCase` | Tính tổng với `discountPercent` | Giảm theo phần trăm đúng, `finalAmount` không âm. |

## 3. Checkout / Billing

| ID | Class/Use case | Scenario | Expected |
|---|---|---|---|
| UT-B01 | `CheckoutOrderUseCase` | Checkout hợp lệ, không giảm giá | Tạo payment, order `COMPLETED`, bàn được clear. |
| UT-B02 | `CheckoutOrderUseCase` | `paidAmount < finalAmount` | Ném `CHECKOUT_PAID_AMOUNT_INSUFFICIENT`, không tạo payment. |
| UT-B03 | `CheckoutOrderUseCase` | Có `discountAmount` và/hoặc `discountPercent` | Tính `finalAmount` đúng, không âm. |
| UT-B04 | `CheckoutOrderUseCase` | Checkout order không ở trạng thái payable | Ném `CHECKOUT_ORDER_NOT_PAYABLE_STATE`. |
| UT-B05 | `CheckoutOrderUseCase` | Checkout có `customerPhone` hợp lệ | Điểm khách hàng được cộng đúng (nếu bật customer flow). |
| UT-B06 | `PrintReceiptUseCase` | In hóa đơn với payment hợp lệ | Gọi service in đúng, không lỗi runtime. |

## 4. Kitchen / Printing

| ID | Class/Use case | Scenario | Expected |
|---|---|---|---|
| UT-P01 | `PrintKitchenTicketUseCase` | In tất cả món chưa in | Chỉ món chưa in được in, sau in set cờ printed đúng. |
| UT-P02 | `PrintKitchenTicketUseCase` | Không có món cần in | Trả kết quả không in, không ném exception. |
| UT-P03 | `PrintSelectedItemsUseCase` | In theo danh sách item ID được chọn | Chỉ item được chọn bị ảnh hưởng trạng thái in. |
| UT-P04 | `PrintSelectedItemsUseCase` | Danh sách item ID rỗng | Không in, trả output phù hợp, dữ liệu không đổi. |

## 5. Identity / Repository

| ID | Class/Use case | Scenario | Expected |
|---|---|---|---|
| UT-I01 | `ModuleRepositoryImpl` | Lưu module mới | Gán ID tự tăng, `findById` trả đúng module. |
| UT-I02 | `ModuleRepositoryImpl` | Lưu module trùng tên | Vi phạm unique, xử lý lỗi đúng kỳ vọng. |
| UT-I03 | `ModuleRepositoryImpl` | Cập nhật module hiện có | Tên module cập nhật đúng sau `save`. |
| UT-I04 | `ModuleRepositoryImpl` | Xóa module hiện có | `findById` sau xóa trả `Optional.empty`. |

## 6. Mức ưu tiên viết test

| Priority | Nhóm | Gợi ý thứ tự triển khai |
|---|---|---|
| P0 | Orders + Checkout + Stock atomic | Viết trước để khóa các logic tiền/tồn kho quan trọng. |
| P1 | Kitchen printing + Catalog CRUD | Viết sau P0 để giảm regression ở luồng vận hành. |
| P2 | Identity/repository phụ trợ | Hoàn thiện độ phủ và ổn định nền tảng. |

## 7. Definition of Done cho mỗi kịch bản

| Tiêu chí | Mô tả hoàn thành |
|---|---|
| Naming | Method test theo mẫu `action_condition_expectedResult`. |
| AAA | Tách rõ Arrange, Act, Assert. |
| Assertion | Assert cả output lẫn side-effect quan trọng. |
| Error code | Với lỗi nghiệp vụ phải assert đúng `ErrorCode`. |
| Isolation | Không phụ thuộc thứ tự chạy test khác. |
| Execution | Chạy pass bằng `mvn test`. |
| Principles | Tuân thủ đầy đủ mục "Nguyên tắc chung áp dụng cho mọi kịch bản". |
