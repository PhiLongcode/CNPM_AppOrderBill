# UNIT TESTING STANDARD

## 1. Mục tiêu
- Đảm bảo logic nghiệp vụ chạy đúng, ổn định khi refactor.
- Phát hiện regression sớm trước khi merge.
- Tài liệu này áp dụng cho toàn bộ test unit trong project `AppOrderBill`.

## 2. Stack và phạm vi
- Ngôn ngữ: Java 21.
- Build tool: Maven.
- Framework test: JUnit 5 (`junit-jupiter-api`, `junit-jupiter-engine`).
- Unit test chỉ kiểm tra logic của 1 đơn vị (class/method/use case), không phụ thuộc UI JavaFX hoặc service bên ngoài.

## 3. Nguyên tắc chung (bắt buộc)
- **Đúng mục tiêu**: mỗi test chỉ xác minh 1 behavior nghiệp vụ chính.
- **Deterministic**: kết quả phải ổn định, không phụ thuộc thời gian ngẫu nhiên/thứ tự chạy.
- **Độc lập**: test không dùng trạng thái dùng chung có thể bị thay đổi bởi test khác.
- **Rõ ràng**: tên test thể hiện hành vi + điều kiện + kết quả mong đợi.
- **Có thể lặp lại**: chạy local/CI đều cho cùng kết quả.
- **Ưu tiên rủi ro cao**: luồng tiền/tồn kho/trạng thái order phải được cover trước.

## 4. Cấu trúc thư mục test
- Đặt test trong `src/test/java`, mirror theo package production.
- Quy ước tên file:
  - `<ClassName>Test.java` cho unit test class.
  - Tên method test theo dạng `action_condition_expectedResult`.

Ví dụ hiện có:
- `src/test/java/com/giadinh/apporderbill/orders/usecase/OrderUseCasesTest.java`
- `src/test/java/com/giadinh/apporderbill/identity/infrastructure/repository/sqlite/ModuleRepositoryImplTest.java`

## 5. Quy tắc viết unit test

### 4.1 Arrange - Act - Assert (AAA)
- **Arrange**: chuẩn bị dữ liệu, dependency giả (in-memory/fake/stub).
- **Act**: gọi đúng 1 hành động chính cần kiểm thử.
- **Assert**: xác nhận kết quả mong đợi bằng `assertEquals`, `assertTrue`, `assertThrows`, ...

### 4.2 Mỗi test chỉ kiểm tra 1 behavior chính
- Không nhồi nhiều logic vào 1 test.
- Nếu có nhiều behavior khác nhau, tách thành nhiều test methods.

### 4.3 Tính độc lập
- Test không phụ thuộc thứ tự chạy.
- Mỗi test tự setup dữ liệu riêng.
- Không dùng shared state có thể bị mutate chéo.

### 4.4 Không phụ thuộc hạ tầng thật
- Unit test không gọi network/external API.
- Tránh truy cập DB thật cho unit test nghiệp vụ.
- Ưu tiên fake repository/in-memory object cho use case.

### 4.5 Bao phủ tối thiểu theo nhóm case
Với mỗi use case/service quan trọng cần có:
- Case thành công (happy path).
- Case validation lỗi (input null, âm, thiếu dữ liệu).
- Case business rule lỗi (không đủ tồn kho, trạng thái order không hợp lệ).
- Case biên (0, max/min, empty list, null field).

## 6. Quy ước assertion và exception
- Với business error, ưu tiên `assertThrows(DomainException.class, ...)`.
- Khi dùng `DomainException`, assert thêm `ErrorCode` để đảm bảo đúng rule:
  - `assertEquals(ErrorCode.XYZ, ex.getErrorCode());`
- Với dữ liệu số tiền/số lượng, assert rõ ràng từng field (subtotal, finalAmount, quantity, stock).

## 7. Mẫu test tham chiếu trong project

### 6.1 Use case test (khuyến nghị)
Từ `OrderUseCasesTest`:
- Dùng fake repository in-memory để test logic thuần.
- Test cả success path và failure path bằng `assertThrows`.
- Verify side-effects nghiệp vụ: tồn kho giảm/tăng, trạng thái order đổi đúng.

### 6.2 Repository test (khi cần test truy vấn)
Từ `ModuleRepositoryImplTest`:
- Có `@BeforeAll`, `@BeforeEach`, `@AfterEach` để quản lý dữ liệu test.
- Mỗi test nên reset bảng dữ liệu để đảm bảo độc lập.
- Chỉ test repository behavior (CRUD/query), không trộn nghiệp vụ.

## 8. Checklist trước khi merge
- [ ] Test mới tuân thủ AAA, đọc tên test hiểu được behavior.
- [ ] Có cả happy path + ít nhất 1 case lỗi quan trọng.
- [ ] Không phụ thuộc dữ liệu từ test khác.
- [ ] Không hard-code phụ thuộc môi trường local (path, user-specific config).
- [ ] `mvn test` chạy pass toàn bộ.
- [ ] Không có flaky test (chạy lại nhiều lần vẫn pass ổn định).
- [ ] Có bám theo mục "Nguyên tắc chung (bắt buộc)" trong tài liệu này.

## 9. Lệnh chạy test
- Chạy toàn bộ test:
  - `mvn test`
- Chạy một test class:
  - `mvn -Dtest=OrderUseCasesTest test`
- Chạy một test method:
  - `mvn -Dtest=OrderUseCasesTest#addMenuItem_shouldDeductStock_andFailWhenInsufficient test`

## 10. Tiêu chí chất lượng unit test
- **Rõ ràng**: tên test mô tả được ý đồ.
- **Nhanh**: unit test chạy nhanh, không phụ thuộc IO nặng.
- **Tin cậy**: kết quả ổn định, không random/flaky.
- **Dễ bảo trì**: setup ngắn gọn, reuse helper hợp lý, tránh copy-paste dài.

## 11. Không làm trong unit test
- Không test giao diện JavaFX.
- Không test luồng tích hợp nhiều tầng trong cùng 1 unit test.
- Không dùng `Thread.sleep(...)` để chờ trạng thái.
- Không assert mơ hồ kiểu `assertTrue(true)` hoặc không assert gì.

---

**Version:** 1.0  
**Applies to:** `AppOrderBill` (Java 21, Maven, JUnit 5)
