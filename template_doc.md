# Template tài liệu dự án phần mềm

Mục đích: Đây là **bộ khung + nguyên tắc + mẫu cụ thể** để clone sang dự án khác. Phần **danh sách file .md** phía dưới là **ảnh chụp inventory từ Viet-ERP** — khi sang repo mới, bạn có thể xóa hoặc thay bằng danh sách do script sinh (vd. `find docs -name '*.md'`).

**Cách dùng nhanh cho dự án mới**

1. Giữ các mục: *Phân loại*, *Vòng đời*, *Cấu trúc thư mục gợi ý*, *Nguyên tắc*, *Template mẫu*.
2. Tạo tối thiểu: `README.md`, `SECURITY.md`, `CONTRIBUTING.md`, `docs/guides/getting-started.md`, `docs/adr/README.md`, `CHANGELOG.md`.
3. Gắn **Owner (tên/team)** cho từng nhóm tài liệu trong CONTRIBUTING hoặc README.
4. Diagram: ưu tiên **Mermaid** trong `docs/architecture/` (flow/sequence giống mô hình `docs/architecture/data-flow.mermaid`)

## Vòng đời tài liệu (map vào dự án bất kỳ)


| Giai đoạn            | Tài liệu thường cần                                             | Ghi chú                       |
| -------------------- | --------------------------------------------------------------- | ----------------------------- |
| Ý tưởng / RFC        | `docs/rfc/` hoặc issue có label `rfc`                           | Quyết định trước khi code     |
| Thiết kế             | ADR, diagram trong `docs/architecture/`, thiết kế API (OpenAPI) | Giảm tranh luận lặp lại       |
| Xây dựng             | README dev, getting-started, CONTRIBUTING                       | Onboarding < 1 ngày           |
| Kiểm thử             | `docs/guides/testing.md`, báo cáo test/E2E theo module          | Liên kết CI                   |
| Triển khai           | `docs/guides/deployment.md`, runbook, env matrix                | Staging trước production      |
| Vận hành             | Runbook, DR, backup/restore, on-call                            | Sự cố không phụ thuộc 1 người |
| Bàn giao / Nghỉ việc | HANDOVER, danh sách repo secrets, diagram tổng                  | Tránh kiến thức “trong đầu”   |


## Cấu trúc thư mục `docs/` gợi ý (monorepo hoặc mono-app)

Áp dụng cho dự án mới (tùy chỉnh tên):

```text
docs/
├── adr/                  # Quyết định kiến trúc (có README chỉ mục ADR)
├── architecture/         # Sơ đồ Mermaid, C4 light, data-flow
├── api/                  # OpenAPI export hoặc markdown theo module
├── database/             # Schema tổng quan, quy ước migration, ER notes
├── guides/               # getting-started, deployment, testing, contributing
├── runbooks/             # Sự cố thường gặp, thao tác DB, rollback
└── security/             # (tuỳ chọn) threat model, checklist pentest nhẹ
```

Tài liệu **theo app** đặt trong `apps/<tên-app>/docs/`; tài liệu **dùng chung** giữ ở `docs/` hoặc `packages/<pkg>/README.md`.

## Các loại tài liệu nên có trong dự án phần mềm mới

Ưu tiên (P0 → P3), kèm **owner gợi ý** và **cập nhật khi nào**:


