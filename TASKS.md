## Checklist use case & tính năng

- [ ] **Hoàn thiện contract UI `OrderScreenView`**
  - [ ] Khai báo đầy đủ các method mà `OrderScreenPresenter` đang sử dụng.
  - [ ] Đảm bảo `OrderScreenController` implements đúng interface và compile được.

- [x] **Luồng thanh toán (Checkout) end-to-end**
  - [x] Đồng bộ lại `CheckoutOrderUseCase` constructor với cách khởi tạo trong `OrderPosApplication`.
  - [x] Cập nhật `CheckoutOrderInput`/`CheckoutOrderOutput` để khớp với logic trong `OrderScreenPresenter` (paymentId, orderId, paymentMethod, discount,...).
  - [x] Cài đặt đầy đủ logic trong `CheckoutOrderUseCase` (lưu payment/bill, cập nhật order, giải phóng bàn).
  - [x] Tạo/cập nhật DTO `PrintReceiptInput` và implement `PrintReceiptUseCase` để in hóa đơn.

- [x] **Lấy dữ liệu thanh toán từ UI**
  - [x] Thêm UI field/checkout dialog để nhập số tiền khách đưa, phương thức thanh toán.
  - [x] Cập nhật `OrderScreenController.getPaidAmount()` để đọc từ UI thay vì trả về `"0"`.
  - [x] Cập nhật `OrderScreenController.getPaymentMethod()` để đọc từ dropdown thay vì trả về `"CASH"`.
  - [x] Implement logic trong `CheckoutDialogController` để cung cấp dữ liệu cho presenter.

- [x] **Use case Billing (lịch sử thanh toán / in lại hóa đơn)**
  - [x] Implement `GetTodayPaymentsUseCase`, `GetPaymentsByDateRangeUseCase`, `GetPaymentDetailUseCase`, `DeletePaymentsByDateRangeUseCase`.
  - [x] Implement `ReprintReceiptUseCase` và DTO liên quan.
  - [x] Kết nối các use case này với `OrderScreenPresenter.setReprintUseCases(...)` và màn báo cáo/lịch sử thanh toán.

- [ ] **Use case in bếp (Kitchen)**
  - [ ] Implement `PrintKitchenTicketUseCase` và `PrintSelectedItemsUseCase`.
  - [ ] Đảm bảo các DTO in bếp đầy đủ thông tin món, bàn, ghi chú.
  - [ ] Kết nối với các nút in phiếu bếp trên `OrderScreenController` và cập nhật trạng thái món đã in.

- [ ] **Use case Menu (danh mục/mon ăn)**
  - [x] Implement `GetActiveMenuItemsUseCase` và `GetAllMenuItemsUseCase`.
  - [x] Implement CRUD: `CreateMenuItemUseCase`, `UpdateMenuItemUseCase`, `DeleteMenuItemUseCase`.
  - [ ] Implement `ImportMenuFromExcelUseCase` và `ExportMenuToExcelUseCase` (thêm `ExcelService` thay vì truyền `null`).
  - [x] Đảm bảo `MenuItemHandler` và các màn quản lý menu sử dụng các use case này.

- [ ] **Use case Order (quản lý order & item)**
  - [ ] Implement `OpenOrCreateOrderUseCase`, `AddCustomItemToOrderUseCase`, `AddMenuItemToOrderUseCase`, `GetOrderDetailsUseCase`.
  - [ ] Implement `CalculateOrderTotalUseCase`, `CancelOrderUseCase`.
  - [ ] Implement `UpdateOrderItemQuantityUseCase`, `RemoveOrderItemUseCase`, `DeleteOrderItemUseCase`, `UpdateOrderItemNoteUseCase`, `UpdateOrderItemDiscountUseCase`.
  - [ ] Viết unit test cơ bản cho các use case chính (add item, calculate total, cancel item).

- [ ] **Use case Table (bàn)**
  - [ ] Implement `AddTableUseCase`, `ClearTableUseCase`, `DeleteTableUseCase`, `GetAllTablesUseCase`.
  - [ ] Đảm bảo `TransferTableUseCase` làm việc cùng dữ liệu từ các use case trên.
  - [ ] Kết nối với `TableHandler` trên màn order và màn quản lý bàn (nếu có).

- [ ] **Use case Printer config/template**
  - [ ] Implement `GetPrinterConfigUseCase`, `UpdatePrinterConfigUseCase`.
  - [ ] Implement `GetPrintTemplateUseCase`, `UpdatePrintTemplateUseCase`.
  - [ ] Kết nối với màn cấu hình in và test in (`printTest`).

- [ ] **Use case System & Reporting**
  - [ ] Implement `CheckStorageUsageUseCase` và màn dialog kiểm tra dung lượng.
  - [ ] Implement `GetDailyRevenueUseCase`, `GetWeeklyRevenueUseCase`, `GetMonthlyRevenueUseCase`, `GetRevenueByDateRangeUseCase`.
  - [ ] Kết nối với dashboard/báo cáo doanh thu trong `MainLayoutController`.

- [ ] **Service in ấn**
  - [ ] Hoàn thiện `UsbThermalPrinterService` (tìm `PrintService` theo `printerName`, handle lỗi đầy đủ).
  - [ ] Kiểm thử in bếp, in hóa đơn, in test trên môi trường thật.

