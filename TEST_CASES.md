# TÀI LIỆU TEST CASE - HỆ THỐNG QUẢN LÝ ORDER BÒ

## Mục lục
1. [Quản lý Menu](#1-quản-lý-menu)
2. [Quản lý Order](#2-quản-lý-order)
3. [Quản lý Bàn](#3-quản-lý-bàn)
4. [Quản lý Tồn kho](#4-quản-lý-tồn-kho)
5. [Thanh toán](#5-thanh-toán)
6. [In hóa đơn và Phiếu bếp](#6-in-hóa-đơn-và-phiếu-bếp)
7. [Dashboard/Thống kê](#7-dashboardthống-kê)
8. [Cấu hình Máy in](#8-cấu-hình-máy-in)

---

## 1. QUẢN LÝ MENU

### TC-001: Thêm món mới vào menu
**Mô tả:** Thêm một món ăn mới vào menu với đầy đủ thông tin

**Các bước thực hiện:**
1. Vào menu "Quản lý" > "Quản lý Menu"
2. Click nút "Thêm món mới"
3. Nhập thông tin:
   - Tên món: "Bò tơ nướng"
   - Danh mục: "Bò Tơ"
   - Giá bán: 200.000 VNĐ
   - Đơn vị: "Phần"
   - Trạng thái: Hoạt động
4. Click "Lưu"

**Kết quả mong đợi:**
- Món mới được thêm vào danh sách menu
- Hiển thị trong danh sách với đầy đủ thông tin
- Có thể tìm kiếm món mới bằng tên

---

### TC-002: Sửa thông tin món
**Mô tả:** Cập nhật thông tin của món đã có

**Các bước thực hiện:**
1. Vào "Quản lý Menu"
2. Tìm món cần sửa (có thể dùng tìm kiếm)
3. Click vào món để mở dialog sửa
4. Thay đổi giá từ 200.000 → 220.000 VNĐ
5. Click "Lưu"

**Kết quả mong đợi:**
- Giá món được cập nhật thành công
- Giá mới hiển thị trên card menu
- Giá mới được áp dụng cho order mới

---

### TC-003: Xóa món khỏi menu
**Mô tả:** Xóa một món khỏi menu (soft delete)

**Các bước thực hiện:**
1. Vào "Quản lý Menu"
2. Tìm món cần xóa
3. Click vào món để mở dialog
4. Click nút "Xóa"
5. Xác nhận xóa

**Kết quả mong đợi:**
- Món bị đánh dấu là không hoạt động
- Món không hiển thị trong danh sách menu khi order
- Món vẫn tồn tại trong database (có thể khôi phục)

---

### TC-004: Tìm kiếm món trong menu
**Mô tả:** Tìm kiếm món theo tên

**Các bước thực hiện:**
1. Vào màn hình Order
2. Nhập "Bò tơ" vào ô tìm kiếm (hoặc nhấn F3)
3. Xem kết quả

**Kết quả mong đợi:**
- Hiển thị tất cả món có tên chứa "Bò tơ"
- Tìm kiếm không phân biệt dấu (có thể tìm "bo to" vẫn ra "Bò tơ")
- Kết quả cập nhật realtime khi gõ

---

### TC-005: Lọc món theo danh mục
**Mô tả:** Hiển thị món theo từng danh mục

**Các bước thực hiện:**
1. Vào màn hình Order
2. Click vào tab danh mục "Bò Tơ"
3. Xem danh sách món

**Kết quả mong đợi:**
- Chỉ hiển thị món thuộc danh mục "Bò Tơ"
- Tab "Bò Tơ" được highlight
- Click "Tất cả" để hiển thị lại tất cả món

---

## 2. QUẢN LÝ ORDER

### TC-006: Tạo order mới cho bàn
**Mô tả:** Tạo order mới khi chọn bàn chưa có order

**Các bước thực hiện:**
1. Vào màn hình Order
2. Click vào một bàn trống (ví dụ: "Bàn 1")
3. Xem trạng thái order

**Kết quả mong đợi:**
- Order mới được tạo tự động
- Hiển thị "Bàn: 1" và trạng thái "(Đang phục vụ)"
- Bàn 1 chuyển sang màu "đang dùng"
- Có thể thêm món vào order

---

### TC-007: Mở order đang phục vụ
**Mô tả:** Mở lại order đang phục vụ của một bàn

**Các bước thực hiện:**
1. Click vào bàn đang có order (màu xanh)
2. Xem danh sách món trong order

**Kết quả mong đợi:**
- Order được load và hiển thị đầy đủ món
- Tổng tiền được tính đúng
- Có thể thêm/sửa/xóa món

---

### TC-008: Thêm món từ menu vào order
**Mô tả:** Thêm món từ menu vào order hiện tại

**Các bước thực hiện:**
1. Chọn bàn (hoặc mở order đang có)
2. Click vào một món trong menu (ví dụ: "Bò tơ nướng")
3. Xem danh sách món trong order

**Kết quả mong đợi:**
- Món được thêm vào danh sách order
- Số lượng mặc định = 1 (hoặc số lượng trong ô "Số lượng")
- Tổng tiền được cập nhật
- Nếu món có quản lý tồn kho: tồn kho được trừ ngay (reserve)

---

### TC-009: Thêm món phát sinh (custom item)
**Mô tả:** Thêm món không có trong menu

**Các bước thực hiện:**
1. Chọn bàn
2. Nhập tên món: "Nước suối"
3. Nhập giá: 10.000
4. Click "Thêm"

**Kết quả mong đợi:**
- Món phát sinh được thêm vào order
- Hiển thị với tên và giá đã nhập
- Tổng tiền được cập nhật

---

### TC-010: Tăng số lượng món trong order
**Các bước thực hiện:**
1. Chọn một món trong order
2. Click nút "+" trong cột "Thao tác"
3. Xem số lượng mới

**Kết quả mong đợi:**
- Số lượng tăng lên 1
- Thành tiền của món được cập nhật
- Tổng tiền order được cập nhật
- Nếu có quản lý tồn kho: tồn kho được trừ thêm

---

### TC-011: Giảm số lượng món trong order
**Các bước thực hiện:**
1. Chọn một món có số lượng > 1
2. Click nút "-" trong cột "Thao tác"
3. Xem số lượng mới

**Kết quả mong đợi:**
- Số lượng giảm đi 1 (tối thiểu = 1)
- Thành tiền của món được cập nhật
- Tổng tiền order được cập nhật
- Nếu có quản lý tồn kho: tồn kho được hoàn lại

---

### TC-012: Sửa số lượng món trực tiếp
**Mô tả:** Sửa số lượng bằng cách click vào cột "SL" và nhập số mới

**Các bước thực hiện:**
1. Click vào cột "SL" của một món
2. Nhập số lượng mới: 5
3. Nhấn Enter

**Kết quả mong đợi:**
- Số lượng được cập nhật thành 5
- Thành tiền = đơn giá × 5
- Tổng tiền được cập nhật
- Tồn kho được điều chỉnh tương ứng

---

### TC-013: Hủy món trong order
**Mô tả:** Hủy món (đánh dấu canceled, không xóa khỏi order)

**Các bước thực hiện:**
1. Chọn một món trong order (click vào row)
2. Click nút "Hủy món" phía trên
3. Xác nhận hủy

**Kết quả mong đợi:**
- Món bị gạch ngang và màu xám
- Món không được tính vào tổng tiền
- Món vẫn hiển thị trong danh sách (để theo dõi)
- Tồn kho được hoàn lại (nếu có)
- Nút "Hủy món" bị disable cho món đã hủy

---

### TC-014: Xóa món khỏi order
**Mô tả:** Xóa hoàn toàn món khỏi order (chỉ cho món chưa in phiếu bếp)

**Các bước thực hiện:**
1. Chọn một món chưa in phiếu bếp
2. Click nút "Xóa món" phía trên
3. Xác nhận xóa

**Kết quả mong đợi:**
- Món bị xóa khỏi danh sách order
- Tổng tiền được cập nhật
- Tồn kho được hoàn lại (nếu có)
- Món không còn trong database cho order này

---

### TC-015: Xóa món đã in phiếu bếp (không được phép)
**Các bước thực hiện:**
1. Chọn một món đã in phiếu bếp (có dấu ✓ trong cột "Đã in")
2. Click nút "Xóa món"
3. Xem thông báo

**Kết quả mong đợi:**
- Hiển thị thông báo: "Không thể xóa món đã in phiếu bếp. Vui lòng dùng chức năng 'Hủy món'"
- Nút "Xóa món" bị disable cho món đã in

---

### TC-016: Hủy/xóa món khi chưa chọn món
**Các bước thực hiện:**
1. Không chọn món nào trong order
2. Click nút "Hủy món" hoặc "Xóa món"

**Kết quả mong đợi:**
- Hiển thị thông báo: "Vui lòng chọn một món để hủy/xóa"
- Không có hành động nào được thực hiện

---

### TC-017: Thêm ghi chú cho món
**Mô tả:** Thêm ghi chú đặc biệt cho món (ví dụ: "Không cay", "Ít muối")

**Các bước thực hiện:**
1. Click vào cột "Ghi chú" của một món
2. Nhập: "Không cay, ít muối"
3. Nhấn Enter

**Kết quả mong đợi:**
- Ghi chú được lưu
- Hiển thị trong cột "Ghi chú"
- Ghi chú được in trên phiếu bếp (nếu có)

---

### TC-018: Cập nhật tồn kho realtime khi thêm món
**Mô tả:** Kiểm tra tồn kho được trừ ngay khi thêm món vào order

**Các bước thực hiện:**
1. Xem tồn kho của một món (ví dụ: "Bia 333" có 10 chai)
2. Thêm món vào order với số lượng 3
3. Xem lại tồn kho trên card menu

**Kết quả mong đợi:**
- Tồn kho giảm từ 10 → 7 ngay lập tức
- Card menu hiển thị tồn kho mới
- Nếu tồn kho < mức tối thiểu: hiển thị cảnh báo màu cam
- Nếu tồn kho = 0: hiển thị "Hết hàng" màu đỏ

---

### TC-019: Hoàn lại tồn kho khi hủy món
**Các bước thực hiện:**
1. Thêm món có quản lý tồn kho vào order (SL = 2)
2. Xem tồn kho giảm
3. Hủy món đó
4. Xem lại tồn kho

**Kết quả mong đợi:**
- Tồn kho được hoàn lại (tăng lên)
- Card menu cập nhật tồn kho mới
- Tồn kho = tồn kho ban đầu

---

### TC-020: Hoàn lại tồn kho khi xóa món
**Các bước thực hiện:**
1. Thêm món có quản lý tồn kho vào order (SL = 2)
2. Xem tồn kho giảm
3. Xóa món đó
4. Xem lại tồn kho

**Kết quả mong đợi:**
- Tồn kho được hoàn lại
- Card menu cập nhật tồn kho mới

---

### TC-021: Kiểm tra tồn kho khi thêm món (không đủ)
**Các bước thực hiện:**
1. Món "Bia 333" còn 2 chai trong kho
2. Bàn 1 đã order 2 chai (tồn kho còn 0)
3. Bàn 2 cố gắng order thêm 1 chai

**Kết quả mong đợi:**
- Hiển thị thông báo: "Không đủ tồn kho cho món 'Bia 333'. Tồn hiện tại: 0, cần: 1"
- Món không được thêm vào order
- Tồn kho không thay đổi

---

### TC-022: Thêm món khi chưa chọn bàn
**Các bước thực hiện:**
1. Không chọn bàn nào
2. Click vào một món trong menu

**Kết quả mong đợi:**
- Hiển thị thông báo: "Vui lòng chọn bàn trước"
- Món không được thêm vào order

---

## 3. QUẢN LÝ BÀN

### TC-023: Thêm bàn mới
**Mô tả:** Thêm một bàn mới vào hệ thống

**Các bước thực hiện:**
1. Vào màn hình Order
2. Scroll xuống phần "Danh sách bàn"
3. Click nút "+ Thêm bàn"
4. Nhập số bàn: "21"
5. Xác nhận

**Kết quả mong đợi:**
- Bàn 21 được thêm vào danh sách
- Bàn được sắp xếp theo thứ tự số (1, 2, 3, ..., 10, 11, ..., 21)
- Bàn hiển thị với màu "trống"

---

### TC-024: Xóa bàn
**Mô tả:** Xóa một bàn khỏi hệ thống

**Các bước thực hiện:**
1. Right-click vào một bàn trống
2. Chọn "Xóa bàn"
3. Xác nhận xóa

**Kết quả mong đợi:**
- Bàn bị xóa khỏi danh sách
- Không thể chọn bàn đã xóa

---

### TC-025: Lọc bàn theo trạng thái
**Mô tả:** Hiển thị bàn theo trạng thái (Tất cả / Đang dùng / Trống)

**Các bước thực hiện:**
1. Click tab "Đang dùng"
2. Xem danh sách bàn

**Kết quả mong đợi:**
- Chỉ hiển thị bàn đang có order
- Bàn trống không hiển thị
- Click "Trống" để xem bàn trống
- Click "Tất cả" để xem tất cả

---

### TC-026: Sắp xếp bàn theo số
**Mô tả:** Kiểm tra bàn được sắp xếp đúng thứ tự số

**Các bước thực hiện:**
1. Xem danh sách bàn
2. Kiểm tra thứ tự

**Kết quả mong đợi:**
- Bàn được sắp xếp: 1, 2, 3, ..., 9, 10, 11, ... (không phải 1, 10, 11, 2, 3, ...)
- Bàn có tên không phải số được sắp xếp sau

---

## 4. QUẢN LÝ TỒN KHO

### TC-027: Thiết lập quản lý tồn kho cho món
**Mô tả:** Bật chế độ quản lý tồn kho cho một món

**Các bước thực hiện:**
1. Vào "Quản lý Menu"
2. Mở dialog sửa món
3. Check "Quản lý tồn kho"
4. Nhập:
   - Tồn kho hiện tại: 50
   - Tồn tối thiểu: 10
5. Lưu

**Kết quả mong đợi:**
- Món được đánh dấu là có quản lý tồn kho
- Card menu hiển thị: "📦 Kho: 50"
- Khi tồn kho ≤ 10: hiển thị cảnh báo "⚠ Kho: X (Sắp hết)"
- Khi tồn kho = 0: hiển thị "⛔ Hết hàng (Kho: 0)"

---

### TC-028: Cảnh báo tồn kho thấp
**Các bước thực hiện:**
1. Món có tồn kho = 5, tồn tối thiểu = 10
2. Xem card menu

**Kết quả mong đợi:**
- Card có viền màu cam
- Hiển thị: "⚠ Kho: 5 (Sắp hết)" màu cam

---

### TC-029: Cảnh báo hết hàng
**Các bước thực hiện:**
1. Món có tồn kho = 0
2. Xem card menu

**Kết quả mong đợi:**
- Card có viền màu đỏ
- Background màu hồng nhạt
- Hiển thị: "⛔ Hết hàng (Kho: 0)" màu đỏ
- Không thể thêm món vào order (hoặc có cảnh báo)

---

## 5. THANH TOÁN

### TC-030: Thanh toán order (không giảm giá)
**Mô tả:** Thanh toán order với đầy đủ thông tin

**Các bước thực hiện:**
1. Chọn bàn có order
2. Click nút "Thanh toán (F9)"
3. Xem dialog thanh toán:
   - Tổng tiền hàng: 500.000 VNĐ
   - Giảm giá: 0 VNĐ
   - Thành tiền: 500.000 VNĐ
4. Chọn phương thức thanh toán: "Tiền mặt"
5. Nhập số tiền khách đưa: 500.000
6. Click "Thanh toán"

**Kết quả mong đợi:**
- Order được đánh dấu là "PAID"
- Hóa đơn được in tự động
- Bàn chuyển sang trạng thái "trống"
- Order được lưu vào lịch sử thanh toán

---

### TC-031: Thanh toán với giảm giá
**Các bước thực hiện:**
1. Chọn bàn có order
2. Nhập giảm giá: 50.000 VNĐ (trong phần Order Summary)
3. Click "Thanh toán"
4. Xem dialog thanh toán

**Kết quả mong đợi:**
- Dialog hiển thị:
   - Tổng tiền hàng: 500.000 VNĐ
   - Giảm giá: 50.000 VNĐ
   - Thành tiền: 450.000 VNĐ
- Thanh toán thành công với số tiền 450.000

---

### TC-032: Thanh toán với tiền thừa
**Các bước thực hiện:**
1. Chọn bàn có order
2. Click "Thanh toán"
3. Nhập số tiền khách đưa: 600.000 (thành tiền: 500.000)
4. Click "Thanh toán"

**Kết quả mong đợi:**
- Dialog hiển thị:
   - Thành tiền: 500.000 VNĐ
   - Khách thanh toán: 600.000 VNĐ
   - Tiền thừa: 100.000 VNĐ
- Hóa đơn in ra có thông tin tiền thừa

---

### TC-033: Thanh toán khi chưa có món nào
**Các bước thực hiện:**
1. Chọn bàn mới (chưa có món)
2. Click "Thanh toán"

**Kết quả mong đợi:**
- Hiển thị thông báo: "Order chưa có món nào. Vui lòng thêm món trước khi thanh toán"
- Không mở dialog thanh toán

---

### TC-034: Hủy order
**Mô tả:** Hủy toàn bộ order (không thanh toán)

**Các bước thực hiện:**
1. Chọn bàn có order
2. Tìm nút "Hủy order" (nếu có) hoặc xóa tất cả món
3. Xác nhận hủy

**Kết quả mong đợi:**
- Order bị đánh dấu là "CANCELED"
- Tất cả tồn kho được hoàn lại
- Bàn chuyển sang trạng thái "trống"
- Order được lưu vào lịch sử (để thống kê)

---

## 6. IN HÓA ĐƠN VÀ PHIẾU BẾP

### TC-035: In phiếu bếp cho tất cả món
**Mô tả:** In phiếu bếp cho tất cả món chưa in trong order

**Các bước thực hiện:**
1. Chọn bàn có order với nhiều món
2. Click nút "🖨️ In phiếu bếp"
3. Xem kết quả

**Kết quả mong đợi:**
- Phiếu bếp được in ra
- Tất cả món được đánh dấu "Đã in" (có dấu ✓)
- Món đã in không thể xóa (chỉ có thể hủy)

---

### TC-036: In món đã chọn
**Mô tả:** In phiếu bếp chỉ cho các món đã được chọn (checkbox)

**Các bước thực hiện:**
1. Chọn bàn có order với nhiều món
2. Check vào checkbox của 2 món
3. Click nút "📋 In món đã chọn"
4. Xem kết quả

**Kết quả mong đợi:**
- Chỉ 2 món đã chọn được in
- 2 món đó được đánh dấu "Đã in"
- Món khác không bị ảnh hưởng

---

### TC-037: In lại phiếu bếp
**Mô tả:** In lại phiếu bếp của một order

**Các bước thực hiện:**
1. Chọn bàn có order đã in phiếu bếp
2. Click nút "🔄 In lại PB"
3. Xem kết quả

**Kết quả mong đợi:**
- Phiếu bếp được in lại với tất cả món
- Có thể in nhiều lần

---

### TC-038: In hóa đơn sau thanh toán
**Các bước thực hiện:**
1. Thanh toán một order
2. Xem hóa đơn được in

**Kết quả mong đợi:**
- Hóa đơn được in tự động sau khi thanh toán
- Hóa đơn có đầy đủ thông tin:
   - Mã hóa đơn
   - Phòng bàn
   - Ngày giờ
   - Danh sách món (Tên, SL, Đơn giá, Thành tiền)
   - Tổng tiền hàng
   - Giảm giá (nếu có)
   - Thành tiền
   - Phương thức thanh toán
   - Tiền thừa (nếu có)

---

### TC-039: In lại hóa đơn
**Mô tả:** In lại hóa đơn đã thanh toán

**Các bước thực hiện:**
1. Click nút " In lại HĐ"
2. Chọn hóa đơn từ danh sách (hôm nay)
3. Click "In lại"

**Kết quả mong đợi:**
- Hóa đơn được in lại
- Có thể in nhiều lần

---

### TC-040: Kiểm tra format hóa đơn
**Mô tả:** Kiểm tra hóa đơn có đầy đủ thông tin và format đúng

**Các bước thực hiện:**
1. Thanh toán một order
2. Xem file PDF hóa đơn được tạo

**Kết quả mong đợi:**
- Header: Tên cửa hàng, địa chỉ, SĐT (nếu có cấu hình)
- Thông tin order: Mã HĐ, Phòng bàn, Ngày giờ
- Bảng món:
   - Cột: Mon | SL | Don gia | Thanh tien
   - Tên món dài được xuống dòng
   - Số lượng, đơn giá, thành tiền đúng
- Footer: Tổng tiền, Giảm giá, Thành tiền, Cảm ơn

---

## 7. DASHBOARD/THỐNG KÊ

### TC-041: Xem thống kê hôm nay
**Mô tả:** Xem thống kê doanh thu và số lượng order hôm nay

**Các bước thực hiện:**
1. Vào menu "Hệ thống" > "Dashboard"
2. Xem tab "Hôm nay"

**Kết quả mong đợi:**
- Hiển thị:
   - Tổng doanh thu hôm nay
   - Số lượng order hôm nay
   - Số lượng món đã bán
   - Biểu đồ (nếu có)
- Dữ liệu được cập nhật realtime

---

### TC-042: Xem thống kê tuần này
**Các bước thực hiện:**
1. Vào Dashboard
2. Click tab "Tuần này"

**Kết quả mong đợi:**
- Hiển thị thống kê từ đầu tuần đến hôm nay
- Có thể xem theo ngày trong tuần

---

### TC-043: Xem thống kê tháng này
**Các bước thực hiện:**
1. Vào Dashboard
2. Click tab "Tháng này"

**Kết quả mong đợi:**
- Hiển thị thống kê từ đầu tháng đến hôm nay
- Có thể xem theo ngày trong tháng

---

### TC-044: Xem thống kê tùy chọn (custom date range)
**Các bước thực hiện:**
1. Vào Dashboard
2. Click tab "Tùy chọn"
3. Chọn ngày bắt đầu và ngày kết thúc
4. Xem thống kê

**Kết quả mong đợi:**
- Hiển thị thống kê trong khoảng thời gian đã chọn
- Có thể export dữ liệu (nếu có)

---

### TC-045: Xem lịch sử thanh toán
**Mô tả:** Xem danh sách các hóa đơn đã thanh toán

**Các bước thực hiện:**
1. Vào Dashboard
2. Scroll xuống phần "Lịch sử thanh toán"
3. Xem danh sách

**Kết quả mong đợi:**
- Hiển thị bảng với các cột:
   - Mã HĐ
   - Bàn
   - Thời gian
   - Tổng tiền
   - Phương thức
   - Thao tác (In lại)
- Có thể sắp xếp và tìm kiếm

---

## 8. CẤU HÌNH MÁY IN

### TC-046: Cấu hình máy in hóa đơn
**Mô tả:** Thiết lập máy in cho hóa đơn

**Các bước thực hiện:**
1. Vào menu "Hệ thống" > "Cấu hình máy in"
2. Chọn tab "Hóa đơn"
3. Chọn máy in từ dropdown
4. Click "Lưu"

**Kết quả mong đợi:**
- Máy in được lưu
- Hóa đơn sẽ in ra máy in đã chọn

---

### TC-047: Cấu hình máy in phiếu bếp
**Các bước thực hiện:**
1. Vào "Cấu hình máy in"
2. Chọn tab "Phiếu bếp"
3. Chọn máy in
4. Click "Lưu"

**Kết quả mong đợi:**
- Máy in phiếu bếp được lưu
- Phiếu bếp sẽ in ra máy in đã chọn

---

### TC-048: Test in thử
**Mô tả:** In thử để kiểm tra máy in hoạt động

**Các bước thực hiện:**
1. Vào "Cấu hình máy in"
2. Chọn máy in
3. Click "In thử"

**Kết quả mong đợi:**
- Một trang in thử được in ra
- Có thể kiểm tra chất lượng in

---

## 9. CÁC KỊCH BẢN TỔNG HỢP

### TC-049: Kịch bản order hoàn chỉnh
**Mô tả:** Test toàn bộ quy trình từ order đến thanh toán

**Các bước thực hiện:**
1. Chọn bàn 1 (tạo order mới)
2. Thêm 3 món vào order:
   - "Bò tơ nướng" x 2
   - "Cơm chiên" x 1
   - "Nước suối" (món phát sinh) x 2
3. Sửa số lượng "Bò tơ nướng" từ 2 → 3
4. Thêm ghi chú "Không cay" cho "Cơm chiên"
5. In phiếu bếp
6. Hủy món "Nước suối" (1 món)
7. Xóa món "Nước suối" còn lại
8. Thêm giảm giá 50.000
9. Thanh toán bằng tiền mặt: 600.000
10. Xem hóa đơn

**Kết quả mong đợi:**
- Tất cả các bước thực hiện thành công
- Tồn kho được cập nhật đúng
- Hóa đơn in ra đúng thông tin
- Bàn 1 chuyển sang trạng thái trống

---

### TC-050: Kịch bản nhiều bàn cùng order
**Mô tả:** Test khi nhiều bàn cùng order món có tồn kho

**Các bước thực hiện:**
1. Món "Bia 333" có tồn kho = 10
2. Bàn 1 order 5 chai → tồn kho còn 5
3. Bàn 2 order 3 chai → tồn kho còn 2
4. Bàn 3 cố gắng order 5 chai

**Kết quả mong đợi:**
- Bàn 1 và 2 order thành công
- Bàn 3 bị từ chối: "Không đủ tồn kho"
- Tồn kho hiển thị đúng trên card menu
- Khi bàn 1 thanh toán, tồn kho vẫn giữ nguyên (đã trừ khi order)

---

### TC-051: Kịch bản hủy order
**Mô tả:** Test hủy order và hoàn lại tồn kho

**Các bước thực hiện:**
1. Bàn 1 order 3 món có quản lý tồn kho
2. Xem tồn kho giảm
3. Hủy toàn bộ order (hoặc xóa tất cả món)
4. Xem lại tồn kho

**Kết quả mong đợi:**
- Tất cả tồn kho được hoàn lại
- Tồn kho = tồn kho ban đầu
- Bàn chuyển sang trạng thái trống

---

### TC-052: Kịch bản refresh order
**Mô tả:** Test refresh order sau khi thao tác

**Các bước thực hiện:**
1. Bàn 1 có order với 2 món
2. Xóa 1 món
3. Refresh order (hoặc chọn lại bàn)
4. Xem danh sách món

**Kết quả mong đợi:**
- Chỉ còn 1 món trong order
- Món đã xóa không hiển thị lại
- Tổng tiền đúng

---

## 10. EDGE CASES VÀ LỖI

### TC-053: Thêm món với số lượng = 0
**Các bước thực hiện:**
1. Chọn bàn
2. Nhập số lượng = 0
3. Click vào món

**Kết quả mong đợi:**
- Hiển thị thông báo: "Số lượng phải lớn hơn 0"
- Món không được thêm

---

### TC-054: Thêm món với số lượng âm
**Các bước thực hiện:**
1. Chọn bàn
2. Nhập số lượng = -1
3. Click vào món

**Kết quả mong đợi:**
- Hiển thị thông báo lỗi
- Món không được thêm

---

### TC-055: Sửa số lượng = 0
**Các bước thực hiện:**
1. Chọn món trong order
2. Sửa số lượng = 0
3. Nhấn Enter

**Kết quả mong đợi:**
- Hiển thị thông báo: "Số lượng phải lớn hơn 0"
- Số lượng không thay đổi

---

### TC-056: Thanh toán với số tiền < thành tiền
**Các bước thực hiện:**
1. Chọn bàn có order
2. Click "Thanh toán"
3. Nhập số tiền: 100.000 (thành tiền: 500.000)

**Kết quả mong đợi:**
- Hiển thị cảnh báo: "Số tiền khách đưa phải >= thành tiền"
- Không cho phép thanh toán

---

### TC-057: Xóa món đã hủy
**Các bước thực hiện:**
1. Hủy một món
2. Chọn món đã hủy
3. Click "Xóa món"

**Kết quả mong đợi:**
- Hiển thị thông báo: "Món này đã bị hủy rồi"
- Nút "Xóa món" bị disable

---

### TC-058: Hủy món đã hủy
**Các bước thực hiện:**
1. Hủy một món
2. Chọn lại món đã hủy
3. Click "Hủy món"

**Kết quả mong đợi:**
- Hiển thị thông báo: "Món này đã bị hủy rồi"
- Nút "Hủy món" bị disable

---

### TC-059: Thêm món khi tồn kho = 0
**Các bước thực hiện:**
1. Món có tồn kho = 0
2. Cố gắng thêm món vào order

**Kết quả mong đợi:**
- Hiển thị thông báo: "Không đủ tồn kho" hoặc "Hết hàng"
- Món không được thêm

---

### TC-060: Thêm bàn trùng số
**Các bước thực hiện:**
1. Thêm bàn số 1 (đã tồn tại)
2. Xem thông báo

**Kết quả mong đợi:**
- Hiển thị thông báo: "Bàn số 1 đã tồn tại"
- Bàn không được thêm

---

## GHI CHÚ TEST

### Môi trường test:
- Hệ điều hành: Windows 10/11
- Java: JDK 21
- Database: SQLite
- Máy in: Cần cấu hình máy in 80mm

### Dữ liệu test cần chuẩn bị:
1. Menu mẫu với ít nhất 10 món thuộc nhiều danh mục
2. Một số món có quản lý tồn kho với số lượng khác nhau
3. Ít nhất 5 bàn
4. Một số order mẫu ở các trạng thái khác nhau

### Checklist trước khi test:
- [ ] Database đã được khởi tạo
- [ ] Máy in đã được cấu hình
- [ ] Có dữ liệu menu mẫu
- [ ] Có dữ liệu bàn mẫu
- [ ] Ứng dụng đã được build và chạy thành công

### Các lỗi thường gặp cần kiểm tra:
1. Lỗi tồn kho không được cập nhật realtime
2. Lỗi món đã xóa lại hiển thị khi refresh
3. Lỗi món đã hủy bị mất
4. Lỗi format hóa đơn không đúng
5. Lỗi sắp xếp bàn không đúng thứ tự số

---

**Ngày tạo:** 11/12/2025  
**Phiên bản:** 1.0  
**Người tạo:** AI Assistant

