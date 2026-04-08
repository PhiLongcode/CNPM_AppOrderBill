# Deployment guide — Docker compose

## Mục tiêu

Triển khai nhanh API AppOrderBill trên local/staging bằng Docker.

## Điều kiện tiên quyết

- Đã cài Docker Desktop
- Không có service khác chiếm port `8080` hoặc `3306`

## Các bước

```bash
docker compose up --build
```

Service:

- `apporder-api`
- `mysql`

## Xác minh (verification)

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`
- Health nhanh: goi 1 endpoint bat ky, vi du `GET /api/v1/system/storage`

## Rollback cơ bản

- Dừng stack: `docker compose down`
- Khởi động lại phiên bản image trước đó (nếu bạn có tag/version)

## Troubleshooting

| Triệu chứng | Nguyên nhân có thể | Cách xử |
|---|---|---|
| MySQL không lên healthcheck | Sai biến môi trường / init script | Xem log container `mysql`, kiểm tra `docker/mysql/init/01-schema.sql` |
| API không lên | MySQL chưa ready hoặc config sai | Kiểm tra env `MYSQL_URL/MYSQL_USER/MYSQL_PASSWORD` trong `docker-compose.yml` |
