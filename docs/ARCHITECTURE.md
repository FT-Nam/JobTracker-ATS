# 🏗️ JobTracker ATS Architecture Guide

## 📋 Tổng quan kiến trúc

JobTracker ATS (Applicant Tracking System) sử dụng kiến trúc **Monolithic Multi-Tenant** với thiết kế modular, đảm bảo tính đơn giản trong phát triển và triển khai ban đầu, đồng thời có thể dễ dàng tách thành microservices trong tương lai.

### 🎯 Kiến trúc Multi-Tenant
- **Cô lập Tenant**: Mỗi company = 1 tenant, cô lập dữ liệu bằng `company_id`
- **Database dùng chung**: Single database với tách biệt dữ liệu multi-tenant
- **Bảo mật cấp hàng**: Tất cả truy vấn tự động lọc theo `company_id`
- **Khả năng mở rộng**: Dễ dàng mở rộng cho nhiều SME/Startup

## 🎯 Kiến trúc tổng thể

```
┌─────────────────────────────────────────────────────────────┐
│                    Frontend (React + JavaScript)            │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   Auth      │ │   Jobs      │ │ Applications│           │
│  │   Module    │ │   Module    │ │   Module    │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ Dashboard   │ │ Interviews  │ │ Comments   │           │
│  │   Module    │ │   Module    │ │   Module    │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ HTTPS/REST API
                              │ WebSocket
                              ▼
┌─────────────────────────────────────────────────────────────┐
│         Backend (Spring Boot 3) - Multi-Tenant              │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │  Security   │ │   Business  │ │   Data      │           │
│  │   Layer     │ │   Logic     │ │   Access    │           │
│  │(Multi-Tenant│ │   Layer     │ │   Layer     │           │
│  │  Filter)    │ │             │ │(Company_ID) │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ JPA/Hibernate
                              │ (Auto-filter by company_id)
                              ▼
┌─────────────────────────────────────────────────────────────┐
│         Database (MySQL 8.0) - Multi-Tenant                │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ Companies   │ │   Jobs      │ │Applications │           │
│  │  (Tenants)  │ │ (Postings)  │ │  (CORE ATS) │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   Users     │ │ Interviews  │ │  Comments  │           │
│  │(HR/Recruiter│ │             │ │             │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
```

## 🔧 Công nghệ chi tiết

### Backend Stack

#### Core Framework
- **Spring Boot 3.2+**: Framework chính, hỗ trợ Java 21
- **Java 21**: LTS version với Virtual Threads, Pattern Matching
- **Spring Framework 6**: Dependency injection, AOP, MVC

#### Data Layer
- **Spring Data JPA**: Lớp trừu tượng ORM
- **Hibernate 6**: Triển khai JPA với cải thiện hiệu suất
- **MySQL 8.0**: Database chính với hỗ trợ JSON
- **HikariCP**: Pool kết nối (mặc định trong Spring Boot 3)

#### Security
- **Spring Security 6**: Xác thực và Phân quyền
- **OAuth2 Resource Server**: Xác thực JWT token từ Authorization Server
- **OAuth2 Client**: Tích hợp đăng nhập Google
- **BCrypt**: Băm mật khẩu
- **CORS**: Chia sẻ tài nguyên đa nguồn gốc
- **Bảo mật Multi-Tenant**: Cô lập dữ liệu theo công ty với `@Filter` và lọc `company_id`
- **RBAC**: Kiểm soát truy cập dựa trên vai trò (COMPANY_ADMIN, RECRUITER, HIRING_MANAGER, INTERVIEWER)

#### Validation & Processing
- **Jakarta Validation**: Xác thực Bean (JSR-380)
- **Hibernate Validator**: Triển khai xác thực
- **MapStruct**: Ánh xạ Entity ↔ DTO
- **Jackson**: Tuần tự hóa/Giải tuần tự hóa JSON

#### Communication
- **Spring Web**: Điểm cuối REST API
- **Spring WebSocket**: Thông báo thời gian thực
- **STOMP**: Giao thức con WebSocket
- **Brevo API**: Gửi email transactional (thay thế Spring Mail)
- **Thymeleaf**: Mẫu email (optional, có thể dùng Brevo templates)

#### External Integrations
- **Cloudinary API**: Dịch vụ lưu trữ file và quản lý media
- **Google OAuth2**: Đăng nhập xã hội
- **Brevo API**: Gửi email và quản lý email marketing

