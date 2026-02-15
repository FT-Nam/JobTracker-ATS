# 🔌 JobTracker ATS API Documentation

## 📋 Tổng quan API

JobTracker ATS (Applicant Tracking System) cung cấp RESTful API với thiết kế REST chuẩn, sử dụng JSON cho data exchange và JWT cho authentication. API được thiết kế cho **B2B multi-tenant SaaS** với data isolation theo company.

### 🎯 API Design Principles
- **RESTful**: Tuân thủ REST conventions
- **Stateless**: JWT-based authentication
- **Multi-Tenant**: Data isolation bằng `company_id` trong mọi requests
- **Versioned**: API versioning với `/api/v1`
- **Consistent**: Uniform response format
- **Secure**: HTTPS, JWT, input validation, RBAC, email verification
- **Documented**: OpenAPI 3.0 specification

### 🔧 Base Configuration
```
Base URL: https://api.jobtracker.com/api/v1
Content-Type: application/json
Authorization: Bearer <oauth2_access_token>
X-Company-Id: <company_id> (Optional - auto-extracted from user context)
```

### 🔑 Multi-Tenant Context
- Mọi API request tự động filter theo `company_id` của user
- User chỉ có thể truy cập data của company mình
- System Admin có thể truy cập tất cả companies

## 🔐 Authentication APIs

> **🔑 B2B SaaS Auth Flow**: 
> - **Email + Password** (bắt buộc)
> - **Email Verification** (bắt buộc)
> - **Invite-based User Creation**: Admin tạo user → Gửi invite email → User set password → Email verified
> - **Không có Google OAuth** (trừ enterprise SSO - story khác)

### 1. Company Self-Signup (Company Admin Registration)
**POST** `/auth/register`

Đăng ký công ty mới và tạo Company Admin user. Đây là **mô hình 1 - Self Signup** (phổ biến cho SaaS B2B).

> ⚠️ **Lưu ý**: Chỉ dành cho Company Admin tự signup. Các users khác được tạo qua invite flow.

