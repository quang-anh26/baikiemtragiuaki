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
