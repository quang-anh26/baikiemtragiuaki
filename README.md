# Quản lý món ăn – Spring Boot + Spring Web + Thymeleaf + JPA + MySQL

## Chạy
1. Cài JDK 17+, Maven, MySQL (đang chạy ở localhost:3306).
2. MSSV đã được đặt là BIT240024.
   (Windows: dùng Find & Replace toàn project BIT240024 -> MSSV, rồi đổi tên 3 file Model).
3. Sửa user/password MySQL trong `src/main/resources/application.properties`.
4. `mvn spring-boot:run`  →  http://localhost:8080
   Database `<MSSV>_MID` tự tạo, 3 bảng + cột tự sinh với tiền tố MSSV; dữ liệu mẫu tự nạp.

## Đa ngôn ngữ
Mặc định English (cookie `lang`). Nút English / Tiếng Việt ở thanh menu, giữ nguyên query string.
Thêm `?lang=vi` / `?lang=en` vào bất kỳ URL nào cũng đổi được.

## Đăng nhập bằng JWT (user / admin)
Tài khoản mẫu (tự tạo khi chạy lần đầu, mật khẩu lưu BCrypt):
| Username | Password | Role |
|---|---|---|
| admin | admin123 | ADMIN |
| user | user123 | USER |

Luồng hoạt động:
1. Trang `/login`: bấm **Sign in** -> JS gửi `POST /api/auth/login` lên server.
2. Server kiểm tra username/password, trả về **JWT** (HS256, hết hạn sau 1 giờ, chứa `sub` = username và `role`).
3. Client lưu JWT (localStorage) và gửi kèm `Authorization: Bearer <jwt>` ở mọi lần gọi API tiếp theo.
   Server cũng set cookie HttpOnly `jwt` để các trang HTML (bấm link / submit form) vẫn được xác thực.
4. Server xác thực JWT ở `JwtAuthFilter` rồi kiểm tra role ở `SecurityConfig`:
   - hợp lệ + đúng role -> **authorized** (200)
   - không có / sai / hết hạn JWT -> **unauthorized** 401 (trang web: chuyển về `/login`)
   - đúng JWT nhưng sai role -> **403 Forbidden**

Phân quyền:
- USER: chỉ xem (danh sách, chi tiết món ăn, loại món ăn).
- ADMIN: xem + thêm / sửa / xóa món ăn, loại món ăn, ảnh.
- Trang `/api-demo`: bấm nút để gọi `/api/dishes` (USER+ADMIN), `/api/admin/stats` (chỉ ADMIN), thử không gửi token / token bị sửa.

Cấu hình trong `application.properties`: `jwt.secret` (Base64, nên đổi), `jwt.expiration-ms`.
