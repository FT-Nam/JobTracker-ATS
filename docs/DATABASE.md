# 🗄️ JobTracker Database Schema

## 📋 Tổng quan Database

JobTracker sử dụng **MySQL 8.0** làm database chính với thiết kế normalized để đảm bảo tính toàn vẹn dữ liệu và hiệu suất truy vấn.

### 🎯 Thiết kế nguyên tắc
- **Normalization**: 3NF để tránh redundancy
- **UUID Primary Keys**: Sử dụng VARCHAR(36) cho tất cả primary keys
- **Indexing**: Tối ưu cho các truy vấn thường xuyên
- **Foreign Keys**: Đảm bảo referential integrity với UUID
- **Audit Fields**: Tracking tất cả thay đổi với full audit trail
- **Soft Delete**: Không xóa dữ liệu thực tế với deleted_at

### 🆔 **UUID IMPLEMENTATION STRATEGY**
- **Primary Keys**: VARCHAR(36) với UUID() function
- **Foreign Keys**: VARCHAR(36) references
- **Indexing**: Optimized cho UUID lookups
- **Performance**: Proper indexing cho UUID queries
- **Security**: UUIDs không thể guess được
- **Consistency**: Tất cả bảng đều dùng UUID làm primary key

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

#### 1.4. Job Statuses Table (Bảng trạng thái công việc)
```sql
CREATE TABLE job_statuses (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID trạng thái',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên trạng thái',
    display_name VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị',
    description VARCHAR(255) COMMENT 'Mô tả trạng thái',
    color VARCHAR(7) DEFAULT '#6B7280' COMMENT 'Màu hiển thị (hex)',
    sort_order INT DEFAULT 0 COMMENT 'Thứ tự sắp xếp',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Trạng thái đang hoạt động',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    
    -- Indexes
    INDEX idx_name (name),
    INDEX idx_sort_order (sort_order),
    INDEX idx_is_active (is_active),
    INDEX idx_deleted_at (deleted_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 1.4. Job Types Table (Bảng loại công việc)
```sql
CREATE TABLE job_types (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID loại công việc',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên loại công việc',
    display_name VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị',
    description VARCHAR(255) COMMENT 'Mô tả loại công việc',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Loại đang hoạt động',
    
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

#### 1.5. Priorities Table (Bảng độ ưu tiên)
```sql
CREATE TABLE priorities (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID độ ưu tiên',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên độ ưu tiên',
    display_name VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị',
    level INT NOT NULL COMMENT 'Mức độ ưu tiên (1-4)',
    color VARCHAR(7) DEFAULT '#6B7280' COMMENT 'Màu hiển thị (hex)',
    description VARCHAR(255) COMMENT 'Mô tả độ ưu tiên',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Độ ưu tiên đang hoạt động',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    
    -- Indexes
    INDEX idx_name (name),
    INDEX idx_level (level),
    INDEX idx_is_active (is_active),
    INDEX idx_deleted_at (deleted_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 1.6. Experience Levels Table (Bảng cấp độ kinh nghiệm)
```sql
CREATE TABLE experience_levels (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID cấp độ kinh nghiệm',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên cấp độ',
    display_name VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị',
    min_years INT DEFAULT 0 COMMENT 'Số năm kinh nghiệm tối thiểu',
    max_years INT COMMENT 'Số năm kinh nghiệm tối đa',
    description VARCHAR(255) COMMENT 'Mô tả cấp độ',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Cấp độ đang hoạt động',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    
    -- Indexes
    INDEX idx_name (name),
    INDEX idx_min_years (min_years),
    INDEX idx_is_active (is_active),
    INDEX idx_deleted_at (deleted_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 1.7. Interview Types Table (Bảng loại phỏng vấn)
```sql
CREATE TABLE interview_types (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID loại phỏng vấn',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên loại phỏng vấn',
    display_name VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị',
    description VARCHAR(255) COMMENT 'Mô tả loại phỏng vấn',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Loại đang hoạt động',
    
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

#### 1.8. Interview Statuses Table (Bảng trạng thái phỏng vấn)
```sql
CREATE TABLE interview_statuses (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID trạng thái phỏng vấn',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên trạng thái',
    display_name VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị',
    description VARCHAR(255) COMMENT 'Mô tả trạng thái',
    color VARCHAR(7) DEFAULT '#6B7280' COMMENT 'Màu hiển thị (hex)',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Trạng thái đang hoạt động',
    
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

#### 1.9. Interview Results Table (Bảng kết quả phỏng vấn)
```sql
CREATE TABLE interview_results (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID kết quả phỏng vấn',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên kết quả',
    display_name VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị',
    description VARCHAR(255) COMMENT 'Mô tả kết quả',
    color VARCHAR(7) DEFAULT '#6B7280' COMMENT 'Màu hiển thị (hex)',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Kết quả đang hoạt động',
    
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

#### 1.10. Notification Types Table (Bảng loại thông báo)
```sql
CREATE TABLE notification_types (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID loại thông báo',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên loại thông báo',
    display_name VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị',
    description VARCHAR(255) COMMENT 'Mô tả loại thông báo',
    template VARCHAR(500) COMMENT 'Template thông báo',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Loại đang hoạt động',
    
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

#### 1.11. Notification Priorities Table (Bảng độ ưu tiên thông báo)
```sql
CREATE TABLE notification_priorities (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID độ ưu tiên thông báo',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên độ ưu tiên',
    display_name VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị',
    level INT NOT NULL COMMENT 'Mức độ ưu tiên (1-4)',
    color VARCHAR(7) DEFAULT '#6B7280' COMMENT 'Màu hiển thị (hex)',
    description VARCHAR(255) COMMENT 'Mô tả độ ưu tiên',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Độ ưu tiên đang hoạt động',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    
    -- Indexes
    INDEX idx_name (name),
    INDEX idx_level (level),
    INDEX idx_is_active (is_active),
    INDEX idx_deleted_at (deleted_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 2. Users Table (Bảng người dùng)

```sql
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID người dùng',
    email VARCHAR(255) NOT NULL UNIQUE COMMENT 'Email đăng nhập',
    password VARCHAR(255) COMMENT 'Mật khẩu đã hash (null nếu dùng OAuth)',
    first_name VARCHAR(100) NOT NULL COMMENT 'Tên',
    last_name VARCHAR(100) NOT NULL COMMENT 'Họ',
    phone VARCHAR(20) COMMENT 'Số điện thoại',
    avatar_url VARCHAR(500) COMMENT 'URL ảnh đại diện',
    role_id VARCHAR(36) NOT NULL COMMENT 'UUID vai trò người dùng',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'Trạng thái hoạt động',
    email_verified BOOLEAN DEFAULT FALSE COMMENT 'Email đã xác thực',
    google_id VARCHAR(100) UNIQUE COMMENT 'Google OAuth ID',
    last_login_at TIMESTAMP NULL COMMENT 'Lần đăng nhập cuối',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT,
    
    -- Indexes
    INDEX idx_email (email),
    INDEX idx_google_id (google_id),
    INDEX idx_role_id (role_id),
    INDEX idx_created_at (created_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 3. Companies Table (Bảng công ty)

```sql
CREATE TABLE companies (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID công ty',
    name VARCHAR(255) NOT NULL COMMENT 'Tên công ty',
    website VARCHAR(500) COMMENT 'Website công ty',
    industry VARCHAR(100) COMMENT 'Lĩnh vực hoạt động',
    size VARCHAR(50) COMMENT 'Quy mô công ty (STARTUP, SMALL, MEDIUM, LARGE, ENTERPRISE)',
    location VARCHAR(255) COMMENT 'Địa chỉ công ty',
    description TEXT COMMENT 'Mô tả công ty',
    logo_url VARCHAR(500) COMMENT 'URL logo công ty',
    is_verified BOOLEAN DEFAULT FALSE COMMENT 'Công ty đã xác thực',
    
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
    INDEX idx_created_at (created_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 4. Jobs Table (Bảng công việc)

```sql
CREATE TABLE jobs (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID công việc',
    user_id VARCHAR(36) NOT NULL COMMENT 'UUID người dùng sở hữu',
    company_id VARCHAR(36) NOT NULL COMMENT 'UUID công ty',
    title VARCHAR(255) NOT NULL COMMENT 'Tiêu đề công việc',
    position VARCHAR(255) NOT NULL COMMENT 'Vị trí ứng tuyển',
    job_type_id VARCHAR(36) NOT NULL COMMENT 'UUID loại công việc',
    location VARCHAR(255) COMMENT 'Địa điểm làm việc',
    salary_min DECIMAL(12,2) COMMENT 'Mức lương tối thiểu',
    salary_max DECIMAL(12,2) COMMENT 'Mức lương tối đa',
    currency VARCHAR(3) DEFAULT 'USD' COMMENT 'Đơn vị tiền tệ',
    CONSTRAINT chk_currency CHECK (currency IN ('USD', 'VND', 'EUR', 'GBP', 'JPY')),
    status_id VARCHAR(36) NOT NULL COMMENT 'UUID trạng thái ứng tuyển',
    application_date DATE COMMENT 'Ngày nộp đơn',
    deadline_date DATE COMMENT 'Hạn nộp đơn',
    interview_date DATE COMMENT 'Ngày phỏng vấn',
    offer_date DATE COMMENT 'Ngày nhận offer',
    job_description TEXT COMMENT 'Mô tả công việc',
    requirements TEXT COMMENT 'Yêu cầu công việc',
    benefits TEXT COMMENT 'Quyền lợi',
    job_url VARCHAR(500) COMMENT 'URL tin tuyển dụng',
    notes TEXT COMMENT 'Ghi chú cá nhân',
    priority_id VARCHAR(36) NOT NULL COMMENT 'UUID độ ưu tiên',
    is_remote BOOLEAN DEFAULT FALSE COMMENT 'Làm việc từ xa',
    experience_level_id VARCHAR(36) COMMENT 'UUID cấp độ kinh nghiệm',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT,
    FOREIGN KEY (job_type_id) REFERENCES job_types(id) ON DELETE RESTRICT,
    FOREIGN KEY (status_id) REFERENCES job_statuses(id) ON DELETE RESTRICT,
    FOREIGN KEY (priority_id) REFERENCES priorities(id) ON DELETE RESTRICT,
    FOREIGN KEY (experience_level_id) REFERENCES experience_levels(id) ON DELETE SET NULL,
    
    -- Indexes
    INDEX idx_user_id (user_id),
    INDEX idx_company_id (company_id),
    INDEX idx_job_type_id (job_type_id),
    INDEX idx_status_id (status_id),
    INDEX idx_priority_id (priority_id),
    INDEX idx_experience_level_id (experience_level_id),
    INDEX idx_application_date (application_date),
    INDEX idx_deadline_date (deadline_date),
    INDEX idx_created_at (created_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by),
    INDEX idx_deleted_at (deleted_at),
    
    INDEX idx_user_status (user_id, status_id),
    INDEX idx_user_created (user_id, created_at),
    INDEX idx_deadline_status (deadline_date, status_id)
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

### 7. User Skills Table (Bảng kỹ năng người dùng)

```sql
CREATE TABLE user_skills (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID user skill',
    user_id VARCHAR(36) NOT NULL COMMENT 'UUID người dùng',
    skill_id VARCHAR(36) NOT NULL COMMENT 'UUID kỹ năng',
    proficiency_level VARCHAR(50) NOT NULL COMMENT 'Mức độ thành thạo (BEGINNER, INTERMEDIATE, ADVANCED, EXPERT)',
    CONSTRAINT chk_proficiency_level CHECK (proficiency_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT')),
    years_of_experience DECIMAL(3,1) COMMENT 'Số năm kinh nghiệm',
    is_verified BOOLEAN DEFAULT FALSE COMMENT 'Kỹ năng đã xác thực',
    
    -- Partial Audit Fields (Junction Table)
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT 'Đã xóa (soft delete)',
    
    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    
    -- Indexes
    UNIQUE KEY uk_user_skill (user_id, skill_id),
    INDEX idx_user_id (user_id),
    INDEX idx_skill_id (skill_id),
    INDEX idx_proficiency (proficiency_level),
    INDEX idx_created_by (created_by),
    INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 8. Interviews Table (Bảng phỏng vấn)

```sql
CREATE TABLE interviews (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID phỏng vấn',
    job_id VARCHAR(36) NOT NULL COMMENT 'UUID công việc',
    round_number INT NOT NULL COMMENT 'Số vòng phỏng vấn',
    interview_type_id VARCHAR(36) NOT NULL COMMENT 'UUID loại phỏng vấn',
    scheduled_date TIMESTAMP NOT NULL COMMENT 'Thời gian phỏng vấn dự kiến',
    actual_date TIMESTAMP NULL COMMENT 'Thời gian phỏng vấn thực tế',
    duration_minutes INT COMMENT 'Thời lượng phỏng vấn (phút)',
    interviewer_name VARCHAR(255) COMMENT 'Tên người phỏng vấn',
    interviewer_email VARCHAR(255) COMMENT 'Email người phỏng vấn',
    interviewer_position VARCHAR(255) COMMENT 'Vị trí người phỏng vấn',
    status_id VARCHAR(36) NOT NULL COMMENT 'UUID trạng thái phỏng vấn',
    result_id VARCHAR(36) COMMENT 'UUID kết quả phỏng vấn',
    feedback TEXT COMMENT 'Phản hồi từ nhà tuyển dụng',
    notes TEXT COMMENT 'Ghi chú cá nhân',
    questions_asked TEXT COMMENT 'Câu hỏi được hỏi',
    answers_given TEXT COMMENT 'Câu trả lời đã đưa ra',
    rating INT CHECK (rating >= 1 AND rating <= 5) COMMENT 'Đánh giá chất lượng phỏng vấn (1-5)',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (interview_type_id) REFERENCES interview_types(id) ON DELETE RESTRICT,
    FOREIGN KEY (status_id) REFERENCES interview_statuses(id) ON DELETE RESTRICT,
    FOREIGN KEY (result_id) REFERENCES interview_results(id) ON DELETE SET NULL,
    
    -- Indexes
    INDEX idx_job_id (job_id),
    INDEX idx_interview_type_id (interview_type_id),
    INDEX idx_status_id (status_id),
    INDEX idx_result_id (result_id),
    INDEX idx_scheduled_date (scheduled_date),
    INDEX idx_created_at (created_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by),
    INDEX idx_deleted_at (deleted_at),
    
    INDEX idx_job_round (job_id, round_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 9. Job Resumes Table (Bảng liên kết CV với công việc)

```sql
CREATE TABLE job_resumes (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID job resume',
    job_id VARCHAR(36) NOT NULL COMMENT 'UUID công việc',
    resume_id VARCHAR(36) NOT NULL COMMENT 'UUID CV',
    is_primary BOOLEAN DEFAULT TRUE COMMENT 'CV chính được sử dụng',
    
    -- Partial Audit Fields (Junction Table)
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT 'Đã xóa (soft delete)',
    
    -- Foreign Keys
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE,
    
    -- Indexes
    UNIQUE KEY uk_job_resume (job_id, resume_id),
    INDEX idx_job_id (job_id),
    INDEX idx_resume_id (resume_id),
    INDEX idx_created_by (created_by),
    INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 10. Resumes Table (Bảng CV)

```sql
CREATE TABLE resumes (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID CV',
    user_id VARCHAR(36) NOT NULL COMMENT 'UUID người dùng sở hữu',
    name VARCHAR(255) NOT NULL COMMENT 'Tên file CV',
    original_filename VARCHAR(255) NOT NULL COMMENT 'Tên file gốc',
    file_path VARCHAR(500) NOT NULL COMMENT 'Đường dẫn file trên Dropbox',
    file_size BIGINT NOT NULL COMMENT 'Kích thước file (bytes)',
    file_type VARCHAR(100) NOT NULL COMMENT 'Loại file (pdf, doc, docx)',
    version VARCHAR(50) DEFAULT '1.0' COMMENT 'Phiên bản CV',
    is_default BOOLEAN DEFAULT FALSE COMMENT 'CV mặc định',
    description TEXT COMMENT 'Mô tả CV',
    tags JSON COMMENT 'Tags phân loại CV (JSON array)',
    is_active BOOLEAN DEFAULT TRUE COMMENT 'CV đang hoạt động',
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian upload',
    
    -- Full Audit Fields
    created_by VARCHAR(36) COMMENT 'Người tạo (FK to users)',
    updated_by VARCHAR(36) COMMENT 'Người cập nhật cuối (FK to users)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    deleted_at TIMESTAMP NULL COMMENT 'Thời gian xóa (soft delete)',
    
    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_user_id (user_id),
    INDEX idx_is_default (is_default),
    INDEX idx_is_active (is_active),
    INDEX idx_uploaded_at (uploaded_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 11. Attachments Table (Bảng file đính kèm)

```sql
CREATE TABLE attachments (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID file đính kèm',
    job_id VARCHAR(36) NOT NULL COMMENT 'UUID công việc',
    user_id VARCHAR(36) NOT NULL COMMENT 'UUID người dùng upload',
    filename VARCHAR(255) NOT NULL COMMENT 'Tên file',
    original_filename VARCHAR(255) NOT NULL COMMENT 'Tên file gốc',
    file_path VARCHAR(500) NOT NULL COMMENT 'Đường dẫn file trên Dropbox',
    file_size BIGINT NOT NULL COMMENT 'Kích thước file (bytes)',
    file_type VARCHAR(100) NOT NULL COMMENT 'Loại file',
    attachment_type ENUM('JOB_DESCRIPTION', 'COVER_LETTER', 'CERTIFICATE', 'PORTFOLIO', 'OTHER') NOT NULL COMMENT 'Loại file đính kèm',
    CONSTRAINT chk_attachment_type CHECK (attachment_type IN ('JOB_DESCRIPTION', 'COVER_LETTER', 'CERTIFICATE', 'PORTFOLIO', 'OTHER')),
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
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Indexes
    INDEX idx_job_id (job_id),
    INDEX idx_user_id (user_id),
    INDEX idx_attachment_type (attachment_type),
    INDEX idx_uploaded_at (uploaded_at),
    INDEX idx_created_by (created_by),
    INDEX idx_updated_by (updated_by),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 12. Notifications Table (Bảng thông báo)

```sql
CREATE TABLE notifications (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID thông báo',
    user_id VARCHAR(36) NOT NULL COMMENT 'UUID người dùng nhận thông báo',
    job_id VARCHAR(36) NULL COMMENT 'UUID công việc liên quan (nullable)',
    type_id VARCHAR(36) NOT NULL COMMENT 'UUID loại thông báo',
    title VARCHAR(255) NOT NULL COMMENT 'Tiêu đề thông báo',
    message TEXT NOT NULL COMMENT 'Nội dung thông báo',
    is_read BOOLEAN DEFAULT FALSE COMMENT 'Đã đọc chưa',
    is_sent BOOLEAN DEFAULT FALSE COMMENT 'Đã gửi chưa',
    sent_at TIMESTAMP NULL COMMENT 'Thời gian gửi',
    scheduled_at TIMESTAMP NULL COMMENT 'Thời gian lên lịch gửi',
    priority_id VARCHAR(36) NOT NULL COMMENT 'UUID độ ưu tiên',
    metadata JSON COMMENT 'Dữ liệu bổ sung (JSON)',
    
    -- System Table - Only created_at, updated_at (no user tracking)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Thời gian cập nhật',
    
    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE SET NULL,
    FOREIGN KEY (type_id) REFERENCES notification_types(id) ON DELETE RESTRICT,
    FOREIGN KEY (priority_id) REFERENCES notification_priorities(id) ON DELETE RESTRICT,
    
    INDEX idx_user_id (user_id),
    INDEX idx_job_id (job_id),
    INDEX idx_type_id (type_id),
    INDEX idx_priority_id (priority_id),
    INDEX idx_is_read (is_read),
    INDEX idx_is_sent (is_sent),
    INDEX idx_scheduled_at (scheduled_at),
    INDEX idx_created_at (created_at),
    
    INDEX idx_user_unread (user_id, is_read),
    INDEX idx_scheduled_unsent (scheduled_at, is_sent)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### 13. User Sessions Table (Bảng phiên đăng nhập)

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

### 14. Audit Logs Table (Bảng log audit)

```sql
CREATE TABLE audit_logs (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()) COMMENT 'UUID audit log',
    user_id VARCHAR(36) NULL COMMENT 'UUID người dùng thực hiện (nullable cho system actions)',
    entity_type VARCHAR(100) NOT NULL COMMENT 'Loại entity (User, Job, Company, etc.)',
    entity_id VARCHAR(36) NOT NULL COMMENT 'UUID của entity',
    action VARCHAR(50) NOT NULL COMMENT 'Hành động thực hiện (CREATE, UPDATE, DELETE, LOGIN, LOGOUT, UPLOAD, DOWNLOAD)',
    old_values JSON COMMENT 'Giá trị cũ (JSON)',
    new_values JSON COMMENT 'Giá trị mới (JSON)',
    ip_address VARCHAR(45) COMMENT 'Địa chỉ IP',
    user_agent TEXT COMMENT 'User agent string',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời gian tạo',
    
    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    
    -- Indexes
    INDEX idx_user_id (user_id),
    INDEX idx_entity_type (entity_type),
    INDEX idx_entity_id (entity_id),
    INDEX idx_action (action),
    INDEX idx_created_at (created_at),
    
    INDEX idx_entity_action (entity_type, entity_id, action),
    INDEX idx_user_action (user_id, action)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

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

### Query Optimization Indexes
```sql
-- Job queries optimization
CREATE INDEX idx_jobs_user_status_date ON jobs(user_id, status, created_at);
CREATE INDEX idx_jobs_deadline_status ON jobs(deadline_date, status);

-- Interview queries optimization  
CREATE INDEX idx_interviews_job_round ON interviews(job_id, round_number);
CREATE INDEX idx_interviews_scheduled_status ON interviews(scheduled_date, status);

-- Notification queries optimization
CREATE INDEX idx_notifications_user_unread ON notifications(user_id, is_read);
CREATE INDEX idx_notifications_scheduled_unsent ON notifications(scheduled_at, is_sent);
```

## 🔄 Database Relationships

### Entity Relationship Diagram
```
Users (1) ──── (N) Jobs
Users (1) ──── (N) Resumes  
Users (1) ──── (N) User_Skills
Users (1) ──── (N) Notifications
Users (1) ──── (N) User_Sessions
Users (1) ──── (N) Audit_Logs

Companies (1) ──── (N) Jobs

Jobs (1) ──── (N) Job_Skills
Jobs (1) ──── (N) Interviews
Jobs (1) ──── (N) Job_Resumes
Jobs (1) ──── (N) Attachments
Jobs (1) ──── (N) Notifications

Skills (1) ──── (N) Job_Skills
Skills (1) ──── (N) User_Skills

Resumes (1) ──── (N) Job_Resumes
```

## 📊 Sample Data

### Initial Lookup Data

#### Roles Data
```sql
INSERT INTO roles (name, description) VALUES
('USER', 'Regular user with basic permissions'),
('ADMIN', 'Administrator with full system access'),
('MODERATOR', 'Moderator with limited admin permissions');
```

#### Permissions Data
```sql
INSERT INTO permissions (name, resource, action, description) VALUES
('USER_READ', 'USER', 'READ', 'Read user information'),
('USER_CREATE', 'USER', 'CREATE', 'Create new users'),
('USER_UPDATE', 'USER', 'UPDATE', 'Update user information'),
('USER_DELETE', 'USER', 'DELETE', 'Delete users'),
('JOB_READ', 'JOB', 'READ', 'Read job information'),
('JOB_CREATE', 'JOB', 'CREATE', 'Create new jobs'),
('JOB_UPDATE', 'JOB', 'UPDATE', 'Update job information'),
('JOB_DELETE', 'JOB', 'DELETE', 'Delete jobs'),
('COMPANY_READ', 'COMPANY', 'READ', 'Read company information'),
('COMPANY_CREATE', 'COMPANY', 'CREATE', 'Create new companies'),
('COMPANY_UPDATE', 'COMPANY', 'UPDATE', 'Update company information'),
('COMPANY_DELETE', 'COMPANY', 'DELETE', 'Delete companies');
```

#### Job Statuses Data
```sql
INSERT INTO job_statuses (name, display_name, description, color, sort_order) VALUES
('SAVED', 'Saved', 'Job saved but not yet applied', '#6B7280', 1),
('APPLIED', 'Applied', 'Application submitted', '#3B82F6', 2),
('INTERVIEW', 'Interview', 'Interview scheduled or in progress', '#F59E0B', 3),
('OFFER', 'Offer', 'Job offer received', '#10B981', 4),
('REJECTED', 'Rejected', 'Application rejected', '#EF4444', 5),
('WITHDRAWN', 'Withdrawn', 'Application withdrawn', '#8B5CF6', 6),
('ACCEPTED', 'Accepted', 'Job offer accepted', '#059669', 7);
```

#### Job Types Data
```sql
INSERT INTO job_types (name, display_name, description) VALUES
('FULL_TIME', 'Full Time', 'Full-time employment'),
('PART_TIME', 'Part Time', 'Part-time employment'),
('CONTRACT', 'Contract', 'Contract-based work'),
('INTERNSHIP', 'Internship', 'Internship position'),
('FREELANCE', 'Freelance', 'Freelance work');
```

#### Priorities Data
```sql
INSERT INTO priorities (name, display_name, level, color, description) VALUES
('LOW', 'Low', 1, '#6B7280', 'Low priority'),
('MEDIUM', 'Medium', 2, '#3B82F6', 'Medium priority'),
('HIGH', 'High', 3, '#F59E0B', 'High priority'),
('URGENT', 'Urgent', 4, '#EF4444', 'Urgent priority');
```

#### Experience Levels Data
```sql
INSERT INTO experience_levels (name, display_name, min_years, max_years, description) VALUES
('ENTRY', 'Entry Level', 0, 1, 'Entry level position'),
('JUNIOR', 'Junior', 1, 3, 'Junior level position'),
('MID', 'Mid Level', 3, 5, 'Mid level position'),
('SENIOR', 'Senior', 5, 8, 'Senior level position'),
('LEAD', 'Lead', 8, 12, 'Lead level position'),
('PRINCIPAL', 'Principal', 12, NULL, 'Principal level position');
```

#### Interview Types Data
```sql
INSERT INTO interview_types (name, display_name, description) VALUES
('PHONE', 'Phone Interview', 'Phone-based interview'),
('VIDEO', 'Video Interview', 'Video call interview'),
('IN_PERSON', 'In-Person Interview', 'Face-to-face interview'),
('TECHNICAL', 'Technical Interview', 'Technical skills assessment'),
('HR', 'HR Interview', 'Human resources interview'),
('FINAL', 'Final Interview', 'Final round interview');
```

#### Interview Statuses Data
```sql
INSERT INTO interview_statuses (name, display_name, description, color) VALUES
('SCHEDULED', 'Scheduled', 'Interview scheduled', '#3B82F6'),
('COMPLETED', 'Completed', 'Interview completed', '#10B981'),
('CANCELLED', 'Cancelled', 'Interview cancelled', '#EF4444'),
('RESCHEDULED', 'Rescheduled', 'Interview rescheduled', '#F59E0B');
```

#### Interview Results Data
```sql
INSERT INTO interview_results (name, display_name, description, color) VALUES
('PASSED', 'Passed', 'Interview passed', '#10B981'),
('FAILED', 'Failed', 'Interview failed', '#EF4444'),
('PENDING', 'Pending', 'Result pending', '#6B7280');
```

#### Notification Types Data
```sql
INSERT INTO notification_types (name, display_name, description, template) VALUES
('DEADLINE_REMINDER', 'Deadline Reminder', 'Reminder for job application deadline', 'Your job application for {job_title} at {company_name} is due in {days} days.'),
('INTERVIEW_REMINDER', 'Interview Reminder', 'Reminder for upcoming interview', 'You have an interview for {job_title} at {company_name} in {hours} hours.'),
('STATUS_UPDATE', 'Status Update', 'Job status update notification', 'Your application status for {job_title} at {company_name} has been updated to {status}.'),
('SYSTEM', 'System Notification', 'System-generated notification', '{message}'),
('EMAIL_SENT', 'Email Sent', 'Email notification sent', 'Email notification has been sent successfully.');
```

#### Notification Priorities Data
```sql
INSERT INTO notification_priorities (name, display_name, level, color, description) VALUES
('LOW', 'Low', 1, '#6B7280', 'Low priority notification'),
('MEDIUM', 'Medium', 2, '#3B82F6', 'Medium priority notification'),
('HIGH', 'High', 3, '#F59E0B', 'High priority notification'),
('URGENT', 'Urgent', 4, '#EF4444', 'Urgent priority notification');
```

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
- **All Lookup Tables** (11 bảng): roles, permissions, job_statuses, job_types, priorities, experience_levels, interview_types, interview_statuses, interview_results, notification_types, notification_priorities
- **Core Business Entities**: users, companies, jobs, skills, interviews, resumes, attachments

### ⚠️ **PARTIAL AUDIT FIELDS** (created_by, created_at, updated_at):
- **Junction Tables**: user_skills, job_skills, job_resumes
- **Lý do**: Junction tables ít khi update, không cần track updated_by

### 🔧 **SYSTEM TABLES** (created_at, updated_at only):
- **System Generated**: notifications, user_sessions, audit_logs
- **Lý do**: System generated, không cần user tracking

### 🗑️ **SOFT DELETE STRATEGY - CHI TIẾT LÝ DO:**

#### **1. deleted_at (TIMESTAMP) - Business Entities & Lookup Tables:**
**Bảng sử dụng**: 
- **Business Entities**: users, companies, jobs, skills, interviews, resumes, attachments
- **Lookup Tables**: roles, permissions, job_statuses, job_types, priorities, experience_levels, interview_types, interview_statuses, interview_results, notification_types, notification_priorities

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

**Lookup Tables (Admin Management):**
```sql
-- Tìm job statuses đã bị admin xóa
SELECT * FROM job_statuses 
WHERE deleted_at IS NOT NULL;

-- Audit: Admin nào đã xóa role nào khi nào
SELECT r.name, u.email, r.deleted_at 
FROM roles r 
JOIN users u ON r.updated_by = u.id 
WHERE r.deleted_at IS NOT NULL;

-- Kiểm tra xem có jobs nào đang dùng status đã bị xóa
SELECT j.title, js.name as status_name, js.deleted_at
FROM jobs j 
JOIN job_statuses js ON j.status_id = js.id 
WHERE js.deleted_at IS NOT NULL;

-- Restore job status đã bị xóa nhầm
UPDATE job_statuses 
SET deleted_at = NULL, updated_at = NOW() 
WHERE id = ? AND deleted_at IS NOT NULL;
```

#### **2. is_deleted (BOOLEAN) - Junction Tables:**
**Bảng sử dụng**: user_skills, job_skills, job_resumes

**Lý do sử dụng BOOLEAN:**
- **Performance**: Boolean queries nhanh hơn timestamp comparisons
- **Simplicity**: Chỉ cần biết có bị xóa hay không, không cần khi nào
- **Index Efficiency**: Boolean index nhỏ hơn timestamp index
- **Query Optimization**: `WHERE is_deleted = FALSE` nhanh hơn `WHERE deleted_at IS NULL`
- **Memory Usage**: 1 byte vs 8 bytes cho timestamp

**Ví dụ use cases:**
```sql
-- Tìm skills active của user
SELECT s.name FROM user_skills us
JOIN skills s ON us.skill_id = s.id
WHERE us.user_id = ? AND us.is_deleted = FALSE;

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

**Lookup Tables (Admin Management):**
```sql
-- Job Statuses table
CREATE TABLE job_statuses (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(50) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    -- ... other fields
    deleted_at TIMESTAMP NULL,
    
    INDEX idx_deleted_at (deleted_at),
    INDEX idx_name_active (name, deleted_at) -- Composite index
);

-- Query active job statuses
SELECT * FROM job_statuses WHERE deleted_at IS NULL;

-- Query deleted job statuses (admin can restore)
SELECT * FROM job_statuses WHERE deleted_at IS NOT NULL;

-- Check if any jobs are using deleted status
SELECT COUNT(*) FROM jobs j 
JOIN job_statuses js ON j.status_id = js.id 
WHERE js.deleted_at IS NOT NULL;
```

#### **2. Junction Tables với is_deleted:**
```sql
-- User Skills table
CREATE TABLE user_skills (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id VARCHAR(36) NOT NULL,
    skill_id VARCHAR(36) NOT NULL,
    -- ... other fields
    is_deleted BOOLEAN DEFAULT FALSE,
    
    INDEX idx_user_skill_active (user_id, skill_id, is_deleted),
    INDEX idx_is_deleted (is_deleted)
);

-- Query active skills
SELECT * FROM user_skills WHERE is_deleted = FALSE;

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

#### **1. Admin Management Requirements:**
```sql
-- Admin có thể thêm job status mới
INSERT INTO job_statuses (name, display_name, color) 
VALUES ('On Hold', 'On Hold', '#FFA500');

-- Admin có thể xóa job status (soft delete)
UPDATE job_statuses 
SET deleted_at = NOW(), updated_by = ? 
WHERE id = ?;

-- Admin có thể restore job status đã xóa
UPDATE job_statuses 
SET deleted_at = NULL, updated_at = NOW() 
WHERE id = ? AND deleted_at IS NOT NULL;
```

#### **2. Data Integrity Protection:**
```sql
-- Kiểm tra trước khi xóa: Có jobs nào đang dùng status này không?
SELECT COUNT(*) FROM jobs 
WHERE status_id = ? AND deleted_at IS NULL;

-- Nếu có jobs đang dùng, không cho phép xóa hard
-- Chỉ cho phép soft delete để bảo vệ data integrity
```

#### **3. Business Continuity:**
```sql
-- Khi admin xóa nhầm job status
-- Có thể restore ngay lập tức mà không ảnh hưởng existing data
UPDATE job_statuses 
SET deleted_at = NULL 
WHERE name = 'Applied' AND deleted_at IS NOT NULL;

-- Existing jobs vẫn hoạt động bình thường
SELECT j.title, js.display_name 
FROM jobs j 
JOIN job_statuses js ON j.status_id = js.id 
WHERE j.deleted_at IS NULL;
```

#### **4. Audit Trail cho Admin Actions:**
```sql
-- Track admin actions trên lookup tables
SELECT 
    js.name,
    u.email as admin_email,
    js.deleted_at,
    js.updated_at
FROM job_statuses js
JOIN users u ON js.updated_by = u.id
WHERE js.deleted_at IS NOT NULL
ORDER BY js.deleted_at DESC;
```

#### **5. Rollback Capability:**
```sql
-- Admin có thể rollback toàn bộ changes
UPDATE job_statuses 
SET deleted_at = NULL, updated_at = NOW() 
WHERE deleted_at BETWEEN '2024-01-01' AND '2024-01-31';

-- Hoặc rollback specific changes
UPDATE job_statuses 
SET deleted_at = NULL 
WHERE id IN (1, 2, 3) AND deleted_at IS NOT NULL;
```

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

#### **2.1. Users ↔ Jobs (One-to-Many)**
```sql
-- Quan hệ: 1 user có thể có nhiều jobs
jobs.user_id → users.id
```
- **Mục đích**: Tracking jobs của từng user
- **Cardinality**: 1:N (1 user → N jobs)
- **Foreign Key**: `jobs.user_id` → `users.id`
- **Constraint**: `ON DELETE CASCADE` (xóa user thì xóa jobs)

#### **2.2. Companies ↔ Jobs (One-to-Many)**
```sql
-- Quan hệ: 1 company có thể có nhiều jobs
jobs.company_id → companies.id
```
- **Mục đích**: Tracking jobs của từng company
- **Cardinality**: 1:N (1 company → N jobs)
- **Foreign Key**: `jobs.company_id` → `companies.id`
- **Constraint**: `ON DELETE RESTRICT` (không cho xóa company nếu còn jobs)

#### **2.3. Job Statuses ↔ Jobs (One-to-Many)**
```sql
-- Quan hệ: 1 status có thể có nhiều jobs
jobs.status_id → job_statuses.id
```
- **Mục đích**: Tracking trạng thái jobs (APPLIED, INTERVIEW, OFFER, REJECTED)
- **Cardinality**: 1:N (1 status → N jobs)
- **Foreign Key**: `jobs.status_id` → `job_statuses.id`

#### **2.4. Job Types ↔ Jobs (One-to-Many)**
```sql
-- Quan hệ: 1 type có thể có nhiều jobs
jobs.job_type_id → job_types.id
```
- **Mục đích**: Phân loại jobs (FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP)
- **Cardinality**: 1:N (1 type → N jobs)
- **Foreign Key**: `jobs.job_type_id` → `job_types.id`

#### **2.5. Priorities ↔ Jobs (One-to-Many)**
```sql
-- Quan hệ: 1 priority có thể có nhiều jobs
jobs.priority_id → priorities.id
```
- **Mục đích**: Độ ưu tiên jobs (HIGH, MEDIUM, LOW)
- **Cardinality**: 1:N (1 priority → N jobs)
- **Foreign Key**: `jobs.priority_id` → `priorities.id`

#### **2.6. Experience Levels ↔ Jobs (One-to-Many)**
```sql
-- Quan hệ: 1 level có thể có nhiều jobs
jobs.experience_level_id → experience_levels.id
```
- **Mục đích**: Yêu cầu kinh nghiệm (ENTRY, MID, SENIOR, LEAD)
- **Cardinality**: 1:N (1 level → N jobs)
- **Foreign Key**: `jobs.experience_level_id` → `experience_levels.id`

### 📋 **3. SKILLS MANAGEMENT RELATIONSHIPS**

#### **3.1. Users ↔ Skills (Many-to-Many)**
```sql
-- Junction table: user_skills
CREATE TABLE user_skills (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id VARCHAR(36) NOT NULL,
    skill_id VARCHAR(36) NOT NULL,
    proficiency_level VARCHAR(50) NOT NULL,
    years_of_experience DECIMAL(3,1),
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    is_deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE,
    
    UNIQUE KEY uk_user_skill (user_id, skill_id),
    INDEX idx_user_id (user_id),
    INDEX idx_skill_id (skill_id),
    INDEX idx_proficiency_level (proficiency_level)
);
```
- **Mục đích**: Tracking skills của users
- **Cardinality**: M:N (1 user → N skills, 1 skill → N users)
- **Additional Fields**: proficiency_level, years_of_experience, is_verified

#### **3.2. Jobs ↔ Skills (Many-to-Many)**
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

### 📋 **4. INTERVIEW MANAGEMENT RELATIONSHIPS**

#### **4.1. Jobs ↔ Interviews (One-to-Many)**
```sql
-- Quan hệ: 1 job có thể có nhiều interviews
interviews.job_id → jobs.id
```
- **Mục đích**: Tracking interviews của jobs
- **Cardinality**: 1:N (1 job → N interviews)
- **Foreign Key**: `interviews.job_id` → `jobs.id`

#### **4.2. Users ↔ Interviews (One-to-Many)**
```sql
-- Quan hệ: 1 user có thể có nhiều interviews
interviews.user_id → users.id
```
- **Mục đích**: Tracking interviews của users
- **Cardinality**: 1:N (1 user → N interviews)
- **Foreign Key**: `interviews.user_id` → `users.id`

#### **4.3. Interview Types ↔ Interviews (One-to-Many)**
```sql
-- Quan hệ: 1 type có thể có nhiều interviews
interviews.interview_type_id → interview_types.id
```
- **Mục đích**: Phân loại interviews (PHONE, VIDEO, ONSITE, TECHNICAL)
- **Cardinality**: 1:N (1 type → N interviews)
- **Foreign Key**: `interviews.interview_type_id` → `interview_types.id`

#### **4.4. Interview Statuses ↔ Interviews (One-to-Many)**
```sql
-- Quan hệ: 1 status có thể có nhiều interviews
interviews.interview_status_id → interview_statuses.id
```
- **Mục đích**: Trạng thái interviews (SCHEDULED, COMPLETED, CANCELLED)
- **Cardinality**: 1:N (1 status → N interviews)
- **Foreign Key**: `interviews.interview_status_id` → `interview_statuses.id`

#### **4.5. Interview Results ↔ Interviews (One-to-Many)**
```sql
-- Quan hệ: 1 result có thể có nhiều interviews
interviews.interview_result_id → interview_results.id
```
- **Mục đích**: Kết quả interviews (PASSED, FAILED, PENDING)
- **Cardinality**: 1:N (1 result → N interviews)
- **Foreign Key**: `interviews.interview_result_id` → `interview_results.id`

### 📋 **5. RESUME MANAGEMENT RELATIONSHIPS**

#### **5.1. Users ↔ Resumes (One-to-Many)**
```sql
-- Quan hệ: 1 user có thể có nhiều resumes
resumes.user_id → users.id
```
- **Mục đích**: Tracking resumes của users
- **Cardinality**: 1:N (1 user → N resumes)
- **Foreign Key**: `resumes.user_id` → `users.id`

#### **5.2. Jobs ↔ Resumes (Many-to-Many)**
```sql
-- Junction table: job_resumes
CREATE TABLE job_resumes (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    job_id VARCHAR(36) NOT NULL,
    resume_id VARCHAR(36) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    is_deleted BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE,
    
    UNIQUE KEY uk_job_resume (job_id, resume_id),
    INDEX idx_job_id (job_id),
    INDEX idx_resume_id (resume_id),
    INDEX idx_is_primary (is_primary)
);
```
- **Mục đích**: Tracking resumes được sử dụng cho jobs
- **Cardinality**: M:N (1 job → N resumes, 1 resume → N jobs)
- **Additional Fields**: is_primary (resume chính cho job)

### 📋 **6. NOTIFICATION SYSTEM RELATIONSHIPS**

#### **6.1. Users ↔ Notifications (One-to-Many)**
```sql
-- Quan hệ: 1 user có thể có nhiều notifications
notifications.user_id → users.id
```
- **Mục đích**: Tracking notifications của users
- **Cardinality**: 1:N (1 user → N notifications)
- **Foreign Key**: `notifications.user_id` → `users.id`

#### **6.2. Notification Types ↔ Notifications (One-to-Many)**
```sql
-- Quan hệ: 1 type có thể có nhiều notifications
notifications.notification_type_id → notification_types.id
```
- **Mục đích**: Phân loại notifications (JOB_APPLICATION, INTERVIEW_REMINDER, OFFER_RECEIVED)
- **Cardinality**: 1:N (1 type → N notifications)
- **Foreign Key**: `notifications.notification_type_id` → `notification_types.id`

#### **6.3. Notification Priorities ↔ Notifications (One-to-Many)**
```sql
-- Quan hệ: 1 priority có thể có nhiều notifications
notifications.notification_priority_id → notification_priorities.id
```
- **Mục đích**: Độ ưu tiên notifications (HIGH, MEDIUM, LOW)
- **Cardinality**: 1:N (1 priority → N notifications)
- **Foreign Key**: `notifications.notification_priority_id` → `notification_priorities.id`

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

### 📋 **8. ATTACHMENT RELATIONSHIPS**

#### **8.1. Users ↔ Attachments (One-to-Many)**
```sql
-- Quan hệ: 1 user có thể có nhiều attachments
attachments.user_id → users.id
```
- **Mục đích**: Tracking attachments của users
- **Cardinality**: 1:N (1 user → N attachments)
- **Foreign Key**: `attachments.user_id` → `users.id`

#### **8.2. Jobs ↔ Attachments (One-to-Many)**
```sql
-- Quan hệ: 1 job có thể có nhiều attachments
attachments.job_id → jobs.id
```
- **Mục đích**: Tracking attachments của jobs
- **Cardinality**: 1:N (1 job → N attachments)
- **Foreign Key**: `attachments.job_id` → `jobs.id`

## 🔄 **QUAN HỆ TỔNG QUAN (ENTITY RELATIONSHIP DIAGRAM)**

### **Core Entities:**
- **users** (trung tâm) ↔ **jobs**, **resumes**, **interviews**, **notifications**, **attachments**
- **companies** ↔ **jobs**
- **jobs** (trung tâm) ↔ **skills**, **resumes**, **interviews**, **attachments**

### **Lookup Tables:**
- **roles** ↔ **users**
- **job_statuses**, **job_types**, **priorities**, **experience_levels** ↔ **jobs**
- **interview_types**, **interview_statuses**, **interview_results** ↔ **interviews**
- **notification_types**, **notification_priorities** ↔ **notifications**

### **Junction Tables:**
- **role_permissions** (roles ↔ permissions)
- **user_skills** (users ↔ skills)
- **job_skills** (jobs ↔ skills)
- **job_resumes** (jobs ↔ resumes)

### **System Tables:**
- **user_sessions** ↔ **users**
- **audit_logs** ↔ **users**

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
CREATE INDEX idx_user_status ON jobs(user_id, status_id);
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
