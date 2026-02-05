# 🔌 JobTracker API Documentation

## 📋 Tổng quan API

JobTracker cung cấp RESTful API với thiết kế REST chuẩn, sử dụng JSON cho data exchange và JWT cho authentication.

### 🎯 API Design Principles
- **RESTful**: Tuân thủ REST conventions
- **Stateless**: JWT-based authentication
- **Versioned**: API versioning với `/api/v1`
- **Consistent**: Uniform response format
- **Secure**: HTTPS, JWT, input validation
- **Documented**: OpenAPI 3.0 specification

### 🔧 Base Configuration
```
Base URL: https://api.jobtracker.com/api/v1
Content-Type: application/json
Authorization: Bearer <oauth2_access_token>
```

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
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+1234567890",
    "role": {
      "id": 1,
      "name": "USER",
      "displayName": "User",
      "description": "Regular user with basic permissions"
    },
    "isActive": true,
    "emailVerified": false,
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
      "id": 1,
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": {
        "id": 1,
        "name": "USER",
        "displayName": "User"
      },
      "avatarUrl": null
    },
    "tokens": {
      "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
      "expiresIn": 3600,
      "tokenType": "Bearer"
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
      "id": 1,
      "email": "user@gmail.com",
      "firstName": "John",
      "lastName": "Doe",
      "role": "USER",
      "avatarUrl": "https://lh3.googleusercontent.com/...",
      "googleId": "123456789"
    },
    "tokens": {
      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "expiresIn": 3600
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
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600
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
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+1234567890",
    "avatarUrl": "https://dropbox.com/avatar.jpg",
    "role": "USER",
    "isActive": true,
    "emailVerified": true,
    "googleId": null,
    "lastLoginAt": "2024-01-15T09:00:00Z",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
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
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phone": "+1234567890",
    "avatarUrl": "https://dropbox.com/avatar.jpg",
    "role": "USER",
    "isActive": true,
    "emailVerified": true,
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
    "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "createdAt": "2024-01-20T08:00:00Z",
    "updatedAt": "2024-01-20T08:00:00Z",
    "deletedAt": null
  },
  "timestamp": "2024-01-20T08:00:00Z"
}
```

> Server sẽ hash `password` theo chuẩn (BCrypt) trước khi lưu xuống cột `password`. Các trường audit (`createdBy`, `updatedBy`, `createdAt`, `updatedAt`) được populate tự động.

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
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-15T10:30:00Z",
    "createdBy": null,
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "deletedAt": null
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

## 💼 Job Management APIs

### 1. Get All Jobs
**GET** `/jobs`

Lấy danh sách tất cả jobs của user với pagination và filtering.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Query Parameters
```
page=0&size=20&sort=createdAt,desc&status=APPLIED&company=Google&search=developer
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Roles retrieved successfully",
  "data": [
    {
        "id": 1,
        "title": "Senior Java Developer",
        "position": "Backend Developer",
        "company": {
          "id": 1,
          "name": "Google",
          "website": "https://google.com",
          "industry": "Technology",
          "size": "LARGE",
          "location": "Mountain View, CA",
          "logoUrl": "https://google.com/logo.png"
        },
        "jobType": {
          "id": 1,
          "name": "FULL_TIME",
          "displayName": "Full Time",
          "description": "Full-time employment"
        },
        "location": "Mountain View, CA",
        "salaryMin": 120000,
        "salaryMax": 180000,
        "currency": "USD",
        "status": {
          "id": 2,
          "name": "APPLIED",
          "displayName": "Applied",
          "description": "Application submitted",
          "color": "#3B82F6"
        },
        "applicationDate": "2024-01-10",
        "deadlineDate": "2024-01-25",
        "interviewDate": null,
        "offerDate": null,
        "jobDescription": "We are looking for a senior Java developer...",
        "requirements": "5+ years of Java experience...",
        "benefits": "Health insurance, 401k, stock options...",
        "jobUrl": "https://careers.google.com/jobs/123",
        "notes": "Applied through referral",
        "priority": {
          "id": 3,
          "name": "HIGH",
          "displayName": "High",
          "level": 3,
          "color": "#F59E0B"
        },
        "isRemote": false,
        "experienceLevel": {
          "id": 4,
          "name": "SENIOR",
          "displayName": "Senior",
          "minYears": 5,
          "maxYears": 8
        },
        "skills": [
          {
            "id": 1,
            "name": "Java",
            "category": "PROGRAMMING",
            "isRequired": true,
            "proficiencyLevel": "ADVANCED"
          },
          {
            "id": 2,
            "name": "Spring Boot",
            "category": "FRAMEWORK",
            "isRequired": true,
            "proficiencyLevel": "ADVANCED"
          }
        ],
        "resumes": [
          {
            "id": 1,
            "name": "John_Doe_Resume_2024.pdf",
            "isPrimary": true
          }
        ],
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
    "id": 1,
    "title": "Senior Java Developer",
    "position": "Backend Developer",
    "company": {
      "id": 1,
      "name": "Google",
      "website": "https://google.com",
      "industry": "Technology",
      "size": "LARGE",
      "location": "Mountain View, CA",
      "logoUrl": "https://google.com/logo.png"
    },
    "jobType": "FULL_TIME",
    "location": "Mountain View, CA",
    "salaryMin": 120000,
    "salaryMax": 180000,
    "currency": "USD",
    "status": "APPLIED",
    "applicationDate": "2024-01-10",
    "deadlineDate": "2024-01-25",
    "interviewDate": null,
    "offerDate": null,
    "jobDescription": "We are looking for a senior Java developer...",
    "requirements": "5+ years of Java experience...",
    "benefits": "Health insurance, 401k, stock options...",
    "jobUrl": "https://careers.google.com/jobs/123",
    "notes": "Applied through referral",
    "priority": "HIGH",
    "isRemote": false,
    "experienceLevel": "SENIOR",
    "skills": [
      {
        "id": 1,
        "name": "Java",
        "category": "PROGRAMMING",
        "isRequired": true,
        "proficiencyLevel": "ADVANCED"
      }
    ],
    "resumes": [
      {
        "id": 1,
        "name": "John_Doe_Resume_2024.pdf",
        "isPrimary": true
      }
    ],
    "interviews": [
      {
        "id": 1,
        "roundNumber": 1,
        "interviewType": "PHONE",
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
        "rating": null
      }
    ],
    "attachments": [
      {
        "id": 1,
        "filename": "job_description.pdf",
        "originalFilename": "Google_Job_Description.pdf",
        "fileType": "application/pdf",
        "attachmentType": "JOB_DESCRIPTION",
        "description": "Official job description from Google",
        "uploadedAt": "2024-01-10T09:00:00Z"
      }
    ],
    "createdAt": "2024-01-10T09:00:00Z",
    "updatedAt": "2024-01-10T09:00:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 3. Create New Job
**POST** `/jobs`

Tạo job mới.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Request Body
```json
{
  "companyId": 1,
  "title": "Senior Java Developer",
  "position": "Backend Developer",
  "jobTypeId": 1,
  "location": "Mountain View, CA",
  "salaryMin": 120000,
  "salaryMax": 180000,
  "currency": "USD",
  "applicationDate": "2024-01-10",
  "deadlineDate": "2024-01-25",
  "jobDescription": "We are looking for a senior Java developer...",
  "requirements": "5+ years of Java experience...",
  "benefits": "Health insurance, 401k, stock options...",
  "jobUrl": "https://careers.google.com/jobs/123",
  "notes": "Applied through referral",
  "priorityId": 3,
  "isRemote": false,
  "experienceLevelId": 4,
  "skillIds": [1, 2, 3],
  "resumeIds": [1]
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Job created successfully",
  "data": {
    "id": 1,
    "title": "Senior Java Developer",
    "position": "Backend Developer",
    "company": {
      "id": 1,
      "name": "Google",
      "website": "https://google.com"
    },
    "jobType": "FULL_TIME",
    "location": "Mountain View, CA",
    "salaryMin": 120000,
    "salaryMax": 180000,
    "currency": "USD",
    "status": "SAVED",
    "applicationDate": "2024-01-10",
    "deadlineDate": "2024-01-25",
    "priority": "HIGH",
    "isRemote": false,
    "experienceLevel": "SENIOR",
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
  "status": "INTERVIEW",
  "interviewDate": "2024-01-20",
  "notes": "Updated notes after phone screening"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job updated successfully",
  "data": {
    "id": 1,
    "title": "Senior Java Developer - Updated",
    "position": "Backend Developer",
    "status": "INTERVIEW",
    "interviewDate": "2024-01-20",
    "notes": "Updated notes after phone screening",
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

### 6. Update Job Status
**PATCH** `/jobs/{id}/status`

Cập nhật trạng thái job.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Request Body
```json
{
  "status": "OFFER",
  "offerDate": "2024-01-25",
  "notes": "Received offer with $150k base salary"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job status updated successfully",
  "data": {
    "id": 1,
    "status": "OFFER",
    "offerDate": "2024-01-25",
    "notes": "Received offer with $150k base salary",
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
      "skillId": "a3e6e84c-5f21-4c4d-8d7d-4a38e9ab6f52",
      "name": "Java",
      "category": "PROGRAMMING",
      "isRequired": true,
      "proficiencyLevel": "ADVANCED"
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
    "skill": {
      "id": "b7e58a6e-5c5e-4de8-9a3f-6b1ae2d042b5",
      "name": "Spring Boot",
      "category": "FRAMEWORK"
    },
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
    "skill": {
      "id": "b7e58a6e-5c5e-4de8-9a3f-6b1ae2d042b5",
      "name": "Spring Boot",
      "category": "FRAMEWORK"
    },
    "isRequired": false,
    "proficiencyLevel": "ADVANCED",
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

### 8. Manage Job Resumes

**POST** `/jobs/{jobId}/resumes`

#### Request Body
```json
{
  "resumeId": "e31ab668-0f3e-4ac4-a904-2acd07c05436",
  "isPrimary": true
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Resume linked to job successfully",
  "data": {
    "id": "g9h0i1j2-3k4l-5m6n-7o8p-q9r0s1t2u3v4",
    "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "resumeId": "e31ab668-0f3e-4ac4-a904-2acd07c05436",
    "resume": {
      "id": "e31ab668-0f3e-4ac4-a904-2acd07c05436",
      "name": "John_Doe_Resume_2024.pdf"
    },
    "isPrimary": true,
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**PATCH** `/jobs/{jobId}/resumes/{resumeId}`

#### Request Body
```json
{
  "isPrimary": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job resume updated successfully",
  "data": {
    "id": "g9h0i1j2-3k4l-5m6n-7o8p-q9r0s1t2u3v4",
    "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "resumeId": "e31ab668-0f3e-4ac4-a904-2acd07c05436",
    "resume": {
      "id": "e31ab668-0f3e-4ac4-a904-2acd07c05436",
      "name": "John_Doe_Resume_2024.pdf"
    },
    "isPrimary": true,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**DELETE** `/jobs/{jobId}/resumes/{resumeId}`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Resume unlinked from job",
  "data": null,
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
      "id": 1,
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
    "id": 2,
    "name": "New Tech Company",
    "website": "https://newtech.com",
    "industry": "Technology",
    "size": "MEDIUM",
    "location": "San Francisco, CA",
    "description": "A innovative technology company...",
    "logoUrl": null,
    "isVerified": false,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z",
    "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "deletedAt": null
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
    "id": 1,
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
    "id": 1,
    "name": "Google",
    "website": "https://newtech.com",
    "industry": "Technology",
    "size": "LARGE",
    "location": "Remote",
    "description": "Updated description",
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

### 1. Get Job Statuses
**GET** `/lookup/job-statuses`

Lấy danh sách tất cả job statuses.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job statuses retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "SAVED",
      "displayName": "Saved",
      "description": "Job saved but not yet applied",
      "color": "#6B7280",
      "sortOrder": 1,
      "isActive": true
    },
    {
      "id": 2,
      "name": "APPLIED",
      "displayName": "Applied",
      "description": "Application submitted",
      "color": "#3B82F6",
      "sortOrder": 2,
      "isActive": true
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Create Job Status
**POST** `/lookup/job-statuses`

#### Request Body
```json
{
  "name": "ON_HOLD",
  "displayName": "On Hold",
  "description": "Application paused",
  "color": "#FBBF24",
  "sortOrder": 7
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Job status created successfully",
  "data": {
    "id": 7,
    "name": "ON_HOLD",
    "displayName": "On Hold",
    "description": "Application paused",
    "color": "#FBBF24",
    "sortOrder": 7,
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Update Job Status
**PUT** `/lookup/job-statuses/{id}`

#### Request Body
```json
{
  "displayName": "On Hold",
  "description": "Paused by company",
  "color": "#FBBF24",
  "isActive": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job status updated successfully",
  "data": {
    "id": 7,
    "name": "ON_HOLD",
    "displayName": "On Hold",
    "description": "Paused by company",
    "color": "#FBBF24",
    "sortOrder": 7,
    "isActive": true,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Delete Job Status
**DELETE** `/lookup/job-statuses/{id}`

Soft delete trạng thái khỏi danh sách khả dụng. Response chuẩn:
```json
{
  "success": true,
  "message": "Job status deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 2. Get Job Types
**GET** `/lookup/job-types`

Lấy danh sách tất cả job types.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job types retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "FULL_TIME",
      "displayName": "Full Time",
      "description": "Full-time employment",
      "isActive": true
    },
    {
      "id": 2,
      "name": "PART_TIME",
      "displayName": "Part Time",
      "description": "Part-time employment",
      "isActive": true
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Create Job Type
**POST** `/lookup/job-types`

#### Request Body
```json
{
  "name": "APPRENTICESHIP",
  "displayName": "Apprenticeship",
  "description": "Apprenticeship program"
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Job type created successfully",
  "data": {
    "id": 5,
    "name": "APPRENTICESHIP",
    "displayName": "Apprenticeship",
    "description": "Apprenticeship program",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Update Job Type
**PUT** `/lookup/job-types/{id}`

#### Request Body
```json
{
  "displayName": "Apprenticeship",
  "description": "On-the-job apprenticeship",
  "isActive": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job type updated successfully",
  "data": {
    "id": 5,
    "name": "APPRENTICESHIP",
    "displayName": "Apprenticeship",
    "description": "On-the-job apprenticeship",
    "isActive": true,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Delete Job Type
**DELETE** `/lookup/job-types/{id}`

```json
{
  "success": true,
  "message": "Job type deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 3. Get Priorities
**GET** `/lookup/priorities`

Lấy danh sách tất cả priorities.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Priorities retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "LOW",
      "displayName": "Low",
      "level": 1,
      "color": "#6B7280",
      "description": "Low priority",
      "isActive": true
    },
    {
      "id": 2,
      "name": "MEDIUM",
      "displayName": "Medium",
      "level": 2,
      "color": "#3B82F6",
      "description": "Medium priority",
      "isActive": true
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Create Priority
**POST** `/lookup/priorities`

#### Request Body
```json
{
  "name": "BLOCKER",
  "displayName": "Blocker",
  "level": 5,
  "color": "#DC2626",
  "description": "Must act immediately"
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Priority created successfully",
  "data": {
    "id": 5,
    "name": "BLOCKER",
    "displayName": "Blocker",
    "level": 5,
    "color": "#DC2626",
    "description": "Must act immediately",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Update Priority
**PUT** `/lookup/priorities/{id}`

#### Request Body
```json
{
  "displayName": "Blocker",
  "level": 5,
  "color": "#DC2626",
  "description": "Highest urgency",
  "isActive": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Priority updated successfully",
  "data": {
    "id": 5,
    "name": "BLOCKER",
    "displayName": "Blocker",
    "level": 5,
    "color": "#DC2626",
    "description": "Highest urgency",
    "isActive": true,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Delete Priority
**DELETE** `/lookup/priorities/{id}`

```json
{
  "success": true,
  "message": "Priority deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 4. Get Experience Levels
**GET** `/lookup/experience-levels`

Lấy danh sách tất cả experience levels.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Experience levels retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "ENTRY",
      "displayName": "Entry Level",
      "minYears": 0,
      "maxYears": 1,
      "description": "Entry level position",
      "isActive": true
    },
    {
      "id": 2,
      "name": "JUNIOR",
      "displayName": "Junior",
      "minYears": 1,
      "maxYears": 3,
      "description": "Junior level position",
      "isActive": true
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Create Experience Level
**POST** `/lookup/experience-levels`

#### Request Body
```json
{
  "name": "STAFF",
  "displayName": "Staff",
  "minYears": 8,
  "maxYears": 12,
  "description": "Staff engineer level"
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Experience level created successfully",
  "data": {
    "id": 5,
    "name": "STAFF",
    "displayName": "Staff",
    "minYears": 8,
    "maxYears": 12,
    "description": "Staff engineer level",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Update Experience Level
**PUT** `/lookup/experience-levels/{id}`

#### Request Body
```json
{
  "displayName": "Staff",
  "minYears": 8,
  "maxYears": 12,
  "description": "Staff / Principal track",
  "isActive": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Experience level updated successfully",
  "data": {
    "id": 5,
    "name": "STAFF",
    "displayName": "Staff",
    "minYears": 8,
    "maxYears": 12,
    "description": "Staff / Principal track",
    "isActive": true,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Delete Experience Level
**DELETE** `/lookup/experience-levels/{id}`

```json
{
  "success": true,
  "message": "Experience level deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 5. Get Interview Types
**GET** `/lookup/interview-types`

Lấy danh sách tất cả interview types.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Interview types retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "PHONE",
      "displayName": "Phone Interview",
      "description": "Phone-based interview",
      "isActive": true
    },
    {
      "id": 2,
      "name": "VIDEO",
      "displayName": "Video Interview",
      "description": "Video call interview",
      "isActive": true
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Create Interview Type
**POST** `/lookup/interview-types`

#### Request Body
```json
{
  "name": "PAIR_PROGRAMMING",
  "displayName": "Pair Programming",
  "description": "Live coding with interviewer"
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Interview type created successfully",
  "data": {
    "id": 5,
    "name": "PAIR_PROGRAMMING",
    "displayName": "Pair Programming",
    "description": "Live coding with interviewer",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Update Interview Type
**PUT** `/lookup/interview-types/{id}`

#### Request Body
```json
{
  "displayName": "Pair Programming",
  "description": "Live pair programming session",
  "isActive": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Interview type updated successfully",
  "data": {
    "id": 5,
    "name": "PAIR_PROGRAMMING",
    "displayName": "Pair Programming",
    "description": "Live pair programming session",
    "isActive": true,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Delete Interview Type
**DELETE** `/lookup/interview-types/{id}`

```json
{
  "success": true,
  "message": "Interview type deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 6. Get Interview Statuses
**GET** `/lookup/interview-statuses`

Lấy danh sách tất cả interview statuses.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Interview statuses retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "SCHEDULED",
      "displayName": "Scheduled",
      "description": "Interview scheduled",
      "color": "#3B82F6",
      "isActive": true
    },
    {
      "id": 2,
      "name": "COMPLETED",
      "displayName": "Completed",
      "description": "Interview completed",
      "color": "#10B981",
      "isActive": true
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Create Interview Status
**POST** `/lookup/interview-statuses`

#### Request Body
```json
{
  "name": "NO_SHOW",
  "displayName": "No Show",
  "description": "Candidate did not attend",
  "color": "#F87171"
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Interview status created successfully",
  "data": {
    "id": 5,
    "name": "NO_SHOW",
    "displayName": "No Show",
    "description": "Candidate did not attend",
    "color": "#F87171",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Update Interview Status
**PUT** `/lookup/interview-statuses/{id}`

#### Request Body
```json
{
  "displayName": "No Show",
  "description": "Candidate did not attend",
  "color": "#F87171",
  "isActive": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Interview status updated successfully",
  "data": {
    "id": 5,
    "name": "NO_SHOW",
    "displayName": "No Show",
    "description": "Candidate did not attend",
    "color": "#F87171",
    "isActive": true,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Delete Interview Status
**DELETE** `/lookup/interview-statuses/{id}`

```json
{
  "success": true,
  "message": "Interview status deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 7. Get Interview Results
**GET** `/lookup/interview-results`

Lấy danh sách tất cả interview results.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Interview results retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "PASSED",
      "displayName": "Passed",
      "description": "Interview passed",
      "color": "#10B981",
      "isActive": true
    },
    {
      "id": 2,
      "name": "FAILED",
      "displayName": "Failed",
      "description": "Interview failed",
      "color": "#EF4444",
      "isActive": true
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Create Interview Result
**POST** `/lookup/interview-results`

#### Request Body
```json
{
  "name": "ON_HOLD",
  "displayName": "On Hold",
  "description": "Awaiting leadership decision",
  "color": "#FBBF24"
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Interview result created successfully",
  "data": {
    "id": 5,
    "name": "ON_HOLD",
    "displayName": "On Hold",
    "description": "Awaiting leadership decision",
    "color": "#FBBF24",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Update Interview Result
**PUT** `/lookup/interview-results/{id}`

#### Request Body
```json
{
  "displayName": "On Hold",
  "description": "Pending leadership decision",
  "color": "#FBBF24",
  "isActive": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Interview result updated successfully",
  "data": {
    "id": 5,
    "name": "ON_HOLD",
    "displayName": "On Hold",
    "description": "Pending leadership decision",
    "color": "#FBBF24",
    "isActive": true,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Delete Interview Result
**DELETE** `/lookup/interview-results/{id}`

```json
{
  "success": true,
  "message": "Interview result deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 8. Get Notification Types
**GET** `/lookup/notification-types`

Lấy danh sách loại thông báo hệ thống.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Notification types retrieved successfully",
  "data": [
    {
      "id": "c9d0c7f4-4d1c-4a53-8a51-4f53c9a9f5d0",
      "name": "DEADLINE_REMINDER",
      "displayName": "Deadline Reminder",
      "description": "Reminder for job application deadline",
      "template": "Your job application for {job_title} is due in {days} days.",
      "isActive": true
    },
    {
      "id": "2c0d54a7-ef1d-4ef1-a38e-a89461e4d1cd",
      "name": "INTERVIEW_REMINDER",
      "displayName": "Interview Reminder",
      "description": "Reminder for upcoming interview",
      "template": "You have an interview for {job_title} at {company_name} in {hours} hours.",
      "isActive": true
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Create Notification Type
**POST** `/lookup/notification-types`

#### Request Body
```json
{
  "name": "CUSTOM",
  "displayName": "Custom Message",
  "description": "Manual notification",
  "template": "{message}",
  "isActive": true
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Notification type created successfully",
  "data": {
    "id": "d4e5f6g7-8h9i-0j1k-2l3m-n4o5p6q7r8s9",
    "name": "CUSTOM",
    "displayName": "Custom Message",
    "description": "Manual notification",
    "template": "{message}",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Update Notification Type
**PUT** `/lookup/notification-types/{id}`

#### Request Body
```json
{
  "displayName": "Custom Message",
  "description": "Manual notification template",
  "template": "{message}",
  "isActive": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Notification type updated successfully",
  "data": {
    "id": "d4e5f6g7-8h9i-0j1k-2l3m-n4o5p6q7r8s9",
    "name": "CUSTOM",
    "displayName": "Custom Message",
    "description": "Manual notification template",
    "template": "{message}",
    "isActive": true,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Delete Notification Type
**DELETE** `/lookup/notification-types/{id}`

```json
{
  "success": true,
  "message": "Notification type deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 9. Get Notification Priorities
**GET** `/lookup/notification-priorities`

Lấy danh sách mức độ ưu tiên của thông báo.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Notification priorities retrieved successfully",
  "data": [
    {
      "id": "0da2f18c-4c3d-4d2c-b3f5-2d933515d96e",
      "name": "LOW",
      "displayName": "Low",
      "level": 1,
      "color": "#6B7280",
      "description": "Low priority notification",
      "isActive": true
    },
    {
      "id": "a9fd0c51-fd0a-4a01-bbbd-12cf32c29086",
      "name": "HIGH",
      "displayName": "High",
      "level": 3,
      "color": "#F59E0B",
      "description": "High priority notification",
      "isActive": true
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Create Notification Priority
**POST** `/lookup/notification-priorities`

#### Request Body
```json
{
  "name": "CRITICAL",
  "displayName": "Critical",
  "level": 5,
  "color": "#B91C1C",
  "description": "Critical notifications",
  "isActive": true
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Notification priority created successfully",
  "data": {
    "id": "e5f6g7h8-9i0j-1k2l-3m4n-o5p6q7r8s9t0",
    "name": "CRITICAL",
    "displayName": "Critical",
    "level": 5,
    "color": "#B91C1C",
    "description": "Critical notifications",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Update Notification Priority
**PUT** `/lookup/notification-priorities/{id}`

#### Request Body
```json
{
  "displayName": "Critical",
  "level": 5,
  "color": "#B91C1C",
  "description": "Critical notifications only",
  "isActive": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Notification priority updated successfully",
  "data": {
    "id": "e5f6g7h8-9i0j-1k2l-3m4n-o5p6q7r8s9t0",
    "name": "CRITICAL",
    "displayName": "Critical",
    "level": 5,
    "color": "#B91C1C",
    "description": "Critical notifications only",
    "isActive": true,
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Delete Notification Priority
**DELETE** `/lookup/notification-priorities/{id}`

```json
{
  "success": true,
  "message": "Notification priority deleted successfully",
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
        "id": "5a12b2d5-0b42-4b3c-815a-7cf6fca39a8e",
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
      "id": 1,
      "name": "Java",
      "category": "PROGRAMMING",
      "description": "Object-oriented programming language",
      "isActive": true,
      "createdAt": "2024-01-01T00:00:00Z",
      "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
      "deletedAt": null
    },
    {
      "id": 2,
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
    "id": 1,
    "name": "Java",
    "category": "PROGRAMMING",
    "description": "Object-oriented programming language",
    "isActive": true,
    "createdAt": "2024-01-01T00:00:00Z",
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
    "id": 3,
    "name": "Kubernetes",
    "category": "TOOL",
    "description": "Container orchestration",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "deletedAt": null
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
    "id": 3,
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

### 6. Get User Skills
**GET** `/users/skills`

Lấy skills của user hiện tại.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "User skills retrieved successfully",
  "data": [
    {
      "id": 1,
      "skill": {
        "id": 1,
        "name": "Java",
        "category": "PROGRAMMING",
        "description": "Object-oriented programming language"
      },
      "proficiencyLevel": "ADVANCED",
      "yearsOfExperience": 5.0,
      "isVerified": false,
      "createdAt": "2024-01-01T00:00:00Z",
      "updatedAt": "2024-01-01T00:00:00Z"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 7. Add User Skill
**POST** `/users/skills`

Thêm skill cho user.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Request Body
```json
{
  "skillId": 1,
  "proficiencyLevel": "ADVANCED",
  "yearsOfExperience": 5.0
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "User skill added successfully",
  "data": {
    "id": 1,
    "skill": {
      "id": 1,
      "name": "Java",
      "category": "PROGRAMMING"
    },
    "proficiencyLevel": "ADVANCED",
    "yearsOfExperience": 5.0,
    "isVerified": false,
    "createdAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 8. Update User Skill
**PUT** `/users/skills/{id}`

#### Request Body
```json
{
  "proficiencyLevel": "EXPERT",
  "yearsOfExperience": 6.5,
  "isVerified": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "User skill updated successfully",
  "data": {
    "id": 1,
    "skill": {
      "id": 1,
      "name": "Java",
      "category": "PROGRAMMING",
      "description": "Object-oriented programming language"
    },
    "proficiencyLevel": "EXPERT",
    "yearsOfExperience": 6.5,
    "isVerified": true,
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 9. Delete User Skill
**DELETE** `/users/skills/{id}`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "User skill deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 📄 Resume Management APIs

### 1. Get User Resumes
**GET** `/resumes`

Lấy danh sách resumes của user.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Resumes retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "John_Doe_Resume_2024.pdf",
      "originalFilename": "John_Doe_Resume_2024.pdf",
      "fileSize": 1024000,
      "fileType": "application/pdf",
      "version": "1.0",
      "isDefault": true,
      "description": "Updated resume for 2024",
      "tags": ["senior", "java", "backend"],
      "isActive": true,
      "uploadedAt": "2024-01-10T09:00:00Z",
      "createdAt": "2024-01-10T09:00:00Z",
      "updatedAt": "2024-01-10T09:00:00Z"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 2. Upload Resume
**POST** `/resumes/upload`

Upload resume mới.

#### Request Headers
```
Authorization: Bearer <access_token>
Content-Type: multipart/form-data
```

#### Request Body (Form Data)
```
file: <pdf_file>
description: "Updated resume for 2024"
tags: ["senior", "java", "backend"]
isDefault: true
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Resume uploaded successfully",
  "data": {
    "id": 1,
    "name": "John_Doe_Resume_2024.pdf",
    "originalFilename": "John_Doe_Resume_2024.pdf",
    "fileSize": 1024000,
    "fileType": "application/pdf",
    "version": "1.0",
    "isDefault": true,
    "description": "Updated resume for 2024",
    "tags": ["senior", "java", "backend"],
    "isActive": true,
    "uploadedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 3. Download Resume
**GET** `/resumes/{id}/download`

Download resume file.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Response (200 OK)
```
Content-Type: application/pdf
Content-Disposition: attachment; filename="John_Doe_Resume_2024.pdf"
Content-Length: 1024000

<binary_file_content>
```

### 4. Get Resume Details
**GET** `/resumes/{id}`

Trả về metadata đầy đủ (name, tags, version, audit).

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Resume retrieved successfully",
  "data": {
    "id": 1,
    "userId": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "name": "John_Doe_Resume_2024.pdf",
    "originalFilename": "John_Doe_Resume_2024.pdf",
    "filePath": "/resumes/user_1/resume_1.pdf",
    "fileSize": 1024000,
    "fileType": "application/pdf",
    "version": "1.0",
    "isDefault": true,
    "description": "Updated resume for 2024",
    "tags": "senior,java,backend",
    "isActive": true,
    "uploadedAt": "2024-01-10T09:00:00Z",
    "createdAt": "2024-01-10T09:00:00Z",
    "updatedAt": "2024-01-10T09:00:00Z",
    "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "deletedAt": null
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 5. Update Resume Metadata
**PUT** `/resumes/{id}`

#### Request Body
```json
{
  "name": "John_Doe_Resume_2025.pdf",
  "description": "Updated for 2025 season",
  "tags": ["lead", "manager"],
  "isDefault": true
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Resume updated successfully",
  "data": {
    "id": 1,
    "userId": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "name": "John_Doe_Resume_2025.pdf",
    "originalFilename": "John_Doe_Resume_2024.pdf",
    "filePath": "/resumes/user_1/resume_1.pdf",
    "fileSize": 1024000,
    "fileType": "application/pdf",
    "version": "1.0",
    "isDefault": true,
    "description": "Updated for 2025 season",
    "tags": "lead,manager",
    "isActive": true,
    "uploadedAt": "2024-01-10T09:00:00Z",
    "createdAt": "2024-01-10T09:00:00Z",
    "updatedAt": "2024-01-15T10:30:00Z",
    "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "deletedAt": null
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 6. Delete Resume
**DELETE** `/resumes/{id}`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Resume deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## 🎤 Interview Management APIs

### 1. Get Job Interviews
**GET** `/jobs/{jobId}/interviews`

Lấy danh sách interviews của một job.

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
      "id": 1,
      "roundNumber": 1,
      "interviewType": "PHONE",
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
      "updatedAt": "2024-01-15T10:30:00Z"
    }
  ],
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 2. Create Interview
**POST** `/jobs/{jobId}/interviews`

Tạo interview mới cho job.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Request Body
```json
{
  "roundNumber": 1,
  "interviewType": "PHONE",
  "scheduledDate": "2024-01-20T14:00:00Z",
  "durationMinutes": 60,
  "interviewerName": "Jane Smith",
  "interviewerEmail": "jane.smith@google.com",
  "interviewerPosition": "Senior Engineer",
  "notes": "Technical interview"
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Interview created successfully",
  "data": {
    "id": 1,
    "roundNumber": 1,
    "interviewType": "PHONE",
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
    "createdAt": "2024-01-15T10:30:00Z"
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
    "id": 1,
    "actualDate": "2024-01-20T14:30:00Z",
    "status": "COMPLETED",
    "result": "PASSED",
    "feedback": "Great technical skills, good communication",
    "notes": "Interview went well, waiting for next round",
    "questionsAsked": "What is your experience with Spring Boot?",
    "answersGiven": "I have 3 years of experience with Spring Boot...",
    "rating": 4,
    "updatedAt": "2024-01-15T10:30:00Z"
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
    "id": 1,
    "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "roundNumber": 1,
    "interviewTypeId": "a1b2c3d4-5e6f-7g8h-9i0j-k1l2m3n4o5p6",
    "interviewStatusId": "b2c3d4e5-6f7g-8h9i-0j1k-l2m3n4o5p6q7",
    "interviewResultId": "c3d4e5f6-7g8h-9i0j-1k2l-m3n4o5p6q7r8",
    "scheduledDate": "2024-01-20T14:00:00Z",
    "actualDate": "2024-01-20T14:30:00Z",
    "durationMinutes": 60,
    "interviewerName": "Jane Smith",
    "interviewerEmail": "jane.smith@google.com",
    "interviewerPosition": "Senior Engineer",
    "location": "Google HQ, Building 43",
    "status": "COMPLETED",
    "result": "PASSED",
    "feedback": "Great technical skills, good communication",
    "notes": "Interview went well, waiting for next round",
    "questionsAsked": "What is your experience with Spring Boot?",
    "answersGiven": "I have 3 years of experience with Spring Boot...",
    "rating": 4,
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-20T15:00:00Z",
    "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "deletedAt": null
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
        "id": 1,
        "type": "JOB_CREATED",
        "message": "Created new job application for Google",
        "createdAt": "2024-01-15T10:30:00Z"
      },
      {
        "id": 2,
        "type": "INTERVIEW_SCHEDULED",
        "message": "Interview scheduled for Microsoft",
        "createdAt": "2024-01-15T09:00:00Z"
      }
    ],
    "upcomingDeadlines": [
      {
        "id": 1,
        "title": "Senior Java Developer",
        "company": "Google",
        "deadlineDate": "2024-01-25",
        "daysRemaining": 10
      }
    ],
    "topSkills": [
      {
        "skill": "Java",
        "count": 15,
        "percentage": 60.0
      },
      {
        "skill": "Spring Boot",
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
        "applied": 8,
        "interview": 2,
        "offer": 1,
        "rejected": 3
      },
      {
        "date": "2024-02",
        "applied": 12,
        "interview": 4,
        "offer": 2,
        "rejected": 5
      }
    ],
    "companyStats": [
      {
        "company": "Google",
        "totalApplications": 5,
        "interviews": 2,
        "offers": 1,
        "successRate": 20.0
      },
      {
        "company": "Microsoft",
        "totalApplications": 3,
        "interviews": 1,
        "offers": 0,
        "successRate": 0.0
      }
    ],
    "skillStats": [
      {
        "skill": "Java",
        "totalJobs": 15,
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

## 🔔 Notification APIs

### 1. Get User Notifications
**GET** `/notifications`

Lấy danh sách notifications của user.

#### Request Headers
```
Authorization: Bearer <access_token>
```

#### Query Parameters
```
page=0&size=20&isRead=false&type=DEADLINE_REMINDER
```

#### Response (200 OK)
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "jobId": 1,
      "type": "DEADLINE_REMINDER",
      "title": "Deadline Reminder",
      "message": "Google application deadline is in 3 days",
      "isRead": false,
      "isSent": true,
      "sentAt": "2024-01-15T10:00:00Z",
      "priority": "HIGH",
      "metadata": {
        "deadlineDate": "2024-01-18",
        "companyName": "Google"
      },
      "createdAt": "2024-01-15T10:00:00Z"
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
    "id": 1,
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
  "typeId": "c9d0c7f4-4d1c-4a53-8a51-4f53c9a9f5d0",
  "priorityId": "0da2f18c-4c3d-4d2c-b3f5-2d933515d96e",
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
    "id": 2,
    "userId": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "notificationTypeId": "c9d0c7f4-4d1c-4a53-8a51-4f53c9a9f5d0",
    "notificationPriorityId": "0da2f18c-4c3d-4d2c-b3f5-2d933515d96e",
    "title": "Custom Reminder",
    "message": "Follow up with recruiter tomorrow",
    "isRead": false,
    "isSent": false,
    "scheduledAt": "2024-01-16T09:00:00Z",
    "sentAt": null,
    "metadata": "{\"channel\":\"EMAIL\"}",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z",
    "createdBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "updatedBy": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "deletedAt": null
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
    "id": 1,
    "userId": "e2019f85-4a2f-4a6a-94b8-42c9b62b34be",
    "jobId": "d7e6d2c9-0c6e-4ca8-bc52-2e95746bffc3",
    "notificationTypeId": "c9d0c7f4-4d1c-4a53-8a51-4f53c9a9f5d0",
    "notificationPriorityId": "0da2f18c-4c3d-4d2c-b3f5-2d933515d96e",
    "title": "Deadline Reminder",
    "message": "Google application deadline is in 3 days",
    "isRead": false,
    "isSent": true,
    "sentAt": "2024-01-15T10:00:00Z",
    "scheduledAt": null,
    "metadata": "{\"deadlineDate\":\"2024-01-18\",\"companyName\":\"Google\"}",
    "createdAt": "2024-01-15T10:00:00Z",
    "updatedAt": "2024-01-15T10:00:00Z",
    "createdBy": null,
    "updatedBy": null,
    "deletedAt": null
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
        "status": "APPLIED"
      },
      "newValues": {
        "status": "INTERVIEW"
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

## 📁 File Management APIs

### 1. Upload Job Attachment
**POST** `/jobs/{jobId}/attachments`

Upload file đính kèm cho job.

#### Request Headers
```
Authorization: Bearer <access_token>
Content-Type: multipart/form-data
```

#### Request Body (Form Data)
```
file: <file>
attachmentType: JOB_DESCRIPTION
description: "Official job description"
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "Attachment uploaded successfully",
  "data": {
    "id": 1,
    "filename": "job_description.pdf",
    "originalFilename": "Google_Job_Description.pdf",
    "fileSize": 512000,
    "fileType": "application/pdf",
    "attachmentType": "JOB_DESCRIPTION",
    "description": "Official job description",
    "isPublic": false,
    "uploadedAt": "2024-01-15T10:30:00Z"
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
Content-Disposition: attachment; filename="Google_Job_Description.pdf"
Content-Length: 512000

<binary_file_content>
```

### 3. List Job Attachments
**GET** `/jobs/{jobId}/attachments`

#### Response (200 OK)
```json
{
  "success": true,
  "message": "Job attachments retrieved successfully",
  "data": [
    {
      "id": "5f47e8b3-338f-4f1a-8e65-92dbd1dcb2f2",
      "filename": "job_description.pdf",
      "attachmentType": "JOB_DESCRIPTION",
      "uploadedAt": "2024-01-15T10:30:00Z"
    }
  ],
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
