# BẢNG BÁO GIÁ — Dự án: AppOrderBill ✅

**Ngày:** [_____/_____/_____]
**Khách hàng:** [Tên khách hàng]
**Địa chỉ:** [Địa chỉ khách hàng]
**Người liên hệ:** [Họ tên — SĐT — Email]

---

## 1) Thông tin dự án 🔧

- **Tên dự án:** Hệ thống quản lý order & in hóa đơn (AppOrderBill)
- **Mô tả ngắn:** Ứng dụng POS cho nhà hàng/cafe: quản lý menu, order theo bàn, quản lý tồn kho, in hóa đơn & phiếu bếp, cấu hình máy in, dashboard báo cáo và tài liệu hướng dẫn.
- **Tài liệu tham khảo trong repo:** `TEST_CASES.md`, `PRINTER_SETUP_GUIDE.md`, `MARGIN_PADDING_REPORT.md`

---

## 2) Phạm vi công việc 📦

- Phân tích & thiết kế nghiệp vụ
- Phát triển chức năng: Quản lý Menu, Quản lý Bàn, Order, Thanh toán, Tồn kho cơ bản, Dashboard
- Tích hợp in (58mm/80mm), Print preview, cấu hình máy in
- Tài liệu: Hướng dẫn sử dụng, Hướng dẫn cấu hình máy in, Test cases
- Kiểm thử & Sửa lỗi theo Test Cases
- Triển khai & bàn giao mã nguồn, file build, script dữ liệu
- Đào tạo 1 buổi (2 giờ) + Hỗ trợ 30 ngày

---

## 3) Bảng chi phí (mẫu) 💰

> Lưu ý: **Dưới đây có hai dạng bảng** — (A) bảng mẫu theo gói dịch vụ, (B) bảng chi phí theo chức năng dựa trên số use-case trong `TEST_CASES.md` (500.000 VNĐ / use-case).

| STT | Nội dung                  | Số lượng | Giá đơn vị (VNĐ) | Thành tiền (VNĐ) |
| --: | ------------------------- | -------: | ---------------: | ---------------: |
|   1 | Phân tích & Thiết kế      |    1 gói |          500.000 |          500.000 |
|   2 | Phát triển chức năng core |    1 gói |          500.000 |          500.000 |
|   3 | Tích hợp in & template    |    1 gói |          500.000 |          500.000 |
|   4 | Kiểm thử & Sửa lỗi        |    1 gói |          500.000 |          500.000 |
|   5 | Tài liệu & Hướng dẫn      |    1 gói |          500.000 |          500.000 |
|   6 | Đào tạo 2 giờ             |   1 buổi |          500.000 |          500.000 |
|   7 | Bảo hành & Hỗ trợ 30 ngày |    1 gói |          500.000 |          500.000 |
|     | **Tổng cộng (mẫu)**       |          |                  |    **3.500.000** |

### Bảng chi phí theo chức năng (tính theo use-case — 500.000 VNĐ / use-case)

Dưới đây là bảng tổng hợp số lượng use-case theo chức năng (tham chiếu `TEST_CASES.md`) và chi phí tương ứng:

| Chức năng                                     | Số use-case | Đơn giá (VNĐ) | Thành tiền (VNĐ) |
| --------------------------------------------- | ----------: | ------------: | ---------------: |
| Quản lý sản phẩm (Menu)                       |           5 |       500.000 |        2.500.000 |
| Order                                         |          17 |       500.000 |        8.500.000 |
| Quản lý bàn                                   |           4 |       500.000 |        2.000.000 |
| Quản lý kho                                   |           3 |       500.000 |        1.500.000 |
| Thanh toán                                    |           5 |       500.000 |        2.500.000 |
| In (hóa đơn & phiếu bếp)                      |           6 |       500.000 |        3.000.000 |
| Khác (Dashboard, cấu hình, kịch bản tổng hợp) |          20 |       500.000 |       10.000.000 |
| **Tổng cộng**                                 |      **60** |       500.000 |   **30.000.000** |

