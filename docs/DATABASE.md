# 🗄️ JobTracker ATS Database Schema

## 📋 Tổng quan Database

JobTracker ATS (Applicant Tracking System) sử dụng **MySQL 8.0** làm database chính với thiết kế **multi-tenant** cho SME/Startup. Database được thiết kế normalized để đảm bảo tính toàn vẹn dữ liệu, hiệu suất truy vấn và **data isolation** giữa các công ty.

### 🎯 Thiết kế nguyên tắc
- **Multi-Tenant Architecture**: Mỗi company = 1 tenant, data isolation bằng `company_id`
- **Normalization**: 3NF để tránh redundancy
- **UUID Primary Keys**: Sử dụng VARCHAR(36) cho tất cả primary keys
- **Indexing**: Tối ưu cho các truy vấn thường xuyên, đặc biệt multi-tenant queries
- **Foreign Keys**: Đảm bảo referential integrity với UUID
- **Audit Fields**: Tracking tất cả thay đổi với full audit trail
- **Soft Delete**: Không xóa dữ liệu thực tế với deleted_at
- **RBAC**: Role-based access control với fine-grained permissions

### 🆔 **UUID IMPLEMENTATION STRATEGY**
- **Primary Keys**: VARCHAR(36) với UUID() function
- **Foreign Keys**: VARCHAR(36) references
- **Indexing**: Optimized cho UUID lookups
- **Performance**: Proper indexing cho UUID queries
- **Security**: UUIDs không thể guess được
- **Consistency**: Tất cả bảng đều dùng UUID làm primary key

## 🔄 **REFACTORING SUMMARY - PERSONAL TRACKER → SME ATS**

### ✅ **GIỮ LẠI (80% - Core structure tốt)**
- Companies, Users, Jobs, Skills, Interviews (với sửa đổi)
- RBAC (Roles, Permissions, Role_Permissions) - **GIỮ** (cần flexibility)
- Skills table - **GIỮ** (dynamic, user có thể thêm)
- System tables (User_Sessions, Audit_Logs, Notifications)
- Audit fields, Soft delete strategy

### ❌ **BỎ HOÀN TOÀN (10% - Personal tracker only)**
- **resumes** table → Thay bằng `applications.resume_file_path`
- **job_resumes** junction table → Không cần
- **user_skills** table → ATS không track HR skills
- **priorities** table → Không cần cho job postings
- **experience_levels** table → Đơn giản hóa, ghi tự do trong job description

### 🔄 **CHUYỂN SANG ENUM (Simplification)**
- **job_statuses** table → ENUM trong `jobs.job_status` (DRAFT, PUBLISHED, PAUSED, CLOSED, FILLED)
- **job_types** table → ENUM trong `jobs.job_type` (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE)
- **interview_types** table → ENUM trong `interviews.interview_type` (PHONE, VIDEO, IN_PERSON, TECHNICAL, HR, FINAL)
- **interview_statuses** table → ENUM trong `interviews.status` (SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED)
- **interview_results** table → ENUM trong `interviews.result` (PASSED, FAILED, PENDING)
- **notification_types** table → ENUM trong `notifications.type` (APPLICATION_RECEIVED, INTERVIEW_SCHEDULED, etc.)
- **notification_priorities** table → ENUM trong `notifications.priority` (HIGH, MEDIUM, LOW)
- **attachment_types** → ENUM trong `attachments.attachment_type` (RESUME, COVER_LETTER, CERTIFICATE, PORTFOLIO, OTHER)

### ➕ **THÊM MỚI (5% - ATS specific)**
- **applications** table (CORE ATS) - Candidates apply to jobs
- **application_status_history** table - Audit trail cho status workflow
- **comments** table - Team collaboration về candidates

### 🔄 **SỬA ĐỔI (5% - Adjust for multi-tenant)**
- **companies**: Thêm subscription fields (plan, limits, expires_at)
- **users**: Thêm `company_id` (CRITICAL - Multi-tenant key)
- **jobs**: Đổi semantic từ "job applied" → "job posting" (thêm job_status, published_at, applications_count)
- **interviews**: Đổi `job_id` → `application_id` (interview belongs to application)
- **notifications**: Thêm `company_id`, `application_id`
- **attachments**: Đổi `job_id` → `application_id` (CVs belong to applications)
- **audit_logs**: Thêm `company_id` (multi-tenant audit)
- **roles**: Đổi sang ATS roles (COMPANY_ADMIN, RECRUITER, HIRING_MANAGER, INTERVIEWER) - **GIỮ TABLE** (cần flexibility)
- **permissions**: Đổi sang ATS permissions (JOB_PUBLISH, APPLICATION_ASSIGN, etc.) - **GIỮ TABLE** (cần flexibility)
- **job_statuses**: Chuyển sang ENUM trong `jobs.job_status` (DRAFT, PUBLISHED, PAUSED, CLOSED, FILLED)
- **job_types**: Chuyển sang ENUM trong `jobs.job_type` (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE)
- **interview_types/statuses/results**: Chuyển sang ENUM trong `interviews` table
- **notification_types/priorities**: Chuyển sang ENUM trong `notifications` table

### 🔑 **CRITICAL CHANGES (Must implement first)**
1. **users.company_id** - Multi-tenant isolation key
2. **applications table** - Core ATS entity
3. **jobs semantic change** - From "applied" to "posting"
4. **interviews.application_id** - Link to applications, not jobs

## 📊 **ENUM VALUES REFERENCE**

Tất cả các ENUM values được sử dụng trong database:

### 1. Job Status ENUM (`jobs.job_status`)
- `DRAFT` - Nháp, chưa publish
- `PUBLISHED` - Đã publish, đang tuyển
- `PAUSED` - Tạm dừng tuyển
- `CLOSED` - Đã đóng tuyển
- `FILLED` - Đã tuyển đủ người

### 2. Job Type ENUM (`jobs.job_type`)
- `FULL_TIME` - Toàn thời gian
- `PART_TIME` - Bán thời gian
- `CONTRACT` - Hợp đồng
- `INTERNSHIP` - Thực tập
- `FREELANCE` - Freelance

### 3. Interview Type ENUM (`interviews.interview_type`)
- `PHONE` - Phỏng vấn qua điện thoại
- `VIDEO` - Phỏng vấn qua video call
- `IN_PERSON` - Phỏng vấn trực tiếp
- `TECHNICAL` - Phỏng vấn kỹ thuật
- `HR` - Phỏng vấn HR
- `FINAL` - Phỏng vấn cuối

### 4. Interview Status ENUM (`interviews.status`)
- `SCHEDULED` - Đã lên lịch
- `COMPLETED` - Đã hoàn thành
- `CANCELLED` - Đã hủy
- `RESCHEDULED` - Đã lên lịch lại

### 5. Interview Result ENUM (`interviews.result`)
- `PASSED` - Đạt
- `FAILED` - Không đạt
- `PENDING` - Chờ kết quả

### 6. Notification Type ENUM (`notifications.type`)
- `APPLICATION_RECEIVED` - Nhận được đơn ứng tuyển
- `INTERVIEW_SCHEDULED` - Đã lên lịch phỏng vấn
- `INTERVIEW_REMINDER` - Nhắc nhở phỏng vấn
- `STATUS_CHANGE` - Thay đổi trạng thái
- `DEADLINE_REMINDER` - Nhắc nhở deadline
- `COMMENT_ADDED` - Có comment mới
- `ASSIGNMENT_CHANGED` - Thay đổi người phụ trách

### 7. Notification Priority ENUM (`notifications.priority`)
- `HIGH` - Ưu tiên cao
- `MEDIUM` - Ưu tiên trung bình
- `LOW` - Ưu tiên thấp

### 8. Attachment Type ENUM (`attachments.attachment_type`)
- `RESUME` - CV/Resume
- `COVER_LETTER` - Thư xin việc
- `CERTIFICATE` - Chứng chỉ
- `PORTFOLIO` - Portfolio
- `OTHER` - Khác

### 9. Subscription Status ENUM (`company_subscriptions.status`)
- `PENDING` - Chờ thanh toán
- `ACTIVE` - Đang hoạt động
- `EXPIRED` - Đã hết hạn
- `CANCELLED` - Đã hủy

### 10. Payment Status ENUM (`payments.status`)
- `INIT` - Khởi tạo
- `SUCCESS` - Thành công
- `FAILED` - Thất bại

## 🏗️ Database Schema

### 1. Lookup Tables (Bảng tra cứu)

