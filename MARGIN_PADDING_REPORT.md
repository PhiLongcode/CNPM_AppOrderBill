# Báo cáo Margin/Padding hiện tại trong hệ thống in

## 📊 Tổng hợp các giá trị Margin/Padding

### 1. **PageLayout Margin (Trong SimplePrinterService)**
- **Vị trí**: `createCustomPageLayout()` - Line 727
- **Giá trị**: `Printer.MarginType.HARDWARE_MINIMUM`
- **Ý nghĩa**: Margin = 0, tận dụng tối đa không gian in
- **Code**:
```java
Printer.MarginType.HARDWARE_MINIMUM
```

### 2. **Padding trong PageContainer (Khi in)**
- **Vị trí**: `printToPrinterSync()` - Line 1366-1382
- **Giá trị**: **0 (KHÔNG CÓ PADDING)**
- **Chi tiết**: 
  - `pageContainer`: Không có padding
  - `outerContainer`: Không có padding
  - Chỉ sử dụng `alignment CENTER` để căn giữa

### 3. **DEFAULT_MARGIN_MM trong PrintUtils**
- **Vị trí**: `PrintUtils.java` - Line 16
- **Giá trị**: `2.0 mm` mỗi bên
- **Mục đích**: Chỉ dùng cho **tính toán** `charsPerLine`, **KHÔNG dùng** trong actual printing
- **Code**:
```java
private static final double DEFAULT_MARGIN_MM = 2.0;
```

### 4. **Padding trong Preview Dialog**
- **Vị trí**: `showPrintPreview()` - Line 1061
- **Giá trị**: `new Insets(10)` = **10 pixels** cho tất cả các bên
- **Mục đích**: Chỉ dùng cho UI preview, không ảnh hưởng đến in thật

### 5. **LinesPerPage Calculation**
- **Vị trí**: `printToPrinterSync()` - Line 1290
- **Giá trị**: Không trừ margin (margin = 0)
- **Code**:
```java
int linesPerPage = (int) (printableHeight / lineHeight); // Không trừ margin (margin = 0)
```

## 📝 Tóm tắt

| Vị trí | Loại | Giá trị | Ghi chú |
|--------|------|---------|---------|
| PageLayout | Margin | **0** (HARDWARE_MINIMUM) | Tận dụng tối đa không gian in |
| PageContainer | Padding | **0** | Không có padding khi in |
| OuterContainer | Padding | **0** | Không có padding khi in |
| PrintUtils | DEFAULT_MARGIN_MM | **2.0mm** | Chỉ dùng cho tính toán, không dùng khi in |
| Preview Dialog | Padding | **10px** | Chỉ dùng cho UI preview |

## ✅ Kết luận

**Hiện tại hệ thống đang sử dụng:**
- ✅ **Margin = 0** (HARDWARE_MINIMUM) - Tận dụng tối đa không gian in
- ✅ **Padding = 0** - Không có padding khi in
- ✅ Nội dung được căn giữa bằng `alignment CENTER` thay vì padding

**Lưu ý:**
- `DEFAULT_MARGIN_MM = 2.0mm` trong `PrintUtils` chỉ dùng để tính toán số ký tự trên mỗi dòng (`charsPerLine`), không ảnh hưởng đến margin thực tế khi in.
- Preview dialog có padding 10px chỉ để hiển thị đẹp hơn, không ảnh hưởng đến in thật.