---

### Nhóm chức năng — chi tiết (bảng báo giá theo nhóm)

| Nhóm chức năng                | Chức năng                                                                                                              | Thành tiền (VNĐ) |
| ----------------------------- | ---------------------------------------------------------------------------------------------------------------------- | ---------------: |
| Quản lý menu                  | Thêm/sửa/xóa món; ngừng bán; tìm kiếm; lọc theo danh mục                                                               |          500.000 |
| Quản lý tồn kho               | Bật quản lý kho; cảnh báo tồn kho thấp/hết hàng                                                                        |          500.000 |
| Order                         | Tạo order, thêm món, thêm món phát sinh, sửa SL, hủy/xóa món, ghi chú, giảm giá trên từng món, chồng bill khi gọi thêm |          700.000 |
| Bàn                           | Thêm, xóa, chuyển trạng thái bàn                                                                                       |          400.000 |
| Thanh toán                    | Thanh toán (không giảm giá/giảm giá); tiền thừa; hủy order                                                             |          500.000 |
| In                            | In PB, in hóa đơn, in lại, kiểm tra format                                                                             |          600.000 |
| Dashboard, lịch sử thanh toán | Dashboard thống kê theo ngày, tuần, tháng; xem lịch sử thanh toán trong 1 ngày                                         |          400.000 |
| Cấu hình máy in               | Cấu hình máy in, mẫu in                                                                                                |          400.000 |
| **Tổng cộng**                 |                                                                                                                        |    **4.000.000** |

_Ghi chú: tất cả các chi phí trên đã bao gồm custom theo mong muốn khách hàng, sửa lỗi, bảo trì._

---

## Những bảng cần chuẩn bị để báo giá (gợi ý)

1. **Bảng mẫu theo gói dịch vụ** — báo giá theo gói (Phân tích, Phát triển, Tích hợp in, Kiểm thử, Tài liệu, Đào tạo, Bảo hành).
2. **Bảng chi phí theo chức năng** — tổng theo nhóm chức năng (đã có ở trên).
3. **Bảng chi tiết theo use-case (TC)** — liệt kê từng TC (TC-001, TC-002, ...) với đơn giá per-usecase (dùng khi khách yêu cầu báo giá chi tiết).
4. **Bảng milestone / tiến độ & thanh toán** — phân bổ chi phí theo milestone (ký HĐ, hoàn thành phát triển, nghiệm thu).
5. **Bảng dịch vụ bảo trì & SLA** — báo giá duy trì/hỗ trợ dài hạn.

> Muốn tôi tạo sẵn các file Markdown cho từng bảng trong `docs/` không? Nếu có, tôi sẽ tạo và (nếu bạn muốn) export PDF/Word.

## 4) Tiến độ & Điều khoản thanh toán 🗓️

- **Tiến độ dự kiến:** 3–6 tuần (tùy phạm vi chi tiết)
- **Thanh toán (gợi ý):**
  - 30% ký hợp đồng (đặt cọc)
  - 50% sau khi hoàn thành phát triển & test chấp nhận
  - 20% sau khi bàn giao chính thức và nghiệm thu
- **Thời hạn báo giá:** 30 ngày kể từ ngày phát hành

---

## 5) Bảo hành & Hỗ trợ 🔧

- Bảo hành kỹ thuật: 30 ngày kể từ ngày bàn giao chính thức
- Hỗ trợ mở rộng / SLA: thỏa thuận riêng (tính phí)

---

## 6) Chữ ký xác nhận ✍️

Khách hàng: **\*\*\*\***\_\_\_**\*\*\*\*** Ngày: **_/_**/**\_**  
Đơn vị cung cấp: **\*\*\*\***\_\_\_**\*\*\*\*** Ngày: **_/_**/**\_**

---

_Nếu bạn muốn, tôi có thể điền thông tin khách hàng và điều chỉnh mức giá cụ thể — hoặc xuất file này sang PDF/Word._
