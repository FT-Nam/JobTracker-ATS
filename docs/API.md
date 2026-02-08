# 🔌 JobTracker ATS API Documentation

## 📋 Tổng quan API

JobTracker ATS (Applicant Tracking System) cung cấp RESTful API với thiết kế REST chuẩn, sử dụng JSON cho data exchange và OAuth2/JWT cho authentication. API được thiết kế cho **multi-tenant architecture** với data isolation theo company.

### 🎯 API Design Principles
- **RESTful**: Tuân thủ REST conventions
- **Stateless**: JWT-based authentication
- **Multi-Tenant**: Data isolation bằng `company_id` trong mọi requests
- **Versioned**: API versioning với `/api/v1`
- **Consistent**: Uniform response format
- **Secure**: HTTPS, OAuth2, JWT, input validation, RBAC
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

### 1. User Registration
**POST** `/auth/register`

Đăng ký tài khoản người dùng mới.

#### Request Body
```json
{
  "email": "user@example.com",
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
  "message": "User registered successfully",
  "data": {
    "id": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+1234567890",
    "avatarUrl": null,
    "roleName": "USER",
    "isActive": true,
    "emailVerified": false,
    "googleId": null,
    "lastLoginAt": null,
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

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

### 3. Google OAuth Login
**POST** `/auth/google`

Đăng nhập với Google OAuth2.

#### Request Body
```json
{
  "idToken": "google_id_token_here"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Google login successful",
  "data": {
    "user": {
      "id": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "email": "user@gmail.com",
      "firstName": "John",
      "lastName": "Doe",
      "roleName": "USER",
      "avatarUrl": "https://lh3.googleusercontent.com/...",
      "googleId": "123456789"
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

### 4. Refresh Token
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

#### Request Headers
```
Authorization: Bearer <access_token>
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

> Chỉ dành cho ADMIN để quản lý bảng `users`.

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

### 2. Create User
**POST** `/admin/users`

Tạo user mới theo đầy đủ schema bảng `users`.

#### Request Headers
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

#### Request Body
```json
{
  "email": "new.user@jobtracker.com",
  "password": "TempPassword123!",
  "firstName": "New",
  "lastName": "User",
  "phone": "+12065551212",
  "avatarUrl": "https://cdn.jobtracker.com/avatars/new_user.png",
  "roleId": "34d9a2e3-1a30-4a1a-b1ad-4b6d2619f1ce",
  "isActive": true,
  "emailVerified": false,
  "googleId": null
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "id": "8b54b7f1-3f14-43a6-9a9a-5fefdc136d91",
    "email": "new.user@jobtracker.com",
    "firstName": "New",
    "lastName": "User",
    "phone": "+12065551212",
    "avatarUrl": "https://cdn.jobtracker.com/avatars/new_user.png",
    "roleId": "34d9a2e3-1a30-4a1a-b1ad-4b6d2619f1ce",
    "isActive": true,
    "emailVerified": false,
    "googleId": null,
    "lastLoginAt": null,
    "createdAt": "2024-01-20T08:00:00Z"
  },
  "timestamp": "2024-01-20T08:00:00Z"
}
```

> Server sẽ hash `password` theo chuẩn (BCrypt) trước khi lưu xuống cột `password`. Trường audit `createdAt` được populate tự động.

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

> **Lý do**: ATS không cần candidates upload CV. CVs được lưu trong `applications.resume_file_path` hoặc `attachments` table.

## 📝 Applications Management APIs (CORE ATS) ➕

> **🔑 CORE**: Applications là core entity của ATS. Candidates apply to job postings, HR/Recruiter quản lý applications qua workflow (NEW → SCREENING → INTERVIEWING → OFFERED → HIRED/REJECTED).

### 1. Get All Applications
**GET** `/applications`

Lấy danh sách tất cả applications của company với pagination và filtering.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Query Parameters
```
page=0&size=20&sort=appliedDate,desc&status=NEW&jobId=xxx&assignedTo=xxx&search=john
```

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
      "status": "NEW",
      "source": "Email",
      "appliedDate": "2024-01-15",
      "resumeFilePath": "/applications/app1/resume.pdf",
      "coverLetter": "I am interested in this position...",
      "notes": "Strong candidate, good fit",
      "rating": 4,
      "assignedTo": "user1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
      "assignedToName": "Jane Recruiter",
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

Lấy thông tin chi tiết một application.

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
    "status": "NEW",
    "source": "Email",
    "appliedDate": "2024-01-15",
    "resumeFilePath": "/applications/app1/resume.pdf",
    "coverLetter": "I am interested in this position...",
    "notes": "Strong candidate, good fit",
    "rating": 4,
    "assignedTo": "user1a2b3c4-5d6e-7f8g-9h0i-j1k2l3m4n5o6",
    "assignedToName": "Jane Recruiter",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 3. Create Application (Manual Entry)
**POST** `/applications`

HR/Recruiter tạo application thủ công (khi nhận CV qua email).

#### Request Body
```json
{
  "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
  "candidateName": "John Doe",
  "candidateEmail": "john.doe@example.com",
  "candidatePhone": "+1234567890",
  "status": "NEW",
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
    "status": "NEW",
    "appliedDate": "2024-01-15",
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
  "status": "SCREENING",
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
    "status": "SCREENING",
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

Cập nhật thông tin application (notes, rating, etc.).

#### Request Body
```json
{
  "notes": "Updated notes after phone screening",
  "rating": 5,
  "coverLetter": "Updated cover letter"
}
```

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
      "fromStatus": "NEW",
      "toStatus": "SCREENING",
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

## 📋 Lookup Tables APIs

> **🔄 CHUYỂN SANG ENUM**: Các lookup tables sau đã chuyển sang ENUM trong database, không cần APIs riêng:
> - **Job Statuses** → ENUM trong `jobs.jobStatus` (DRAFT, PUBLISHED, PAUSED, CLOSED, FILLED)
> - **Job Types** → ENUM trong `jobs.jobType` (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE)
> - **Interview Types** → ENUM trong `interviews.interviewType` (PHONE, VIDEO, IN_PERSON, TECHNICAL, HR, FINAL)
> - **Interview Statuses** → ENUM trong `interviews.status` (SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED)
> - **Interview Results** → ENUM trong `interviews.result` (PASSED, FAILED, PENDING)
> - **Notification Types** → ENUM trong `notifications.type` (APPLICATION_RECEIVED, INTERVIEW_SCHEDULED, etc.)
> - **Notification Priorities** → ENUM trong `notifications.priority` (HIGH, MEDIUM, LOW)

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

## 🔐 RBAC & Permission APIs

> ⚠️ Các endpoint này yêu cầu quyền `ADMIN`.

### 1. Get Roles
**GET** `/admin/roles`

Lấy danh sách roles cùng metadata để gán cho user.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Query Parameters
```
page=0&size=20&sort=name,asc&isActive=true&search=admin
```

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
  "paginationInfo": {
    "page": 0,
    "size": 20,
    "totalElements": 2,
    "totalPages": 1
  }
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
      "interviewerName": "Jane Smith",
      "interviewerEmail": "jane.smith@google.com",
      "interviewerPosition": "Senior Engineer",
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

Tạo interview mới cho application.

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
  "interviewerName": "Jane Smith",
  "interviewerEmail": "jane.smith@google.com",
  "interviewerPosition": "Senior Engineer",
  "status": "SCHEDULED",
  "meetingLink": "https://meet.google.com/xxx-yyyy-zzz",
  "location": "Office Building A, Room 101",
  "notes": "Technical interview"
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
    "interviewerName": "Jane Smith",
    "interviewerEmail": "jane.smith@google.com",
    "interviewerPosition": "Senior Engineer",
    "status": "SCHEDULED",
    "result": null,
    "meetingLink": "https://meet.google.com/xxx-yyyy-zzz",
    "location": "Office Building A, Room 101",
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
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 3. Update Interview
**PUT** `/interviews/{id}`

Cập nhật thông tin interview.

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
    "interviewerName": "Jane Smith",
    "interviewerEmail": "jane.smith@google.com",
    "interviewerPosition": "Senior Engineer",
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
    "interviewerName": "Jane Smith",
    "interviewerEmail": "jane.smith@google.com",
    "interviewerPosition": "Senior Engineer",
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

### 1. Upload Application Attachment
**POST** `/applications/{applicationId}/attachments`

Upload file đính kèm cho application (CV, certificate, portfolio).

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

### 4. Delete Attachment
**DELETE** `/attachments/{id}`

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

