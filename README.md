# Ourilang Backend

## Overview
Đây là backend của một ứng dụng học ngoại ngữ thông qua các nội dung thực tế như tin tức hằng ngày.
Ứng dụng được định hướng hỗ trợ nhiều ngôn ngữ khác nhau thay vì chỉ tập trung vào tiếng Anh.
Backend chịu trách nhiệm cung cấp API và xử lý logic cho mobile app.

## Features
- Đăng ký tài khoản
- Đăng nhập
- Đăng nhập bằng Google
- Liên kết tài khoản
- Đăng xuất
- Bài báo
- Lấy bài báo từ The Guardian và tự động lưu tạm
- Tự động xóa bài báo quá hạn
- Lấy danh sách bài báo
- Xem chi tiết bài báo

## Tech Stack
- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- BCrypt / PasswordEncoder
- Spring Mail
- Spring Security OAuth2 Client
- The Guardian Open Platform API

## Architecture
Backend được tổ chức thành các tầng, mỗi tầng thực hiện một chức năng riêng. 
Controller nhận request, Service xử lý logic và Repository truy cập database.

## Project Structure
```
com.langly.langly_backend
├── model/
│   ├── User.java
│   ├── AuthProvider.java
│   ├── PendingRegistration.java
│   ├── RefreshToken.java
│   └── Article.java
├── repository/
│   ├── UserRepository.java
│   ├── PendingRegistrationRepository.java
│   ├── RefreshTokenRepository.java
│   └── ArticleRepository.java
├── util/
│   ├── TokenGenerator.java
│   └── JwtUtil.java
├── client/
│   └── GuardianClient.java
├── service/
│   ├── EmailService.java
│   ├── AuthService.java
│   ├── GuardianFetchService.java
│   └── ArticleService.java
├── scheduler/
│   ├── GuardianFetchScheduler.java
│   └── ArticleCleanupScheduler.java
├── controller/
│   ├── AuthController.java
│   └── ArticleController.java
├── config/
│   ├── SecurityConfig.java
│   ├── PasswordEncoderConfig.java
│   ├── JwtAuthenticationFilter.java
│   └── OAuth2SuccessHandler.java
├── dto/
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── RefreshTokenRequest.java
│   ├── AuthResponse.java
│   ├── GuardianSearchResponse.java
│   ├── ArticleSummary.java
│   ├── ArticleSummaryResponse.java
│   ├── ArticleDetailResponse.java
│   └── ArticlePageResponse.java
└── exception/
    └── EmailAlreadyExistsException.java
```

## Authentication

### Đăng ký
- User đăng ký bằng email và password
- Gửi email xác nhận
- User xác nhận email
- Tạo tài khoản

### Đăng nhập
- User đăng nhập bằng email và password
- Kiểm tra thông tin đăng nhập
- Trả Access Token và Refresh Token

### Đăng nhập bằng Google
- Xác thực bằng Google
- Đăng nhập
- Trả Access Token và Refresh Token

### Liên kết tài khoản
- User đã đăng ký bằng email/password
- Đăng nhập bằng Google với cùng email
- Liên kết tài khoản Google với tài khoản hiện tại

### Đăng xuất
- Xóa Refresh Token

## Article & Guardian System

### Lấy bài báo
Backend lấy bài báo từ The Guardian Open Platform API thông qua `GuardianFetchScheduler`.
Sau khi lấy được danh sách bài, backend chỉ giữ các bài có loại `article`, rồi kiểm tra xem bài đã tồn tại trong cơ sở dữ liệu hay chưa:
- Nếu chưa tồn tại, bài báo được lưu vào database cùng thời điểm lấy về (`fetchedAt`).
- Nếu đã tồn tại, bài báo sẽ được bỏ qua.
  Nội dung bài (`body`, dạng HTML) được lưu nguyên văn, backend không chỉnh sửa.

### Xóa bài báo quá hạn
Theo điều khoản của The Guardian, nội dung chỉ được lưu tạm trong thời gian giới hạn.
`ArticleCleanupScheduler` chạy định kỳ và xóa các bài đã được lấy về quá số giờ cho phép (tính từ `fetchedAt`).

### Article
Mobile gọi API của backend để lấy dữ liệu từ database:
- `GET /api/articles`: danh sách bài báo (không kèm nội dung `body`), phân trang theo cursor.
- `GET /api/articles/{id}`: chi tiết một bài báo, có kèm `body`. Trả về 404 nếu bài không còn tồn tại.

## Configuration
Backend cần cấu hình các thành phần sau để có thể hoạt động:
- Kết nối với database
- Cấu hình mail để gửi email xác nhận tài khoản
- Cấu hình JWT secret key
- Cấu hình Google OAuth2
- Cấu hình The Guardian API: API key (biến môi trường `GUARDIAN_API_KEY`), số bài mỗi lần lấy
- Cấu hình thời gian tự động lấy bài báo từ Guardian, thời gian chạy dọn bài và số giờ tối đa được giữ một bài