#### Scheduling & Events
- **Spring @Scheduled**: Cron jobs cho nhắc nhở
- **ApplicationEventPublisher**: Kiến trúc hướng sự kiện
- **@Async**: Xử lý bất đồng bộ

#### Documentation & Monitoring
- **SpringDoc OpenAPI 3**: Tài liệu API
- **Spring Boot Actuator**: Kiểm tra sức khỏe, số liệu
- **SLF4J + Logback**: Framework ghi log
- **Micrometer**: Số liệu ứng dụng

### Frontend Stack

#### Core Framework
- **React 18**: Thư viện UI với Concurrent Features
- **JavaScript ES6+**: Tính năng JavaScript hiện đại
- **Create React App (CRA)**: Công cụ build và development server
- **Webpack**: Module bundler (tích hợp sẵn trong CRA)

#### State Management
- **Redux Toolkit**: Container trạng thái dự đoán được
- **RTK Query**: Lấy dữ liệu và caching
- **React Redux**: Liên kết React

#### Routing & Navigation
- **React Router v6**: Định tuyến phía client
- **React Router DOM**: Định tuyến trình duyệt
- **Lazy Loading**: Tách code cho hiệu suất

#### UI & Styling
- **TailwindCSS**: Framework CSS utility-first
- **shadcn/ui**: Thư viện component có sẵn
- **Lucide React**: Thư viện icon
- **React Hook Form**: Quản lý form
- **Yup**: Xác thực schema

#### Data & Communication
- **Axios**: HTTP client với interceptors
- **React Query**: Quản lý trạng thái server
- **WebSocket**: Giao tiếp thời gian thực
- **React Toastify**: Thông báo toast

#### Charts & Visualization
- **Recharts**: Thư viện biểu đồ
- **React Quill**: Trình soạn thảo văn bản phong phú
- **React Dropzone**: Tải file lên
- **dayjs**: Thao tác ngày tháng

### Database Design

#### Primary Database: MySQL 8.0
- **ACID Compliance**: Tính toàn vẹn giao dịch
- **JSON Support**: Lưu trữ dữ liệu linh hoạt
- **Full-text Search**: Khả năng tìm kiếm nâng cao
- **Indexing**: Tối ưu hiệu suất (đặc biệt composite indexes multi-tenant)
- **Replication**: Tính khả dụng cao
- **Kiến trúc Multi-Tenant**: Cô lập dữ liệu bằng `company_id` trong tất cả bảng nghiệp vụ
- **UUID Primary Keys**: VARCHAR(36) cho tất cả primary keys (bảo mật & hệ thống phân tán)

#### Connection Management
- **HikariCP**: Pool kết nối hiệu suất cao
- **Kích thước Pool**: 10-20 kết nối
- **Cấu hình Timeout**: Timeout kết nối 30s
- **Health Checks**: Xác thực kết nối

### External Services

#### File Storage: Cloudinary
- **REST API**: Tải lên/Tải xuống file và hình ảnh
- **Image Transformation**: Tự động resize, crop, optimize hình ảnh
- **Video Support**: Quản lý video files
- **CDN Delivery**: Phân phối nội dung qua CDN
- **API Key Authentication**: Xác thực bằng API key và secret
- **Public/Private URLs**: Hỗ trợ cả public và private file access

#### Email Service: Brevo (formerly Sendinblue)
- **Brevo API**: REST API để gửi transactional emails
- **Template Management**: Quản lý email templates trên Brevo dashboard
- **Email Tracking**: Theo dõi email delivery, opens, clicks
- **Async Processing**: Gửi email không chặn
- **Retry Logic**: Xử lý email thất bại với retry mechanism
- **SMTP Alternative**: Có thể dùng SMTP relay nếu cần

#### Authentication: Google OAuth2
- **OAuth2 Client**: Đăng nhập xã hội
- **User Profile**: Tích hợp tài khoản Google
- **Token Management**: Access/refresh tokens

## 🏛️ Kiến trúc Backend (Monolithic)