| Ưu tiên | Tài liệu                                              | Owner gợi ý        | Cập nhật khi                          |
| ------- | ----------------------------------------------------- | ------------------ | ------------------------------------- |
| P0      | `README.md` (mô tả, quick start, link docs)           | Tech lead          | Mỗi release nhỏ có thay đổi cách chạy |
| P0      | `LICENSE`                                             | Legal / maintainer | Khi đổi giấy phép                     |
| P0      | `SECURITY.md` (cách báo lỗ hổng, policy)              | Security / lead    | Khi đổi contact hoặc quy trình        |
| P0      | `CONTRIBUTING.md`                                     | Maintainer         | Khi đổi branch/CI/review              |
| P0      | `docs/guides/getting-started.md`                      | DevEx              | Khi đổi Node/Java/Docker tối thiểu    |
| P1      | `CHANGELOG.md` hoặc release notes tự động             | Release manager    | Mỗi release                           |
| P1      | `docs/adr/` + `docs/adr/README.md`                    | Architect / senior | Mỗi quyết định kiến trúc đáng kể      |
| P1      | `docs/api/README.md` + spec (OpenAPI nếu có)          | Backend            | Mỗi breaking API                      |
| P1      | `docs/guides/deployment.md`                           | DevOps             | Mỗi thay đổi infra                    |
| P2      | `ROADMAP.md` hoặc milestone trong GitHub              | PM / lead          | Định kỳ                               |
| P2      | `docs/guides/testing.md`                              | QA / dev           | Khi thêm loại test                    |
| P2      | `docs/database/` (schema overview, migration quy ước) | DBA / backend      | Khi đổi schema lớn                    |
| P2      | `docs/runbooks/`                                      | On-call            | Sau mỗi sự cố postmortem              |
| P3      | `CODE_OF_CONDUCT.md`, templates issue/PR              | Maintainer         | Ít thay đổi                           |


**Bổ sung so với checklist tối thiểu cũ**

- **Contract-first API**: file OpenAPI YAML/JSON trong `docs/api/` hoặc link tới endpoint `/openapi.json`.
- **Ma trận môi trường**: bảng `.env` / secrets theo môi trường (local / staging / prod) — có thể nằm trong `deployment.md` (mô tả tên biến, không commit giá trị thật).
- **Bàn giao (handover)**: một file hoặc thư mục `HANDOVER.md` khi đổi nhóm hoặc contractor.

## Lưu ý và nguyên tắc khi viết tài liệu cho toàn bộ team trong dự án

Dưới đây là một số nguyên tắc và lưu ý quan trọng để đảm bảo tài liệu trong dự án luôn chất lượng, dễ duy trì và hữu ích cho mọi người. Những nguyên tắc này dựa trên các thực hành tốt nhất (như từ GitHub, Agile, và OSS Guides). Team nên tuân thủ để tránh tài liệu lỗi thời hoặc lộn xộn.

### Nguyên tắc chính (Principles)

