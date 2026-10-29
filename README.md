# Ourilang Backend

## Overview
Đây là backend của một ứng dụng học ngoại ngữ thông qua các nội dung thực tế như tin tức hằng ngày.
Ứng dụng được định hướng hỗ trợ nhiều ngôn ngữ khác nhau thay vì chỉ tập trung vào tiếng Anh.
Backend chịu trách nhiệm cung cấp API và xử lý logic cho mobile app.

## Features

- Đăng ký bằng email và mật khẩu, xác minh qua email.
- Gửi lại email xác minh với giới hạn thời gian và số lần gửi.
- Đăng nhập bằng email/mật khẩu.
- Đăng nhập bằng Google và liên kết tài khoản theo email.
- Phát hành Access Token và Refresh Token; làm mới Access Token và đăng xuất.
- Bảo vệ các API cần xác thực bằng JWT.
- Tự động lấy bài báo mới từ The Guardian Open Platform API.
- Lọc bài theo loại nội dung và khả năng hiển thị HTML trước khi lưu.
- Tránh lưu trùng bài theo ID từ The Guardian.
- Trả danh sách bài theo cursor và trả chi tiết bài báo.
- Tự động xóa bài đã lưu quá thời hạn cấu hình.

## Tech Stack

- Java
- Spring Boot
- Spring Web MVC
- Spring Security
- Spring Data JPA / Hibernate
- PostgreSQL
- JWT (JJWT)
- BCrypt / `PasswordEncoder`
- Spring Mail
- Spring Security OAuth2 Client
- The Guardian Open Platform API
- Bean Validation
- Maven

## Architecture

Backend được tổ chức theo các tầng, mỗi tầng đảm nhiệm một vai trò:

- **Controller** nhận HTTP request và trả response.
- **Service** điều phối nghiệp vụ.
- **Repository** truy vấn và thao tác với database.
- **Model** biểu diễn dữ liệu lưu trữ.
- **DTO** định nghĩa dữ liệu request/response và dữ liệu nhận từ Guardian.
- **Client** giao tiếp với API bên ngoài.
- **Scheduler** chạy tác vụ theo lịch.
- **Config** cấu hình bảo mật và các thành phần dùng chung.

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
│   ├── ArticleBodyFilter.java
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
│   ├── AsyncConfig.java
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
    ├── ArticleNotFoundException.java
    ├── EmailAlreadyExistsException.java
    └── GlobalExceptionHandler.java
