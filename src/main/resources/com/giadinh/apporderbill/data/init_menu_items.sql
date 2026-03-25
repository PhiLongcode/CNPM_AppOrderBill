-- Script khởi tạo dữ liệu menu items từ hình ảnh menu viết tay
-- Chạy script này để thêm các món ăn vào database

-- Xóa dữ liệu cũ (nếu cần)
-- DELETE FROM menu_items;

-- Các Món Khai Vị (Appetizers)
INSERT INTO menu_items (name, category, unit_price, is_active, created_at, updated_at) VALUES
('Chả giò mỹ phước', 'Khai Vị', 99000, 1, datetime('now'), datetime('now')),
('Cơm chay patê', 'Khai Vị', 99000, 1, datetime('now'), datetime('now')),
('Đậu hủ chiên giòn', 'Khai Vị', 49000, 1, datetime('now'), datetime('now')),
('Tổ yên chiên', 'Khai Vị', 129000, 1, datetime('now'), datetime('now')),
('Rau muốn xào tỏi', 'Khai Vị', 49000, 1, datetime('now'), datetime('now')),
('Rau luộc thập cẩm', 'Khai Vị', 99000, 1, datetime('now'), datetime('now')),
('Đậu bắp luộc', 'Khai Vị', 49000, 1, datetime('now'), datetime('now'));

-- Các Món Cơm chiên (Fried Rice)
INSERT INTO menu_items (name, category, unit_price, is_active, created_at, updated_at) VALUES
('Cơm chiên dưa bò', 'Cơm Chiên', 89000, 1, datetime('now'), datetime('now')),
('Cơm chiên tỏi', 'Cơm Chiên', 59000, 1, datetime('now'), datetime('now')),
('Cơm chiên Hải Sản', 'Cơm Chiên', 79000, 1, datetime('now'), datetime('now')),
('Cơm chiên muối ớt', 'Cơm Chiên', 69000, 1, datetime('now'), datetime('now'));

-- Các món Mì Xào (Stir-fried Noodles)
INSERT INTO menu_items (name, category, unit_price, is_active, created_at, updated_at) VALUES
('Mì xào bò', 'Mì Xào', 129000, 1, datetime('now'), datetime('now')),
('Mì xào Hải Sản', 'Mì Xào', 129000, 1, datetime('now'), datetime('now')),
('Hủ Tiếu xào bò', 'Mì Xào', 129000, 1, datetime('now'), datetime('now')),
('Hủ Tiếu xào Hải Sản', 'Mì Xào', 139000, 1, datetime('now'), datetime('now'));

-- Các Món Hải Sản (Seafood)
INSERT INTO menu_items (name, category, unit_price, is_active, created_at, updated_at) VALUES
('Tôm cháy tỏi', 'Hải Sản', 169000, 1, datetime('now'), datetime('now')),
('Tôm nướng muối ớt', 'Hải Sản', 169000, 1, datetime('now'), datetime('now')),
('Tôm rang muối', 'Hải Sản', 169000, 1, datetime('now'), datetime('now')),
('Mực xào chua ngọt', 'Hải Sản', 159000, 1, datetime('now'), datetime('now')),
('Mực chiên nước mắm', 'Hải Sản', 179000, 1, datetime('now'), datetime('now')),
('Mực chiên giòn', 'Hải Sản', 159000, 1, datetime('now'), datetime('now')),
('Nghêu hấp sả', 'Hải Sản', 79000, 1, datetime('now'), datetime('now')),
('Nghêu hấp Thái', 'Hải Sản', 89000, 1, datetime('now'), datetime('now'));

-- Các món đồng quê (Country-style dishes)
INSERT INTO menu_items (name, category, unit_price, is_active, created_at, updated_at) VALUES
('Ếch chui rơm', 'Đồng Quê', 149000, 1, datetime('now'), datetime('now')),
('Ếch chiên nước mắm', 'Đồng Quê', 149000, 1, datetime('now'), datetime('now')),
('Cá chạch cháy lá lốt', 'Đồng Quê', 159000, 1, datetime('now'), datetime('now')),
('Cá chạch chiên giòn', 'Đồng Quê', 149000, 1, datetime('now'), datetime('now'));

-- Các món Lươn (Eel)
INSERT INTO menu_items (name, category, unit_price, is_active, created_at, updated_at) VALUES
('Lươn nỗ muối', 'Lươn', 176000, 1, datetime('now'), datetime('now')),
('Lươn xào lăn', 'Lươn', 189000, 1, datetime('now'), datetime('now')),
('Lươn chiên giòn', 'Lươn', 179000, 1, datetime('now'), datetime('now'));

-- Các món Cá lóc (Snakehead fish)
INSERT INTO menu_items (name, category, unit_price, is_active, created_at, updated_at) VALUES
('Cá lóc nướng trui', 'Cá Lóc', 179000, 1, datetime('now'), datetime('now')),
('Cá lóc quay me', 'Cá Lóc', 199000, 1, datetime('now'), datetime('now'));

-- Các món Lẩu (Hotpot)
INSERT INTO menu_items (name, category, unit_price, is_active, created_at, updated_at) VALUES
('Lẩu Thái hải sản', 'Lẩu', 219000, 1, datetime('now'), datetime('now')),
('Lẩu Cua đồng', 'Lẩu', 219000, 1, datetime('now'), datetime('now')),
('Đặc biệt lẩu bò Thảo Mộc', 'Lẩu', 259000, 1, datetime('now'), datetime('now')),
('Lẩu đuôi bò', 'Lẩu', 229000, 1, datetime('now'), datetime('now')),
('Lẩu bò thập cẩm', 'Lẩu', 199000, 1, datetime('now'), datetime('now'));
-- Lưu ý: Lẩu gà ớt hiểm và Lẩu ếch măng chua đã bị gạch trong menu nên không thêm vào

-- Các Món bò tơ (Young Beef Dishes)
INSERT INTO menu_items (name, category, unit_price, is_active, created_at, updated_at) VALUES
('Bò tơ cuốn rau rừng', 'Bò Tơ', 179000, 1, datetime('now'), datetime('now')),
('Bò tơ tái chanh', 'Bò Tơ', 149000, 1, datetime('now'), datetime('now')),
('Bò tơ cháy Tiêu xanh', 'Bò Tơ', 179000, 1, datetime('now'), datetime('now')),
('Bò tơ xào hành cần', 'Bò Tơ', 159000, 1, datetime('now'), datetime('now')),
('Bò tơ xào xạ tế', 'Bò Tơ', 159000, 1, datetime('now'), datetime('now')),
('Bò tơ bóp trái Vả', 'Bò Tơ', 149000, 1, datetime('now'), datetime('now')),
('Dựng Sườn sốt thái', 'Bò Tơ', 169000, 1, datetime('now'), datetime('now')),
('Bò 1 nắng nướng', 'Bò Tơ', 169000, 1, datetime('now'), datetime('now')),
('Bò tơ nướng mắm nhĩ', 'Bò Tơ', 169000, 1, datetime('now'), datetime('now')),
('Bò nhúng mắm ruốc', 'Bò Tơ', 199000, 1, datetime('now'), datetime('now')),
('Bò nhúng giấm', 'Bò Tơ', 199000, 1, datetime('now'), datetime('now')),
('Bò nhúng mẻ', 'Bò Tơ', 199000, 1, datetime('now'), datetime('now'));
-- Lưu ý: Sườn bò 1 nắng nướng theo ký không có giá cố định nên không thêm vào