#### Request Body
```json
{
  "companyName": "Acme Corp",
  "email": "admin@acme.com",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890"
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Company and admin user created successfully. Please verify your email.",
  "data": {
    "company": {
      "id": "c1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "name": "Acme Corp"
    },
    "user": {
      "id": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "email": "admin@acme.com",
      "firstName": "John",
      "lastName": "Doe",
      "roleName": "COMPANY_ADMIN",
      "emailVerified": false,
      "isActive": true
    }
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

> **Flow sau registration**:
> 1. System tạo Company
> 2. System tạo Admin user với `email_verified = false`
> 3. System gửi email verification token
> 4. User click link trong email → Verify email → `email_verified = true` → User có thể login

#### Error Response (400 Bad Request)
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Email is required"
    },
    {
      "field": "password",
      "message": "Password must be at least 8 characters"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 2. User Login
**POST** `/auth/login`

Đăng nhập với email và password.

#### Request Body
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "id": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "roleName": "USER",
      "avatarUrl": null
    },
    "tokens": {
      "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
      "expiresIn": "2024-01-15T11:30:00Z",
      "refreshExpiresIn": "2024-02-15T10:30:00Z"
    }
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 3. Email Verification
**POST** `/auth/verify-email`

Xác thực email với token từ email verification link.

> ⚠️ **Bắt buộc**: User phải verify email trước khi có thể login (trừ khi được Admin tạo và verify sẵn).

#### Request Body
```json
{
  "token": "email_verification_token_here"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Email verified successfully",
  "data": {
    "email": "admin@acme.com",
    "emailVerified": true
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### Error Response (400 Bad Request)
```json
{
  "success": false,
  "message": "Invalid or expired verification token",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 4. Resend Verification Email
**POST** `/auth/resend-verification`

Gửi lại email verification.

#### Request Body
```json
{
  "email": "admin@acme.com"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Verification email sent",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 5. Refresh Token
**POST** `/auth/refresh`

Làm mới access token bằng refresh token.

#### Request Body
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Token refreshed successfully",
  "data": {
    "user": {
      "id": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "roleName": "USER",
      "avatarUrl": null
    },
    "tokens": {
      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "expiresIn": "2024-01-15T11:30:00Z",
      "refreshExpiresIn": "2024-02-15T10:30:00Z"
    }
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 5. Logout
**POST** `/auth/logout`

Đăng xuất và vô hiệu hóa token.

> **Token Invalidation Flow**:
> 1. System parse access token từ Authorization header
> 2. System lấy JWT ID (`jit`) và `expiry_time` từ token claims
> 3. System lưu vào bảng `invalidated_token` với `id = jit` và `expiry_time = token expiry`
> 4. System xóa refresh token từ Redis cache (nếu có)
> 5. Token đã bị invalidate → Không thể dùng lại cho các requests sau
> 
> **Token Verification**: Khi verify token trong authentication filter, system sẽ check xem `jit` có trong `invalidated_token` không. Nếu có → Reject request.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Request Body
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Logout successful",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 6. Forgot Password
**POST** `/auth/forgot-password`

Gửi email reset password.

#### Request Body
```json
{
  "email": "user@example.com"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Password reset email sent",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 7. Reset Password
**POST** `/auth/reset-password`

Reset password với token từ email.

#### Request Body
```json
{
  "token": "reset_token_here",
  "newPassword": "NewSecurePassword123!"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Password reset successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 👤 User Management APIs

### 1. Get Current User Profile
**GET** `/users/profile`

Lấy thông tin profile của user hiện tại.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Profile retrieved successfully",
  "data": {
    "id": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+1234567890",
    "avatarUrl": "https://dropbox.com/avatar.jpg",
    "roleName": "USER",
    "isActive": true,
    "emailVerified": true,
    "googleId": null,
    "lastLoginAt": "2024-01-15T09:00:00Z",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-15T10:30:00Z",
    "createdBy": null,
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "deletedAt": null
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 2. Update User Profile
**PUT** `/users/profile`

Cập nhật thông tin profile.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Request Body
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "id": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+1234567890",
    "avatarUrl": "https://dropbox.com/avatar.jpg",
    "roleName": "USER",
    "isActive": true,
    "emailVerified": true,
    "googleId": null,
    "lastLoginAt": "2024-01-15T09:00:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 3. Upload Avatar
**POST** `/users/avatar`

Upload ảnh đại diện.

#### Request Headers
```
Authorization: Bearer <access_token>
Content-Type: multipart/form-data
```

#### Request Body (Form Data)
```
file: <image_file>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Avatar uploaded successfully",
  "data": {
    "avatarUrl": "https://dropbox.com/avatars/user_1_avatar.jpg"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 4. Change Password
**PUT** `/users/change-password`

Thay đổi mật khẩu.

#### Request Headers
```

Authorization: Bearer <access_token>
```

#### Request Body
```json
{
  "currentPassword": "CurrentPassword123!",
  "newPassword": "NewPassword123!"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Password changed successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 👥 Admin User Management APIs

> **🔑 Invite-based User Creation**: Admin tạo user → System gửi invite email → User click link → Set password → Email verified
> 
> Chỉ dành cho **COMPANY_ADMIN** hoặc **HR** (có quyền) để quản lý users trong company của mình.

### 1. Get Users
**GET** `/admin/users`

Query hỗ trợ `role`, `status`, `search`, `createdFrom`.

```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": [
    {
      "id": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "email": "admin@gmail.com",
      "firstName": "Admin",
      "lastName": "User",
      "phone": null,
      "avatarUrl": null,
      "roleId": "34d9a2e3-1a30-4a1a-b1ad-4b6d2619f1ce",
      "roleName": "ADMIN",
      "isActive": true,
      "emailVerified": true,
      "lastLoginAt": "2024-01-15T09:00:00Z",
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-15T09:00:00Z",
      "deletedAt": null
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z",
  "paginationInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 12,
    "totalPages": 1
  }
}
```

### 2. Invite User (Create User via Invite)
**POST** `/admin/users/invite`

Tạo user mới và gửi invite email. Đây là **flow chuẩn B2B SaaS** (Jira, Linear, Slack).

> **Flow**:
> 1. Admin tạo user → `email_verified = false`, `password = NULL`, `is_active = false`
> 2. System generate invite token (random UUID hoặc secure random string) → Lưu vào bảng `user_invitations` với `expires_at = NOW() + 7 days`
> 3. System gửi invite email với link: `https://app.jobtracker.com/accept-invite?token={token}`
> 4. User click link trong email → `POST /auth/accept-invite` với token → Set password → `email_verified = true`, `is_active = true`, `used_at` được set trong `user_invitations`
> 
> **Token Storage**: Token được lưu trong bảng `user_invitations` với các fields:
> - `token`: Unique invite token (VARCHAR(255))
> - `user_id`: FK to users
> - `company_id`: Multi-tenant key
> - `expires_at`: Thời gian hết hạn (7 ngày)
> - `used_at`: NULL nếu chưa dùng, TIMESTAMP nếu đã accept
> - `sent_at`: Thời gian gửi email

#### Request Headers
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

#### Request Body
```json
{
  "email": "new.user@company.com",
  "firstName": "New",
  "lastName": "User",
  "phone": "+12065551212",
  "roleId": "34d9a2e3-1a30-4a1a-b1ad-4b6d2619f1ce",
  "isBillable": true
}
```

> **Lưu ý**:
> - `password` không cần trong request (user sẽ set qua invite link)
> - `isBillable`: `true` cho ADMIN/HR, `false` cho INTERVIEWER
> - System tự động set `email_verified = false`, `password = NULL`, `is_active = false`

#### Response (201 Created)
```json
{
  "success": true,
  "message": "User invited successfully. Invitation email sent.",
  "data": {
    "id": "8b54b7f1-3f14-43a6-9a9a-5fefdc136d91",
    "email": "new.user@company.com",
    "firstName": "New",
    "lastName": "User",
    "phone": "+12065551212",
    "roleName": "HR",
    "isActive": false,
    "emailVerified": false,
    "isBillable": true,
    "inviteSentAt": "2024-01-20T08:00:00Z",
    "createdAt": "2024-01-20T08:00:00Z"
  },
  "timestamp": "2024-01-20T08:00:00Z"
}
```

### 3. Accept Invite (Set Password)
**POST** `/auth/accept-invite`

User nhận invite email, click link, và set password. Sau khi set password, `email_verified = true` và `is_active = true`.

> ⚠️ **Public endpoint**: Không cần authentication (chỉ cần invite token).
> 
> **Token Validation**:
> 1. System tìm record trong `user_invitations` với `token = {token}`
> 2. Validate: `used_at IS NULL` (chưa dùng) AND `expires_at > NOW()` (chưa hết hạn) AND `deleted_at IS NULL`
> 3. Nếu valid → Set password → Update `users.email_verified = true`, `users.is_active = true` → Set `user_invitations.used_at = NOW()`
> 4. Nếu invalid → Return error: "Invalid or expired invitation token"

#### Request Body
```json
{
  "token": "invite_token_from_email",
  "password": "SecurePassword123!"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Invitation accepted. Email verified. You can now login.",
  "data": {
    "email": "new.user@company.com",
    "emailVerified": true,
    "isActive": true
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 4. Resend Invite
**POST** `/admin/users/{userId}/resend-invite`

Gửi lại invite email cho user chưa verify.

> **Flow**:
> 1. System tìm user với `email_verified = false` hoặc `is_active = false`
> 2. System tạo invite token mới → Insert record mới vào `user_invitations` (hoặc update record cũ nếu chưa used)
> 3. System gửi email với token mới
> 4. Token cũ vẫn có thể dùng (nếu chưa expired), nhưng thường chỉ dùng token mới nhất

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Invitation email resent",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 5. Get User Details

### 3. Get User Details
**GET** `/admin/users/{id}`

Trả về thông tin đầy đủ của user kèm audit.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+1234567890",
    "avatarUrl": null,
    "roleId": "34d9a2e3-1a30-4a1a-b1ad-4b6d2619f1ce",
    "roleName": "USER",
    "isActive": true,
    "emailVerified": true,
    "googleId": null,
    "lastLoginAt": "2024-01-15T09:00:00Z",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-15T10:30:00Z",
    "createdBy": null,
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "deletedAt": null
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 4. Update User
**PUT** `/admin/users/{id}`

#### Request Body
```json
{
  "firstName": "Jane",
  "lastName": "Doe",
  "phone": "+84123456789",
  "roleId": "781af566-48d8-4066-9fd7-78284b642df0",
  "isActive": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "User updated successfully",
  "data": {
    "id": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "email": "user@example.com",
    "firstName": "Jane",
    "lastName": "Doe",
    "phone": "+84123456789",
    "avatarUrl": null,
    "roleId": "781af566-48d8-4066-9fd7-78284b642df0",
    "roleName": "HIRING_MANAGER",
    "isActive": true,
    "emailVerified": true,
    "googleId": null,
    "lastLoginAt": "2024-01-15T09:00:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 5. Deactivate / Soft Delete User
**DELETE** `/admin/users/{id}`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "User deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 6. Restore User
**PATCH** `/admin/users/{id}/restore`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "User restored successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 💼 Job Management APIs (Job Postings - ATS)

> **🔄 SEMANTIC CHANGE**: Jobs = Job Postings (tin tuyển dụng), không phải "job applied". HR/Recruiter tạo job postings để candidates apply.

### 1. Get All Jobs
**GET** `/jobs`

Lấy danh sách tất cả job postings của company với pagination và filtering.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Query Parameters
```
page=0&size=20&sort=createdAt,desc&status=PUBLISHED&jobStatus=DRAFT&search=developer&isRemote=true
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Jobs retrieved successfully",
  "data": [
    {
      "id": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
      "userId": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
      "title": "Senior Java Developer",
      "position": "Backend Developer",
      "jobType": "FULL_TIME",
      "location": "Mountain View, CA",
      "salaryMin": 120000,
      "salaryMax": 180000,
      "currency": "USD",
      "jobStatus": "PUBLISHED",
      "deadlineDate": "2024-01-25",
      "publishedAt": "2024-01-10T09:00:00Z",
      "expiresAt": "2024-01-25T23:59:59Z",
      "viewsCount": 150,
      "applicationsCount": 25,
      "jobDescription": "We are looking for a senior Java developer...",
      "requirements": "5+ years of Java experience...",
      "benefits": "Health insurance, 401k, stock options...",
      "jobUrl": "https://careers.google.com/jobs/123",
      "isRemote": false,
      "createdAt": "2024-01-10T09:00:00Z",
      "updatedAt": "2024-01-10T09:00:00Z",
      "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "deletedAt": null
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z",
  "paginationInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 2. Get Job by ID
**GET** `/jobs/{id}`

Lấy thông tin chi tiết một job.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job retrieved successfully",
  "data": {
    "id": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "userId": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
    "title": "Senior Java Developer",
    "position": "Backend Developer",
    "jobType": "FULL_TIME",
    "location": "Mountain View, CA",
    "salaryMin": 120000,
    "salaryMax": 180000,
    "currency": "USD",
    "jobStatus": "PUBLISHED",
    "deadlineDate": "2024-01-25",
    "publishedAt": "2024-01-10T09:00:00Z",
    "expiresAt": "2024-01-25T23:59:59Z",
    "viewsCount": 150,
    "applicationsCount": 25,
    "jobDescription": "We are looking for a senior Java developer...",
    "requirements": "5+ years of Java experience...",
    "benefits": "Health insurance, 401k, stock options...",
    "jobUrl": "https://careers.google.com/jobs/123",
    "isRemote": false,
    "createdAt": "2024-01-10T09:00:00Z",
    "updatedAt": "2024-01-10T09:00:00Z",
    "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "deletedAt": null
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 3. Create New Job Posting
**POST** `/jobs`

Tạo job posting mới (HR/Recruiter tạo tin tuyển dụng).

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Request Body
```json
{
  "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
  "title": "Senior Java Developer",
  "position": "Backend Developer",
  "jobType": "FULL_TIME",
  "location": "Mountain View, CA",
  "salaryMin": 120000,
  "salaryMax": 180000,
  "currency": "USD",
  "jobStatus": "DRAFT",
  "deadlineDate": "2024-01-25",
  "jobDescription": "We are looking for a senior Java developer...",
  "requirements": "5+ years of Java experience...",
  "benefits": "Health insurance, 401k, stock options...",
  "jobUrl": "https://careers.google.com/jobs/123",
  "isRemote": false,
  "skillIds": ["skill1", "skill2", "skill3"]
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Job created successfully",
  "data": {
    "id": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "userId": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
    "title": "Senior Java Developer",
    "position": "Backend Developer",
    "jobType": "FULL_TIME",
    "location": "Mountain View, CA",
    "salaryMin": 120000,
    "salaryMax": 180000,
    "currency": "USD",
    "jobStatus": "DRAFT",
    "deadlineDate": "2024-01-25",
    "publishedAt": null,
    "expiresAt": null,
    "viewsCount": 0,
    "applicationsCount": 0,
    "isRemote": false,
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 4. Update Job
**PUT** `/jobs/{id}`

Cập nhật thông tin job.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Request Body
```json
{
  "title": "Senior Java Developer - Updated",
  "position": "Backend Developer",
  "jobStatus": "PUBLISHED",
  "publishedAt": "2024-01-20T09:00:00Z"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job updated successfully",
  "data": {
    "id": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "title": "Senior Java Developer - Updated",
    "position": "Backend Developer",
    "jobStatus": "PUBLISHED",
    "publishedAt": "2024-01-20T09:00:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 5. Delete Job
**DELETE** `/jobs/{id}`

Xóa job (soft delete).

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 6. Publish/Unpublish Job Posting
**PATCH** `/jobs/{id}/status`

Publish hoặc unpublish job posting (chuyển từ DRAFT → PUBLISHED, hoặc ngược lại).

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Request Body
```json
{
  "jobStatus": "PUBLISHED",
  "publishedAt": "2024-01-15T10:30:00Z",
  "expiresAt": "2024-02-15T23:59:59Z"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job status updated successfully",
  "data": {
    "id": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "jobStatus": "PUBLISHED",
    "publishedAt": "2024-01-15T10:30:00Z",
    "expiresAt": "2024-02-15T23:59:59Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 7. Manage Job Skills

**GET** `/jobs/{jobId}/skills`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job skills retrieved successfully",
  "data": [
    {
      "id": "f8g9h0i1-2j3k-4l5m-6n7o-p8q9r0s1t2u3",
      "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
      "skillId": "a3e6e84c-5f21-4c4d-8d7d-4a38e9ab6f52",
      "name": "Java",
      "category": "PROGRAMMING",
      "isRequired": true,
      "proficiencyLevel": "ADVANCED",
      "createdAt": "2024-01-10T10:30:00Z",
      "updatedAt": "2024-01-10T10:30:00Z"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**POST** `/jobs/{jobId}/skills`

#### Request Body
```json
{
  "skillId": "b7e58a6e-5c5e-4de8-9a3f-6b1ae2d042b5",
  "isRequired": true,
  "proficiencyLevel": "INTERMEDIATE"
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Job skill added successfully",
  "data": {
    "id": "f8g9h0i1-2j3k-4l5m-6n7o-p8q9r0s1t2u3",
    "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "skillId": "b7e58a6e-5c5e-4de8-9a3f-6b1ae2d042b5",
    "name": "Spring Boot",
    "category": "FRAMEWORK",
    "isRequired": true,
    "proficiencyLevel": "INTERMEDIATE",
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**PATCH** `/jobs/{jobId}/skills/{skillId}`

#### Request Body
```json
{
  "isRequired": false,
  "proficiencyLevel": "ADVANCED"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job skill updated successfully",
  "data": {
    "id": "f8g9h0i1-2j3k-4l5m-6n7o-p8q9r0s1t2u3",
    "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "skillId": "b7e58a6e-5c5e-4de8-9a3f-6b1ae2d042b5",
    "name": "Spring Boot",
    "category": "FRAMEWORK",
    "isRequired": false,
    "proficiencyLevel": "ADVANCED",
    "createdAt": "2024-01-10T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**DELETE** `/jobs/{jobId}/skills/{skillId}`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job skill removed",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### ~~8. Manage Job Resumes~~ ❌ **REMOVED**

> **Lý do**: Modern ATS không cần bảng riêng cho resumes. CVs được lưu trong `attachments` table:
> - **Workflow chính**: Candidates tự upload CV qua public API `/public/jobs/{jobId}/apply`
> - **Workflow phụ**: HR upload CV thủ công khi nhận qua email

## 📝 Applications Management APIs (CORE ATS) ➕

> **🔑 CORE**: Applications là core entity của ATS. **Modern ATS = Candidate Self-Service Portal**: Candidates tự apply online qua trang công ty mà không cần login. HR/Recruiter quản lý applications qua workflow (NEW → SCREENING → INTERVIEWING → OFFERED → HIRED/REJECTED).
> 
> **Workflow chính**: Candidate Self-Service (apply online, upload CV/attachments)  
> **Workflow phụ**: HR manual upload (khi nhận CV qua email)

### 🔓 Public APIs (Candidate Self-Service - Không cần Authentication)

#### 1. Apply to Job (Public - Candidate Self-Service)
**POST** `/public/jobs/{jobId}/apply`

Candidates tự apply online mà không cần login. Đây là **workflow chính** của Modern ATS.

> ⚠️ **Public endpoint**: Không yêu cầu `Authorization` header.  
> ✅ **Security**: Rate limiting, CAPTCHA (optional), email verification token

#### Request Headers
```
Content-Type: multipart/form-data
```

#### Request Body (Form Data)
```
candidateName: "John Doe"
candidateEmail: "john.doe@example.com"
candidatePhone: "+1234567890"
coverLetter: "I am interested in this position..."
resume: <file> (PDF - max 5B) [REQUIRED]
```

> **Lưu ý về Attachments:**
> - ✅ **Khi apply**: Chỉ upload CV (resume) - đây là bắt buộc
> - ❌ **Không upload** certificates/portfolio khi apply lần đầu
> - 📋 **Sau khi apply**: Nếu HR yêu cầu thêm documents (khi status = SCREENING/INTERVIEWING), candidate sẽ upload qua API `/public/applications/{applicationToken}/attachments`

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Đơn ứng tuyển đã được gửi thành công! Chúng tôi sẽ liên hệ với bạn qua email.",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

> **Lưu ý**: 
> - Response đơn giản, không expose thông tin không cần thiết
> - Candidate đã biết jobTitle, candidateName, email (họ vừa submit)
> - Application được tạo với `status = NEW` tự động
> - Email confirmation được gửi sau đó với `applicationToken` để candidate track status
> - CV scoring được xử lý trong background (2-3 giây), không cần trả về trong response

> **Lưu ý**: 
> - Application được tạo với `status = NEW` tự động
> - `created_by` = NULL (candidate không có account)
> - Email confirmation được gửi đến candidate
> - Application token cho phép candidate track status mà không cần login
> - **CV Scoring**: CV được xử lý **synchronous** (2-3 giây) → Match score có ngay trong response
> - `matchScore = null` nếu parsing failed hoặc chưa có CV

> **🔍 CV Scoring Process (Synchronous - 2-3 giây)**:
> 
> Sau khi upload CV, system tự động tính match score ngay trong request:
> 1. **PDF Parsing**: Extract text từ CV (PDF) → ~1-2 giây
> 2. **Load Job Skills**: Query `job_skills` table → ~100ms
> 3. **Skill Matching**: Normalize, tokenize, match skills → ~500ms
> 4. **Score Calculation**: Tính điểm (0-100) → ~100ms
> 5. **Save Results**: Lưu `matchScore` và breakdown vào response
> 
> **Total**: ~2-3 giây (sync processing, không cần async)
> 
> **Response**:
> - `matchScore`: Integer 0-100 (hoặc `null` nếu failed)
> - `matchScoreDetails`: Breakdown skills (hoặc `null` nếu failed)

#### 2. Upload Additional Attachments (Public - HR Request Only)
**POST** `/public/applications/{applicationToken}/attachments`

Candidates chỉ có thể upload thêm attachments (certificates, portfolio) **khi HR yêu cầu** trong quá trình review.

> ⚠️ **Public endpoint**: Chỉ cần `applicationToken` (không phải JWT), không cần login

> 📋 **Business Logic - Chỉ cho phép upload khi HR đã yêu cầu:**
> 
> **Điều kiện upload:**
> - ✅ Application status phải là: `SCREENING` hoặc `INTERVIEWING` (HR đang review)
> - ✅ **VÀ** `allow_additional_uploads = true` (HR đã set flag yêu cầu documents)
> 
> **Workflow:**
> 1. Candidate apply → Upload CV (RESUME) - **Bắt buộc khi apply**
>    - `allow_additional_uploads = false` (mặc định)
> 2. HR review → Status chuyển sang SCREENING/INTERVIEWING
> 3. HR yêu cầu thêm documents → Set `allow_additional_uploads = true` (qua API hoặc UI)
>    - HR có thể set flag này khi:
>      - Comment với `requestDocuments = true`
>      - Hoặc qua API `PATCH /applications/{id}` với `allowAdditionalUploads: true`
> 4. Candidate thấy flag được bật → Upload thêm documents qua API này
> 5. Sau khi upload xong → HR có thể set `allow_additional_uploads = false` để tắt
> 
> **Lý do**: 
> - Tránh spam upload, chỉ upload khi HR thực sự yêu cầu
> - HR có control hoàn toàn về việc khi nào cho phép upload
> - Candidate không thể tự ý upload khi chỉ thấy status = SCREENING/INTERVIEWING

#### Request Headers
```
Content-Type: multipart/form-data
```

#### Request Body (Form Data)
```
file: <file>
attachmentType: CERTIFICATE | PORTFOLIO | OTHER
description: "AWS Certification"
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Attachment uploaded successfully",
  "data": {
    "id": "a1b2c3d4-5e6f-7g8h-9i0j-k1l2m3n4o5p6",
    "applicationId": "app1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "filename": "aws_certificate.pdf",
    "attachmentType": "CERTIFICATE",
    "fileSize": 256000,
    "uploadedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### Error Responses

**403 Forbidden** - Không cho phép upload (status không đúng hoặc HR chưa yêu cầu)
```json
{
  "success": false,
  "message": "Cannot upload attachments. HR has not requested additional documents yet. Please wait for HR to request documents before uploading.",
  "errors": [
    {
      "field": "allowAdditionalUploads",
      "message": "Attachments can only be uploaded when: 1) Application status is SCREENING or INTERVIEWING, AND 2) HR has set allowAdditionalUploads = true. Current status: NEW, allowAdditionalUploads: false"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**403 Forbidden** - Status không đúng (không phải SCREENING/INTERVIEWING)
```json
{
  "success": false,
  "message": "Cannot upload attachments. Application status must be SCREENING or INTERVIEWING.",
  "errors": [
    {
      "field": "applicationStatus",
      "message": "Attachments can only be uploaded when application status is SCREENING or INTERVIEWING. Current status: OFFERED"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**404 Not Found** - Application token không hợp lệ
```json
{
  "success": false,
  "message": "Application not found",
  "errors": [
    {
      "field": "applicationToken",
      "message": "Invalid application token"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### 3. Track Application Status (Public)
**GET** `/public/applications/{applicationToken}/status`

Candidates có thể track status của application bằng token (không cần login). 

> ⚠️ **Lưu ý**: API này **KHÔNG** trả về match score, missing skills, hoặc các thông tin nội bộ. Chỉ trả về thông tin cần thiết cho candidate.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Application status retrieved successfully",
  "data": {
    "id": "app1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "jobTitle": "Senior Java Developer",
    "candidateName": "John Doe",
    "candidateEmail": "john.doe@example.com",
    "status": {
      "name": "SCREENING",
      "displayName": "Sàng lọc",
      "color": "#8B5CF6"
    },
    "appliedDate": "2024-01-15",
    "updatedAt": "2024-01-16T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

> **Lưu ý**: 
> - **KHÔNG** trả về `matchScore`, `matchScoreDetails`, `missingSkills` - đây là thông tin nội bộ cho HR
> - Chỉ trả về thông tin cần thiết: status, job title, applied date
> - Candidates không cần biết điểm số hay thiếu skill gì

### 🔐 Protected APIs (HR/Recruiter Management - Yêu cầu Authentication)

### 1. Get All Applications
**GET** `/applications`

Lấy danh sách tất cả applications của company với pagination và filtering. Hỗ trợ filter/sort theo match score.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Query Parameters
```
page=0&size=20&sort=appliedDate,desc&status=NEW&jobId=xxx&assignedTo=xxx&search=john
&sortBy=matchScore&sortOrder=desc&minMatchScore=50&maxMatchScore=100
```

**Query Parameters:**
- `page`: Page number (default: 0)
- `size`: Page size (default: 20)
- `sort`: Sort field và direction (default: `appliedDate,desc`)
  - Available fields: `appliedDate`, `matchScore`, `candidateName`, `createdAt`
- `status`: Filter by application status (NEW, SCREENING, INTERVIEWING, etc.)
- `jobId`: Filter by job ID
- `assignedTo`: Filter by assigned HR/Recruiter user ID
- `search`: Search by candidate name or email
- `sortBy`: Sort by field (optional, overrides `sort` param)
  - `matchScore`: Sort by match score (highest first)
  - `appliedDate`: Sort by applied date
- `sortOrder`: `asc` or `desc` (default: `desc`)
- `minMatchScore`: Filter applications với match score >= value (0-100)
- `maxMatchScore`: Filter applications với match score <= value (0-100)

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Applications retrieved successfully",
  "data": [
    {
      "id": "app1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
      "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
      "candidateName": "John Doe",
      "candidateEmail": "john.doe@example.com",
      "candidatePhone": "+1234567890",
      "statusId": "status1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "status": {
      "id": "status1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "name": "NEW",
      "displayName": "Mới",
      "color": "#3B82F6"
    },
      "source": "Email",
      "appliedDate": "2024-01-15",
      "resumeFilePath": "/applications/app1/resume.pdf",
      "coverLetter": "I am interested in this position...",
      "notes": "Strong candidate, good fit",
      "rating": 4,
      "assignedTo": "user1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "assignedToName": "Jane Recruiter",
      "matchScore": 82,
      "matchScoreDetails": {
        "matchedRequiredCount": 3,
        "totalRequiredCount": 4,
        "matchedOptionalCount": 2,
        "totalOptionalCount": 5,
        "matchedRequiredSkills": ["Java", "Spring Boot", "MySQL"],
        "missingRequiredSkills": ["Docker"],
        "matchedOptionalSkills": ["Git", "JUnit"],
        "missingOptionalSkills": ["AWS", "Redis", "Kubernetes"]
      },
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z",
  "paginationInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 2. Get Application by ID
**GET** `/applications/{id}`

Lấy thông tin chi tiết một application, bao gồm full match score breakdown.

> **🔍 Match Score Details**: Response bao gồm đầy đủ thông tin về CV scoring:
> - `matchScore`: Điểm khớp (0-100)
> - `matchScoreDetails`: Breakdown chi tiết skills matched/missing

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Application retrieved successfully",
  "data": {
    "id": "app1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "jobTitle": "Senior Java Developer",
    "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
    "candidateName": "John Doe",
    "candidateEmail": "john.doe@example.com",
    "candidatePhone": "+1234567890",
    "statusId": "status1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "status": {
      "id": "status1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "name": "NEW",
      "displayName": "Mới",
      "color": "#3B82F6"
    },
    "source": "Email",
    "appliedDate": "2024-01-15",
    "resumeFilePath": "/applications/app1/resume.pdf",
    "coverLetter": "I am interested in this position...",
    "notes": "Strong candidate, good fit",
    "rating": 4,
    "assignedTo": "user1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "assignedToName": "Jane Recruiter",
    "matchScore": 82,
    "matchScoreDetails": {
      "matchedRequiredCount": 3,
      "totalRequiredCount": 4,
      "matchedOptionalCount": 2,
      "totalOptionalCount": 5,
      "matchedRequiredSkills": ["Java", "Spring Boot", "MySQL"],
      "missingRequiredSkills": ["Docker"],
      "matchedOptionalSkills": ["Git", "JUnit"],
      "missingOptionalSkills": ["AWS", "Redis", "Kubernetes"]
    },
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

> **📊 Match Score Breakdown Explanation**:
> - **matchScore**: 82/100 - Điểm khớp tổng thể giữa CV và Job Description
> - **matchedRequiredCount**: 3/4 - Đã match 3 trong 4 required skills
> - **matchedOptionalCount**: 2/5 - Đã match 2 trong 5 optional skills
> - **matchedRequiredSkills**: Danh sách required skills đã tìm thấy trong CV
> - **missingRequiredSkills**: Danh sách required skills chưa tìm thấy trong CV (cần cải thiện)
> - **matchedOptionalSkills**: Danh sách optional skills đã tìm thấy trong CV
> - **missingOptionalSkills**: Danh sách optional skills chưa tìm thấy trong CV
> 
> **Cách tính score**:
> - Required skills: 3/4 = 75% (weight: 70%)
> - Optional skills: 2/5 = 40% (weight: 30%)
> - Final score: (75 × 0.7) + (40 × 0.3) = 52.5 + 12 = 64.5 → **82** (rounded)

### 3. Create Application (Manual Entry - HR Workflow)
**POST** `/applications`

HR/Recruiter tạo application thủ công khi nhận CV qua email. Đây là **workflow phụ** (backup workflow), không phải workflow chính.

> ⚠️ **Protected endpoint**: Yêu cầu `Authorization: Bearer <access_token>`  
> 📝 **Use case**: HR nhận CV qua email → Upload vào system thủ công → Tạo application

#### Request Body
```json
{
  "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
  "candidateName": "John Doe",
  "candidateEmail": "john.doe@example.com",
  "candidatePhone": "+1234567890",
  "statusId": "status1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
  "source": "Email",
  "appliedDate": "2024-01-15",
  "resumeFilePath": "/applications/app1/resume.pdf",
  "coverLetter": "I am interested in this position...",
  "notes": "Received via email"
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Application created successfully",
  "data": {
    "id": "app1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
    "candidateName": "John Doe",
    "candidateEmail": "john.doe@example.com",
    "statusId": "status1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "status": {
      "id": "status1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "name": "NEW",
      "displayName": "Mới",
      "color": "#3B82F6"
    },
    "appliedDate": "2024-01-15",
    "matchScore": null,
    "matchScoreDetails": null,
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```


### 4. Update Application Status
**PATCH** `/applications/{id}/status`

Cập nhật status của application (workflow: NEW → SCREENING → INTERVIEWING → OFFERED → HIRED/REJECTED).

#### Request Body
```json
{
  "statusId": "status2a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
  "notes": "Moved to screening phase"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Application status updated successfully",
  "data": {
    "id": "app1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "statusId": "status2a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "previousStatus": "NEW",
    "notes": "Moved to screening phase",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 5. Assign Application to Recruiter
**PATCH** `/applications/{id}/assign`

Assign application cho HR/Recruiter để xử lý.

#### Request Body
```json
{
  "assignedTo": "user1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Application assigned successfully",
  "data": {
    "id": "app1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "assignedTo": "user1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "assignedToName": "Jane Recruiter",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 6. Update Application Details
**PUT** `/applications/{id}`

Cập nhật thông tin application (notes, rating, allowAdditionalUploads, etc.).

#### Request Body
```json
{
  "notes": "Updated notes after phone screening",
  "rating": 5,
  "coverLetter": "Updated cover letter",
  "allowAdditionalUploads": true
}
```

> **Lưu ý về `allowAdditionalUploads`:**
> - HR set `allowAdditionalUploads = true` khi yêu cầu candidate upload thêm documents
> - Candidate chỉ có thể upload khi flag này = `true` VÀ status = `SCREENING` hoặc `INTERVIEWING`
> - Sau khi candidate upload xong, HR có thể set `allowAdditionalUploads = false` để tắt

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Application updated successfully",
  "data": {
    "id": "app1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "notes": "Updated notes after phone screening",
    "rating": 5,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 7. Delete Application
**DELETE** `/applications/{id}`

Soft delete application.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Application deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 8. Get Application Status History
**GET** `/applications/{id}/status-history`

Lấy lịch sử thay đổi status của application.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Status history retrieved successfully",
  "data": [
    {
      "id": "hist1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "applicationId": "app1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "fromStatusId": "status1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "fromStatus": {
        "id": "status1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
        "name": "NEW",
        "displayName": "Mới",
        "color": "#3B82F6"
      },
      "toStatusId": "status2a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "toStatus": {
        "id": "status2a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
        "name": "SCREENING",
        "displayName": "Sàng lọc",
        "color": "#8B5CF6"
      },
      "changedBy": "user1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "changedByName": "Jane Recruiter",
      "notes": "Moved to screening phase",
      "createdAt": "2024-01-15T10:30:00Z"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 🏢 Company Management APIs

### 1. Get All Companies
**GET** `/companies`

Lấy danh sách tất cả companies.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Query Parameters
```
page=0&size=20&sort=name,asc&industry=Technology&search=Google
```

#### Response (200 OK)
```json
{
  "success": true,
  "data": [
    {
      "id": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
      "name": "Google",
      "website": "https://google.com",
      "industry": "Technology",
      "size": "LARGE",
      "location": "Mountain View, CA",
      "description": "Google is a multinational technology company...",
      "logoUrl": "https://google.com/logo.png",
      "isVerified": true,
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z",
      "createdBy": null,
      "updatedBy": null,
      "deletedAt": null
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z",
  "paginationInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 2. Create Company
**POST** `/companies`

Tạo company mới.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Request Body
```json
{
  "name": "New Tech Company",
  "website": "https://newtech.com",
  "industry": "Technology",
  "size": "MEDIUM",
  "location": "San Francisco, CA",
  "description": "A innovative technology company..."
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Company created successfully",
  "data": {
    "id": "c2f9a8e3-4b5c-6d7e-8f90-2345678901bc",
    "name": "New Tech Company",
    "website": "https://newtech.com",
    "industry": "Technology",
    "size": "MEDIUM",
    "location": "San Francisco, CA",
    "description": "A innovative technology company...",
    "logoUrl": null,
    "isVerified": false,
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 3. Get Company by ID
**GET** `/companies/{id}`

Trả về thông tin chi tiết cùng metadata audit.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Company retrieved successfully",
  "data": {
    "id": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
    "name": "Google",
    "website": "https://google.com",
    "industry": "Technology",
    "size": "LARGE",
    "location": "Mountain View, CA",
    "description": "Google is a multinational technology company...",
    "logoUrl": "https://google.com/logo.png",
    "isVerified": true,
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-15T10:30:00Z",
    "createdBy": null,
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "deletedAt": null
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 4. Update Company
**PUT** `/companies/{id}`

#### Request Body
```json
{
  "website": "https://newtech.com",
  "industry": "Technology",
  "size": "LARGE",
  "location": "Remote",
  "description": "Updated description",
  "isVerified": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Company updated successfully",
  "data": {
    "id": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
    "name": "Google",
    "website": "https://newtech.com",
    "industry": "Technology",
    "size": "LARGE",
    "location": "Remote",
    "description": "Updated description",
    "logoUrl": "https://google.com/logo.png",
    "isVerified": true,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 5. Delete Company (Soft Delete)
**DELETE** `/companies/{id}`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Company deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 💳 Subscription Management APIs (Lookup + History) ➕

> **Thiết kế sau refactor**: Subscription KHÔNG còn là ENUM hay field trong `companies`.  
> Thay vào đó:
> - `subscription_plans`: catalog gói hệ thống (FREE, BASIC, PRO, ENTERPRISE, ...), có metadata (price, duration_days, max_jobs, max_users, max_applications, is_active).  
> - `company_subscriptions`: history theo thời gian cho từng company (plan_id, start_date, end_date, status = PENDING/ACTIVE/EXPIRED/CANCELLED).

### 🔵 SubscriptionPlan APIs (System Catalog)

#### 1. Get Subscription Plans

**GET** `/admin/subscription-plans`

Lấy danh sách tất cả gói subscription mà hệ thống hỗ trợ (dùng cho UI chọn gói, pricing page, v.v.).

##### Request Headers
```
Authorization: Bearer <access_token>
```

> ⚠️ Thường chỉ **SYSTEM_ADMIN** mới được phép quản lý/nhìn toàn bộ plans.  
> **Lookup/config data → trả về List, không paginate.**

##### Response (200 OK)
```json
{
  "success": true,
  "message": "Subscription plans retrieved successfully",
  "data": [
    {
      "id": "plan-free-uuid",
      "code": "FREE",
      "name": "Free",
      "price": 0.0,
      "durationDays": 0,
      "maxJobs": 5,
      "maxUsers": 3,
      "maxApplications": 100,
      "isActive": true,
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z"
    },
    {
      "id": "plan-pro-uuid",
      "code": "PRO",
      "name": "Pro",
      "price": 49.0,
      "durationDays": 30,
      "maxJobs": 50,
      "maxUsers": 20,
      "maxApplications": 5000,
      "isActive": true,
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z",
}
```

#### 2. Create Subscription Plan

**POST** `/admin/subscription-plans`

Tạo một subscription plan mới trong hệ thống (catalog level).

##### Request Headers
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

##### Request Body
```json
{
  "code": "PRO",
  "name": "Pro",
  "price": 49.0,
  "durationDays": 30,
  "maxJobs": 50,
  "maxUsers": 20,
  "maxApplications": 5000,
  "isActive": true
}
```

##### Response (201 Created)
```json
{
  "success": true,
  "message": "Subscription plan created successfully",
  "data": {
    "id": "plan-pro-uuid",
    "code": "PRO",
    "name": "Pro",
    "price": 49.0,
    "durationDays": 30,
    "maxJobs": 50,
    "maxUsers": 20,
    "maxApplications": 5000,
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### 3. Get Subscription Plan by ID

**GET** `/admin/subscription-plans/{id}`

##### Response (200 OK)
```json
{
  "success": true,
  "message": "Subscription plan detail retrieved successfully",
  "data": {
    "id": "plan-pro-uuid",
    "code": "PRO",
    "name": "Pro",
    "price": 49.0,
    "durationDays": 30,
    "maxJobs": 50,
    "maxUsers": 20,
    "maxApplications": 5000,
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### 4. Update Subscription Plan

**PUT** `/admin/subscription-plans/{id}`

##### Request Body
```json
{
  "name": "Pro (Updated)",
  "price": 59.0,
  "durationDays": 30,
  "maxJobs": 100,
  "maxUsers": 50,
  "maxApplications": 10000,
  "isActive": true
}
```

##### Response (200 OK)
```json
{
  "success": true,
  "message": "Subscription plan updated successfully",
  "data": {
    "id": "plan-pro-uuid",
    "code": "PRO",
    "name": "Pro (Updated)",
    "price": 59.0,
    "durationDays": 30,
    "maxJobs": 100,
    "maxUsers": 50,
    "maxApplications": 10000,
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-16T09:00:00Z"
  },
  "timestamp": "2024-01-16T09:00:00Z"
}
```

#### 5. Deactivate Subscription Plan

**DELETE** `/admin/subscription-plans/{id}`

> Thay vì xóa cứng, plan sẽ được mark `isActive = false` để giữ lịch sử billing.

##### Response (200 OK)
```json
{
  "success": true,
  "message": "Subscription plan deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 🟠 CompanySubscription APIs (Per-company History)

#### 1. Create Company Subscription (Admin)

**POST** `/admin/company-subscriptions`

Tạo một subscription record cho company (ví dụ: khi upgrade/downgrade plan).

##### Request Headers
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

##### Request Body
```json
{
  "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
  "planId": "plan-pro-uuid",
  "startDate": "2024-01-01T00:00:00Z",
  "endDate": "2024-01-31T23:59:59Z",
  "status": "PENDING"
}
```

##### Response (201 Created)
```json
{
  "success": true,
  "message": "Company subscription created successfully",
  "data": {
    "id": "sub-uuid-1",
    "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
    "planId": "plan-pro-uuid",
    "planCode": "PRO",
    "planName": "Pro",
    "status": "ACTIVE",
    "startDate": "2024-01-01T00:00:00Z",
    "endDate": "2024-01-31T23:59:59Z",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

#### 2. Get CompanySubscription by ID (Admin)

**GET** `/admin/company-subscriptions/{id}`

##### Response (200 OK)
```json
{
  "success": true,
  "message": "Company subscription detail retrieved successfully",
  "data": {
    "id": "sub-uuid-1",
    "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
    "planId": "plan-pro-uuid",
    "planCode": "PRO",
    "planName": "Pro",
    "status": "ACTIVE",
    "startDate": "2024-01-01T00:00:00Z",
    "endDate": "2024-01-31T23:59:59Z",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### 3. Get All CompanySubscriptions (Admin)

**GET** `/admin/company-subscriptions`

##### Query Parameters
```
page=0&size=20&sort=startDate,desc
```

##### Response (200 OK)
```json
{
  "success": true,
  "message": "Company subscriptions retrieved successfully",
  "data": [
    {
      "id": "sub-uuid-1",
      "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
      "planId": "plan-pro-uuid",
      "planCode": "PRO",
      "planName": "Pro",
      "status": "ACTIVE",
      "startDate": "2024-01-01T00:00:00Z",
      "endDate": "2024-01-31T23:59:59Z"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z",
  "paginationInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

#### 4. Get Company Active Subscription (Per-company)

**GET** `/companies/{companyId}/subscription`

Lấy **subscription hiện tại** (ACTIVE) của một company, kèm thông tin gói.

##### Request Headers
```
Authorization: Bearer <access_token>
```

##### Response (200 OK)
```json
{
  "success": true,
  "message": "Company subscription retrieved successfully",
  "data": {
    "id": "sub-uuid-1",
    "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
    "planId": "plan-pro-uuid",
    "planCode": "PRO",
    "planName": "Pro",
    "status": "ACTIVE",
    "startDate": "2024-01-01T00:00:00Z",
    "endDate": "2024-01-31T23:59:59Z",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

##### Response khi chưa có subscription (404 Not Found)
```json
{
  "success": false,
  "message": "Company subscription not found",
  "errors": [
    {
      "field": "companyId",
      "message": "No active subscription for this company"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### 5. Get Company Subscription History

**GET** `/companies/{companyId}/subscriptions`

Lấy toàn bộ lịch sử subscription của company (phục vụ billing/audit/reporting).

##### Request Headers
```
Authorization: Bearer <access_token>
```

##### Query Parameters
```
page=0&size=20&status=ACTIVE&sort=startDate,desc
```

##### Response (200 OK)
```json
{
  "success": true,
  "message": "Company subscription history retrieved successfully",
  "data": [
    {
      "id": "sub-uuid-1",
      "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
      "planId": "plan-pro-uuid",
      "planCode": "PRO",
      "planName": "Pro",
      "status": "ACTIVE",
      "startDate": "2024-01-01T00:00:00Z",
      "endDate": "2024-01-31T23:59:59Z"
    },
    {
      "id": "sub-uuid-0",
      "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
      "planId": "plan-free-uuid",
      "planCode": "FREE",
      "planName": "Free",
      "status": "EXPIRED",
      "startDate": "2023-10-01T00:00:00Z",
      "endDate": "2023-12-31T23:59:59Z"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z",
  "paginationInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

### 🧾 Payment APIs (Billing Transactions – VNPAY ready)

> Các API này dùng để khởi tạo và tra cứu giao dịch thanh toán cho subscription.  
> Không bind cứng vào VNPAY, nhưng đã đủ field để map `vnp_TxnRef`, `vnp_ResponseCode`, payload callback.

#### 1. Init Payment (tạo URL VNPAY)

**POST** `/admin/payments`

Tạo bản ghi `payment` trạng thái `INIT` và build URL redirect sang VNPAY.

##### Request Headers

```
Authorization: Bearer <access_token>
Content-Type: application/json
```

##### Request Body

```json
{
  "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
  "companySubscriptionId": "sub1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
  "amount": 490000,
  "currency": "VND",
  "gateway": "VNPAY",
  "txnRef": null
}
```

- **companyId**: Company trả tiền (tenant).
- **companySubscriptionId**: Bản ghi subscription (plan + thời gian) mà payment này trả cho.
- **amount**: Số tiền (DECIMAL), backend sẽ nhân `x100` để gửi cho VNPAY.
- **currency**: Mặc định `VND` nếu bỏ trống.
- **gateway**: Mặc định `"VNPAY"` nếu bỏ trống.
- **txnRef**: Nếu null, backend tự sinh mã unique (dùng để map với `vnp_TxnRef`).

##### Response (201 Created)

```json
{
  "success": true,
  "message": "Payment created successfully",
  "data": {
    "id": "pay1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
    "companySubscriptionId": "sub1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "amount": 490000,
    "currency": "VND",
    "gateway": "VNPAY",
    "txnRef": "A1B2C3D4E5F6G7H8I9J0",
    "status": "INIT",
    "paidAt": null,
    "metadata": null,
    "createdAt": "2024-01-15T10:00:00Z",
    "updatedAt": "2024-01-15T10:00:00Z"
  },
  "timestamp": "2024-01-15T10:00:00Z"
}
```

Trong thực tế FE sẽ dùng thêm field `paymentUrl` (từ controller/service) để redirect sang VNPAY:

```json
{
  "success": true,
  "message": "Payment created successfully",
  "data": {
    "payment": {
      "id": "pay1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
      "companySubscriptionId": "sub1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "amount": 490000,
      "currency": "VND",
      "gateway": "VNPAY",
      "txnRef": "A1B2C3D4E5F6G7H8I9J0",
      "status": "INIT",
      "createdAt": "2024-01-15T10:00:00Z",
      "updatedAt": "2024-01-15T10:00:00Z"
    },
    "paymentUrl": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?...&vnp_TxnRef=A1B2C3D4E5F6G7H8I9J0&vnp_SecureHash=..."
  },
  "timestamp": "2024-01-15T10:00:00Z"
}
```

> **Mapping quan trọng**:
> - `payments.txn_ref` ⇔ `vnp_TxnRef`
> - `payments.gateway` = `"VNPAY"`
> - `payments.status` từ `INIT` → `SUCCESS/FAILED` sau callback.

#### 2. VNPAY Return URL (Frontend redirect)

**GET** `/payments/vnpay/return`

Endpoint này dùng làm `vnp_ReturnUrl` để VNPAY redirect browser về sau khi user thanh toán xong.

- Nhận toàn bộ query params từ VNPAY (`vnp_Amount`, `vnp_BankCode`, `vnp_ResponseCode`, `vnp_TxnRef`, `vnp_SecureHash`, ...).
- Verify chữ ký:
  - Bỏ `vnp_SecureHashType`, `vnp_SecureHash` khỏi map.
  - Tính lại hash bằng secretKey (`VnPayConfig.hashAllFields`) và so sánh với `vnp_SecureHash`.
- Lấy `vnp_TxnRef` → tìm `payments` theo `txn_ref`.
- Nếu:
  - Chữ ký hợp lệ **và** `vnp_ResponseCode = "00"`:
    - Cập nhật:
      - `payments.status = SUCCESS`
      - `payments.paid_at = NOW()`
      - `payments.metadata = full JSON payload từ VNPAY`
      - (tuỳ logic sau này) cập nhật `company_subscriptions.status` từ `PENDING` → `ACTIVE`.
  - Ngược lại:
    - `payments.status = FAILED`
    - `payments.metadata` vẫn lưu payload để debug.

API response có thể đơn giản là redirect sang FE (SPA) với query `status=success|failed`, nên docs chỉ cần mô tả luồng, không bắt buộc trả JSON chuẩn.

#### 3. Get Payment Detail (Admin)

**GET** `/admin/payments/{id}`

##### Response (200 OK)

```json
{
  "success": true,
  "message": "Payment detail retrieved successfully",
  "data": {
    "id": "pay1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
    "companySubscriptionId": "sub1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "amount": 490000,
    "currency": "VND",
    "gateway": "VNPAY",
    "txnRef": "A1B2C3D4E5F6G7H8I9J0",
    "status": "SUCCESS",
    "paidAt": "2024-01-15T10:05:00Z",
    "metadata": "{\"vnp_ResponseCode\":\"00\",\"vnp_TransactionNo\":\"123456789\"}",
    "createdAt": "2024-01-15T10:00:00Z",
    "updatedAt": "2024-01-15T10:05:00Z"
  },
  "timestamp": "2024-01-15T10:10:00Z"
}
```

#### 4. List Payments (Admin)

**GET** `/admin/payments?page=0&size=20`

Trả về toàn bộ payments trong hệ thống (phục vụ billing/report).

#### 5. List Payments by Company

**GET** `/companies/{companyId}/payments?page=0&size=20`

Lấy danh sách payment theo từng company.

#### 6. List Payments by Company Subscription

**GET** `/company-subscriptions/{companySubscriptionId}/payments?page=0&size=20`

Lấy lịch sử payments cho một bản ghi subscription cụ thể.

## 📋 Lookup Tables APIs

> **🔄 CHUYỂN SANG ENUM**: Các lookup tables sau đã chuyển sang ENUM trong database, không cần APIs riêng:
> - **Job Statuses** → ENUM trong `jobs.jobStatus` (DRAFT, PUBLISHED, PAUSED, CLOSED, FILLED)
> - **Job Types** → ENUM trong `jobs.jobType` (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE)
> - **Interview Types** → ENUM trong `interviews.interviewType` (PHONE, VIDEO, IN_PERSON, TECHNICAL, HR, FINAL)
> - **Interview Statuses** → ENUM trong `interviews.status` (SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED)
> - **Interview Results** → ENUM trong `interviews.result` (PASSED, FAILED, PENDING)
> - **Notification Types** → ENUM trong `notifications.type` (APPLICATION_RECEIVED, INTERVIEW_SCHEDULED, etc.)
> - **Notification Priorities** → ENUM trong `notifications.priority` (HIGH, MEDIUM, LOW)
> - **Attachment Types** → ENUM trong `attachments.attachmentType` (RESUME, COVER_LETTER, CERTIFICATE, PORTFOLIO, OTHER)

> **✅ LOOKUP TABLE**: Application Statuses giữ lại lookup table vì cần metadata (display_name, color, sort_order) và flexibility:
> - **Application Statuses** → Lookup table `application_statuses` (NEW, SCREENING, INTERVIEWING, OFFERED, HIRED, REJECTED)

### ~~1. Get Job Statuses~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Job statuses giờ là ENUM trong `jobs.jobStatus`. Sử dụng trực tiếp ENUM values trong request/response.

### ~~2. Get Job Types~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Job types giờ là ENUM trong `jobs.jobType`. Sử dụng trực tiếp ENUM values trong request/response.

### ~~3. Get Priorities~~ ❌ **REMOVED**

> **Lý do**: ATS không cần priority cho job postings. Đã bỏ hoàn toàn.

### ~~4. Get Experience Levels~~ ❌ **REMOVED**

> **Lý do**: Quá phức tạp cho ATS. HR có thể ghi tự do trong job description. Đã bỏ hoàn toàn.

### ~~5. Get Interview Types~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Interview types giờ là ENUM trong `interviews.interviewType` (PHONE, VIDEO, IN_PERSON, TECHNICAL, HR, FINAL). Sử dụng trực tiếp ENUM values trong request/response.

### ~~6. Get Interview Statuses~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Interview statuses giờ là ENUM trong `interviews.status` (SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED). Sử dụng trực tiếp ENUM values trong request/response.

### ~~7. Get Interview Results~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Interview results giờ là ENUM trong `interviews.result` (PASSED, FAILED, PENDING). Sử dụng trực tiếp ENUM values trong request/response.

### ~~8. Get Notification Types~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Notification types giờ là ENUM trong `notifications.type` (APPLICATION_RECEIVED, INTERVIEW_SCHEDULED, INTERVIEW_REMINDER, STATUS_CHANGE, DEADLINE_REMINDER, COMMENT_ADDED, ASSIGNMENT_CHANGED). Sử dụng trực tiếp ENUM values trong request/response.

### ~~9. Get Notification Priorities~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Notification priorities giờ là ENUM trong `notifications.priority` (HIGH, MEDIUM, LOW). Sử dụng trực tiếp ENUM values trong request/response.

### 10. Get Application Statuses ✅
**GET** `/admin/application-statuses`

Lấy danh sách application statuses cùng metadata (display_name, color, sort_order) để hiển thị trong UI.

#### Request Headers
```
Authorization: Bearer <access_token>
```

> **Lookup/config data → trả về List, không paginate.**

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Application statuses retrieved successfully",
  "data": [
    {
      "id": "status1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "name": "NEW",
      "displayName": "Mới",
      "description": "Ứng viên vừa nộp đơn",
      "color": "#3B82F6",
      "sortOrder": 1,
      "isActive": true,
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z",
      "createdBy": null,
      "updatedBy": null,
      "deletedAt": null
    },
    {
      "id": "status2a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "name": "SCREENING",
      "displayName": "Sàng lọc",
      "description": "Đang sàng lọc hồ sơ",
      "color": "#8B5CF6",
      "sortOrder": 2,
      "isActive": true,
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z",
      "createdBy": null,
      "updatedBy": null,
      "deletedAt": null
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z",
}
```

### 11. Create Application Status
**POST** `/admin/application-statuses`

Tạo application status mới (chỉ dành cho admin).

#### Request Headers
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

#### Request Body
```json
{
  "name": "ON_HOLD",
  "displayName": "Tạm hoãn",
  "description": "Ứng viên tạm hoãn quy trình",
  "color": "#F59E0B",
  "sortOrder": 3,
  "isActive": true
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Application status created successfully",
  "data": {
    "id": "status3a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "name": "ON_HOLD",
    "displayName": "Tạm hoãn",
    "description": "Ứng viên tạm hoãn quy trình",
    "color": "#F59E0B",
    "sortOrder": 3,
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 12. Update Application Status
**PUT** `/admin/application-statuses/{id}`

Cập nhật application status (display_name, color, sort_order, etc.).

#### Request Body
```json
{
  "displayName": "Tạm hoãn (Cập nhật)",
  "color": "#F97316",
  "sortOrder": 4
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Application status updated successfully",
  "data": {
    "id": "status3a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "name": "ON_HOLD",
    "displayName": "Tạm hoãn (Cập nhật)",
    "color": "#F97316",
    "sortOrder": 4,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 13. Delete Application Status
**DELETE** `/admin/application-statuses/{id}`

Soft delete application status (chỉ khi không có applications nào đang dùng).

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Application status deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 🔐 RBAC & Permission APIs

> ⚠️ Các endpoint này yêu cầu quyền `ADMIN`.

### 1. Get Roles
**GET** `/admin/roles`

Lấy danh sách roles cùng metadata để gán cho user.

#### Request Headers
```
Authorization: Bearer <access_token>
```

> **Lookup/config data (RBAC) → trả về List, không paginate.**

#### Response (200 OK)
```json
{
  "success": true,
  "data": [
    {
      "id": "34d9a2e3-1a30-4a1a-b1ad-4b6d2619f1ce",
      "name": "ADMIN",
      "description": "Administrator with full system access",
      "isActive": true,
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-10T12:00:00Z",
      "createdBy": null,
      "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "deletedAt": null
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z",
}
```

### 2. Create Role
**POST** `/admin/roles`

Tạo role mới cho hệ thống.

#### Request Headers
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

#### Request Body
```json
{
  "name": "HIRING_MANAGER",
  "description": "Limited admin role for managing job data",
  "isActive": true
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Role created successfully",
  "data": {
    "id": "781af566-48d8-4066-9fd7-78284b642df0",
    "name": "HIRING_MANAGER",
    "description": "Limited admin role for managing job data",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 3. Get Role Details
**GET** `/admin/roles/{id}`

Lấy thông tin chi tiết một role kèm metadata.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Role retrieved successfully",
  "data": {
    "id": "34d9a2e3-1a30-4a1a-b1ad-4b6d2619f1ce",
    "name": "ADMIN",
    "description": "Administrator with full system access",
    "isActive": true,
    "permissions": [
      {
        "permissionId": "5a12b2d5-0b42-4b3c-815a-7cf6fca39a8e",
        "name": "JOB_READ",
        "resource": "JOB",
        "action": "READ"
      }
    ],
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-10T12:00:00Z",
    "deletedAt": null
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 4. Update Role
**PUT** `/admin/roles/{id}`

#### Request Body
```json
{
  "description": "System administrator role",
  "isActive": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Role updated successfully",
  "data": {
    "id": "34d9a2e3-1a30-4a1a-b1ad-4b6d2619f1ce",
    "name": "ADMIN",
    "description": "System administrator role",
    "isActive": true,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 5. Delete Role (Soft Delete)
**DELETE** `/admin/roles/{id}`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Role deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 6. Get Permissions
**GET** `/admin/permissions`

Liệt kê toàn bộ permissions có thể gán cho roles.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Permissions retrieved successfully",
  "data": [
    {
      "id": "5a12b2d5-0b42-4b3c-815a-7cf6fca39a8e",
      "name": "JOB_READ",
      "resource": "JOB",
      "action": "READ",
      "description": "Read job information",
      "isActive": true
    },
    {
      "id": "6df6adf7-02f0-4d66-92bb-59f32b2b7a25",
      "name": "JOB_CREATE",
      "resource": "JOB",
      "action": "CREATE",
      "description": "Create new jobs",
      "isActive": true
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 7. Create Permission
**POST** `/admin/permissions`

```json
{
  "name": "COMPANY_DELETE",
  "resource": "COMPANY",
  "action": "DELETE",
  "description": "Delete companies",
  "isActive": true
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Permission created successfully",
  "data": {
    "id": "85a1cb38-4e9f-4f90-a7d5-f45df3a5515d",
    "name": "COMPANY_DELETE",
    "resource": "COMPANY",
    "action": "DELETE",
    "description": "Delete companies",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 8. Update Permission
**PUT** `/admin/permissions/{id}`

```json
{
  "description": "Delete company records",
  "isActive": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Permission updated successfully",
  "data": {
    "id": "85a1cb38-4e9f-4f90-a7d5-f45df3a5515d",
    "name": "COMPANY_DELETE",
    "resource": "COMPANY",
    "action": "DELETE",
    "description": "Delete company records",
    "isActive": true,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 9. Delete Permission
**DELETE** `/admin/permissions/{id}`

```json
{
  "success": true,
  "message": "Permission deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 10. Update Role Permissions
**PUT** `/admin/roles/{roleId}/permissions`

Cập nhật danh sách permission cho role cụ thể.

#### Request Body
```json
{
  "permissionIds": [
    "5a12b2d5-0b42-4b3c-815a-7cf6fca39a8e",
    "6df6adf7-02f0-4d66-92bb-59f32b2b7a25"
  ]
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Role permissions updated successfully",
  "data": {
    "roleId": "34d9a2e3-1a30-4a1a-b1ad-4b6d2619f1ce",
    "permissionIds": [
      "5a12b2d5-0b42-4b3c-815a-7cf6fca39a8e",
      "6df6adf7-02f0-4d66-92bb-59f32b2b7a25"
    ],
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 11. Get Role Permissions
**GET** `/admin/roles/{roleId}/permissions`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Role permissions retrieved successfully",
  "data": [
    {
      "permissionId": "5a12b2d5-0b42-4b3c-815a-7cf6fca39a8e",
      "name": "JOB_READ",
      "resource": "JOB",
      "action": "READ"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 12. Add Single Permission to Role
**POST** `/admin/roles/{roleId}/permissions`

#### Request Body
```json
{
  "permissionId": "6df6adf7-02f0-4d66-92bb-59f32b2b7a25"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Permission added to role",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 13. Remove Permission from Role
**DELETE** `/admin/roles/{roleId}/permissions/{permissionId}`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Permission removed from role",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 🎯 Skills Management APIs

### 1. Get All Skills
**GET** `/skills`

Lấy danh sách tất cả skills.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Query Parameters
```
page=0&size=50&sort=name,asc&category=PROGRAMMING&search=Java
```

#### Response (200 OK)
```json
{
  "success": true,
  "data": [
    {
      "id": "b7e58a6e-5c5e-4de8-9a3f-6b1ae2d042b5",
      "name": "Java",
      "category": "PROGRAMMING",
      "description": "Object-oriented programming language",
      "isActive": true,
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z",
      "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "deletedAt": null
    },
    {
      "id": "c8f69b7f-6d6f-5ef9-0b4g-7c2bf3e153c6",
      "name": "Spring Boot",
      "category": "FRAMEWORK",
      "description": "Java framework for building web applications",
      "isActive": true,
      "createdAt": "2024-01-01T00:00:00Z",
      "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "deletedAt": null
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z",
  "paginationInfo": {
    "page": 0,
    "size": 50,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

### 2. Get Skill by ID
**GET** `/skills/{id}`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Skill retrieved successfully",
  "data": {
    "id": "b7e58a6e-5c5e-4de8-9a3f-6b1ae2d042b5",
    "name": "Java",
    "category": "PROGRAMMING",
    "description": "Object-oriented programming language",
    "isActive": true,
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z",
    "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "deletedAt": null
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 3. Create Skill
**POST** `/skills`

#### Request Body
```json
{
  "name": "Kubernetes",
  "category": "TOOL",
  "description": "Container orchestration"
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Skill created successfully",
  "data": {
    "id": "c8f69b7f-6d6f-5ef9-0b4g-7c2bf3e153c6",
    "name": "Kubernetes",
    "category": "TOOL",
    "description": "Container orchestration",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 4. Update Skill
**PUT** `/skills/{id}`

#### Request Body
```json
{
  "description": "Managed Kubernetes platform",
  "isActive": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Skill updated successfully",
  "data": {
    "id": "c8f69b7f-6d6f-5ef9-0b4g-7c2bf3e153c6",
    "name": "Kubernetes",
    "category": "TOOL",
    "description": "Managed Kubernetes platform",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z",
    "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "deletedAt": null
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 5. Delete Skill
**DELETE** `/skills/{id}`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Skill deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### ~~6. Get User Skills~~ ❌ **REMOVED**

> **Lý do**: ATS không track skills của HR/Recruiter. Chỉ cần track skills yêu cầu của job (job_skills). Candidates skills nằm trong CV text.

### ~~7. Add User Skill~~ ❌ **REMOVED**
### ~~8. Update User Skill~~ ❌ **REMOVED**
### ~~9. Delete User Skill~~ ❌ **REMOVED**

## ~~📄 Resume Management APIs~~ ❌ **REMOVED**

> **Lý do**: ATS không cần candidates upload CV. CVs được lưu trong `applications.resume_file_path` hoặc `attachments` table khi HR upload.

## 💬 Comments Management APIs (ATS) ➕

> **Mục đích**: HR/Recruiter trao đổi về candidates trên applications. Comments có thể là internal (không gửi candidate) hoặc external.

### 1. Get Application Comments
**GET** `/applications/{applicationId}/comments`

Lấy danh sách comments của một application.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Query Parameters
```
page=0&size=20&sort=createdAt,desc&isInternal=true
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Comments retrieved successfully",
  "data": [
    {
      "id": "comm1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "applicationId": "app1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "userId": "user1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "userName": "Jane Recruiter",
      "userAvatar": "https://...",
      "commentText": "Strong technical background, good fit for the role.",
      "isInternal": true,
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z",
  "paginationInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 2. Create Comment
**POST** `/applications/{applicationId}/comments`

Thêm comment mới cho application.

#### Request Body
```json
{
  "commentText": "Strong technical background, good fit for the role.",
  "isInternal": true
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Comment created successfully",
  "data": {
    "id": "comm1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "applicationId": "app1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "userId": "user1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "userName": "Jane Recruiter",
    "commentText": "Strong technical background, good fit for the role.",
    "isInternal": true,
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 3. Update Comment
**PUT** `/applications/{applicationId}/comments/{commentId}`

Cập nhật comment (chỉ author mới có thể update).

#### Request Body
```json
{
  "commentText": "Updated comment text",
  "isInternal": false
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Comment updated successfully",
  "data": {
    "id": "comm1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "commentText": "Updated comment text",
    "isInternal": false,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 4. Delete Comment
**DELETE** `/applications/{applicationId}/comments/{commentId}`

Soft delete comment (chỉ author hoặc admin mới có thể delete).

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Comment deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 🎤 Interview Management APIs (ATS) 🔄

> **🔄 SEMANTIC CHANGE**: Interviews belong to Applications, không phải Jobs. Một application có thể có nhiều vòng interview.

### 1. Get Application Interviews
**GET** `/applications/{applicationId}/interviews`

Lấy danh sách interviews của một application.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Interviews retrieved successfully",
  "data": [
    {
      "id": "a1b2c3d4-5e6f-7g8h-9i0j-k1l2m3n4o5p6",
      "applicationId": "app1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
      "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
      "roundNumber": 1,
      "meetingLink": "https://meet.google.com/xxx-yyyy-zzz",
      "location": "Office Building A, Room 101",
      "interviewType": "TECHNICAL",
      "scheduledDate": "2024-01-20T14:00:00Z",
      "actualDate": null,
      "durationMinutes": 60,
      "interviewers": [
        {
          "id": "user-id-1",
          "name": "Jane Smith",
          "email": "jane.smith@company.com",
          "isPrimary": true
        },
        {
          "id": "user-id-2",
          "name": "John Doe",
          "email": "john.doe@company.com",
          "isPrimary": false
        }
      ],
      "status": "SCHEDULED",
      "result": null,
      "feedback": null,
      "notes": "Technical interview",
      "questionsAsked": null,
      "answersGiven": null,
      "rating": null,
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z",
      "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "deletedAt": null
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 2. Create Interview
**POST** `/applications/{applicationId}/interviews`

Tạo interview mới cho application với nhiều interviewers.

> **👥 Multiple Interviewers**: Một interview có thể có nhiều interviewers (array `interviewerIds`).
> 
> **⏰ Schedule Validation**: System tự động validate trùng lịch cho từng interviewer:
> - Nếu interviewer đã có interview khác trong khoảng thời gian `scheduledDate` ± `durationMinutes` → Reject với error
> - Chỉ validate cho interviews có status = `SCHEDULED` hoặc `RESCHEDULED`
> - Validate overlap: Nếu interview A từ 10:00-11:00 và interview B từ 10:30-11:30 → Trùng lịch (overlap)

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Request Body
```json
{
  "roundNumber": 1,
  "interviewType": "TECHNICAL",
  "scheduledDate": "2024-01-20T14:00:00Z",
  "durationMinutes": 60,
  "interviewerIds": [
    "user-id-1",
    "user-id-2"
  ],
  "primaryInterviewerId": "user-id-1",
  "status": "SCHEDULED",
  "meetingLink": "https://meet.google.com/xxx-yyyy-zzz",
  "location": "Office Building A, Room 101",
  "notes": "Technical interview with 2 interviewers"
}
```

> **Lưu ý**:
> - `interviewerIds`: Array các `user_id` với role = `INTERVIEWER` (bắt buộc, ít nhất 1 interviewer)
> - `primaryInterviewerId`: Interviewer chính (optional, nếu không set thì lấy interviewer đầu tiên)

#### Error Response (400 Bad Request - Schedule Conflict)
```json
{
  "success": false,
  "message": "Schedule conflict detected",
  "errors": [
    {
      "field": "interviewerIds",
      "message": "Interviewer user-id-2 already has an interview scheduled at 2024-01-20T14:00:00Z with duration 60 minutes"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Interview created successfully",
  "data": {
    "id": "a1b2c3d4-5e6f-7g8h-9i0j-k1l2m3n4o5p6",
    "applicationId": "app1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
    "roundNumber": 1,
    "interviewType": "TECHNICAL",
    "scheduledDate": "2024-01-20T14:00:00Z",
    "actualDate": null,
    "durationMinutes": 60,
    "interviewers": [
      {
        "id": "user-id-1",
        "name": "Jane Smith",
        "email": "jane.smith@company.com",
        "isPrimary": true
      },
      {
        "id": "user-id-2",
        "name": "John Doe",
        "email": "john.doe@company.com",
        "isPrimary": false
      }
    ],
    "status": "SCHEDULED",
    "result": null,
    "meetingLink": "https://meet.google.com/xxx-yyyy-zzz",
    "location": "Office Building A, Room 101",
    "feedback": null,
    "notes": "Technical interview with 2 interviewers",
    "questionsAsked": null,
    "answersGiven": null,
    "rating": null,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z",
    "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "deletedAt": null
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 3. Update Interview
**PUT** `/interviews/{id}`

Cập nhật thông tin interview. Có thể cập nhật `interviewerIds` và `scheduledDate` (sẽ validate trùng lịch lại).

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Request Body
```json
{
  "actualDate": "2024-01-20T14:30:00Z",
  "status": "COMPLETED",
  "result": "PASSED",
  "feedback": "Great technical skills, good communication",
  "notes": "Interview went well, waiting for next round",
  "questionsAsked": "What is your experience with Spring Boot?",
  "answersGiven": "I have 3 years of experience with Spring Boot...",
  "rating": 4
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Interview updated successfully",
  "data": {
    "id": "a1b2c3d4-5e6f-7g8h-9i0j-k1l2m3n4o5p6",
    "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "roundNumber": 1,
    "interviewType": "TECHNICAL",
    "scheduledDate": "2024-01-20T14:00:00Z",
    "actualDate": "2024-01-20T14:30:00Z",
    "durationMinutes": 60,
    "interviewers": [
      {
        "id": "user-id-1",
        "name": "Jane Smith",
        "email": "jane.smith@company.com",
        "isPrimary": true
      }
    ],
    "status": "COMPLETED",
    "result": "PASSED",
    "feedback": "Great technical skills, good communication",
    "notes": "Interview went well, waiting for next round",
    "questionsAsked": "What is your experience with Spring Boot?",
    "answersGiven": "I have 3 years of experience with Spring Boot...",
    "rating": 4,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z",
    "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "deletedAt": null
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 4. Get Interview Details
**GET** `/interviews/{id}`

Trả về đầy đủ thông tin của một interview (bao gồm audit, feedback).

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Interview retrieved successfully",
  "data": {
    "id": "a1b2c3d4-5e6f-7g8h-9i0j-k1l2m3n4o5p6",
    "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "roundNumber": 1,
    "interviewType": "TECHNICAL",
    "scheduledDate": "2024-01-20T14:00:00Z",
    "actualDate": "2024-01-20T14:30:00Z",
    "durationMinutes": 60,
    "interviewers": [
      {
        "id": "user-id-1",
        "name": "Jane Smith",
        "email": "jane.smith@company.com",
        "isPrimary": true
      }
    ],
    "status": "COMPLETED",
    "result": "PASSED",
    "feedback": "Great technical skills, good communication",
    "notes": "Interview went well, waiting for next round",
    "questionsAsked": "What is your experience with Spring Boot?",
    "answersGiven": "I have 3 years of experience with Spring Boot...",
    "rating": 4,
    "updatedAt": "2024-01-20T15:00:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 5. Delete Interview
**DELETE** `/interviews/{id}`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Interview deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 📊 Dashboard & Analytics APIs

### 1. Get Dashboard Statistics
**GET** `/dashboard/statistics`

Lấy thống kê tổng quan cho dashboard.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Dashboard statistics retrieved successfully",
  "data": {
    "totalJobs": 25,
    "jobsByStatus": {
      "SAVED": 5,
      "APPLIED": 15,
      "INTERVIEW": 3,
      "OFFER": 2,
      "REJECTED": 8,
      "WITHDRAWN": 1,
      "ACCEPTED": 1
    },
    "successRate": {
      "applicationToInterview": 20.0,
      "interviewToOffer": 66.7,
      "applicationToOffer": 13.3
    },
    "recentActivity": [
      {
        "id": "act1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
        "type": "JOB_CREATED",
        "message": "Created new job application for Google",
        "createdAt": "2024-01-15T10:30:00Z"
      },
      {
        "id": "act2b3c4d5-6e7f-8g9h-0i1j-k2l3m4n5o6p7",
        "type": "INTERVIEW_SCHEDULED",
        "message": "Interview scheduled for Microsoft",
        "createdAt": "2024-01-15T09:00:00Z"
      }
    ],
    "upcomingDeadlines": [
      {
        "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
        "title": "Senior Java Developer",
        "company": "Google",
        "deadlineDate": "2024-01-25",
        "daysRemaining": 10
      }
    ],
    "topSkills": [
      {
        "skillId": "b7e58a6e-5c5e-4de8-9a3f-6b1ae2d042b5",
        "skillName": "Java",
        "count": 15,
        "percentage": 60.0
      },
      {
        "skillId": "c8f69b7f-6d6f-5ef9-0b4g-7c2bf3e153c6",
        "skillName": "Spring Boot",
        "count": 12,
        "percentage": 48.0
      }
    ],
    "monthlyApplications": [
      {
        "month": "2024-01",
        "count": 8
      },
      {
        "month": "2024-02",
        "count": 12
      }
    ]
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 2. Get Job Analytics
**GET** `/analytics/jobs`

Lấy phân tích chi tiết về jobs.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Query Parameters
```
startDate=2024-01-01&endDate=2024-12-31&groupBy=month
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job analytics retrieved successfully",
  "data": {
    "timeline": [
      {
        "date": "2024-01",
        "new": 45,
        "screening": 20,
        "interviewing": 15,
        "offered": 5,
        "hired": 8,
        "rejected": 25
      },
      {
        "date": "2024-02",
        "new": 80,
        "screening": 35,
        "interviewing": 25,
        "offered": 10,
        "hired": 15,
        "rejected": 45
      }
    ],
    "jobPostingStats": [
      {
        "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
        "jobTitle": "Senior Java Developer",
        "totalApplications": 25,
        "interviews": 10,
        "offers": 3,
        "hired": 2,
        "conversionRate": 8.0
      }
    ],
    "sourceStats": [
      {
        "source": "Email",
        "totalApplications": 50,
        "hired": 10,
        "conversionRate": 20.0
      },
      {
        "source": "LinkedIn",
        "totalApplications": 30,
        "hired": 5,
        "conversionRate": 16.7
      }
    ],
    "skillStats": [
      {
        "skill": "Java",
        "totalApplications": 45,
        "successRate": 26.7
      },
      {
        "skill": "React",
        "totalJobs": 8,
        "successRate": 37.5
      }
    ]
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 🔔 Notification APIs (ATS) 🔄

> **🔄 SEMANTIC CHANGE**: Notifications có thể link đến applications (status changes, interview reminders).

### 1. Get User Notifications
**GET** `/notifications`

Lấy danh sách notifications của user (filtered by company).

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Query Parameters
```
page=0&size=20&isRead=false&type=APPLICATION_RECEIVED&applicationId=xxx
```

#### Response (200 OK)
```json
{
  "success": true,
  "data": [
    {
      "id": "n1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "userId": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
      "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
      "applicationId": "app1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "type": "APPLICATION_RECEIVED",
      "title": "New Application Received",
      "message": "John Doe applied for Senior Java Developer",
      "isRead": false,
      "isSent": true,
      "sentAt": "2024-01-15T10:00:00Z",
      "scheduledAt": null,
      "priority": "MEDIUM",
      "metadata": "{\"candidateName\":\"John Doe\",\"jobTitle\":\"Senior Java Developer\"}",
      "createdAt": "2024-01-15T10:00:00Z",
      "updatedAt": "2024-01-15T10:00:00Z"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z",
  "paginationInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 2. Mark Notification as Read
**PATCH** `/notifications/{id}/read`

Đánh dấu notification đã đọc.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Notification marked as read",
  "data": {
    "id": "n1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "isRead": true,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 3. Mark All Notifications as Read
**PATCH** `/notifications/read-all`

Đánh dấu tất cả notifications đã đọc.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "All notifications marked as read",
  "data": {
    "updatedCount": 5
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 4. Create Notification (Manual/Admin)
**POST** `/notifications`

#### Request Body
```json
{
  "userId": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
  "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
  "type": "DEADLINE_REMINDER",
  "priority": "HIGH",
  "title": "Custom Reminder",
  "message": "Follow up with recruiter tomorrow",
  "scheduledAt": "2024-01-16T09:00:00Z",
  "metadata": {
    "channel": "EMAIL"
  }
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Notification created successfully",
  "data": {
    "id": "n2b3c4d5-6e7f-8g9h-0i1j-k2l3m4n5o6p7",
    "userId": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "type": "DEADLINE_REMINDER",
    "priority": "HIGH",
    "title": "Custom Reminder",
    "message": "Follow up with recruiter tomorrow",
    "isRead": false,
    "isSent": false,
    "scheduledAt": "2024-01-16T09:00:00Z",
    "sentAt": null,
    "metadata": "{\"channel\":\"EMAIL\"}",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 5. Get Notification Details
**GET** `/notifications/{id}`

Trả về đầy đủ metadata (job, user, template data).

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Notification retrieved successfully",
  "data": {
    "id": "n1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "userId": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "type": "DEADLINE_REMINDER",
    "priority": "MEDIUM",
    "title": "Deadline Reminder",
    "message": "Google application deadline is in 3 days",
    "isRead": false,
    "isSent": true,
    "sentAt": "2024-01-15T10:00:00Z",
    "scheduledAt": null,
    "metadata": "{\"deadlineDate\":\"2024-01-18\",\"companyName\":\"Google\"}",
    "createdAt": "2024-01-15T10:00:00Z",
    "updatedAt": "2024-01-15T10:00:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 6. Delete Notification
**DELETE** `/notifications/{id}`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Notification deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 🔑 Session Management APIs

### 1. Get Active Sessions
**GET** `/sessions`

Lấy danh sách phiên đăng nhập của user hiện tại (bao gồm thiết bị khác).

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "data": [
    {
      "id": "13af47a3-9f8b-4ab0-8f2b-b0199a55de6b",
      "deviceInfo": {
        "os": "Windows 11",
        "browser": "Chrome 118"
      },
      "ipAddress": "203.0.113.10",
      "userAgent": "Mozilla/5.0 ...",
      "isActive": true,
      "expiresAt": "2024-02-01T09:00:00Z",
      "lastUsedAt": "2024-01-15T09:30:00Z",
      "createdAt": "2024-01-10T08:00:00Z"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z",
  "paginationInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 2. Revoke Session
**DELETE** `/sessions/{id}`

Đăng xuất (revoke) một session cụ thể.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Session revoked successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 📜 Audit Log APIs

### 1. Get Audit Logs
**GET** `/audit-logs`

> ⚠️ Chỉ dành cho ADMIN.

Lấy log hành động của người dùng/system để phục vụ kiểm tra.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Query Parameters
```
page=0&size=20&entityType=JOB&action=UPDATE&startDate=2024-01-01&endDate=2024-01-31
```

#### Response (200 OK)
```json
{
  "success": true,
  "data": [
    {
      "id": "f4f7c10a-9052-431c-8f4c-92669aa4bcd0",
      "entityType": "JOB",
      "entityId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
      "action": "UPDATE",
      "userId": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "userEmail": "admin@gmail.com",
      "oldValues": {
        "jobStatus": "DRAFT"
      },
      "newValues": {
        "jobStatus": "PUBLISHED"
      },
      "ipAddress": "203.0.113.10",
      "userAgent": "Mozilla/5.0 ...",
      "createdAt": "2024-01-12T08:15:00Z"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z",
  "paginationInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 125,
    "totalPages": 7
  }
}
```

### 2. Delete Audit Log (Archive)
**DELETE** `/audit-logs/{id}`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Audit log archived successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 📁 File Management APIs (ATS) 🔄

> **🔄 SEMANTIC CHANGE**: Attachments belong to Applications (CVs, certificates), không phải Jobs.

### 1. Upload Application Attachment (HR Workflow)
**POST** `/applications/{applicationId}/attachments`

HR/Recruiter upload file đính kèm cho application (CV, certificate, portfolio). Đây là **workflow phụ** cho HR manual upload.

> ⚠️ **Protected endpoint**: Yêu cầu `Authorization: Bearer <access_token>`  
> 📝 **Use case**: HR nhận CV qua email → Upload vào system → Link với application

#### Request Headers
```
Authorization: Bearer <access_token>
Content-Type: multipart/form-data
```

#### Request Body (Form Data)
```
file: <file>
attachmentType: RESUME
description: "Candidate's resume"
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Attachment uploaded successfully",
  "data": {
    "id": "a1b2c3d4-5e6f-7g8h-9i0j-k1l2m3n4o5p6",
    "applicationId": "app1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "companyId": "c1f9a8e2-3b4c-5d6e-7f80-1234567890ab",
    "userId": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "filename": "john_doe_resume.pdf",
    "originalFilename": "John_Doe_Resume_2024.pdf",
    "filePath": "/attachments/app_1/john_doe_resume.pdf",
    "fileSize": 512000,
    "fileType": "application/pdf",
    "attachmentType": "RESUME",
    "description": "Candidate's resume",
    "isPublic": false,
    "uploadedAt": "2024-01-15T10:30:00Z",
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 2. Download Attachment
**GET** `/attachments/{id}/download`

Download file đính kèm.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="John_Doe_Resume_2024.pdf"
Content-Length: 512000

<binary_file_content>
```

### 3. List Application Attachments
**GET** `/applications/{applicationId}/attachments`

Lấy danh sách attachments của application.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Application attachments retrieved successfully",
  "data": [
    {
      "id": "5f47e8b3-338f-4f1a-8e65-92dbd1dcb2f2",
      "filename": "john_doe_resume.pdf",
      "attachmentType": "RESUME",
      "fileSize": 512000,
      "uploadedAt": "2024-01-15T10:30:00Z"
    },
    {
      "id": "6g58f9c4-449g-5g2b-9f76-a3ece2edc3g3",
      "filename": "john_doe_certificate.pdf",
      "attachmentType": "CERTIFICATE",
      "fileSize": 256000,
      "uploadedAt": "2024-01-15T11:00:00Z"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 4. Delete Attachment
**DELETE** `/attachments/{id}`

Xóa attachment.

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Attachment deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 🚨 Error Responses

### Standard Error Format
```json
{
  "success": false,
  "message": "Error description",
  "errors": [
    {
      "field": "fieldName",
      "message": "Field-specific error message"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Common HTTP Status Codes
- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Access denied
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource conflict
- **422 Unprocessable Entity**: Validation failed
- **500 Internal Server Error**: Server error

### Error Examples

#### 401 Unauthorized
```json
{
  "success": false,
  "message": "Authentication required",
  "errors": [
    {
      "field": "authorization",
      "message": "JWT token is missing or invalid"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### 404 Not Found
```json
{
  "success": false,
  "message": "Resource not found",
  "errors": [
    {
      "field": "id",
      "message": "Job with ID 999 not found"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### 422 Validation Error
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Email format is invalid"
    },
    {
      "field": "password",
      "message": "Password must be at least 8 characters"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 🔧 API Configuration

### Rate Limiting
```
Rate Limit: 1000 requests per hour per user
Burst Limit: 100 requests per minute
```

### Request Size Limits
```
Max Request Size: 10MB
Max File Upload: 50MB
Max Array Size: 1000 items
```

### CORS Configuration
```
Allowed Origins: https://jobtracker.com, https://app.jobtracker.com
Allowed Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
Allowed Headers: Authorization, Content-Type, X-Requested-With
Max Age: 3600 seconds
```

## 📚 OpenAPI Documentation

API documentation được tự động generate bằng SpringDoc OpenAPI 3 và có thể truy cập tại:

- **Swagger UI**: `https://api.jobtracker.com/swagger-ui.html`
- **OpenAPI JSON**: `https://api.jobtracker.com/v3/api-docs`
- **OpenAPI YAML**: `https://api.jobtracker.com/v3/api-docs.yaml`

### API Versioning
```
Current Version: v1
Version Header: X-API-Version
Deprecation Policy: 6 months notice
```

## 🔐 Security Headers

### Required Headers
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
X-Requested-With: XMLHttpRequest
```

### Security Headers (Server Response)
```
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000; includeSubDomains
Content-Security-Policy: default-src 'self'
```

```