- **Ngắn gọn và rõ ràng (Concise and Clear)**: Viết ngắn, sử dụng ngôn ngữ đơn giản, tránh lặp lại. Sử dụng tiêu đề, danh sách, và bảng để dễ đọc.
- **Cập nhật thường xuyên (Keep Updated)**: Mỗi khi code thay đổi (ví dụ: sau PR merge), cập nhật tài liệu liên quan. Sử dụng Git hooks để nhắc nhở.
- **Liên kết, đừng sao chép (Link, Don't Duplicate)**: Thay vì copy nội dung, hãy liên kết đến file khác (ví dụ: từ README.md link đến docs/api/README.md).
- **Sử dụng định dạng chuẩn (Standard Format)**: Sử dụng Markdown (.md) cho tất cả tài liệu. Thêm metadata như ngày cập nhật, tác giả ở đầu file.
- **Tập trung vào người dùng (User-Centric)**: Viết cho đối tượng cụ thể (developer, user, admin). Bao gồm ví dụ code, screenshot nếu cần.
- **Phiên bản hóa (Version Control)**: Commit tài liệu cùng code, sử dụng CHANGELOG.md để ghi thay đổi.

### Lưu ý thực tế (Notes)

- **Trách nhiệm**: Mỗi module/app nên có owner chịu trách nhiệm tài liệu (ví dụ: team CRM cập nhật docs/api/crm.md). Reviewer PR phải kiểm tra tài liệu.
- **Công cụ hỗ trợ**: Sử dụng MkDocs hoặc Docusaurus để build site docs tự động. Tích hợp với CI/CD để kiểm tra lỗi Markdown.
- **Tránh lỗi phổ biến**: Không viết tài liệu quá kỹ thuật nếu không cần; kiểm tra lỗi chính tả; đảm bảo tài liệu đa ngôn ngữ nếu dự án quốc tế (ví dụ: tiếng Việt + Anh).
- **Review và Feedback**: Mỗi tài liệu mới nên được review bởi ít nhất 1 team member. Sử dụng issue để thảo luận cải thiện.
- **Lưu trữ**: Đặt tài liệu trong thư mục `docs/` hoặc app-specific (như apps/CRM/docs/). Tránh file lẻ tẻ.

### Định nghĩa hoàn thành (DoD) tài liệu trong PR

Áp dụng cho dự án mới để tránh “code xong, docs để sau”:

- Thay đổi **hành vi người dùng / API công khai** → cập nhật `docs/api` hoặc README phần API.
- Thay đổi **cách cài đặt hoặc biến môi trường** → cập nhật `getting-started` hoặc `deployment`.
- Thay đổi **kiến trúc (stack, luồng auth, DB chính)** → ADR mới hoặc cập nhật diagram trong `docs/architecture/`.
- Thay đổi **schema DB breaking** → ghi trong `docs/database` hoặc ADR + hướng dẫn migration.
- Screenshot: chỉ khi UI thay đổi rõ; lưu trong `docs/screenshots/` hoặc trong issue release.

Tuân thủ những nguyên tắc này sẽ giúp dự án chuyên nghiệp hơn và dễ scale!

## Diagram (Mermaid) — chuẩn trong repo

Khi mô tả luồng request / event, có thể dùng `sequenceDiagram` hoặc `flowchart` trong file `*.mermaid` hoặc khối mermaid trong `.md`. Ví dụ ý tưởng: Client → Gateway → Auth → Service → Cache → ORM → PostgreSQL → Event bus — tương tự cách tách layer trong Viet-ERP (`docs/architecture/data-flow.mermaid`).

## Template Mẫu cho Từng Loại Tài Liệu

Dưới đây là các template mẫu (dưới dạng Markdown) cho từng loại tài liệu đã phân loại ở trên. Mỗi template bao gồm cấu trúc cơ bản để team dễ dàng sử dụng và mở rộng. Bạn có thể copy-paste và chỉnh sửa khi tạo file mới.

### 1. Tài liệu Tổng quan Dự án (Project Overview / README)

Copy vào `README.md` (root repo).

```markdown
# [Tên dự án]

> Một dòng mô tả giá trị cho user / doanh nghiệp.

## Đối tượng đọc

| Ai | Đọc phần nào |
|----|----------------|
| Dev mới | Quick start, link `docs/guides/getting-started.md` |
| DevOps | Deploy, env, monitoring |
| PM / stakeholder | Roadmap, scope module |

## Công nghệ chính

- Runtime: [vd. Node 20+]
- Framework: [vd. Next.js, NestJS]
- DB: [vd. PostgreSQL 16]
- Công cụ build: [vd. Turborepo, npm workspaces]

## Quick start (tối thiểu)

```bash
# ví dụ — thay bằng lệnh thật của repo
cp .env.example .env
npm install
npm run docker:up   # optional
npm run db:migrate
npm run dev
```

## Cấu trúc thư mục (tóm tắt)

- `apps/` — ứng dụng
- `packages/` — thư viện dùng chung
- `docs/` — tài liệu tập trung

## Liên kết quan trọng

- [Getting started](./docs/guides/getting-started.md)
- [Contributing](./CONTRIBUTING.md)
- [Security](./SECURITY.md)
- [Changelog](./CHANGELOG.md)
- [API](./docs/api/README.md)

## Bản quyền

[MIT / khác — trỏ `LICENSE`]

---
Cập nhật: [YYYY-MM-DD] | Maintainer: [tên/email/Slack]
```

### 2. Hướng dẫn (Guides & Tutorials)

Copy vào `docs/guides/[chủ-đề].md`.

```markdown
---
title: [Tiêu đề]
audience: [dev|ops|qa|all]
last_reviewed: YYYY-MM-DD
owner: [team/person]
---

# [Tiêu đề]

## Mục tiêu

Sau khi làm theo hướng dẫn này, người đọc sẽ [kết quả cụ thể].

## Điều kiện tiên quyết

- [ ] Đã cài [tool/version]
- [ ] Đã có quyền [role / secret store]

## Các bước

1. **Bước 1** — [mô tả]
   ```bash
   # lệnh có thể copy
   ```
2. **Bước 2** — [mô tả]

## Xác minh (verification)

- [ ] Lệnh / URL kiểm tra: [vd. curl health, smoke test]

## Xử lý sự cố (troubleshooting)

| Triệu chứng | Nguyên nhân có thể | Cách xử |
|-------------|-------------------|---------|
| ... | ... | ... |

## Tham chiếu

- [Link ADR / issue / code]

## Lưu ý

- [Rủi ro, hạn chế môi trường]
```

### 3. Tài liệu API & kỹ thuật module

Copy vào `docs/api/[module].md` hoặc tạo `openapi.yaml` + README chỉ dẫn.

```markdown
# API — [Tên module / bounded context]

## Phiên bản & base URL

- Version: [v1]
- Base URL local: `http://localhost:[port]`
- OpenAPI: [đường dẫn file hoặc `/openapi.json`]

## Auth

- Kiểu: [Bearer JWT / API key / session cookie]
- Header / cookie: [tên]
- Scope / role: [liệt kê]

## Quy ước lỗi

| HTTP | Code nội bộ | Ý nghĩa |
|------|-------------|---------|
| 400 | ... | ... |

## Endpoints (tóm tắt)

| Method | Path | Mô tả | Auth |
|--------|------|-------|------|
| GET | `/resources` | ... | ... |
| POST | `/resources` | ... | ... |

## Chi tiết endpoint (lặp cho từng endpoint quan trọng)

### `GET /resources/:id`

- **Query**: ...
- **Response 200**: (JSON mẫu)
- **Lỗi**: 404 khi ...

## Ví dụ gọi

```bash
curl -sS -H "Authorization: Bearer $TOKEN" "http://localhost:3000/api/..."
```

## Kiến trúc / luồng

- [Link diagram Mermaid hoặc hình]
- Event publish (nếu có): [tên topic / payload schema]

---
Cập nhật: [YYYY-MM-DD] | Owner: [team]
```

### 4. ADR (Architecture Decision Record)

Đặt file `docs/adr/NNN-short-title.md` và thêm vào `docs/adr/README.md` bảng mục lục.

```markdown
# ADR-NNN: [Tiêu đề ngắn]

| | |
|--|--|
| Trạng thái | Proposed / Accepted / Deprecated / Superseded by ADR-XXX |
| Ngày | YYYY-MM-DD |
| Người đề xuất | [tên] |
| Ngữ cảnh dự án | [monorepo / startup / enterprise compliance] |

## Context

Vấn đề kinh doanh hoặc kỹ thuật cần quyết định. Ràng buộc: [thời gian, budget, compliance].

## Decision

Quyết định cụ thể (một đoạn rõ ràng).

## Consequences

**Ưu điểm**

- ...

**Nhược điểm / rủi ro**

- ...

**Hành động phụ**

- Cập nhật tài liệu ...
- Training team ...

## Alternatives considered

1. **[Option A]** — Loại vì ...
2. **[Option B]** — Loại vì ...

## Links

- Issue / RFC: [#...]
- Diagram: [path]
```

### 5. Tài liệu database

Copy vào `docs/database/README.md` hoặc `schemas.md`.

```markdown
# Database — [tên hệ thống / schema]

## Tổng quan

- Engine: [PostgreSQL x.y]
- ORM / migration tool: [Prisma / TypeORM / Flyway]
- Số schema logic: [public / multi-tenant schema]

## Quy ước đặt tên

- Bảng: [snake_case / PascalCase theo convention stack]
- Khóa ngoại: [prefix `fk`_ ...]

## Sơ đồ (ERD)

- [Link ảnh hoặc pgAdmin export, hoặc Mermaid erDiagram nếu đủ đơn giản]

## Bảng cốt lõi (ví dụ)

| Bảng | Mục đích | Ghi chú |
|------|----------|---------|
| ... | ... | PK/FK chính |

## Migration & seed

- Cách chạy local: ...
- Seed dữ liệu demo: ...
- **Không** commit secret trong seed script.

## Query / index gợi ý

```sql
-- ví dụ báo cáo hoặc index quan trọng
```

---
Cập nhật: [YYYY-MM-DD]
```

### 6. Báo cáo / kiểm toán / đánh giá

```markdown
# [Loại báo cáo] — [Tên dự án / module]

| | |
|--|--|
| Ngày | YYYY-MM-DD |
| Phiên bản được đánh giá | [tag / commit] |
| Người thực hiện | [tên] |
| Phạm vi | [repo đầy đủ / chỉ app X] |

## Executive summary

3–5 bullet: kết luận chính cho người bận.

## Phương pháp

- Đã xem: [code paths, test coverage, CI logs, dependency audit]

## Findings

### [ID-001] [Tiêu đề]

- **Mô tả**
- **Mức độ**: Critical / High / Medium / Low
- **Bằng chứng**: [file, log, screenshot]
- **Khuyến nghị**: ...

## Kế hoạch hành động

| ID | Owner | Hạn | Trạng thái |
|----|-------|-----|------------|
| ... | ... | ... | Open / Done |

## Phụ lục

- Dữ liệu thô, biểu đồ.
```

### 7. Triển khai & di cư

```markdown
# Deployment / Migration — [môi trường hoặc version]

## Môi trường

| Môi trường | URL / cluster | Branch deploy | Ghi chú |
|------------|---------------|---------------|--------|
| Local | ... | ... | ... |
| Staging | ... | ... | ... |
| Production | ... | ... | ... |

## Prerequisites

- [ ] Secret/Config đã có trên [Vault / CI vars]
- [ ] DB backup gần nhất: [ticket / timestamp]

## Các bước triển khai

1. ...
2. Chạy migration: ...
3. Smoke test: ...

## Rollback

- Điều kiện kích hoạt rollback: ...
- Lệnh / bước cụ thể: ...
- Dữ liệu có mất không: ...

## Kiểm tra sau triển khai

- [ ] Health endpoint
- [ ] Metric/alert
- [ ] User acceptance nhanh

---
Cập nhật: [YYYY-MM-DD]
```

### 8. Runbook (vận hành / sự cố)

```markdown
# Runbook: [Tên sự cố hoặn thao tác]

## Triệu chứng

- ...

## Tác động

- User / SLA / dữ liệu

## Chẩn đoán nhanh

1. Kiểm tra ...
2. Lệnh: ...

## Khắc phục

1. ...
2. Escalation: [ai, khi nào]

## Phòng ngừa tái diễn

- ...

## Liên quan

- Dashboard: ...
- ADR / ticket: ...
```

### 9. Bàn giao (Handover)

```markdown
# Handover — [Dự án / vai trò]

| | |
|--|--|
| Người bàn giao | |
| Người nhận | |
| Ngày hiệu lực | |

## Tóm tắt trách nhiệm

- Repo / service chính: ...
- On-call / support channel: ...

## Quyền & secrets (chỉ tên, không giá trị)

- [ ] CI/CD
- [ ] Cloud / DB
- [ ] DNS / SSL

## Nơi tìm thông tin

| Chủ đề | Đường dẫn / công cụ |
|--------|----------------------|
| Runbook | `docs/runbooks/...` |
| ADR | `docs/adr/` |

## Việc đang dở / rủi ro

- ...

## Người liên hệ ngoài team

- Vendor / khách hàng: ...
```

### 10. Mẫu Issue / PR (tham chiếu)

Giữ trong `.github/ISSUE_TEMPLATE/` và `PULL_REQUEST_TEMPLATE.md`: mô tả **repro steps**, **expected/actual**, **environment**, checklist **docs updated**.

---

Sử dụng các template trên để tạo file mới trong dự án khác; chỉnh tên lệnh, port và stack cho đúng repo đích.