### Package Structure
```
com.jobtracker
├── config/                 # Configuration classes
│   ├── SecurityConfig.java
│   ├── WebConfig.java
│   ├── DatabaseConfig.java
│   └── WebSocketConfig.java
├── controller/             # REST Controllers
│   ├── AuthController.java
│   ├── JobController.java      # Job Postings (ATS)
│   ├── ApplicationController.java ➕ # Applications (CORE ATS)
│   ├── CommentController.java ➕
│   ├── InterviewController.java
│   ├── UserController.java
│   ├── CompanyController.java
│   ├── FileController.java      # Attachments
│   ├── NotificationController.java
│   └── DashboardController.java
├── dto/                    # Data Transfer Objects
│   ├── request/           # Request DTOs
│   └── response/          # Response DTOs
├── entity/                 # JPA Entities
│   ├── User.java           # HR/Recruiter (multi-tenant với company_id)
│   ├── Company.java        # Tenant (multi-tenant root)
│   ├── Job.java            # Job Postings (ATS semantic)
│   ├── Application.java    # Applications (CORE ATS entity) ➕
│   ├── ApplicationStatus.java ➕ # Application status lookup table entity
│   ├── ApplicationStatusHistory.java ➕
│   ├── Comment.java        # Comments on applications ➕
│   ├── Interview.java      # Interviews (link to applications)
│   ├── Attachment.java     # Attachments (link to applications)
│   ├── Skill.java          # Skills
│   ├── Role.java           # RBAC Roles
│   └── Permission.java     # RBAC Permissions
├── repository/             # Data Access Layer
│   ├── UserRepository.java
│   ├── CompanyRepository.java
│   ├── JobRepository.java
│   ├── ApplicationRepository.java ➕
│   ├── ApplicationStatusHistoryRepository.java ➕
│   ├── CommentRepository.java ➕
│   ├── InterviewRepository.java
│   ├── AttachmentRepository.java
│   └── SkillRepository.java
├── service/                # Business Logic Layer
│   ├── AuthService.java
│   ├── CompanyService.java      # Multi-tenant management
│   ├── UserService.java         # HR/Recruiter management
│   ├── JobService.java          # Job Postings (ATS)
│   ├── ApplicationService.java ➕ # Applications (CORE ATS)
│   ├── CommentService.java ➕
│   ├── InterviewService.java
│   ├── AttachmentService.java
│   ├── CloudinaryService.java      # Cloudinary integration ➕
│   ├── BrevoService.java           # Brevo email integration ➕
│   ├── NotificationService.java
│   └── DashboardService.java
├── security/               # Security Components
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   ├── CustomUserDetailsService.java
│   ├── TenantFilter.java ➕        # Multi-tenant data filtering
│   └── CompanySecurityContext.java ➕ # Company context holder
├── event/                  # Event Handling
│   ├── ApplicationReceivedEvent.java ➕
│   ├── ApplicationStatusChangedEvent.java ➕
│   ├── InterviewScheduledEvent.java
│   ├── JobDeadlineEvent.java
│   └── EventListener.java
├── scheduler/              # Scheduled Tasks
│   └── ReminderScheduler.java
├── exception/              # Exception Handling
│   ├── GlobalExceptionHandler.java
│   └── BusinessException.java
├── util/                   # Utility Classes
│   ├── DateUtils.java
│   └── ValidationUtils.java
└── JobTrackerApplication.java
```

### Layer Responsibilities

#### 1. Controller Layer
- **Điểm cuối REST API**
- **Ánh xạ Request/Response**
- **Xác thực đầu vào**
- **Xử lý lỗi**
- **Kiểm tra xác thực**

#### 2. Service Layer
- **Triển khai logic nghiệp vụ**
- **Quản lý giao dịch**
- **Tích hợp dịch vụ bên ngoài**
- **Xuất bản sự kiện**
- **Chuyển đổi dữ liệu**

#### 3. Repository Layer
- **Trừu tượng truy cập dữ liệu**
- **Truy vấn tùy chỉnh**
- **Hỗ trợ phân trang**
- **Specification pattern**

#### 4. Entity Layer
- **Ánh xạ database**
- **Định nghĩa quan hệ**
- **Ràng buộc xác thực**
- **Trường audit**
- **Trường multi-tenant**: `company_id` trong tất cả business entities
- **Hibernate Filters**: Tự động lọc theo `company_id`

## 📋 ATS Workflow Architecture

### 🎯 Modern ATS = Candidate Self-Service Portal

**Core Principle**: Modern ATS là **Candidate Self-Service Portal**, không phải Document Management System.

