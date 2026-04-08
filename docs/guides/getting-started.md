# Getting started — Cài đặt & chạy dự án (local)

## Mục tiêu

Chạy được **API**, **Swagger UI** và **POS Desktop** trên máy local.

## Điều kiện tiên quyết

- Java 21
- Maven 3.9+
- (Tuỳ chọn) MySQL 8.x nếu chạy profile `api-mysql`

## Các bước

### 1) Chạy API (mặc định)

```bash
mvn -DskipTests spring-boot:run
```

### 2) Xác minh (verification)

- `http://localhost:8080/swagger-ui/index.html`
- `http://localhost:8080/v3/api-docs`

### 3) Chạy API với MySQL (profile `api-mysql`)

```bash
mvn -DskipTests spring-boot:run "-Dspring-boot.run.profiles=api-mysql"
```

Biến môi trường (nếu không muốn hard-code trong file cấu hình):

- `MYSQL_URL`
- `MYSQL_USER`
- `MYSQL_PASSWORD`

### 4) Chạy POS Desktop (JavaFX)

```bash
mvn javafx:run
```

## Troubleshooting

| Triệu chứng | Nguyên nhân thường gặp | Cách xử |
|---|---|---|
| Không mở được Swagger UI | API chưa chạy hoặc port khác | Kiểm tra log và `server.port` trong `src/main/resources/application.properties` |
| Gọi API bị `401/403` | Thiếu `X-Username` hoặc thiếu quyền | Xem `docs/modules/web-api.md` và `docs/modules/identity.md` |