```

## API Endpoints

### Authentication — `/api/auth`

| Method | Endpoint | Mục đích |
|---|---|---|
| `POST` | `/api/auth/register` | Gửi yêu cầu đăng ký và email xác minh |
| `GET` | `/api/auth/verify?token=...` | Xác minh email và hoàn tất đăng ký |
| `POST` | `/api/auth/resend-verification?email=...` | Gửi lại email xác minh |
| `POST` | `/api/auth/login` | Đăng nhập bằng email/mật khẩu |
| `POST` | `/api/auth/refresh` | Cấp Access Token mới từ Refresh Token hợp lệ |
| `POST` | `/api/auth/logout` | Thu hồi Refresh Token được gửi lên |

Google OAuth2 được xử lý qua Spring Security OAuth2 Login và `OAuth2SuccessHandler`; không phải endpoint REST riêng trong `AuthController`.

### Articles — `/api/articles`

| Method | Endpoint | Mục đích |
|---|---|---|
| `GET` | `/api/articles?cursor={id}&size={n}` | Lấy danh sách tóm tắt theo cursor, không kèm `body` |
| `GET` | `/api/articles/{id}` | Lấy chi tiết bài báo, gồm `body` nếu tìm thấy |

Các endpoint `/api/articles` yêu cầu xác thực theo cấu hình `SecurityConfig`. Kích thước danh sách mặc định là 20 và tối đa là 50. Khi không còn trang tiếp theo, `nextCursor` có thể là `null` và `hasMore` là `false`.

Nếu không tìm thấy bài theo ID, `ArticleNotFoundException` được xử lý thành HTTP 404 với response chứa trường `message`.

## Authentication Flow

### Đăng ký và xác minh email

1. Người dùng gửi email và mật khẩu.
2. Backend kiểm tra email, hash mật khẩu và lưu yêu cầu vào `PendingRegistration`.
3. Backend gửi email chứa token xác minh.
4. Người dùng xác minh bằng endpoint `/api/auth/verify`.
5. Backend tạo tài khoản `User` và xóa yêu cầu đăng ký tạm.

Yêu cầu gửi lại email có kiểm tra thời hạn, cooldown và giới hạn số lần gửi trong `PendingRegistration` / `AuthService`.

### Login, refresh và logout

- Đăng nhập thành công trả về Access Token và Refresh Token.
- Refresh Token được lưu trong database để backend có thể kiểm tra và thu hồi.
- Refresh cấp Access Token mới; trong implementation hiện tại, Refresh Token được giữ nguyên khi refresh thành công.
- Logout xóa Refresh Token được gửi lên nếu token tồn tại.

### Google OAuth2 và liên kết tài khoản

`SecurityConfig` khởi tạo OAuth2 Login và chuyển kết quả thành công cho `OAuth2SuccessHandler`. `AuthService.oauth2Login()` tìm tài khoản theo email, tạo tài khoản Google mới nếu chưa có hoặc chuyển tài khoản `LOCAL` sang trạng thái `LINKED` khi đăng nhập Google bằng cùng email, sau đó phát hành token.

## Article & Guardian System

### Fetch bài báo

`GuardianFetchScheduler` gọi `GuardianFetchService`, dịch vụ này sử dụng `GuardianClient` để gọi The Guardian Open Platform API. Các field được yêu cầu gồm `trailText`, `body`, `thumbnail` và `byline`.

Trước khi lưu, backend:

1. Chỉ nhận kết quả có `type` là `article`.
2. Bỏ qua kết quả thiếu ID hoặc ngày xuất bản.
3. Kiểm tra `body` bằng `ArticleBodyFilter`.
4. Bỏ qua bài có `guardianId` đã tồn tại.
5. Lưu các trường bài báo được hỗ trợ vào database.

`ArticleBodyFilter` hiện chỉ chấp nhận body không rỗng và có tên tag nằm trong danh sách cho phép (`p`, `em`, `strong`, `i`, `b`, `br`, `ul`, `li`, `h2`, `a`, `span`, `div`, `aside`, `figure`, `gu-atom`). Bài có tag ngoài danh sách này sẽ bị bỏ qua thay vì lưu. Với bài được chấp nhận, backend lưu chuỗi body nhận từ Guardian mà không chỉnh sửa nội dung.

Nếu gọi Guardian gặp `RestClientException`, lỗi được ghi log và lượt fetch kết thúc mà không lưu thêm bài từ lượt đó.

### Cleanup bài quá hạn

`ArticleCleanupScheduler` định kỳ xóa các bài có `fetchedAt` cũ hơn số giờ được cấu hình thông qua `guardian.article.max-age-hours`.

### Giá trị cấu hình hiện tại

Các giá trị mặc định đang khai báo trong `application.properties`:

| Property | Giá trị | Ý nghĩa |
|---|---:|---|
| `guardian.page-size` | `50` | Số kết quả yêu cầu mỗi lần gọi Guardian |
| `guardian.fetch.interval` | `21600000` ms (6 giờ) | Chu kỳ lấy bài |
| `guardian.cleanup.interval` | `3600000` ms (1 giờ) | Chu kỳ dọn bài quá hạn |
| `guardian.article.max-age-hours` | `20` giờ | Tuổi tối đa của bài trước khi bị xóa |
| `jwt.access-token-expiration-ms` | `3600000` ms (1 giờ) | Thời hạn Access Token |
| `jwt.refresh-token-expiration-ms` | `15552000000` ms (180 ngày) | Thời hạn Refresh Token |

Thời điểm chạy lần đầu của scheduler được trì hoãn một khoảng ngắn sau khi ứng dụng khởi động.

## Configuration

Để chạy backend, cần cấu hình:

- PostgreSQL và `spring.datasource.*`.
- SMTP mail qua `MAIL_USERNAME` và `MAIL_PASSWORD`.
- JWT secret qua `JWT_SECRET`.
- Google OAuth2 qua `GOOGLE_CLIENT_ID` và `GOOGLE_CLIENT_SECRET`.
- The Guardian API key qua `GUARDIAN_API_KEY`.
- Các giá trị chu kỳ fetch, cleanup, page size và tuổi tối đa của bài nếu cần điều chỉnh.

Không commit mật khẩu database, mail credentials, OAuth client secret hoặc JWT secret thật lên repository. Nên chuyển mọi thông tin nhạy cảm còn khai báo trực tiếp trong file cấu hình sang biến môi trường trước khi chia sẻ repository hoặc triển khai.

## Notes

- Nội dung bài báo được lưu tạm và tự động xóa theo cấu hình; hãy xác nhận điều khoản sử dụng của The Guardian phù hợp với cách fetch, lưu và hiển thị nội dung của ứng dụng.
- README mô tả hành vi thể hiện trong source code hiện tại; khi API hoặc logic thay đổi, cần cập nhật lại tài liệu.
