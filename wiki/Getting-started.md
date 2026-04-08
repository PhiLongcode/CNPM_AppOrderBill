# Getting started

## Mục tiêu

Chạy được API/Swagger và POS Desktop trên máy local.

## API (mặc định, SQLite)

```bash
mvn -DskipTests spring-boot:run
```

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`

## API (profile MySQL)

```bash
mvn -DskipTests spring-boot:run "-Dspring-boot.run.profiles=api-mysql"
```

Biến môi trường:

- `MYSQL_URL`
- `MYSQL_USER`
- `MYSQL_PASSWORD`

## POS Desktop

```bash
mvn javafx:run
```