#### 1.1. Roles Table (Bảng vai trò)
```sql
CREATE TABLE roles (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID vai trò',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên vai trò',
    description VARCHAR(255) COMMENT 'Mô tả vai trò',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Vai trò đang hoạt động',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    
    -- Indexes
    INDEX idx_name (name),
    INDEX idx_is_active (is_active),
    INDEX idx_deleted_at (deleted_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 1.2. Permissions Table (Bảng quyền)
```sql
CREATE TABLE permissions (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID quyền',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Tên quyền',
    resource VARCHAR(100) NOT NULL COMMENT 'Tài nguyên',
    action VARCHAR(50) NOT NULL COMMENT 'Hành động (CREATE, READ, UPDATE, DELETE)',
    description VARCHAR(255) COMMENT 'Mô tả quyền',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Quyền đang hoạt động',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    
    -- Indexes
    INDEX idx_name (name),
    INDEX idx_resource_action (resource, action),
    INDEX idx_is_active (is_active),
    INDEX idx_deleted_at (deleted_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 1.3. Role Permissions Table (Bảng phân quyền - Many-to-Many)
```sql
CREATE TABLE role_permissions (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID role permission',
    role_id VARCHAR(36) NOT NULL COMMENT 'UUID vai trò',
    permission_id VARCHAR(36) NOT NULL COMMENT 'UUID quyền',
    
    -- Partial Audit Fields (Junction Table)
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT 'Đã xóa (soft delete)',
    
    -- Foreign Keys
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    
    -- Indexes
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id),
    INDEX idx_created_by (created_by),
    INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### ~~1.4. Job Statuses Table~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Job statuses là fixed values (DRAFT, PUBLISHED, CLOSED, FILLED), không cần lookup table. Dùng ENUM trong `jobs.job_status`.

#### 1.5. Application Statuses Table (Bảng trạng thái ứng tuyển) ✅

> **Lý do**: Application statuses cần metadata (display name, color, sort order) và có thể thay đổi workflow. Cần lookup table để linh hoạt.

```sql
CREATE TABLE application_statuses (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID application status',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên status (NEW, SCREENING, INTERVIEWING, OFFERED, HIRED, REJECTED)',
    display_name VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị',
    description VARCHAR(255) COMMENT 'Mô tả status',
    color VARCHAR(7) DEFAULT '#6B7280' COMMENT 'Màu sắc hiển thị',
    sort_order INT DEFAULT 0 COMMENT 'Thứ tự sắp xếp',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Status đang hoạt động',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Indexes
    INDEX idx_name (name),
    INDEX idx_sort_order (sort_order),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### ~~1.5. Job Types Table~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Job types là fixed values (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE), không cần lookup table. Dùng ENUM trong `jobs.job_type`.

#### 1.5. ~~Priorities Table~~ ❌ **REMOVED**
> **Lý do**: ATS không cần priority cho job postings. Đã bỏ hoàn toàn.

#### 1.6. ~~Experience Levels Table~~ ❌ **REMOVED**
> **Lý do**: Quá phức tạp cho ATS. HR có thể ghi tự do trong job description. Đã bỏ hoàn toàn.

#### ~~1.7. Interview Types Table~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Interview types là fixed values (PHONE, VIDEO, IN_PERSON, TECHNICAL, HR, FINAL), không cần lookup table. Dùng ENUM trong `interviews.interview_type`.

#### ~~1.8. Interview Statuses Table~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Interview statuses là fixed values (SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED), không cần lookup table. Dùng ENUM trong `interviews.status`.

#### ~~1.9. Interview Results Table~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Interview results là fixed values (PASSED, FAILED, PENDING), không cần lookup table. Dùng ENUM trong `interviews.result`.

#### ~~1.10. Notification Types Table~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Notification types là fixed values (APPLICATION_RECEIVED, INTERVIEW_SCHEDULED, STATUS_CHANGE, etc.), không cần lookup table. Dùng ENUM trong `notifications.type`.

#### ~~1.11. Notification Priorities Table~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Notification priorities là fixed values (HIGH, MEDIUM, LOW), không cần lookup table. Dùng ENUM trong `notifications.priority`.

### 2. Users Table (Bảng người dùng - Multi-Tenant)

> **🔑 CRITICAL**: Mỗi user thuộc về 1 company. `company_id` là multi-tenant key.
> 
> **💰 BILLABLE USERS**: Field `is_billable` phân biệt users tính vào plan limit:
> - `ADMIN`, `HR` → `is_billable = true` (tính vào quota)
> - `INTERVIEWER` → `is_billable = false` (không tính vào quota)
> 
> **🔐 AUTH FLOW**: B2B SaaS invite-only:
> - Email + Password (bắt buộc)
> - Email Verification (bắt buộc)
> - Admin tạo user → `email_verified = false`, `password = NULL` → Gửi invite email → User set password → `email_verified = true`
> - Không có Google OAuth (trừ enterprise SSO)

```sql
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID người dùng',
    company_id VARCHAR(36) NOT NULL COMMENT 'UUID công ty (Multi-tenant key)',
    email VARCHAR(255) NOT NULL COMMENT 'Email đăng nhập',
    password VARCHAR(255) COMMENT 'Mật khẩu đã hash (null khi user chưa set password qua invite)',
    first_name VARCHAR(100) NOT NULL COMMENT 'Tên',
    last_name VARCHAR(100) NOT NULL COMMENT 'Họ',
    phone VARCHAR(20) COMMENT 'Số điện thoại',
    avatar_url VARCHAR(500) COMMENT 'URL ảnh đại diện',
    avatar_public_id VARCHAR(255) COMMENT 'Cloudinary public ID ảnh đại diện',
    role_id VARCHAR(36) NOT NULL COMMENT 'UUID vai trò người dùng',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Trạng thái hoạt động',
    email_verified BOOLEAN DEFAULT FALSE COMMENT 'Email đã xác thực',
    is_billable BOOLEAN DEFAULT TRUE COMMENT 'Có tính vào quota plan hay không (Admin/HR = true, Interviewer = false)',
    last_login_at TIMESTAMP NULL COMMENT 'Lần đăng nhập cuối',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT,
    
    -- Indexes
    INDEX idx_company_id (company_id),
    INDEX idx_email (email),
    INDEX idx_role_id (role_id),
    INDEX idx_is_billable (is_billable),
    INDEX idx_created_at (created_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by),
    INDEX idx_deleted_at (deleted_at),
    
    -- Composite Indexes (Multi-tenant queries)
    UNIQUE KEY uk_company_email (company_id, email),
    INDEX idx_company_role_active (company_id, role_id, is_active),
    INDEX idx_company_billable_active (company_id, is_billable, is_active, deleted_at) COMMENT 'Index cho query COUNT billable users (plan limit check)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

> **💰 Plan Limit Check Query**:
> ```sql
> SELECT COUNT(*) 
> FROM users
> WHERE company_id = ?
>   AND is_billable = true
>   AND deleted_at IS NULL;
> ```
> 
> **Logic `is_billable`**:
> - `COMPANY_ADMIN`, `HR`, `RECRUITER` → `is_billable = true` (tính vào quota)
> - `INTERVIEWER` → `is_billable = false` (không tính vào quota)

### 3. Companies Table (Bảng công ty - Multi-Tenant)

> **🔑 CRITICAL**: Companies = Tenants trong multi-tenant ATS system  
> Subscription KHÔNG nằm trực tiếp trong bảng companies, mà tách ra thành các bảng riêng.

```sql
CREATE TABLE companies (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID công ty (Tenant ID)',
    name VARCHAR(255) NOT NULL COMMENT 'Tên công ty',
    website VARCHAR(500) COMMENT 'Website công ty',
    industry VARCHAR(100) COMMENT 'Lĩnh vực hoạt động',
    size VARCHAR(50) COMMENT 'Quy mô công ty (STARTUP, SMALL, MEDIUM, LARGE, ENTERPRISE)',
    location VARCHAR(255) COMMENT 'Địa chỉ công ty',
    description TEXT COMMENT 'Mô tả công ty',
    logo_url VARCHAR(500) COMMENT 'URL logo công ty',
    is_verified BOOLEAN DEFAULT FALSE COMMENT 'Công ty đã xác thực',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Company đang hoạt động',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    
    -- Indexes
    INDEX idx_name (name),
    INDEX idx_industry (industry),
    INDEX idx_size (size),
    INDEX idx_is_active (is_active),
    INDEX idx_created_at (created_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 3.1. Subscription Plans Table (Bảng gói subscription hệ thống)

> **Vai trò**: Catalog các gói của hệ thống (FREE, BASIC, PRO, ENTERPRISE, ...).  
> Chứa toàn bộ metadata: giá, thời lượng, giới hạn, feature flags (nếu cần mở rộng sau này).

```sql
CREATE TABLE subscription_plans (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID subscription plan',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT 'FREE, BASIC, PRO, ENTERPRISE, ...',
    name VARCHAR(100) NOT NULL COMMENT 'Tên gói hiển thị',
    price DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT 'Giá gói',
    duration_days INT NOT NULL COMMENT 'Thời lượng gói (ngày, 0 = không giới hạn)',
    
    max_jobs INT COMMENT 'Số job tối đa',
    max_users INT COMMENT 'Số user tối đa',
    max_applications INT COMMENT 'Số application tối đa',
    
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Gói đang hoạt động',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    
    INDEX idx_code (code),
    INDEX idx_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 3.2. Company Subscriptions Table (Bảng subscription theo thời gian cho company)

> **Vai trò**: Track lịch sử subscription theo thời gian cho từng company.  
> Đây mới là thứ company “đang dùng gói nào, trong khoảng thời gian nào”.

```sql
CREATE TABLE company_subscriptions (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID company subscription',
    company_id VARCHAR(36) NOT NULL COMMENT 'UUID công ty',
    plan_id VARCHAR(36) NOT NULL COMMENT 'UUID gói subscription',
    
    start_date TIMESTAMP NOT NULL COMMENT 'Ngày bắt đầu subscription',
    end_date TIMESTAMP NULL COMMENT 'Ngày kết thúc subscription',
    status ENUM('PENDING', 'ACTIVE', 'EXPIRED', 'CANCELLED') NOT NULL COMMENT 'Trạng thái subscription (PENDING, ACTIVE, EXPIRED, CANCELLED)',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT,
    FOREIGN KEY (plan_id) REFERENCES subscription_plans(id) ON DELETE RESTRICT,
    
    INDEX idx_company_status (company_id, status),
    INDEX idx_plan_id (plan_id),
    INDEX idx_dates (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 3.3. Payments Table (Bảng thanh toán - VNPAY, v.v.)

> **Vai trò**: Lưu các giao dịch thanh toán cho subscription theo từng company.  
> Không phụ thuộc vào gateway cụ thể, nhưng hiện tại chủ yếu dùng cho VNPAY.

```sql
CREATE TABLE payments (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID payment',
    company_id VARCHAR(36) NOT NULL COMMENT 'UUID công ty',
    company_subscription_id VARCHAR(36) NOT NULL COMMENT 'UUID company subscription',
    amount DECIMAL(15,2) NOT NULL COMMENT 'Số tiền thanh toán',
    currency VARCHAR(3) DEFAULT 'VND' COMMENT 'Đơn vị tiền tệ',
    gateway VARCHAR(50) NOT NULL COMMENT 'Cổng thanh toán (VD: VNPAY)',
    txn_ref VARCHAR(100) NOT NULL UNIQUE COMMENT 'Mã giao dịch phía gateway (vnp_TxnRef)',
    status ENUM('INIT', 'SUCCESS', 'FAILED') NOT NULL COMMENT 'Trạng thái thanh toán',
    paid_at TIMESTAMP NULL COMMENT 'Thời gian thanh toán thành công',
    metadata JSON NULL COMMENT 'Dữ liệu thêm (raw payload từ gateway)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT,
    FOREIGN KEY (company_subscription_id) REFERENCES company_subscriptions(id) ON DELETE RESTRICT,
    
    INDEX idx_payments_company (company_id),
    INDEX idx_payments_subscription (company_subscription_id),
    INDEX idx_payments_gateway_status (gateway, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```


### 4. Jobs Table (Bảng Job Postings - ATS)

> **🔄 SEMANTIC CHANGE**: Jobs = Job Postings (tin tuyển dụng), không phải "job applied"

```sql
CREATE TABLE jobs (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID job posting',
    user_id VARCHAR(36) NOT NULL COMMENT 'UUID HR/Recruiter tạo job',
    company_id VARCHAR(36) NOT NULL COMMENT 'UUID công ty (Multi-tenant)',
    title VARCHAR(255) NOT NULL COMMENT 'Tiêu đề tin tuyển dụng',
    position VARCHAR(255) NOT NULL COMMENT 'Vị trí cần tuyển',
    job_type ENUM('FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERNSHIP', 'FREELANCE') NOT NULL COMMENT 'Loại công việc',
    location VARCHAR(255) COMMENT 'Địa điểm làm việc',
    salary_min DECIMAL(12,2) COMMENT 'Mức lương tối thiểu',
    salary_max DECIMAL(12,2) COMMENT 'Mức lương tối đa',
    currency VARCHAR(3) DEFAULT 'USD' COMMENT 'Đơn vị tiền tệ',
    CONSTRAINT chk_currency CHECK (currency IN ('USD', 'VND', 'EUR', 'GBP', 'JPY')),
    job_status ENUM('DRAFT', 'PUBLISHED', 'PAUSED', 'CLOSED', 'FILLED') DEFAULT 'DRAFT' COMMENT 'Trạng thái posting',
    deadline_date DATE COMMENT 'Hạn nộp đơn',
    job_description TEXT COMMENT 'Mô tả công việc',
    requirements TEXT COMMENT 'Yêu cầu công việc',
    benefits TEXT COMMENT 'Quyền lợi',
    job_url VARCHAR(500) COMMENT 'URL tin tuyển dụng',
    is_remote BOOLEAN DEFAULT FALSE COMMENT 'Làm việc từ xa',
    published_at TIMESTAMP NULL COMMENT 'Ngày đăng tin',
    expires_at TIMESTAMP NULL COMMENT 'Ngày hết hạn',
    views_count INT DEFAULT 0 COMMENT 'Số lượt xem',
    applications_count INT DEFAULT 0 COMMENT 'Số lượng ứng tuyển',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT,
    
    -- Indexes
    INDEX idx_user_id (user_id),
    INDEX idx_company_id (company_id),
    INDEX idx_job_type (job_type),
    INDEX idx_job_status (job_status),
    INDEX idx_published_at (published_at),
    INDEX idx_deadline_date (deadline_date),
    INDEX idx_created_at (created_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by),
    INDEX idx_deleted_at (deleted_at),
    
    -- Composite Indexes (Multi-tenant + ATS queries)
    INDEX idx_company_status_published (company_id, job_status, published_at),
    INDEX idx_company_created (company_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 5. Skills Table (Bảng kỹ năng)

```sql
CREATE TABLE skills (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID kỹ năng',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT 'Tên kỹ năng',
    category VARCHAR(50) NOT NULL COMMENT 'Danh mục kỹ năng (PROGRAMMING, FRAMEWORK, DATABASE, TOOL, LANGUAGE, SOFT_SKILL, OTHER)',
    description TEXT COMMENT 'Mô tả kỹ năng',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Kỹ năng đang hoạt động',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    
    -- Indexes
    INDEX idx_name (name),
    INDEX idx_category (category),
    INDEX idx_is_active (is_active),
    INDEX idx_deleted_at (deleted_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 6. Job Skills Table (Bảng kỹ năng công việc - Many-to-Many)

```sql
CREATE TABLE job_skills (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID job skill',
    job_id VARCHAR(36) NOT NULL COMMENT 'UUID công việc',
    skill_id VARCHAR(36) NOT NULL COMMENT 'UUID kỹ năng',
    is_required BOOLEAN DEFAULT TRUE COMMENT 'Kỹ năng bắt buộc',
    proficiency_level VARCHAR(50) COMMENT 'Mức độ thành thạo yêu cầu (BEGINNER, INTERMEDIATE, ADVANCED, EXPERT)',
    CONSTRAINT chk_job_skill_proficiency CHECK (proficiency_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT')),
    
    -- Partial Audit Fields (Junction Table)
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT 'Đã xóa (soft delete)',
    
    -- Foreign Keys
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    
    UNIQUE KEY uk_job_skill (job_id, skill_id),
    INDEX idx_job_id (job_id),
    INDEX idx_skill_id (skill_id),
    INDEX idx_created_by (created_by),
    INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 7. ~~User Skills Table~~ ❌ **REMOVED**

> **Lý do**: ATS không track skills của HR/Recruiter. Chỉ cần track skills yêu cầu của job (job_skills). Candidates skills nằm trong CV text.

### 8. Applications Table (Bảng ứng tuyển - CORE ATS) ➕

> **🔑 CORE**: Thay thế hoàn toàn bảng resumes. Đây là core của ATS system.

```sql
CREATE TABLE applications (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID ứng tuyển',
    job_id VARCHAR(36) NOT NULL COMMENT 'UUID công việc',
    company_id VARCHAR(36) NOT NULL COMMENT 'UUID công ty (Multi-tenant)',
    
    -- Candidate Info (từ CV/Email hoặc Candidate Self-Service Portal)
    candidate_name VARCHAR(255) NOT NULL COMMENT 'Tên ứng viên',
    candidate_email VARCHAR(255) NOT NULL COMMENT 'Email ứng viên',
    candidate_phone VARCHAR(20) COMMENT 'Số điện thoại ứng viên',
    application_token VARCHAR(100) UNIQUE COMMENT 'Token để candidate track status (cho public API, không cần login)',
    
    -- Application Status Workflow
    status_id VARCHAR(36) NOT NULL COMMENT 'UUID trạng thái ứng tuyển (FK to application_statuses)',
    source VARCHAR(100) COMMENT 'Nguồn ứng viên (Email, LinkedIn, Referral)',
    applied_date DATE NOT NULL COMMENT 'Ngày nộp đơn',
    
    -- CV/Resume
    resume_file_path VARCHAR(500) COMMENT 'Đường dẫn CV trên Dropbox',
    cover_letter TEXT COMMENT 'Cover letter',
    
    -- HR Notes
    notes TEXT COMMENT 'Ghi chú của HR',
    rating INT CHECK (rating >= 1 AND rating <= 5) COMMENT 'Đánh giá ứng viên (1-5)',
    
    -- Assignment
    assigned_to VARCHAR(36) COMMENT 'HR/Recruiter được assign (FK to users)',
    
    -- Document Upload Control
    allow_additional_uploads BOOLEAN DEFAULT FALSE COMMENT 'Cho phép candidate upload thêm documents (chỉ khi HR yêu cầu)',
    
    -- CV Scoring & Matching
    match_score INT COMMENT 'Điểm khớp giữa CV và JD (0-100), tính tự động khi upload CV (sync processing, 2-3 giây). NULL nếu parsing failed hoặc chưa có CV',
    extracted_text TEXT COMMENT 'Text đã extract từ CV (PDF parsing)',
    matched_skills JSON COMMENT 'Breakdown skills matched: {matchedRequired: [], missingRequired: [], matchedOptional: [], missingOptional: []}',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (NULL nếu candidate tự apply qua public API)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    
    -- Foreign Keys
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT,
    FOREIGN KEY (status_id) REFERENCES application_statuses(id) ON DELETE RESTRICT,
    FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE SET NULL,
    
    -- Indexes
    INDEX idx_job_id (job_id),
    INDEX idx_company_id (company_id),
    INDEX idx_candidate_email (candidate_email),
    INDEX idx_application_token (application_token), -- For public API status tracking
    INDEX idx_status_id (status_id),
    INDEX idx_assigned_to (assigned_to),
    INDEX idx_applied_date (applied_date),
    INDEX idx_created_at (created_at),
    INDEX idx_deleted_at (deleted_at),
    
    -- Composite Indexes (Multi-tenant + ATS queries)
    INDEX idx_company_job_status (company_id, job_id, status_id),
    INDEX idx_assigned_status (assigned_to, status_id),
    INDEX idx_company_status_date (company_id, status_id, applied_date),
    INDEX idx_match_score (match_score) COMMENT 'Index cho filter/sort by match score',
    INDEX idx_job_match_score (job_id, match_score) COMMENT 'Index cho query applications by job với sort by match score'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 8.1. Application Status History Table ➕

```sql
CREATE TABLE application_status_history (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    application_id VARCHAR(36) NOT NULL COMMENT 'UUID ứng tuyển',
    from_status_id VARCHAR(36) COMMENT 'UUID trạng thái cũ (FK to application_statuses)',
    to_status_id VARCHAR(36) NOT NULL COMMENT 'UUID trạng thái mới (FK to application_statuses)',
    changed_by VARCHAR(36) NOT NULL COMMENT 'Người thay đổi (FK to users)',
    notes TEXT COMMENT 'Ghi chú',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    FOREIGN KEY (from_status_id) REFERENCES application_statuses(id) ON DELETE SET NULL,
    FOREIGN KEY (to_status_id) REFERENCES application_statuses(id) ON DELETE RESTRICT,
    FOREIGN KEY (changed_by) REFERENCES users(id) ON DELETE SET NULL,
    
    INDEX idx_application_id (application_id),
    INDEX idx_from_status_id (from_status_id),
    INDEX idx_to_status_id (to_status_id),
    INDEX idx_changed_by (changed_by),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 8.2. Comments Table ➕

```sql
CREATE TABLE comments (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    application_id VARCHAR(36) NOT NULL COMMENT 'UUID ứng tuyển',
    user_id VARCHAR(36) NOT NULL COMMENT 'Người comment (HR/Recruiter)',
    comment_text TEXT NOT NULL COMMENT 'Nội dung comment',
    is_internal BOOLEAN DEFAULT TRUE COMMENT 'Comment nội bộ (không gửi candidate)',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_application_id (application_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 9. Interviews Table (Bảng phỏng vấn - ATS) 🔄

> **🔄 SEMANTIC CHANGE**: Interview belongs to APPLICATION, không phải job
> 
> **👥 MULTIPLE INTERVIEWERS**: Một interview có thể có nhiều interviewers (many-to-many qua bảng `interview_interviewers`).
> 
> **⏰ SCHEDULE VALIDATION**: Validate trùng lịch cho từng interviewer (không phải cho interview):
> - Một interviewer (user với role = INTERVIEWER) không thể có 2 interviews cùng thời gian (trùng `scheduled_date` và `duration_minutes`)
> - Validate khi tạo/cập nhật interview: Check tất cả interviewers trong `interview_interviewers` table
> - Chỉ validate cho interviews có status = `SCHEDULED` hoặc `RESCHEDULED`
> - Validate overlap: Nếu interview A từ 10:00-11:00 và interview B từ 10:30-11:30 → Trùng lịch (overlap)

```sql
CREATE TABLE interviews (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID phỏng vấn',
    application_id VARCHAR(36) NOT NULL COMMENT 'UUID ứng tuyển',
    job_id VARCHAR(36) NOT NULL COMMENT 'UUID công việc (reference)',
    company_id VARCHAR(36) NOT NULL COMMENT 'UUID công ty (Multi-tenant)',
    round_number INT NOT NULL COMMENT 'Số vòng phỏng vấn',
    interview_type ENUM('PHONE', 'VIDEO', 'IN_PERSON', 'TECHNICAL', 'HR', 'FINAL') NOT NULL COMMENT 'Loại phỏng vấn',
    scheduled_date TIMESTAMP NOT NULL COMMENT 'Thời gian phỏng vấn dự kiến',
    actual_date TIMESTAMP NULL COMMENT 'Thời gian phỏng vấn thực tế',
    duration_minutes INT COMMENT 'Thời lượng phỏng vấn (phút)',
    status ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED', 'RESCHEDULED') NOT NULL DEFAULT 'SCHEDULED' COMMENT 'Trạng thái phỏng vấn',
    result ENUM('PASSED', 'FAILED', 'PENDING') NULL COMMENT 'Kết quả phỏng vấn',
    feedback TEXT COMMENT 'Phản hồi từ nhà tuyển dụng',
    notes TEXT COMMENT 'Ghi chú cá nhân',
    questions_asked TEXT COMMENT 'Câu hỏi được hỏi',
    answers_given TEXT COMMENT 'Câu trả lời đã đưa ra',
    rating INT CHECK (rating >= 1 AND rating <= 5) COMMENT 'Đánh giá chất lượng phỏng vấn (1-5)',
    
    -- ATS Specific Fields
    meeting_link VARCHAR(500) COMMENT 'Link Google Meet/Zoom',
    location VARCHAR(255) COMMENT 'Địa điểm (nếu onsite)',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE RESTRICT,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT,
    
    -- Indexes
    INDEX idx_application_id (application_id),
    INDEX idx_job_id (job_id),
    INDEX idx_company_id (company_id),
    INDEX idx_interview_type (interview_type),
    INDEX idx_status (status),
    INDEX idx_result (result),
    INDEX idx_scheduled_date (scheduled_date),
    INDEX idx_created_at (created_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by),
    INDEX idx_deleted_at (deleted_at),
    
    -- Composite Indexes
    INDEX idx_company_scheduled_status (company_id, scheduled_date, status),
    INDEX idx_application_round (application_id, round_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 9.1. Interview Interviewers Table (Junction Table - Many-to-Many) ➕

> **🔑 CRITICAL**: Bảng junction để support nhiều interviewers cho 1 interview.
> 
> **⏰ SCHEDULE VALIDATION**: Validate trùng lịch dựa trên bảng này:
> - Query: Check xem interviewer có interview nào khác trong khoảng thời gian `scheduled_date` ± `duration_minutes` không
> - Chỉ validate cho interviews có status = `SCHEDULED` hoặc `RESCHEDULED`
> - Validate overlap: Nếu interview A từ 10:00-11:00 và interview B từ 10:30-11:30 → Trùng lịch (overlap)

```sql
CREATE TABLE interview_interviewers (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID interview interviewer',
    interview_id VARCHAR(36) NOT NULL COMMENT 'UUID phỏng vấn',
    interviewer_id VARCHAR(36) NOT NULL COMMENT 'UUID interviewer (FK to users, role = INTERVIEWER)',
    company_id VARCHAR(36) NOT NULL COMMENT 'UUID công ty (Multi-tenant)',
    is_primary BOOLEAN DEFAULT FALSE COMMENT 'Interviewer chính (primary interviewer)',
    
    -- Partial Audit Fields (Junction Table)
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT 'Đã xóa (soft delete)',
    
    -- Foreign Keys
    FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
    FOREIGN KEY (interviewer_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT,
    
    -- Indexes
    UNIQUE KEY uk_interview_interviewer (interview_id, interviewer_id),
    INDEX idx_interview_id (interview_id),
    INDEX idx_interviewer_id (interviewer_id),
    INDEX idx_company_id (company_id),
    INDEX idx_is_primary (is_primary),
    INDEX idx_created_by (created_by),
    INDEX idx_is_deleted (is_deleted),
    
    -- Composite Index for Schedule Validation
    INDEX idx_interviewer_schedule_validation (interviewer_id, is_deleted) COMMENT 'Index cho schedule validation query'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

> **💰 SCHEDULE VALIDATION QUERY** (Check trùng lịch cho interviewer):
> ```sql
> -- Check xem interviewer có interview nào khác trùng lịch không
> SELECT COUNT(*) 
> FROM interview_interviewers ii
> INNER JOIN interviews i ON ii.interview_id = i.id
> WHERE ii.interviewer_id = ?  -- Interviewer cần check
>   AND ii.interview_id != ?    -- Exclude current interview (khi update)
>   AND ii.is_deleted = false
>   AND i.deleted_at IS NULL
>   AND i.status IN ('SCHEDULED', 'RESCHEDULED')
>   AND (
>     -- Check overlap: new interview overlaps with existing interview
>     -- Case 1: New interview starts before existing ends
>     (i.scheduled_date <= ? AND DATE_ADD(i.scheduled_date, INTERVAL i.duration_minutes MINUTE) > ?)
>     OR
>     -- Case 2: New interview ends after existing starts
>     (? < DATE_ADD(i.scheduled_date, INTERVAL i.duration_minutes MINUTE) AND DATE_ADD(?, INTERVAL ? MINUTE) >= i.scheduled_date)
>   );
> ```
> 
> **Parameters**:
> - `?` (1st): `interviewer_id` cần check
> - `?` (2nd): `interview_id` hiện tại (khi update, exclude chính nó)
> - `?` (3rd, 4th): `new_scheduled_date` (start time của interview mới)
> - `?` (5th, 6th): `new_scheduled_date` và `new_duration_minutes` (end time của interview mới)
> 
> **Logic**:
> - Nếu COUNT > 0 → Interviewer đã có interview khác trùng lịch → Reject
> - Validate cho TẤT CẢ interviewers trong array khi tạo/cập nhật interview
> - Ví dụ: Interview A (10:00-11:00) và Interview B (10:30-11:30) → Overlap → Reject

### ~~9. Job Resumes Table~~ ❌ **REMOVED**

> **Lý do**: Modern ATS không cần bảng riêng cho resumes. CVs được lưu trong `attachments` table (candidates tự upload hoặc HR upload thủ công).

### ~~10. Resumes Table~~ ❌ **REMOVED**

> **Lý do**: Thay thế bằng `applications.resume_file_path` và `attachments` table. Candidates tự upload CV qua public API, hoặc HR upload thủ công khi nhận CV qua email.

### 10. Attachments Table (Bảng file đính kèm - ATS) 🔄

> **🔄 SEMANTIC CHANGE**: Attachments belong to applications (CVs, certificates), không phải jobs

```sql
CREATE TABLE attachments (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID file đính kèm',
    application_id VARCHAR(36) NULL COMMENT 'UUID ứng tuyển',
    company_id VARCHAR(36) NOT NULL COMMENT 'UUID công ty (Multi-tenant)',
    user_id VARCHAR(36) NULL COMMENT 'UUID người dùng upload (NULL nếu candidate upload qua public API)',
    filename VARCHAR(255) NOT NULL COMMENT 'Tên file',
    original_filename VARCHAR(255) NOT NULL COMMENT 'Tên file gốc',
    file_path VARCHAR(500) NOT NULL COMMENT 'Đường dẫn file trên Dropbox',
    file_size BIGINT NOT NULL COMMENT 'Kích thước file (bytes)',
    file_type VARCHAR(100) NOT NULL COMMENT 'Loại file',
    attachment_type ENUM('RESUME', 'COVER_LETTER', 'CERTIFICATE', 'PORTFOLIO', 'OTHER') NOT NULL COMMENT 'Loại file đính kèm',
    CONSTRAINT chk_attachment_type CHECK (attachment_type IN ('RESUME', 'COVER_LETTER', 'CERTIFICATE', 'PORTFOLIO', 'OTHER')),
    description TEXT COMMENT 'Mô tả file',
    is_public BOOLEAN DEFAULT FALSE COMMENT 'File công khai',
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian upload',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL, -- NULL allowed for public candidate uploads
    
    -- Indexes
    INDEX idx_application_id (application_id),
    INDEX idx_company_id (company_id),
    INDEX idx_user_id (user_id),
    INDEX idx_attachment_type (attachment_type),
    INDEX idx_uploaded_at (uploaded_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 11. Notifications Table (Bảng thông báo - ATS) 🔄

> **🔄 SEMANTIC CHANGE**: Thêm company_id và application_id cho multi-tenant

```sql
CREATE TABLE notifications (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID thông báo',
    user_id VARCHAR(36) NOT NULL COMMENT 'UUID người dùng nhận thông báo',
    company_id VARCHAR(36) NOT NULL COMMENT 'UUID công ty (Multi-tenant)',
    job_id VARCHAR(36) NULL COMMENT 'UUID công việc liên quan (nullable)',
    application_id VARCHAR(36) NULL COMMENT 'UUID ứng tuyển liên quan (nullable)',
    type ENUM('APPLICATION_RECEIVED', 'INTERVIEW_SCHEDULED', 'INTERVIEW_REMINDER', 'STATUS_CHANGE', 'DEADLINE_REMINDER', 'COMMENT_ADDED', 'ASSIGNMENT_CHANGED') NOT NULL COMMENT 'Loại thông báo',
    title VARCHAR(255) NOT NULL COMMENT 'Tiêu đề thông báo',
    message TEXT NOT NULL COMMENT 'Nội dung thông báo',
    is_read BOOLEAN DEFAULT FALSE COMMENT 'Đã đọc chưa',
    is_sent BOOLEAN DEFAULT FALSE COMMENT 'Đã gửi chưa',
    sent_at TIMESTAMP NULL COMMENT 'Thời gian gửi',
    scheduled_at TIMESTAMP NULL COMMENT 'Thời gian lên lịch gửi',
    priority ENUM('HIGH', 'MEDIUM', 'LOW') DEFAULT 'MEDIUM' COMMENT 'Độ ưu tiên',
    metadata JSON COMMENT 'Dữ liệu bổ sung (JSON)',
    
    -- System Table - Only created_at, updated_at (no user tracking)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    
    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE SET NULL,
    FOREIGN KEY (application_id) REFERENCES applications(id) ON DELETE SET NULL,
    
    INDEX idx_user_id (user_id),
    INDEX idx_company_id (company_id),
    INDEX idx_job_id (job_id),
    INDEX idx_application_id (application_id),
    INDEX idx_type (type),
    INDEX idx_priority (priority),
    INDEX idx_is_read (is_read),
    INDEX idx_is_sent (is_sent),
    INDEX idx_scheduled_at (scheduled_at),
    INDEX idx_created_at (created_at),
    
    INDEX idx_user_unread (user_id, is_read),
    INDEX idx_company_unread (company_id, is_read),
    INDEX idx_scheduled_unsent (scheduled_at, is_sent)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

    ### 12. User Sessions Table (Bảng phiên đăng nhập)

```sql
CREATE TABLE user_sessions (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID session',
    user_id VARCHAR(36) NOT NULL COMMENT 'UUID người dùng',
    session_token VARCHAR(500) NOT NULL UNIQUE COMMENT 'Token phiên đăng nhập',
    refresh_token VARCHAR(500) NOT NULL UNIQUE COMMENT 'Refresh token',
    device_info JSON COMMENT 'Thông tin thiết bị (JSON)',
    ip_address VARCHAR(45) COMMENT 'Địa chỉ IP',
    user_agent TEXT COMMENT 'User agent string',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Phiên đang hoạt động',
    expires_at TIMESTAMP NOT NULL COMMENT 'Thời gian hết hạn',
    last_used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Lần sử dụng cuối',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    
    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Indexes
    INDEX idx_user_id (user_id),
    INDEX idx_session_token (session_token),
    INDEX idx_refresh_token (refresh_token),
    INDEX idx_is_active (is_active),
    INDEX idx_expires_at (expires_at),
    INDEX idx_last_used_at (last_used_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 13. Audit Logs Table (Bảng log audit - ATS) 🔄

> **🔄 SEMANTIC CHANGE**: Thêm company_id cho multi-tenant audit

```sql
CREATE TABLE audit_logs (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID audit log',
    user_id VARCHAR(36) NULL COMMENT 'UUID người dùng thực hiện (nullable cho system actions)',
    company_id VARCHAR(36) NULL COMMENT 'UUID công ty (Multi-tenant)',
    entity_type VARCHAR(100) NOT NULL COMMENT 'Loại entity (User, Job, Application, Company, etc.)',
    entity_id VARCHAR(36) NOT NULL COMMENT 'UUID của entity',
    action VARCHAR(50) NOT NULL COMMENT 'Hành động thực hiện (CREATE, UPDATE, DELETE, LOGIN, LOGOUT, UPLOAD, DOWNLOAD)',
    old_values JSON COMMENT 'Giá trị cũ (JSON)',
    new_values JSON COMMENT 'Giá trị mới (JSON)',
    ip_address VARCHAR(45) COMMENT 'Địa chỉ IP',
    user_agent TEXT COMMENT 'User agent string',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    
    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE SET NULL,
    
    -- Indexes
    INDEX idx_user_id (user_id),
    INDEX idx_company_id (company_id),
    INDEX idx_entity_type (entity_type),
    INDEX idx_entity_id (entity_id),
    INDEX idx_action (action),
    INDEX idx_created_at (created_at),
    
    INDEX idx_entity_action (entity_type, entity_id, action),
    INDEX idx_user_action (user_id, action),
    INDEX idx_company_entity (company_id, entity_type, entity_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### ~~7. User Skills Table~~ ❌ **REMOVED**

> **Lý do**: ATS không track skills của HR/Recruiter. Chỉ cần track skills yêu cầu của job (job_skills). Candidates skills nằm trong CV text.

## 🔍 Indexes Strategy

### Primary Indexes
- **Primary Keys**: Tất cả bảng đều có auto-increment primary key
- **Foreign Keys**: Index cho tất cả foreign key constraints
- **Unique Constraints**: Email, Google ID, session tokens

### Performance Indexes
- **Composite Indexes**: Cho các truy vấn phức tạp
- **Date Indexes**: Cho filtering và sorting theo thời gian
- **Status Indexes**: Cho filtering theo trạng thái
- **Search Indexes**: Cho full-text search

### Multi-Tenant Query Optimization Indexes
```sql
-- Applications (Core ATS queries)
CREATE INDEX idx_app_company_status_date ON applications(company_id, status, applied_date);
CREATE INDEX idx_app_assigned_status ON applications(assigned_to, status);
CREATE INDEX idx_app_company_job_status ON applications(company_id, job_id, status);

-- Job Postings (Multi-tenant)
CREATE INDEX idx_jobs_company_status_published ON jobs(company_id, job_status, published_at);
CREATE INDEX idx_jobs_company_created ON jobs(company_id, created_at);

-- Interviews (Multi-tenant)
CREATE INDEX idx_interviews_company_scheduled ON interviews(company_id, scheduled_date, status);
CREATE INDEX idx_interviews_application_round ON interviews(application_id, round_number);

-- Users (Multi-tenant)
CREATE INDEX idx_users_company_role_active ON users(company_id, role_id, is_active);

-- Notifications (Multi-tenant)
CREATE INDEX idx_notifications_company_unread ON notifications(company_id, is_read);
CREATE INDEX idx_notifications_scheduled_unsent ON notifications(scheduled_at, is_sent);

-- Audit Logs (Multi-tenant)
CREATE INDEX idx_audit_company_entity ON audit_logs(company_id, entity_type, entity_id);
```

## 🔄 Database Relationships

### Entity Relationship Diagram (ATS)
```
Companies (1) ──── (N) Users (Multi-tenant)
Companies (1) ──── (N) Jobs (Job Postings)
Companies (1) ──── (N) Applications
Companies (1) ──── (N) Interviews
Companies (1) ──── (N) Notifications
Companies (1) ──── (N) Attachments
Companies (1) ──── (N) Audit_Logs

Users (1) ──── (N) Jobs (HR/Recruiter creates)
Users (1) ──── (N) Applications (assigned_to)
Users (1) ──── (N) Interviews (interviewer)
Users (1) ──── (N) Comments
Users (1) ──── (N) Notifications
Users (1) ──── (N) User_Sessions
Users (1) ──── (N) Audit_Logs

Jobs (1) ──── (N) Applications (Candidates apply)
Jobs (1) ──── (N) Job_Skills

Applications (1) ──── (N) Interviews (Interview rounds)
Applications (1) ──── (N) Comments
Applications (1) ──── (N) Attachments (CVs, certificates)
Applications (1) ──── (N) Application_Status_History

Skills (1) ──── (N) Job_Skills
```

## 📊 Sample Data

### Initial Lookup Data

#### Roles Data (ATS Roles)
```sql
INSERT INTO roles (name, description) VALUES
('COMPANY_ADMIN', 'Company Administrator - Full control within company'),
('RECRUITER', 'Recruiter - Manage jobs and applications'),
('HIRING_MANAGER', 'Hiring Manager - View and comment on applications'),
('INTERVIEWER', 'Interviewer - Schedule and conduct interviews'),
('SYSTEM_ADMIN', 'System Admin - Manage all companies');
```

#### Permissions Data (ATS Permissions)
```sql
INSERT INTO permissions (name, resource, action, description) VALUES
-- Job Posting Permissions
('JOB_CREATE', 'JOB', 'CREATE', 'Create job postings'),
('JOB_EDIT', 'JOB', 'UPDATE', 'Edit job postings'),
('JOB_DELETE', 'JOB', 'DELETE', 'Delete job postings'),
('JOB_PUBLISH', 'JOB', 'PUBLISH', 'Publish job postings'),
('JOB_VIEW', 'JOB', 'READ', 'View job postings'),
-- Application Permissions
('APPLICATION_VIEW', 'APPLICATION', 'READ', 'View applications'),
('APPLICATION_CREATE', 'APPLICATION', 'CREATE', 'Create applications'),
('APPLICATION_UPDATE', 'APPLICATION', 'UPDATE', 'Update application status'),
('APPLICATION_DELETE', 'APPLICATION', 'DELETE', 'Delete applications'),
('APPLICATION_ASSIGN', 'APPLICATION', 'ASSIGN', 'Assign applications to recruiters'),
-- Interview Permissions
('INTERVIEW_SCHEDULE', 'INTERVIEW', 'CREATE', 'Schedule interviews'),
('INTERVIEW_EDIT', 'INTERVIEW', 'UPDATE', 'Edit interview details'),
('INTERVIEW_CANCEL', 'INTERVIEW', 'DELETE', 'Cancel interviews'),
('INTERVIEW_VIEW', 'INTERVIEW', 'READ', 'View interview details'),
-- Comment Permissions
('COMMENT_CREATE', 'COMMENT', 'CREATE', 'Add comments'),
('COMMENT_VIEW', 'COMMENT', 'READ', 'View comments'),
('COMMENT_DELETE', 'COMMENT', 'DELETE', 'Delete comments'),
-- User Management
('USER_INVITE', 'USER', 'CREATE', 'Invite team members'),
('USER_MANAGE', 'USER', 'UPDATE', 'Manage team members'),
('USER_DELETE', 'USER', 'DELETE', 'Remove team members');
```

#### Application Statuses Data (ATS Workflow) ✅
```sql
INSERT INTO application_statuses (name, display_name, description, color, sort_order) VALUES
('NEW', 'Mới', 'Ứng viên vừa nộp đơn', '#3B82F6', 1),
('SCREENING', 'Sàng lọc', 'Đang sàng lọc hồ sơ', '#8B5CF6', 2),
('INTERVIEWING', 'Phỏng vấn', 'Đang trong quá trình phỏng vấn', '#F59E0B', 3),
('OFFERED', 'Đã đề xuất', 'Đã gửi offer cho ứng viên', '#10B981', 4),
('HIRED', 'Đã tuyển', 'Ứng viên đã được tuyển', '#059669', 5),
('REJECTED', 'Từ chối', 'Ứng viên bị từ chối', '#EF4444', 6);
```

#### ~~Job Statuses Data~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Job statuses giờ là ENUM trong `jobs.job_status` (DRAFT, PUBLISHED, PAUSED, CLOSED, FILLED). Không cần seed data.

#### ~~Job Types Data~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Job types giờ là ENUM trong `jobs.job_type` (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE). Không cần seed data.

#### ~~Priorities Data~~ ❌ **REMOVED**

#### ~~Experience Levels Data~~ ❌ **REMOVED**

#### ~~Interview Types Data~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Interview types giờ là ENUM trong `interviews.interview_type` (PHONE, VIDEO, IN_PERSON, TECHNICAL, HR, FINAL). Không cần seed data.

#### ~~Interview Statuses Data~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Interview statuses giờ là ENUM trong `interviews.status` (SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED). Không cần seed data.

#### ~~Interview Results Data~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Interview results giờ là ENUM trong `interviews.result` (PASSED, FAILED, PENDING). Không cần seed data.

#### ~~Notification Types Data~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Notification types giờ là ENUM trong `notifications.type` (APPLICATION_RECEIVED, INTERVIEW_SCHEDULED, INTERVIEW_REMINDER, STATUS_CHANGE, DEADLINE_REMINDER, COMMENT_ADDED, ASSIGNMENT_CHANGED). Không cần seed data.

#### ~~Notification Priorities Data~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Notification priorities giờ là ENUM trong `notifications.priority` (HIGH, MEDIUM, LOW). Không cần seed data.

### Initial Skills Data
```sql
INSERT INTO skills (name, category, created_by) VALUES
('Java', 'PROGRAMMING', 1),
('Spring Boot', 'FRAMEWORK', 1),
('React', 'FRAMEWORK', 1),
('TypeScript', 'PROGRAMMING', 1),
('MySQL', 'DATABASE', 1),
('Docker', 'TOOL', 1),
('Git', 'TOOL', 1),
('English', 'LANGUAGE', 1),
('Communication', 'SOFT_SKILL', 1),
('Problem Solving', 'SOFT_SKILL', 1);
```

### Sample Company Data
```sql
INSERT INTO companies (name, website, industry, size, location, created_by) VALUES
('Google', 'https://google.com', 'Technology', 'LARGE', 'Mountain View, CA', 1),
('Microsoft', 'https://microsoft.com', 'Technology', 'LARGE', 'Redmond, WA', 1),
('Amazon', 'https://amazon.com', 'E-commerce', 'LARGE', 'Seattle, WA', 1),
('Netflix', 'https://netflix.com', 'Entertainment', 'LARGE', 'Los Gatos, CA', 1),
('Spotify', 'https://spotify.com', 'Music', 'MEDIUM', 'Stockholm, Sweden', 1);
```

## 🚀 Database Migration Strategy

### Version Control
- **Liquibase**: Database migration tool với XML/JSON/YAML support
- **Change Sets**: Atomic database changes
- **Rollback Support**: Automatic rollback capabilities
- **Context Support**: Environment-specific changes

### Migration Files Structure
```
src/main/resources/db/changelog/
├── db.changelog-master.xml
├── changesets/
│   ├── 001-create-lookup-tables.xml
│   ├── 002-create-users-table.xml
│   ├── 003-create-companies-table.xml
│   ├── 004-create-jobs-table.xml
│   ├── 005-create-skills-table.xml
│   ├── 006-create-relationships.xml
│   ├── 007-create-interviews-table.xml
│   ├── 008-create-resumes-table.xml
│   ├── 009-create-attachments-table.xml
│   ├── 010-create-notifications-table.xml
│   ├── 011-create-sessions-table.xml
│   ├── 012-create-audit-logs-table.xml
│   └── 013-insert-initial-data.xml
└── rollback/
    ├── rollback-001.xml
    └── rollback-002.xml
```

## 🔧 Database Configuration

### Application Properties
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jobtracker?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:jobtracker}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 25
        order_inserts: true
        order_updates: true
        batch_versioned_data: true
  
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.xml
    contexts: default
```

## 📈 Performance Monitoring

### Query Performance
- **Slow Query Log**: MySQL slow query logging
- **EXPLAIN**: Query execution plan analysis
- **Index Usage**: Monitor index effectiveness
- **Connection Pool**: HikariCP metrics

### Database Metrics
- **Connection Count**: Active/idle connections
- **Query Execution Time**: Average response time
- **Lock Wait Time**: Deadlock detection
- **Buffer Pool Hit Rate**: Cache efficiency

## 🔒 Security Considerations

### Data Protection
- **Encryption at Rest**: MySQL encryption
- **Encryption in Transit**: SSL/TLS connections
- **Password Hashing**: BCrypt with salt
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries

### Access Control
- **Database User**: Limited privileges
- **Connection Security**: IP whitelisting
- **Audit Logging**: All database changes tracked
- **Backup Encryption**: Encrypted backups

## 📊 Audit Strategy Summary

### ✅ **FULL AUDIT FIELDS** (created_by, updated_by, created_at, updated_at):
- **Lookup Tables** (chỉ giữ 2 bảng): roles, permissions (cần flexibility cho RBAC)
- **Core Business Entities**: users, companies, jobs, skills, interviews, attachments, applications, comments

### ⚠️ **PARTIAL AUDIT FIELDS** (created_by, created_at, updated_at):
- **Junction Tables**: job_skills
- **Lý do**: Junction tables ít khi update, không cần track updated_by

### 🔧 **SYSTEM TABLES** (created_at, updated_at only):
- **System Generated**: notifications, user_sessions, audit_logs
- **Lý do**: System generated, không cần user tracking

### 🗑️ **SOFT DELETE STRATEGY - CHI TIẾT LÝ DO:**

#### **1. deleted_at (TIMESTAMP) - Business Entities & Lookup Tables:**
**Bảng sử dụng**: 
- **Business Entities**: users, companies, jobs, skills, interviews, resumes, attachments
- **Lookup Tables**: roles, permissions (chỉ giữ 2 bảng này vì cần flexibility cho RBAC)

**Lý do sử dụng TIMESTAMP:**

**Cho Business Entities:**
- **Compliance Requirements**: Cần biết chính xác khi nào dữ liệu bị xóa
- **Audit Trail**: Tracking thời gian xóa cho forensic analysis
- **Legal Requirements**: GDPR, SOX yêu cầu timestamp cho data deletion
- **Reporting**: Có thể tạo reports về data lifecycle
- **Recovery**: Có thể restore data trong khoảng thời gian cụ thể

**Cho Lookup Tables (Admin Management):**
- **Admin Control**: Admin có thể thêm/sửa/xóa danh mục
- **Data Integrity**: Không thể xóa hard nếu còn records đang sử dụng
- **Audit Trail**: Tracking khi nào admin thay đổi danh mục
- **Rollback Capability**: Có thể restore danh mục đã xóa
- **Historical Data**: Giữ lại lịch sử thay đổi danh mục
- **Business Continuity**: Tránh break existing data khi xóa danh mục

**Ví dụ use cases:**

**Business Entities:**
```sql
-- Tìm users bị xóa trong tháng này
SELECT * FROM users 
WHERE deleted_at BETWEEN '2024-01-01' AND '2024-01-31';

-- Audit report: Ai đã xóa job nào khi nào
SELECT j.title, u.email, j.deleted_at 
FROM jobs j 
JOIN users u ON j.updated_by = u.id 
WHERE j.deleted_at IS NOT NULL;
```

**Lookup Tables (Admin Management - chỉ roles và permissions):**
```sql
-- Audit: Admin nào đã xóa role nào khi nào
SELECT r.name, u.email, r.deleted_at 
FROM roles r 
JOIN users u ON r.updated_by = u.id 
WHERE r.deleted_at IS NOT NULL;

-- Restore role đã bị xóa nhầm
UPDATE roles 
SET deleted_at = NULL, updated_at = NOW() 
WHERE id = ? AND deleted_at IS NOT NULL;
```

#### **2. is_deleted (BOOLEAN) - Junction Tables:**
**Bảng sử dụng**: job_skills

**Lý do sử dụng BOOLEAN:**
- **Performance**: Boolean queries nhanh hơn timestamp comparisons
- **Simplicity**: Chỉ cần biết có bị xóa hay không, không cần khi nào
- **Index Efficiency**: Boolean index nhỏ hơn timestamp index
- **Query Optimization**: `WHERE is_deleted = FALSE` nhanh hơn `WHERE deleted_at IS NULL`
- **Memory Usage**: 1 byte vs 8 bytes cho timestamp

**Ví dụ use cases:**
```sql
-- Tìm skills required của job
SELECT s.name FROM job_skills js
JOIN skills s ON js.skill_id = s.id
WHERE js.job_id = ? AND js.is_deleted = FALSE;

-- Performance: Boolean check nhanh hơn
-- ❌ Chậm: WHERE deleted_at IS NULL
-- ✅ Nhanh: WHERE is_deleted = FALSE
```

#### **3. No Soft Delete - System Tables:**
**Bảng sử dụng**: notifications, user_sessions, audit_logs

**Lý do KHÔNG cần soft delete:**
- **Temporary Data**: Dữ liệu tạm thời, có thể xóa hard
- **Performance**: Tránh overhead của soft delete cho data volume lớn
- **Storage**: Tiết kiệm storage space
- **Cleanup**: Có thể xóa old data mà không ảnh hưởng business logic
- **System Generated**: Không phải user data, ít rủi ro

**Ví dụ use cases:**
```sql
-- Xóa notifications cũ hơn 30 ngày
DELETE FROM notifications 
WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- Xóa expired sessions
DELETE FROM user_sessions 
WHERE expires_at < NOW();

-- Archive old audit logs
DELETE FROM audit_logs 
WHERE created_at < DATE_SUB(NOW(), INTERVAL 1 YEAR);
```

### 📊 **SOFT DELETE STRATEGY COMPARISON:**

| **Strategy** | **Tables** | **Field** | **Size** | **Performance** | **Use Case** |
|--------------|------------|-----------|----------|-----------------|--------------|
| **deleted_at** | Business Entities + Lookup Tables | TIMESTAMP | 8 bytes | Medium | Compliance, Audit, Admin Management |
| **is_deleted** | Junction Tables | BOOLEAN | 1 byte | Fast | Performance, Simple |
| **No Soft Delete** | System Tables | None | 0 bytes | Fastest | Temporary Data |

### 🔍 **CHI TIẾT IMPLEMENTATION:**

#### **1. Business Entities & Lookup Tables với deleted_at:**

**Business Entities:**
```sql
-- Users table
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    email VARCHAR(255) NOT NULL UNIQUE,
    -- ... other fields
    deleted_at TIMESTAMP NULL,
    
    INDEX idx_deleted_at (deleted_at),
    INDEX idx_email_active (email, deleted_at) -- Composite index
);

-- Query active users
SELECT * FROM users WHERE deleted_at IS NULL;

-- Query deleted users
SELECT * FROM users WHERE deleted_at IS NOT NULL;
```

**Lookup Tables (Admin Management - chỉ roles và permissions):**
```sql
-- Roles table (giữ lại vì cần flexibility cho RBAC)
CREATE TABLE roles (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    -- ... other fields
    deleted_at TIMESTAMP NULL,
    
    INDEX idx_deleted_at (deleted_at),
    INDEX idx_name_active (name, deleted_at) -- Composite index
);

-- Query active roles (lookup table còn lại)
SELECT * FROM roles WHERE deleted_at IS NULL;

-- Query deleted roles (admin can restore)
SELECT * FROM roles WHERE deleted_at IS NOT NULL;

-- Check if any users are using deleted role
SELECT COUNT(*) FROM users u 
JOIN roles r ON u.role_id = r.id 
WHERE r.deleted_at IS NOT NULL;
```

#### **2. Junction Tables với is_deleted:**
```sql
-- Job Skills table
CREATE TABLE job_skills (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    job_id VARCHAR(36) NOT NULL,
    skill_id VARCHAR(36) NOT NULL,
    -- ... other fields
    is_deleted BOOLEAN DEFAULT FALSE,
    
    INDEX idx_job_skill_active (job_id, skill_id, is_deleted),
    INDEX idx_is_deleted (is_deleted)
);

-- Query active skills for job
SELECT * FROM job_skills WHERE job_id = ? AND is_deleted = FALSE;

-- Performance: Boolean check
-- ✅ Fast: WHERE is_deleted = FALSE
-- ❌ Slow: WHERE deleted_at IS NULL
```

#### **3. System Tables không soft delete:**
```sql
-- Notifications table
CREATE TABLE notifications (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id VARCHAR(36) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- No soft delete fields
    
    INDEX idx_user_created (user_id, created_at)
);

-- Direct hard delete
DELETE FROM notifications WHERE created_at < DATE_SUB(NOW(), INTERVAL 30 DAY);
```

### 🎯 **QUYẾT ĐỊNH STRATEGY:**

#### **Khi nào dùng deleted_at:**
- ✅ **User data** cần compliance
- ✅ **Business entities** cần audit trail
- ✅ **Financial data** cần timestamp
- ✅ **Personal data** theo GDPR
- ✅ **Lookup Tables** admin quản lý danh mục
- ✅ **Master data** cần rollback capability
- ✅ **Reference data** có thể restore

#### **Khi nào dùng is_deleted:**
- ✅ **Junction tables** với volume lớn
- ✅ **Performance critical** queries
- ✅ **Simple boolean** logic đủ
- ✅ **Temporary relationships**

#### **Khi nào không cần soft delete:**
- ✅ **System generated** data
- ✅ **Temporary data** có lifecycle ngắn
- ✅ **Log data** có thể archive
- ✅ **Cache data** có thể rebuild

### 🎯 **TẠI SAO LOOKUP TABLES CẦN SOFT DELETE:**

#### **1. Admin Management Requirements (chỉ cho roles và permissions):**
```sql
-- Admin có thể thêm role mới
INSERT INTO roles (name, description) 
VALUES ('INTERVIEWER', 'Interviewer role');

-- Admin có thể xóa role (soft delete)
UPDATE roles 
SET deleted_at = NOW(), updated_by = ? 
WHERE id = ?;

-- Admin có thể restore role đã xóa
UPDATE roles 
SET deleted_at = NULL, updated_at = NOW() 
WHERE id = ? AND deleted_at IS NOT NULL;
```

#### **2. Data Integrity Protection:**
```sql
-- Kiểm tra trước khi xóa: Có users nào đang dùng role này không?
SELECT COUNT(*) FROM users 
WHERE role_id = ? AND deleted_at IS NULL;

-- Nếu có users đang dùng, không cho phép xóa hard
-- Chỉ cho phép soft delete để bảo vệ data integrity
```

#### **3. Business Continuity:**
```sql
-- Khi admin xóa nhầm role
-- Có thể restore ngay lập tức mà không ảnh hưởng existing data
UPDATE roles 
SET deleted_at = NULL 
WHERE name = 'RECRUITER' AND deleted_at IS NOT NULL;

-- Existing users vẫn hoạt động bình thường
SELECT u.email, r.name as role_name 
FROM users u 
JOIN roles r ON u.role_id = r.id 
WHERE u.deleted_at IS NULL;
```

#### **4. Audit Trail cho Admin Actions:**
```sql
-- Track admin actions trên lookup tables (roles, permissions)
SELECT 
    r.name,
    u.email as admin_email,
    r.deleted_at,
    r.updated_at
FROM roles r
JOIN users u ON r.updated_by = u.id
WHERE r.deleted_at IS NOT NULL
ORDER BY r.deleted_at DESC;
```

#### **5. Rollback Capability:**
```sql
-- Admin có thể rollback toàn bộ changes
UPDATE roles 
SET deleted_at = NULL, updated_at = NOW() 
WHERE deleted_at BETWEEN '2024-01-01' AND '2024-01-31';

-- Hoặc rollback specific changes
UPDATE roles 
SET deleted_at = NULL 
WHERE id IN (1, 2, 3) AND deleted_at IS NOT NULL;
```

> **Lưu ý**: Các lookup tables khác (job_statuses, job_types, interview_types, etc.) đã chuyển sang ENUM nên không cần soft delete. Chỉ roles và permissions cần soft delete vì cần flexibility cho RBAC.

### 📈 **PERFORMANCE OPTIMIZATIONS**:
- **Junction tables** dùng `is_deleted` để tránh NULL checks
- **Business entities** dùng `deleted_at` để có timestamp
- **System tables** không cần soft delete để tránh overhead
- **Proper indexing** cho tất cả audit fields

### 🔒 **COMPLIANCE BENEFITS**:
- **Complete audit trail** cho user actions
- **Data lineage tracking** cho business entities
- **Regulatory compliance** (GDPR, SOX, etc.)
- **Forensic analysis** capabilities

## 🔗 **CHI TIẾT QUAN HỆ GIỮA CÁC BẢNG**

### 📋 **1. ROLE-BASED ACCESS CONTROL (RBAC)**

#### **1.1. Roles ↔ Users (One-to-Many)**
```sql
-- Quan hệ: 1 role có thể có nhiều users
users.role_id → roles.id
```
- **Mục đích**: Phân quyền người dùng (ADMIN, USER, MANAGER)
- **Cardinality**: 1:N (1 role → N users)
- **Foreign Key**: `users.role_id` → `roles.id`
- **Constraint**: `ON DELETE RESTRICT` (không cho xóa role nếu còn users)

#### **1.2. Roles ↔ Permissions (Many-to-Many)**
```sql
-- Junction table: role_permissions
CREATE TABLE role_permissions (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    role_id VARCHAR(36) NOT NULL,
    permission_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
);
```
- **Mục đích**: Phân quyền chi tiết (CREATE, READ, UPDATE, DELETE)
- **Cardinality**: M:N (1 role → N permissions, 1 permission → N roles)
- **Ví dụ**: ADMIN role có tất cả permissions, USER role chỉ có READ permissions

### 📋 **2. JOB MANAGEMENT RELATIONSHIPS**

#### **2.1. Companies ↔ Users (One-to-Many) - Multi-Tenant** 🔑
```sql
-- Quan hệ: 1 company có thể có nhiều users
users.company_id → companies.id
```
- **Mục đích**: Multi-tenant data isolation. Mỗi user thuộc về 1 company.
- **Cardinality**: 1:N (1 company → N users)
- **Foreign Key**: `users.company_id` → `companies.id`
- **Constraint**: `ON DELETE RESTRICT` (không cho xóa company nếu còn users)
- **🔑 CRITICAL**: Đây là multi-tenant key cho toàn bộ system

#### **2.2. Users ↔ Jobs (One-to-Many) - ATS**
```sql
-- Quan hệ: 1 HR/Recruiter có thể tạo nhiều job postings
jobs.user_id → users.id
```
- **Mục đích**: HR/Recruiter tạo job postings (không phải candidate apply)
- **Cardinality**: 1:N (1 user → N jobs)
- **Foreign Key**: `jobs.user_id` → `users.id`
- **Constraint**: `ON DELETE CASCADE` (xóa user thì xóa jobs)

#### **2.3. Companies ↔ Jobs (One-to-Many) - Multi-Tenant**
```sql
-- Quan hệ: 1 company có thể có nhiều job postings
jobs.company_id → companies.id
```
- **Mục đích**: Multi-tenant isolation. Mỗi job posting thuộc về 1 company.
- **Cardinality**: 1:N (1 company → N jobs)
- **Foreign Key**: `jobs.company_id` → `companies.id`
- **Constraint**: `ON DELETE RESTRICT` (không cho xóa company nếu còn jobs)

#### ~~**2.4. Job Statuses ↔ Jobs**~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Job statuses giờ là ENUM trong `jobs.job_status` (DRAFT, PUBLISHED, PAUSED, CLOSED, FILLED). Không cần foreign key.

#### ~~**2.5. Job Types ↔ Jobs**~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Job types giờ là ENUM trong `jobs.job_type` (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE). Không cần foreign key.

#### ~~**2.5. Priorities ↔ Jobs**~~ ❌ **REMOVED**

#### ~~**2.6. Experience Levels ↔ Jobs**~~ ❌ **REMOVED**

### 📋 **3. APPLICATION MANAGEMENT RELATIONSHIPS (CORE ATS)**

#### **3.1. Jobs ↔ Applications (One-to-Many)** 🔑
```sql
-- Quan hệ: 1 job posting có thể có nhiều applications
applications.job_id → jobs.id
```
- **Mục đích**: Candidates apply to job postings
- **Cardinality**: 1:N (1 job → N applications)
- **Foreign Key**: `applications.job_id` → `jobs.id`
- **Constraint**: `ON DELETE CASCADE` (xóa job thì xóa applications)

#### **3.2. Companies ↔ Applications (One-to-Many) - Multi-Tenant** 🔑
```sql
-- Quan hệ: 1 company có thể có nhiều applications
applications.company_id → companies.id
```
- **Mục đích**: Multi-tenant isolation. Mỗi application thuộc về 1 company.
- **Cardinality**: 1:N (1 company → N applications)
- **Foreign Key**: `applications.company_id` → `companies.id`
- **Constraint**: `ON DELETE RESTRICT`

#### **3.3. Users ↔ Applications (One-to-Many) - Assignment**
```sql
-- Quan hệ: 1 HR/Recruiter có thể được assign nhiều applications
applications.assigned_to → users.id
```
- **Mục đích**: Assign applications cho HR/Recruiter để xử lý
- **Cardinality**: 1:N (1 user → N applications)
- **Foreign Key**: `applications.assigned_to` → `users.id`
- **Constraint**: `ON DELETE SET NULL`

#### **3.4. Applications ↔ Interviews (One-to-Many)**
```sql
-- Quan hệ: 1 application có thể có nhiều vòng interview
interviews.application_id → applications.id
```
- **Mục đích**: Interview rounds cho từng application
- **Cardinality**: 1:N (1 application → N interviews)
- **Foreign Key**: `interviews.application_id` → `applications.id`

#### **3.5. Applications ↔ Comments (One-to-Many)**
```sql
-- Quan hệ: 1 application có thể có nhiều comments
comments.application_id → applications.id
```
- **Mục đích**: HR/Recruiter trao đổi về candidate
- **Cardinality**: 1:N (1 application → N comments)
- **Foreign Key**: `comments.application_id` → `applications.id`

#### **3.6. Applications ↔ Attachments (One-to-Many)**
```sql
-- Quan hệ: 1 application có thể có nhiều attachments
attachments.application_id → applications.id
```
- **Mục đích**: CVs, certificates, portfolio của candidate
- **Cardinality**: 1:N (1 application → N attachments)
- **Foreign Key**: `attachments.application_id` → `applications.id`

#### **3.7. Applications ↔ Application Status History (One-to-Many)**
```sql
-- Quan hệ: 1 application có nhiều status changes
application_status_history.application_id → applications.id
```
- **Mục đích**: Audit trail cho status workflow
- **Cardinality**: 1:N (1 application → N history records)
- **Foreign Key**: `application_status_history.application_id` → `applications.id`

### 📋 **4. SKILLS MANAGEMENT RELATIONSHIPS**

#### ~~**3.1. Users ↔ Skills**~~ ❌ **REMOVED**

#### **4.1. Jobs ↔ Skills (Many-to-Many)**
```sql
-- Junction table: job_skills
CREATE TABLE job_skills (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    job_id VARCHAR(36) NOT NULL,
    skill_id VARCHAR(36) NOT NULL,
    is_required BOOLEAN DEFAULT TRUE,
    proficiency_level VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    is_deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    
    UNIQUE KEY uk_job_skill (job_id, skill_id),
    INDEX idx_job_id (job_id),
    INDEX idx_skill_id (skill_id),
    INDEX idx_is_required (is_required)
);
```
- **Mục đích**: Tracking skills yêu cầu cho jobs
- **Cardinality**: M:N (1 job → N skills, 1 skill → N jobs)
- **Additional Fields**: is_required, proficiency_level

### 📋 **5. INTERVIEW MANAGEMENT RELATIONSHIPS (ATS)**

#### **5.1. Applications ↔ Interviews (One-to-Many)** 🔄
```sql
-- Quan hệ: 1 application có thể có nhiều vòng interview
interviews.application_id → applications.id
```
- **Mục đích**: Interview rounds cho từng application (không phải job)
- **Cardinality**: 1:N (1 application → N interviews)
- **Foreign Key**: `interviews.application_id` → `applications.id`
- **Constraint**: `ON DELETE CASCADE`

#### **5.2. Jobs ↔ Interviews (One-to-Many) - Reference**
```sql
-- Quan hệ: 1 job có thể có nhiều interviews (reference only)
interviews.job_id → jobs.id
```
- **Mục đích**: Reference để biết interview thuộc job nào
- **Cardinality**: 1:N (1 job → N interviews)
- **Foreign Key**: `interviews.job_id` → `jobs.id`
- **Constraint**: `ON DELETE RESTRICT`

#### **5.3. Companies ↔ Interviews (One-to-Many) - Multi-Tenant**
```sql
-- Quan hệ: 1 company có thể có nhiều interviews
interviews.company_id → companies.id
```
- **Mục đích**: Multi-tenant isolation
- **Cardinality**: 1:N (1 company → N interviews)

#### **5.4. Interviews ↔ Users (Many-to-Many) - Interviewers** ➕
```sql
-- Quan hệ: 1 interview có thể có nhiều interviewers, 1 interviewer có thể có nhiều interviews
interview_interviewers.interview_id → interviews.id
interview_interviewers.interviewer_id → users.id (role = INTERVIEWER)
```
- **Mục đích**: Support nhiều interviewers cho 1 interview và validate trùng lịch
- **Cardinality**: M:N (1 interview → N interviewers, 1 interviewer → N interviews)
- **Junction Table**: `interview_interviewers`
- **Additional Fields**: `is_primary` (interviewer chính)
- **Schedule Validation**: Validate trùng lịch dựa trên `interviewer_id`, `scheduled_date`, `duration_minutes`
- **Foreign Key**: `interviews.company_id` → `companies.id`

#### ~~**5.4. Interview Types ↔ Interviews**~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Interview types giờ là ENUM trong `interviews.interview_type` (PHONE, VIDEO, IN_PERSON, TECHNICAL, HR, FINAL). Không cần foreign key.

#### ~~**5.5. Interview Statuses ↔ Interviews**~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Interview statuses giờ là ENUM trong `interviews.status` (SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED). Không cần foreign key.

#### ~~**5.6. Interview Results ↔ Interviews**~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Interview results giờ là ENUM trong `interviews.result` (PASSED, FAILED, PENDING). Không cần foreign key.

### ~~📋 **5. RESUME MANAGEMENT RELATIONSHIPS**~~ ❌ **REMOVED**

> **Lý do**: ATS không cần bảng resumes riêng. CVs lưu trong `applications.resume_file_path` hoặc `attachments`.

### 📋 **6. NOTIFICATION SYSTEM RELATIONSHIPS (ATS)**

#### **6.1. Users ↔ Notifications (One-to-Many)**
```sql
-- Quan hệ: 1 user có thể có nhiều notifications
notifications.user_id → users.id
```
- **Mục đích**: Tracking notifications của users
- **Cardinality**: 1:N (1 user → N notifications)
- **Foreign Key**: `notifications.user_id` → `users.id`

#### **6.2. Companies ↔ Notifications (One-to-Many) - Multi-Tenant**
```sql
-- Quan hệ: 1 company có thể có nhiều notifications
notifications.company_id → companies.id
```
- **Mục đích**: Multi-tenant isolation
- **Cardinality**: 1:N (1 company → N notifications)
- **Foreign Key**: `notifications.company_id` → `companies.id`

#### **6.3. Applications ↔ Notifications (One-to-Many)**
```sql
-- Quan hệ: 1 application có thể có nhiều notifications
notifications.application_id → applications.id
```
- **Mục đích**: Notifications về application status changes, interview reminders
- **Cardinality**: 1:N (1 application → N notifications)
- **Foreign Key**: `notifications.application_id` → `applications.id`

#### ~~**6.4. Notification Types ↔ Notifications**~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Notification types giờ là ENUM trong `notifications.type` (APPLICATION_RECEIVED, INTERVIEW_SCHEDULED, INTERVIEW_REMINDER, STATUS_CHANGE, DEADLINE_REMINDER, COMMENT_ADDED, ASSIGNMENT_CHANGED). Không cần foreign key.

#### ~~**6.5. Notification Priorities ↔ Notifications**~~ ❌ **CHUYỂN SANG ENUM**

> **Lý do**: Notification priorities giờ là ENUM trong `notifications.priority` (HIGH, MEDIUM, LOW). Không cần foreign key.

### 📋 **7. SYSTEM TABLES RELATIONSHIPS**

#### **7.1. Users ↔ User Sessions (One-to-Many)**
```sql
-- Quan hệ: 1 user có thể có nhiều sessions
user_sessions.user_id → users.id
```
- **Mục đích**: Tracking active sessions của users
- **Cardinality**: 1:N (1 user → N sessions)
- **Foreign Key**: `user_sessions.user_id` → `users.id`

#### **7.2. Users ↔ Audit Logs (One-to-Many)**
```sql
-- Quan hệ: 1 user có thể có nhiều audit logs
audit_logs.user_id → users.id
```
- **Mục đích**: Tracking actions của users
- **Cardinality**: 1:N (1 user → N audit logs)
- **Foreign Key**: `audit_logs.user_id` → `users.id`

#### **7.3. Companies ↔ Audit Logs (One-to-Many) - Multi-Tenant**
```sql
-- Quan hệ: 1 company có thể có nhiều audit logs
audit_logs.company_id → companies.id
```
- **Mục đích**: Multi-tenant audit isolation
- **Cardinality**: 1:N (1 company → N audit logs)
- **Foreign Key**: `audit_logs.company_id` → `companies.id`

### 📋 **8. ATTACHMENT RELATIONSHIPS (ATS)**

#### **8.1. Applications ↔ Attachments (One-to-Many)** 🔄
```sql
-- Quan hệ: 1 application có thể có nhiều attachments
attachments.application_id → applications.id
```
- **Mục đích**: CVs, certificates, portfolio của candidate
- **Cardinality**: 1:N (1 application → N attachments)
- **Foreign Key**: `attachments.application_id` → `applications.id`
- **Constraint**: `ON DELETE CASCADE`

#### **8.2. Companies ↔ Attachments (One-to-Many) - Multi-Tenant**
```sql
-- Quan hệ: 1 company có thể có nhiều attachments
attachments.company_id → companies.id
```
- **Mục đích**: Multi-tenant isolation
- **Cardinality**: 1:N (1 company → N attachments)
- **Foreign Key**: `attachments.company_id` → `companies.id`

#### **8.3. Users ↔ Attachments (One-to-Many)**
```sql
-- Quan hệ: 1 user (HR) có thể upload nhiều attachments
attachments.user_id → users.id
```
- **Mục đích**: HR upload CVs, certificates cho applications
- **Cardinality**: 1:N (1 user → N attachments)
- **Foreign Key**: `attachments.user_id` → `users.id`

## 🔄 **QUAN HỆ TỔNG QUAN (ENTITY RELATIONSHIP DIAGRAM - ATS)**

### **Core Entities (Multi-Tenant):**
- **companies** (Tenant) ↔ **users**, **jobs**, **applications**, **interviews**, **notifications**, **attachments**, **audit_logs**
- **users** (HR/Recruiter) ↔ **jobs**, **applications** (assigned), **interviews**, **comments**, **notifications**
- **jobs** (Job Postings) ↔ **applications**, **job_skills**
- **applications** (CORE ATS) ↔ **interviews**, **comments**, **attachments**, **application_status_history**

### **Lookup Tables (chỉ giữ RBAC):**
- **roles** ↔ **users** (COMPANY_ADMIN, RECRUITER, HIRING_MANAGER, INTERVIEWER) - **GIỮ TABLE**
- **permissions** ↔ **roles** (JOB_CREATE, APPLICATION_VIEW, etc.) - **GIỮ TABLE**

### **ENUM Values (thay thế lookup tables):**
- **jobs.job_status**: ENUM('DRAFT', 'PUBLISHED', 'PAUSED', 'CLOSED', 'FILLED')
- **jobs.job_type**: ENUM('FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERNSHIP', 'FREELANCE')
- **interviews.interview_type**: ENUM('PHONE', 'VIDEO', 'IN_PERSON', 'TECHNICAL', 'HR', 'FINAL')
- **interviews.status**: ENUM('SCHEDULED', 'COMPLETED', 'CANCELLED', 'RESCHEDULED')
- **interviews.result**: ENUM('PASSED', 'FAILED', 'PENDING')
- **notifications.type**: ENUM('APPLICATION_RECEIVED', 'INTERVIEW_SCHEDULED', 'INTERVIEW_REMINDER', 'STATUS_CHANGE', 'DEADLINE_REMINDER', 'COMMENT_ADDED', 'ASSIGNMENT_CHANGED')
- **notifications.priority**: ENUM('HIGH', 'MEDIUM', 'LOW')

### **Lookup Tables (giữ lại vì cần flexibility):**
- **application_statuses** - Trạng thái ứng tuyển (cần metadata, workflow rules)

### **Junction Tables:**
- **role_permissions** (roles ↔ permissions)
- **job_skills** (jobs ↔ skills)

### **System Tables:**
- **user_sessions** ↔ **users**
- **audit_logs** ↔ **users**, **companies** (multi-tenant)

## 🆔 **UUID IMPLEMENTATION**

### **Tại sao sử dụng UUID:**
- **Security**: Không thể đoán được ID tiếp theo
- **Distributed Systems**: Có thể tạo ID mà không cần database
- **Microservices**: Mỗi service có thể tạo unique ID
- **Privacy**: Không expose thông tin về số lượng records

### **UUID vs BIGINT Comparison:**

| **Aspect** | **BIGINT** | **UUID** |
|------------|-------------|----------|
| **Size** | 8 bytes | 16 bytes |
| **Performance** | Faster (sequential) | Slower (random) |
| **Security** | Predictable | Unpredictable |
| **Distributed** | Requires coordination | No coordination needed |
| **Indexing** | Better for range queries | Better for equality queries |

### **UUID Implementation Strategy:**

#### **1. Primary Keys với UUID:**
```sql
-- Thay vì: id BIGINT PRIMARY KEY AUTO_INCREMENT
-- Sử dụng: id VARCHAR(36) PRIMARY KEY DEFAULT (UUID())

CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    email VARCHAR(255) NOT NULL UNIQUE,
    -- ... other fields
);

-- Hoặc sử dụng BINARY(16) cho performance tốt hơn
CREATE TABLE users (
    id BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    email VARCHAR(255) NOT NULL UNIQUE,
    -- ... other fields
);
```

#### **2. Foreign Keys với UUID:**
```sql
-- Thay vì: user_id BIGINT NOT NULL
-- Sử dụng: user_id VARCHAR(36) NOT NULL

CREATE TABLE jobs (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id VARCHAR(36) NOT NULL,
    company_id VARCHAR(36) NOT NULL,
    -- ... other fields
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT
);
```

#### **3. Indexing Strategy cho UUID:**
```sql
-- UUID với VARCHAR(36) - dễ đọc và debug
CREATE INDEX idx_user_id ON jobs(user_id);

-- UUID với BINARY(16) - nhanh hơn nhưng khó đọc
CREATE INDEX idx_user_id ON jobs(user_id);

-- Composite indexes
CREATE INDEX idx_user_status ON jobs(user_id, job_status);
CREATE INDEX idx_user_created ON jobs(user_id, created_at);
```

#### **4. Application Level UUID Generation:**
```java
// Java - Spring Boot
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    // ... other fields
}

// Hoặc manual generation
@Id
@Column(name = "id", columnDefinition = "CHAR(36)")
private String id = UUID.randomUUID().toString();
```

#### **5. Migration Strategy từ BIGINT sang UUID:**
```sql
-- Step 1: Thêm cột UUID mới
ALTER TABLE users ADD COLUMN uuid CHAR(36) DEFAULT (UUID());

-- Step 2: Populate UUID cho existing records
UPDATE users SET uuid = UUID() WHERE uuid IS NULL;

-- Step 3: Tạo foreign key constraints mới
ALTER TABLE jobs ADD COLUMN user_uuid CHAR(36);
UPDATE jobs j SET user_uuid = (SELECT uuid FROM users u WHERE u.id = j.user_id);

-- Step 4: Drop old constraints và columns
ALTER TABLE jobs DROP FOREIGN KEY fk_jobs_user_id;
ALTER TABLE jobs DROP COLUMN user_id;
ALTER TABLE jobs CHANGE user_uuid user_id CHAR(36) NOT NULL;

-- Step 5: Add new foreign key
ALTER TABLE jobs ADD FOREIGN KEY (user_id) REFERENCES users(uuid) ON DELETE CASCADE;
```

### **Performance Considerations:**

#### **1. UUID với BINARY(16):**
```sql
-- Tốt nhất cho performance
CREATE TABLE users (
    id BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    email VARCHAR(255) NOT NULL UNIQUE,
    -- ... other fields
);

-- Indexes
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_created ON users(created_at);
```

#### **2. UUID với CHAR(36):**
```sql
-- Dễ đọc và debug
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    email VARCHAR(255) NOT NULL UNIQUE,
    -- ... other fields
);
```

#### **3. Hybrid Approach:**
```sql
-- Sử dụng BIGINT cho internal, UUID cho external
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,  -- Internal ID
    uuid VARCHAR(36) UNIQUE DEFAULT (UUID()), -- External ID
    email VARCHAR(255) NOT NULL UNIQUE,
    -- ... other fields
);
```

### **Best Practices:**

#### **1. Consistent UUID Usage:**
- Sử dụng cùng format UUID (VARCHAR(36) hoặc BINARY(16))
- Tạo UUID ở application level để control tốt hơn
- Sử dụng UUID v4 (random) cho security

#### **2. Indexing Strategy:**
- Index trên UUID columns cho foreign keys
- Composite indexes cho queries thường xuyên
- Consider covering indexes cho performance

#### **3. API Design:**
- Expose UUID trong API responses
- Sử dụng UUID trong URLs: `/api/users/{uuid}`
- Hide internal BIGINT IDs

#### **4. Security Benefits:**
- Không thể enumerate records
- Không thể đoán được ID tiếp theo
- Better privacy protection