- **Primary Workflow**: Candidates tự apply online qua trang công ty mà **không cần login**
- **Secondary Workflow**: HR có thể manually upload CVs từ email (backup workflow)
- **Automated Workflow**: Sau khi application được tạo, workflow tự động (status updates, notifications, interviews)

### Application Lifecycle (CORE ATS)
```
1. Job Posting Created (DRAFT)
   ↓
2. Job Published (PUBLISHED) → Candidates can apply
   ↓
3. Application Received (NEW)
   ↓
4. Screening Phase (SCREENING) → HR reviews CV
   ↓
5. Interview Phase (INTERVIEWING) → Multiple interview rounds
   ↓
6. Offer Phase (OFFERED) → Job offer extended
   ↓
7. Final Status (HIRED or REJECTED)
```

### Các thành phần ATS chính
- **Applications**: Entity cốt lõi - ứng viên ứng tuyển vào job postings
  - **Primary Workflow**: Candidate Self-Service Portal (public API, không cần login)
  - **Secondary Workflow**: HR Manual Upload (protected API, khi nhận CV qua email)
- **Application Status History**: Dấu vết audit cho thay đổi trạng thái
- **Comments**: Cộng tác nhóm về ứng viên (HR/Recruiter only)
- **Interviews**: Nhiều vòng phỏng vấn cho mỗi application
- **Attachments**: CV, chứng chỉ, portfolio
  - **Public Upload**: Candidates tự upload qua public API (user_id = NULL)
  - **HR Upload**: HR upload thủ công khi nhận CV qua email (user_id = HR user_id)
- **Notifications**: Cập nhật thời gian thực về trạng thái application

### Luồng dữ liệu Multi-Tenant
```
User Login → Trích xuất company_id từ JWT → Đặt Tenant Context
                ↓
API Request → Tenant Filter → Tự động lọc theo company_id
                ↓
Database Query → WHERE company_id = :tenantId → Trả về dữ liệu cô lập
```

## 🔄 Data Flow

### 1. Authentication Flow (Multi-Tenant)
```
User Login → OAuth2 Authorization Server → JWT Token (with company_id) → Resource Server Validation
                ↓
Extract company_id from JWT → Set Tenant Context → User Info
                ↓
OAuth2UserService ← Token Validation ← JWT Claims (company_id, role, permissions)
```

### 2. Job Posting Flow (ATS)
```
Create Job Posting → JobController → JobService → JobRepository → Database
                ↓
Publish Job → Event Publishing → NotificationService → Email/WebSocket
```

### 3. Application Workflow (CORE ATS) ➕

#### Primary Workflow: Candidate Self-Service Portal (Public API)
```
Candidate Applies Online (Public API - No Auth)
    ↓
POST /public/jobs/{jobId}/apply
    ↓
Upload CV + Attachments (Public API)
    ↓
Application Created (status = NEW, created_by = NULL)
    ↓
Email Confirmation → Candidate receives application_token
    ↓
Candidate Tracks Status (Public API with token)
    ↓
HR Reviews → Status Updates → Automated Workflow
```

#### Secondary Workflow: HR Manual Upload (Protected API)
```
HR Receives CV via Email
    ↓
POST /applications (Protected - HR Auth Required)
    ↓
Upload Attachments (Protected - HR Auth Required)
    ↓
Application Created (status = NEW, created_by = HR user_id)
    ↓
HR Manages → Status Updates → Automated Workflow
```

#### Common Workflow (After Application Created)
```
Status Update (NEW → SCREENING → INTERVIEWING → OFFERED → HIRED/REJECTED)
                ↓
ApplicationStatusHistory → Comments → Interviews → Attachments
                ↓
Event Publishing → NotificationService → Email/WebSocket
```

### 4. File Upload Flow (Attachments to Applications)

#### Public Upload Flow (Candidate Self-Service)
```
Candidate Uploads CV/Attachments (Public API - No Auth)
    ↓
POST /public/jobs/{jobId}/apply (multipart/form-data)
    ↓
AttachmentController (Public) → AttachmentService → CloudinaryService → Cloudinary API
    ↓
File Validation (size, type, virus scan) → Upload to Cloudinary
    ↓
CDN URL Generation → Link to Application (user_id = NULL)
    ↓
Database Update → File Metadata (public_id, format, size, user_id = NULL)
```

