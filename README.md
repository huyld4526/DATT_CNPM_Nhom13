# HƯỚNG DẪN CHẠY TOÀN BỘ HỆ THỐNG

# Bước 1: Chuẩn bị môi trường

## Kiểm tra Java

java -version # Cần Java 17+

## Kiểm tra Node.js

node -v # Cần Node 16+

# Kiểm tra MySQL

mysql --version # Cần MySQL 8.0+

# Bước 2: Setup Database

cd database

docker-compose up -d

cd ..

# Bước 3: Chạy Backend

cd backend

mvn clean install

mvn spring-boot:run

Backend sẽ chạy tại http://localhost:8080/api

# Bước 4: Chạy Frontend

cd frontend
go live file index.html ở post 5500 hoặc 5501

Đăng ký tài khoản mới hoặc dùng tài khoản mặc định:

Email: vungtau@gmail.com
Password: 123456

🎨 TÍNH NĂNG NỔI BẬT
✅ Giao diện đẹp, hiện đại

Thiết kế warm & classic với màu sắc ấm áp
Responsive trên mọi thiết bị
Animation mượt mà

✅ Tìm kiếm thông minh

Filter theo nhiều tiêu chí
Phân trang kết quả
Tìm kiếm real-time

✅ Quản lý bài đăng

Đăng bán sách dễ dàng
Preview trước khi đăng
Quản lý trạng thái

✅ Bảo mật

JWT authentication
Protected routes
Password encryption
