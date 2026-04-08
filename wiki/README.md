# GitHub Wiki (Bộ trang sẵn sàng publish)

Thư mục `wiki/` chứa các trang Markdown để bạn **đẩy lên GitHub Wiki** (repo riêng có dạng `<repo>.wiki.git`).

## Cách publish lên GitHub Wiki

1) Bật Wiki trong GitHub repo
- Repo → **Settings** → tick **Wikis**

2) Clone repo wiki (lưu ý: wiki là repo khác)

```bash
git clone https://github.com/PhiLongcode/CNPM_AppOrderBill.wiki.git
```

3) Copy các file trong thư mục `wiki/` sang thư mục repo wiki vừa clone

```bash
# Tại root của dự án (repo code)
cp -r wiki/* ../CNPM_AppOrderBill.wiki/
```

4) (Khuyến nghị) Copy ảnh minh hoạ vào wiki repo

```bash
mkdir -p ../CNPM_AppOrderBill.wiki/images
cp -r docs/screenshots/* ../CNPM_AppOrderBill.wiki/images/
```

5) Commit + push lên wiki

```bash
cd ../CNPM_AppOrderBill.wiki
git add .
git commit -m "docs(wiki): add project wiki pages"
git push
```

## Ghi chú

- GitHub Wiki sẽ tự nhận các trang đặc biệt:
  - `Home.md` (trang chủ)
  - `_Sidebar.md` (menu trái)
- Ảnh trong wiki được nhúng từ thư mục `images/` trong wiki repo.