#### Protected Upload Flow (HR Manual Upload)
```
HR Uploads CV/Attachments (Protected API - Auth Required)
    ↓
POST /applications/{applicationId}/attachments (multipart/form-data)
    ↓
AttachmentController (Protected) → AttachmentService → CloudinaryService → Cloudinary API
    ↓
File Validation → Upload to Cloudinary
    ↓
CDN URL Generation → Link to Application (user_id = HR user_id)
    ↓
Database Update → File Metadata (public_id, format, size, user_id = HR user_id)
```

## 🚀 Performance Considerations

### Tối ưu Database
- **Chiến lược Indexing**: Primary keys, foreign keys, các trường tìm kiếm
- **Indexes Multi-Tenant**: Composite indexes trên `(company_id, ...)` cho tất cả truy vấn
- **Tối ưu truy vấn**: Ngăn chặn vấn đề N+1, tự động lọc `company_id`
- **Connection Pooling**: Cấu hình HikariCP
- **Caching**: Spring Cache với Redis (tương lai) - caching theo tenant

### Hiệu suất ứng dụng
- **Lazy Loading**: Quan hệ JPA
- **Pagination**: Xử lý dataset lớn
- **Xử lý bất đồng bộ**: Email, tải file lên
- **Connection Pooling**: Kết nối database

### Hiệu suất Frontend
- **Code Splitting**: Tách dựa trên route
- **Lazy Loading**: Tải component lười
- **Memoization**: React.memo, useMemo
- **Tối ưu Bundle**: Tối ưu build CRA

## 🏢 Multi-Tenant Architecture

### Mô hình Tenant
- **Company là Tenant**: Mỗi company = 1 tenant trong hệ thống
- **Cô lập dữ liệu**: Tất cả dữ liệu nghiệp vụ được cô lập bằng `company_id`
- **Database dùng chung**: Single database với bảo mật cấp hàng
- **Tenant Context**: JWT token chứa `company_id`, tự động inject vào mọi request

### Chiến lược cô lập dữ liệu
- **Hibernate Filter**: `@FilterDef` và `@Filter` để tự động lọc theo `company_id`
- **Repository Level**: Tất cả truy vấn tự động thêm `WHERE company_id = :tenantId`
- **Service Level**: Xác thực user thuộc company trước khi truy cập dữ liệu
- **Controller Level**: Trích xuất `company_id` từ JWT token hoặc user context

### Multi-Tenant Implementation
```java
// Entity level - Auto filter
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = "string"))
@Filter(name = "tenantFilter", condition = "company_id = :tenantId")
@Entity
public class Application {
    @Column(name = "company_id", nullable = false)
    private String companyId;
}

// Service level - Set tenant context
@Service
public class ApplicationService {
    @Autowired
    private TenantContext tenantContext;
    
    public List<Application> getAllApplications() {
        String companyId = tenantContext.getCurrentCompanyId();
        return applicationRepository.findByCompanyId(companyId);
    }
}
```

### Bảo mật Tenant
- **JWT Claims**: `company_id` trong JWT token
- **Xác thực Context**: Xác minh `company_id` của user khớp với request context
- **Ngăn chặn Cross-Tenant**: Không cho phép truy cập dữ liệu của tenant khác
- **Audit Trail**: Ghi log tất cả các nỗ lực truy cập cross-tenant

## 🔒 Security Architecture

### Xác thực
- **JWT Tokens**: Xác thực không trạng thái
- **Refresh Tokens**: Gia hạn token
- **OAuth2**: Tích hợp đăng nhập xã hội
- **Password Hashing**: BCrypt

### Phân quyền
- **Truy cập dựa trên vai trò**: Các vai trò COMPANY_ADMIN, RECRUITER, HIRING_MANAGER, INTERVIEWER
- **Truy cập dựa trên quyền**: Quyền chi tiết (JOB_CREATE, APPLICATION_VIEW, etc.)
- **Bảo mật cấp phương thức**: @PreAuthorize với company context
- **Bảo mật cấp tài nguyên**: Cô lập dữ liệu multi-tenant (tự động lọc `company_id`)
- **Cô lập Tenant**: Hibernate Filter để tự động lọc theo `company_id`

