# Hướng dẫn kiểm tra và cấu hình Paper Size cho máy in nhiệt

## Cách kiểm tra Paper Size trong Windows

### Bước 1: Mở Printer Properties
1. Mở **Settings** (Cài đặt) → **Devices** (Thiết bị) → **Printers & scanners** (Máy in và máy quét)
2. Hoặc mở **Control Panel** → **Devices and Printers**
3. Tìm máy in nhiệt của bạn (ví dụ: RONGTA 58mm Series Printer)
4. **Right-click** (Click chuột phải) vào máy in → Chọn **Printer properties** (Thuộc tính máy in)

### Bước 2: Kiểm tra Paper Size
1. Trong cửa sổ **Printer properties**, chọn tab **Device Settings** (Cài đặt thiết bị) hoặc **Preferences** (Tùy chọn)
2. Tìm mục **Paper Size** (Khổ giấy) hoặc **Paper/Quality** (Giấy/Chất lượng)
3. Kiểm tra xem paper size có được set đúng không:
   - **58mm thermal printer**: Nên chọn **58mm** hoặc **Receipt 58mm** hoặc **Custom 58mm x ∞**
   - **80mm thermal printer**: Nên chọn **80mm** hoặc **Receipt 80mm** hoặc **Custom 80mm x ∞**

### Bước 3: Kiểm tra trong Print Dialog
1. Khi in từ ứng dụng, trong dialog in của Windows:
   - Click **Preferences** (Tùy chọn) hoặc **Properties** (Thuộc tính)
   - Kiểm tra tab **Paper/Quality** hoặc **Layout**
   - Đảm bảo **Paper Size** được chọn đúng (58mm hoặc 80mm)

### Bước 4: Kiểm tra Default Paper Size
1. Trong **Printer properties**, tab **Device Settings**
2. Tìm **Default Paper Size** hoặc **Paper Source**
3. Đảm bảo nó được set đúng với loại giấy bạn đang dùng

## Các vấn đề thường gặp

### Vấn đề 1: Paper Size không có trong danh sách
- **Giải pháp**: 
  - Cài đặt lại driver máy in từ website nhà sản xuất
  - Hoặc tạo Custom Paper Size:
    1. Trong Printer Properties → Device Settings
    2. Tìm "Custom Paper Sizes" hoặc "User Defined"
    3. Tạo mới với width = 58mm (hoặc 80mm), height = unlimited (hoặc 1000mm)

### Vấn đề 2: In ra nửa giấy
- **Nguyên nhân**: Paper size trong Windows không khớp với cấu hình trong app
- **Giải pháp**:
  1. Kiểm tra cấu hình trong app (màn hình Cấu hình máy in) → Paper Size phải khớp với Windows
  2. Trong Print Dialog, chọn đúng Paper Size trước khi in
  3. Đảm bảo driver máy in hỗ trợ paper size đó

### Vấn đề 3: Text bị cắt hoặc không đúng width
- **Nguyên nhân**: Font size hoặc margin không phù hợp
- **Giải pháp**:
  1. Kiểm tra Font Size trong màn hình Cấu hình Template
  2. Đảm bảo Paper Size trong app khớp với Windows (58mm hoặc 80mm)

## Cấu hình trong ứng dụng

1. Mở màn hình **Cấu hình máy in** trong app
2. Chọn máy in và set **Paper Size**:
   - **58mm** cho máy in 58mm
   - **80mm** cho máy in 80mm
3. Lưu cấu hình

## Kiểm tra nhanh

Sau khi cấu hình, in thử và kiểm tra:
- ✅ Text không bị cắt
- ✅ In đầy đủ width của giấy
- ✅ Không bị in nửa giấy
- ✅ Alignment đúng

Nếu vẫn có vấn đề, kiểm tra log trong console để xem thông tin paper size thực tế.

