# Reporting — Báo cáo doanh thu

## Chức năng

- Báo cáo doanh thu ngày/tuần/tháng
- Báo cáo theo khoảng ngày

## Thành phần chính (code map)

- Package: `src/main/java/com/giadinh/apporderbill/reporting/`
- Use case: `GetDailyRevenueUseCase`, `GetWeeklyRevenueUseCase`, ...

## API liên quan

- `GET /api/v1/reporting/revenue/daily?date=` — doanh thu ngày
- `GET /api/v1/reporting/revenue/weekly` — doanh thu tuần
- `GET /api/v1/reporting/revenue/monthly` — doanh thu tháng
- `GET /api/v1/reporting/revenue/range?start=&end=` — tổng hợp theo khoảng ngày

## Quyền (Authorization)

- `View Reports`

## Dữ liệu & persistence

- Reporting lấy dữ liệu từ payments (billing)
- SQLite/MySQL tuỳ theo payment repository và profile API

## Luồng chính (tóm tắt)

- (1) Client gọi endpoint reporting
- (2) Check quyền `View Reports`
- (3) Use case aggregate từ payments

## Troubleshooting

| Triệu chứng | Nguyên nhân | Cách xử |
|---|---|---|
| Doanh thu = 0 | Chưa có payment hoặc repo in-memory | Kiểm tra luồng checkout và payment persistence |
| `403` | User thiếu quyền | Gán `View Reports` cho role group |

## Liên kết

- Module Billing: `docs/modules/billing.md`