### Bảo vệ dữ liệu
- **Xác thực đầu vào**: Jakarta Validation
- **Ngăn chặn SQL Injection**: JPA/Hibernate
- **Bảo vệ XSS**: Làm sạch đầu vào
- **Cấu hình CORS**: Bảo mật đa nguồn gốc
- **Cô lập dữ liệu Multi-Tenant**: Tự động lọc `company_id` ở tất cả truy vấn
- **Xác thực Tenant Context**: Xác minh user thuộc company trước khi truy cập dữ liệu

## 📊 Monitoring & Observability

### Số liệu ứng dụng
- **Spring Boot Actuator**: Health checks, số liệu
- **Micrometer**: Số liệu ứng dụng
- **Custom Metrics**: Số liệu nghiệp vụ

### Chiến lược ghi log
- **Structured Logging**: Định dạng JSON
- **Log Levels**: DEBUG, INFO, WARN, ERROR
- **Correlation IDs**: Theo dõi request
- **Audit Logging**: Hành động của user

### Xử lý lỗi
- **Global Exception Handler**: Xử lý lỗi tập trung
- **Custom Exceptions**: Lỗi cụ thể nghiệp vụ
- **Error Response Format**: Định dạng lỗi nhất quán
- **Error Monitoring**: Theo dõi exception

## 🔄 Deployment Architecture

### Development Environment
```
Developer Machine → Local MySQL → Spring Boot App → React Dev Server
```

### Production Environment (Multi-Tenant)
```
Load Balancer → Spring Boot App (Multi-Tenant) → MySQL Cluster (Shared Database)
                ↓
Tenant Isolation Layer → Company-based Data Filtering → External Services
```

### Docker Architecture
```
Docker Compose:
├── jobtracker-app (Spring Boot)
├── jobtracker-frontend (React + CRA)
├── mysql-db (MySQL 8.0)
├── redis-cache (Redis - future)
└── nginx-proxy (Reverse Proxy)
```

## 🎯 Scalability Considerations

### Mở rộng ngang
- **Thiết kế không trạng thái**: Xác thực dựa trên JWT
- **Sẵn sàng Multi-Tenant**: Không trạng thái với company context trong JWT
- **Pool kết nối Database**: HikariCP
- **Sẵn sàng Load Balancer**: Nhiều instance ứng dụng (shared database)
- **Cô lập Tenant**: Mỗi request tự động lọc theo `company_id`

### Mở rộng dọc
- **Tối ưu bộ nhớ**: Điều chỉnh JVM
- **Tối ưu Database**: Tối ưu truy vấn
- **Chiến lược Caching**: Caching cấp ứng dụng

### Di chuyển Microservices trong tương lai
- **Thiết kế Modular**: Ranh giới service rõ ràng (Jobs, Applications, Interviews, etc.)
- **Kiến trúc hướng sự kiện**: Ghép nối lỏng với ApplicationEvents
- **Sẵn sàng API Gateway**: RESTful APIs với hỗ trợ multi-tenant
- **Database Per Service**: Cô lập service (có thể tách Applications service riêng)
- **Chiến lược Multi-Tenant**: Shared database → Database per tenant (mở rộng tương lai)

## 📈 Monitoring & Alerting

### Sức khỏe ứng dụng
- **Health Endpoints**: /actuator/health
- **Metrics Endpoints**: /actuator/metrics
- **Custom Health Checks**: Database, dịch vụ bên ngoài

### Số liệu nghiệp vụ
- **Tỷ lệ đăng ký User**: Số user hoạt động hàng ngày mỗi company
- **Tỷ lệ tạo Job Posting**: Số job postings mỗi ngày mỗi company
- **Tỷ lệ Application**: Số applications nhận được mỗi job posting
- **Số liệu Hiring Funnel**: Tỷ lệ chuyển đổi NEW → SCREENING → INTERVIEWING → OFFERED → HIRED
- **Time-to-Hire**: Thời gian trung bình từ application đến hire
- **Tỷ lệ gửi Email**: Thành công thông báo
- **Thời gian phản hồi API**: Số liệu hiệu suất mỗi tenant

### Theo dõi lỗi
- **Giám sát Exception**: Tỷ lệ lỗi
- **Xác thực thất bại**: Giám sát bảo mật
- **Lỗi Database**: Tính toàn vẹn dữ liệu
- **Lỗi dịch vụ bên ngoài**: Giám sát tích hợp
