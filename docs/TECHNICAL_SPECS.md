# 🔧 JobTracker ATS Technical Specifications

## 📋 Tổng quan kỹ thuật

JobTracker ATS (Applicant Tracking System) được thiết kế với kiến trúc **monolith multi-tenant** hiện đại, sử dụng các công nghệ tiên tiến để đảm bảo hiệu suất, bảo mật và khả năng mở rộng cho nhiều SME/Startup.

## 🏗️ Kiến trúc hệ thống chi tiết

### 1. Backend Architecture (Spring Boot 3)

#### Core Dependencies
```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-client</artifactId>
    </dependency>
    <!-- Brevo Email API (thay thế Spring Mail) -->
    <dependency>
        <groupId>com.brevo</groupId>
        <artifactId>sib-api-v3-sdk</artifactId>
        <version>5.0.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
    <dependency>
        <groupId>org.liquibase</groupId>
        <artifactId>liquibase-core</artifactId>
    </dependency>
    
    <!-- OAuth2 Resource Server -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-oauth2-jose</artifactId>
    </dependency>
    
    <!-- Mapping -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct-processor</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
    
    <!-- External APIs -->
    <!-- Cloudinary SDK cho file storage -->
    <dependency>
        <groupId>com.cloudinary</groupId>
        <artifactId>cloudinary-http44</artifactId>
        <version>1.38.0</version>
    </dependency>
    <dependency>
        <groupId>com.cloudinary</groupId>
        <artifactId>cloudinary-taglib</artifactId>
        <version>1.38.0</version>
    </dependency>
    
    <!-- Documentation -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.2.0</version>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### Package Structure
```
com.jobtracker
├── config/                     # Configuration classes
│   ├── SecurityConfig.java     # Spring Security configuration (multi-tenant)
│   ├── WebConfig.java          # Web MVC configuration
│   ├── DatabaseConfig.java     # Database configuration (multi-tenant filters)
│   ├── WebSocketConfig.java    # WebSocket configuration
│   ├── BrevoConfig.java        # Brevo email configuration ➕
│   ├── CloudinaryConfig.java   # Cloudinary file storage configuration ➕
│   └── SwaggerConfig.java      # OpenAPI configuration
├── controller/                 # REST Controllers
│   ├── AuthController.java     # Authentication endpoints
│   ├── UserController.java     # User management (HR/Recruiter)
│   ├── CompanyController.java  # Company management (Multi-tenant)
│   ├── JobController.java      # Job Postings management (ATS)
│   ├── ApplicationController.java ➕ # Applications management (CORE ATS)
│   ├── CommentController.java ➕ # Comments management
│   ├── InterviewController.java # Interview management
│   ├── SkillController.java    # Skills management
│   ├── NotificationController.java # Notifications
│   ├── DashboardController.java # Dashboard analytics
│   └── FileController.java     # File operations (Attachments)
├── dto/                        # Data Transfer Objects
│   ├── request/               # Request DTOs
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── JobCreateRequest.java
│   │   ├── JobUpdateRequest.java
│   │   └── InterviewRequest.java
│   └── response/              # Response DTOs
│       ├── AuthResponse.java
│       ├── UserResponse.java
│       ├── JobResponse.java
│       ├── DashboardResponse.java
│       └── ApiResponse.java
├── entity/                     # JPA Entities
│   ├── User.java              # User entity (HR/Recruiter, multi-tenant)
│   ├── Company.java           # Company entity (Tenant)
│   ├── Job.java               # Job entity (Job Postings - ATS)
│   ├── Application.java ➕     # Application entity (CORE ATS)
│   ├── Application.java ➕     # Application entity (CORE ATS)
│   ├── ApplicationStatus.java ➕ # Application status lookup table entity
│   ├── ApplicationStatusHistory.java ➕ # Application status history
│   ├── Comment.java ➕         # Comment entity
│   ├── Interview.java         # Interview entity (link to applications)
│   ├── Skill.java             # Skill entity
│   ├── JobSkill.java          # Job-Skill relationship
│   ├── Attachment.java        # File attachment entity (link to applications)
│   ├── Notification.java      # Notification entity (multi-tenant)
│   ├── UserSession.java       # User session entity
│   ├── AuditLog.java          # Audit log entity (multi-tenant)
│   ├── Role.java              # RBAC Role entity
│   └── Permission.java        # RBAC Permission entity
├── repository/                 # Data Access Layer
│   ├── UserRepository.java    # User data access (multi-tenant)
│   ├── CompanyRepository.java # Company data access
│   ├── JobRepository.java     # Job data access (multi-tenant)
│   ├── ApplicationRepository.java ➕ # Application data access (multi-tenant)
│   ├── ApplicationStatusRepository.java ➕ # Application status data access
│   ├── ApplicationStatusHistoryRepository.java ➕
│   ├── CommentRepository.java ➕
│   ├── InterviewRepository.java # Interview data access (multi-tenant)
│   ├── SkillRepository.java   # Skill data access
│   ├── AttachmentRepository.java # Attachment data access
│   └── NotificationRepository.java # Notification data access (multi-tenant)
├── service/                    # Business Logic Layer
│   ├── AuthService.java       # Authentication logic
│   ├── UserService.java       # User management logic (HR/Recruiter)
│   ├── CompanyService.java    # Company management logic (Multi-tenant)
│   ├── JobService.java        # Job Postings management logic (ATS)
│   ├── ApplicationService.java ➕ # Application management logic (CORE ATS)
│   ├── CommentService.java ➕  # Comment management logic
│   ├── InterviewService.java  # Interview management logic
│   ├── SkillService.java      # Skill management logic
│   ├── AttachmentService.java # Attachment management logic
│   ├── NotificationService.java # Notification logic
│   ├── BrevoService.java ➕    # Brevo email sending logic
│   ├── CloudinaryService.java ➕ # Cloudinary file operations logic
│   ├── DashboardService.java  # Analytics logic
│   └── TenantService.java ➕   # Multi-tenant context management
├── security/                   # Security Components
│   ├── JwtTokenProvider.java  # JWT token handling (với company_id)
│   ├── JwtAuthenticationFilter.java # JWT filter
│   ├── CustomUserDetailsService.java # User details service
│   ├── PasswordEncoderConfig.java # Password encoding
│   ├── OAuth2UserService.java # OAuth2 user service
│   ├── TenantFilter.java ➕   # Multi-tenant data filtering
│   └── CompanySecurityContext.java ➕ # Company context holder
├── event/                      # Event Handling
│   ├── ApplicationReceivedEvent.java ➕ # Application received event
│   ├── ApplicationStatusChangedEvent.java ➕ # Application status change event
│   ├── InterviewScheduledEvent.java # Interview scheduled event
│   ├── JobDeadlineEvent.java  # Job deadline event
│   └── EventListener.java     # Event listeners
├── scheduler/                  # Scheduled Tasks
│   ├── ReminderScheduler.java # Reminder scheduling
│   └── CleanupScheduler.java  # Data cleanup tasks
├── exception/                  # Exception Handling
│   ├── GlobalExceptionHandler.java # Global exception handler
│   ├── BusinessException.java # Business exceptions
│   ├── ValidationException.java # Validation exceptions
│   └── ResourceNotFoundException.java # Resource not found
├── util/                       # Utility Classes
│   ├── DateUtils.java         # Date utilities
│   ├── ValidationUtils.java   # Validation utilities
│   ├── FileUtils.java         # File utilities
│   ├── EmailUtils.java        # Email utilities
│   └── TenantUtils.java ➕    # Multi-tenant utilities
├── mapper/                     # MapStruct Mappers
│   ├── UserMapper.java        # User entity-DTO mapping
│   ├── JobMapper.java         # Job entity-DTO mapping
│   ├── CompanyMapper.java     # Company entity-DTO mapping
│   └── InterviewMapper.java   # Interview entity-DTO mapping
└── JobTrackerApplication.java # Main application class
```

### 2. Frontend Architecture (React + JavaScript)

#### Package.json Dependencies
```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.22.0",
    "@reduxjs/toolkit": "^2.0.1",
    "react-redux": "^9.0.4",
    "axios": "^1.6.2",
    "react-hook-form": "^7.48.2",
    "yup": "^1.4.0",
    "@hookform/resolvers": "^3.3.2",
    "react-query": "^3.39.3",
    "react-toastify": "^9.1.3",
    "recharts": "^2.8.0",
    "react-quill": "^2.0.0",
    "react-dropzone": "^14.2.3",
    "dayjs": "^1.11.10",
    "lucide-react": "^0.294.0",
    "clsx": "^2.0.0",
    "tailwind-merge": "^2.0.0",
    "class-variance-authority": "^0.7.0"
  },
  "devDependencies": {
    "autoprefixer": "^10.4.16",
    "eslint": "^8.55.0",
    "eslint-plugin-react-hooks": "^4.6.0",
    "eslint-plugin-react-refresh": "^0.4.5",
    "postcss": "^8.4.32",
    "tailwindcss": "^3.3.6"
  }
}
```

#### Frontend Structure
```
src/
├── api/                        # API layer
│   ├── axios.js               # Axios configuration (với company_id header)
│   ├── auth.js                # Authentication API
│   ├── jobs.js                # Job Postings API
│   ├── applications.js ➕      # Applications API (CORE ATS)
│   ├── comments.js ➕          # Comments API
│   ├── users.js               # Users API
│   ├── companies.js           # Companies API
│   ├── skills.js              # Skills API
│   ├── interviews.js          # Interviews API
│   └── notifications.js       # Notifications API
├── components/                 # React Components
│   ├── common/                # Common components
│   │   ├── Button.jsx         # Button component
│   │   ├── Input.jsx          # Input component
│   │   ├── Modal.jsx          # Modal component
│   │   ├── Table.jsx          # Table component
│   │   ├── Card.jsx           # Card component
│   │   ├── Badge.jsx          # Badge component
│   │   ├── Loading.jsx        # Loading component
│   │   └── ErrorBoundary.jsx  # Error boundary
│   ├── forms/                 # Form components
│   │   ├── LoginForm.jsx      # Login form
│   │   ├── RegisterForm.jsx   # Registration form
│   │   ├── JobForm.jsx        # Job form
│   │   ├── InterviewForm.jsx  # Interview form
│   │   └── ProfileForm.jsx    # Profile form
│   ├── layout/                # Layout components
│   │   ├── Header.jsx         # Header component
│   │   ├── Sidebar.jsx        # Sidebar component
│   │   ├── Footer.jsx         # Footer component
│   │   └── Layout.jsx         # Main layout
│   └── charts/                # Chart components
│       ├── JobStatusChart.jsx # Job status chart
│       ├── SuccessRateChart.jsx # Success rate chart
│       └── TimelineChart.jsx  # Timeline chart
├── hooks/                      # Custom React Hooks
│   ├── useAuth.js             # Authentication hook
│   ├── useWebSocket.js        # WebSocket hook
│   ├── useLocalStorage.js     # Local storage hook
│   ├── useDebounce.js         # Debounce hook
│   └── usePagination.js       # Pagination hook
├── pages/                      # Page Components
│   ├── auth/                  # Authentication pages
│   │   ├── LoginPage.jsx      # Login page
│   │   ├── RegisterPage.jsx   # Registration page
│   │   └── ForgotPasswordPage.jsx # Forgot password page
│   ├── dashboard/             # Dashboard pages
│   │   ├── DashboardPage.jsx  # Main dashboard
│   │   └── AnalyticsPage.jsx  # Analytics page
│   ├── jobs/                  # Job pages
│   │   ├── JobsPage.jsx       # Jobs list page
│   │   ├── JobDetailPage.jsx  # Job detail page
│   │   └── JobCreatePage.jsx  # Job creation page
│   ├── companies/             # Company pages
│   │   ├── CompaniesPage.jsx  # Companies list page
│   │   └── CompanyDetailPage.jsx # Company detail page
│   ├── applications/ ➕        # Application pages
│   │   ├── ApplicationsPage.jsx # Applications list page
│   │   ├── ApplicationDetailPage.jsx # Application detail page
│   │   └── ApplicationCreatePage.jsx # Application create page
│   ├── interviews/            # Interview pages
│   │   ├── InterviewsPage.jsx # Interviews list page
│   │   └── InterviewDetailPage.jsx # Interview detail page
│   ├── profile/               # Profile pages
│   │   ├── ProfilePage.jsx    # Profile page
│   │   └── SettingsPage.jsx   # Settings page
│   └── NotFoundPage.jsx       # 404 page
├── store/                      # Redux Store
│   ├── index.js               # Store configuration
│   ├── authSlice.js           # Authentication slice
│   ├── jobsSlice.js           # Jobs slice
│   ├── usersSlice.js          # Users slice
│   ├── companiesSlice.js      # Companies slice
│   ├── skillsSlice.js         # Skills slice
│   ├── applicationsSlice.js ➕ # Applications slice
│   ├── interviewsSlice.js     # Interviews slice
│   └── notificationsSlice.js  # Notifications slice
├── styles/                     # Styles
│   ├── globals.css            # Global styles
│   ├── components.css         # Component styles
│   └── utilities.css          # Utility styles
├── utils/                      # Utility functions
│   ├── constants.js           # Application constants
│   ├── helpers.js             # Helper functions
│   ├── validators.js          # Validation functions
│   ├── formatters.js          # Formatting functions
│   └── storage.js             # Storage utilities
├── App.jsx                     # Main App component
├── index.js                    # Application entry point
└── package.json               # Package configuration
```

## 🔧 Technical Implementation Details

### 1. Security Implementation

#### OAuth2 Resource Server Configuration
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/public/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oauth2UserService())
                )
                .successHandler(oauth2SuccessHandler())
            );
        
        return http.build();
    }
    
    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(issuerUri);
    }
    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("roles");
        
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        // Extract company_id từ JWT claims và set vào SecurityContext
        converter.setPrincipalClaimName("sub");
        return converter;
    }
    
    // Multi-tenant filter - tự động filter theo company_id
    @Bean
    public FilterRegistrationBean<TenantFilter> tenantFilter() {
        FilterRegistrationBean<TenantFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TenantFilter());
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }
}
```

#### OAuth2 User Service
```java
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CompanyService companyService; // Multi-tenant
    
    public CustomOAuth2UserService(UserRepository userRepository, 
                                  RoleRepository roleRepository,
                                  CompanyService companyService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.companyService = companyService;
    }
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
            .getProviderDetails()
            .getUserInfoEndpoint()
            .getUserNameAttributeName();
        
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());
        
        if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }
        
        User user = userRepository.findByEmail(userInfo.getEmail())
            .orElseGet(() -> createNewUser(userInfo));
        
        return new CustomOAuth2User(user, oAuth2User.getAttributes(), userNameAttributeName);
    }
    
    private User createNewUser(OAuth2UserInfo userInfo) {
        Role userRole = roleRepository.findByName("RECRUITER") // Default role cho ATS
            .orElseThrow(() -> new RuntimeException("Default role not found"));
        
        // Tạo hoặc lấy company mặc định (hoặc yêu cầu user chọn company)
        Company defaultCompany = companyService.getOrCreateDefaultCompany();
        
        User user = new User();
        user.setEmail(userInfo.getEmail());
        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());
        user.setAvatarUrl(userInfo.getImageUrl());
        user.setRole(userRole);
        user.setCompany(defaultCompany); // Multi-tenant key
        user.setEmailVerified(true);
        user.setActive(true);
        
        return userRepository.save(user);
    }
}
```

### 2. Database Configuration

#### JPA Entity Example
```java
@Entity
@Table(name = "jobs")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = "string"))
@Filter(name = "tenantFilter", condition = "company_id = :tenantId")
@EntityListeners(AuditingEntityListener.class)
public class Job extends BaseFullAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // HR/Recruiter tạo job posting
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // Multi-tenant key
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String position;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType jobType; // ENUM: FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE
    
    @Enumerated(EnumType.STRING)
    @Column(name = "job_status", nullable = false)
    private JobStatus jobStatus = JobStatus.DRAFT; // ENUM: DRAFT, PUBLISHED, PAUSED, CLOSED, FILLED
    
    @Column(name = "deadline_date")
    private LocalDate deadlineDate;
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "views_count")
    private Integer viewsCount = 0;
    
    @Column(name = "applications_count")
    private Integer applicationsCount = 0;
    
    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;
    
    @Column(columnDefinition = "TEXT")
    private String requirements;
    
    @Column(columnDefinition = "TEXT")
    private String benefits;
    
    @Column(name = "job_url")
    private String jobUrl;
    
    @Column(name = "is_remote")
    private Boolean isRemote = false;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "job_skills",
        joinColumns = @JoinColumn(name = "job_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills = new HashSet<>();
    
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Application> applications = new HashSet<>(); // Applications apply to this job
    
    // Audit fields inherited from BaseFullAuditEntity
    // Constructors, getters, setters
}
```

#### Application Entity Example (CORE ATS) ➕
```java
@Entity
@Table(name = "applications")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = "string"))
@Filter(name = "tenantFilter", condition = "company_id = :tenantId")
@EntityListeners(AuditingEntityListener.class)
public class Application extends BaseFullAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // Multi-tenant key
    
    // Candidate Info
    @Column(name = "candidate_name", nullable = false)
    private String candidateName;
    
    @Column(name = "candidate_email", nullable = false)
    private String candidateEmail;
    
    @Column(name = "candidate_phone")
    private String candidatePhone;
    
    // Application Status Workflow
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private ApplicationStatus status; // Lookup table: application_statuses
    
    @Column(name = "source")
    private String source; // Email, LinkedIn, Referral
    
    @Column(name = "applied_date", nullable = false)
    private LocalDate appliedDate;
    
    @Column(name = "resume_file_path")
    private String resumeFilePath; // Cloudinary URL
    
    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "rating")
    private Integer rating; // 1-5
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo; // HR/Recruiter được assign
    
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ApplicationStatusHistory> statusHistory = new HashSet<>();
    
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();
    
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Interview> interviews = new HashSet<>();
    
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Attachment> attachments = new HashSet<>();
    
    // Audit fields inherited from BaseFullAuditEntity
    // Constructors, getters, setters
}
```

#### Repository with Custom Queries
```java
@Repository
public interface JobRepository extends JpaRepository<Job, String>, JpaSpecificationExecutor<Job> {
    
    // Multi-tenant queries - tự động filter theo company_id
    @Query("SELECT j FROM Job j WHERE j.company.id = :companyId AND j.deletedAt IS NULL")
    Page<Job> findByCompanyIdAndDeletedAtIsNull(@Param("companyId") String companyId, Pageable pageable);
    
    @Query("SELECT j FROM Job j WHERE j.company.id = :companyId AND j.jobStatus = :jobStatus AND j.deletedAt IS NULL")
    List<Job> findByCompanyIdAndJobStatusAndDeletedAtIsNull(@Param("companyId") String companyId, @Param("jobStatus") JobStatus jobStatus);
    
    @Query("SELECT j FROM Job j WHERE j.company.id = :companyId AND j.jobStatus = 'PUBLISHED' AND j.deletedAt IS NULL")
    Page<Job> findPublishedJobsByCompanyId(@Param("companyId") String companyId, Pageable pageable);
    
    @Query("SELECT COUNT(j) FROM Job j WHERE j.company.id = :companyId AND j.jobStatus = :jobStatus AND j.deletedAt IS NULL")
    Long countByCompanyIdAndJobStatusAndDeletedAtIsNull(@Param("companyId") String companyId, @Param("jobStatus") JobStatus jobStatus);
    
    @Query("SELECT j FROM Job j WHERE j.company.id = :companyId AND j.deadlineDate BETWEEN :startDate AND :endDate AND j.deletedAt IS NULL")
    List<Job> findUpcomingDeadlines(@Param("companyId") String companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT j FROM Job j WHERE j.company.id = :companyId AND j.deadlineDate <= :deadlineDate AND j.jobStatus IN :statuses AND j.deletedAt IS NULL")
    List<Job> findJobsWithUpcomingDeadlines(@Param("companyId") String companyId, @Param("deadlineDate") LocalDate deadlineDate, @Param("statuses") List<JobStatus> statuses);
}
```

### 3. Service Layer Implementation

#### Job Service with Business Logic
```java
@Service
@Transactional
public class JobService {
    
    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;
    private final SkillRepository skillRepository;
    private final JobMapper jobMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final TenantService tenantService; // Multi-tenant context
    
    public JobService(JobRepository jobRepository, 
                     CompanyRepository companyRepository,
                     SkillRepository skillRepository,
                     JobMapper jobMapper,
                     ApplicationEventPublisher eventPublisher,
                     TenantService tenantService) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
        this.skillRepository = skillRepository;
        this.jobMapper = jobMapper;
        this.eventPublisher = eventPublisher;
        this.tenantService = tenantService;
    }
    
    @Transactional(readOnly = true)
    public Page<JobResponse> getJobsByCompanyId(String companyId, JobSearchCriteria criteria, Pageable pageable) {
        // Validate company access
        tenantService.validateCompanyAccess(companyId);
        
        Specification<Job> spec = JobSpecification.buildSpecification(criteria, companyId);
        Page<Job> jobs = jobRepository.findAll(spec, pageable);
        return jobs.map(jobMapper::toResponse);
    }
    
    @Transactional(readOnly = true)
    public JobResponse getJobById(String jobId, String companyId) {
        tenantService.validateCompanyAccess(companyId);
        
        Job job = jobRepository.findById(jobId)
            .filter(j -> j.getCompany().getId().equals(companyId))
            .filter(j -> j.getDeletedAt() == null)
            .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        return jobMapper.toResponse(job);
    }
    
    public JobResponse createJob(JobCreateRequest request, String userId, String companyId) {
        tenantService.validateCompanyAccess(companyId);
        
        // Validate company exists
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));
        
        // Create job entity
        Job job = jobMapper.toEntity(request);
        job.setUser(new User(userId));
        job.setCompany(company);
        job.setJobStatus(JobStatus.DRAFT); // Default status
        
        // Set skills if provided
        if (request.getSkillIds() != null && !request.getSkillIds().isEmpty()) {
            Set<Skill> skills = skillRepository.findAllById(request.getSkillIds());
            job.setSkills(skills);
        }
        
        // Save job
        Job savedJob = jobRepository.save(job);
        
        // Publish event for deadline reminder
        if (savedJob.getDeadlineDate() != null) {
            eventPublisher.publishEvent(new JobDeadlineEvent(savedJob));
        }
        
        return jobMapper.toResponse(savedJob);
    }
    
    public JobResponse publishJob(String jobId, String companyId) {
        tenantService.validateCompanyAccess(companyId);
        
        Job job = jobRepository.findById(jobId)
            .filter(j -> j.getCompany().getId().equals(companyId))
            .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        job.setJobStatus(JobStatus.PUBLISHED);
        job.setPublishedAt(LocalDateTime.now());
        
        if (job.getDeadlineDate() != null) {
            job.setExpiresAt(job.getDeadlineDate().atTime(23, 59, 59));
        }
        
        Job updatedJob = jobRepository.save(job);
        eventPublisher.publishEvent(new JobPublishedEvent(updatedJob));
        
        return jobMapper.toResponse(updatedJob);
    }
    
    public JobResponse updateJob(String jobId, JobUpdateRequest request, String companyId) {
        tenantService.validateCompanyAccess(companyId);
        
        Job job = jobRepository.findById(jobId)
            .filter(j -> j.getCompany().getId().equals(companyId))
            .filter(j -> j.getDeletedAt() == null)
            .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        // Update job fields
        jobMapper.updateEntity(request, job);
        
        // Update skills if provided
        if (request.getSkillIds() != null) {
            Set<Skill> skills = skillRepository.findAllById(request.getSkillIds());
            job.setSkills(skills);
        }
        
        Job updatedJob = jobRepository.save(job);
        
        // Publish event if status changed
        if (request.getJobStatus() != null && !request.getJobStatus().equals(job.getJobStatus())) {
            eventPublisher.publishEvent(new JobStatusChangeEvent(updatedJob));
        }
        
        return jobMapper.toResponse(updatedJob);
    }
    
    public void deleteJob(String jobId, String companyId) {
        tenantService.validateCompanyAccess(companyId);
        
        Job job = jobRepository.findById(jobId)
            .filter(j -> j.getCompany().getId().equals(companyId))
            .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + jobId));
        
        // Soft delete
        job.softDelete();
        jobRepository.save(job);
    }
}
```

### 4. Frontend Implementation

#### Redux Store Configuration
```javascript
// store/index.js
import { configureStore } from '@reduxjs/toolkit';
import { authSlice } from './authSlice';
import { jobsSlice } from './jobsSlice';
import { usersSlice } from './usersSlice';
import { companiesSlice } from './companiesSlice';
import { skillsSlice } from './skillsSlice';
import { applicationsSlice } from './applicationsSlice';
import { interviewsSlice } from './interviewsSlice';
import { notificationsSlice } from './notificationsSlice';

export const store = configureStore({
  reducer: {
    auth: authSlice.reducer,
    jobs: jobsSlice.reducer,
    users: usersSlice.reducer,
    companies: companiesSlice.reducer,
    skills: skillsSlice.reducer,
    applications: applicationsSlice.reducer,
    interviews: interviewsSlice.reducer,
    notifications: notificationsSlice.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['persist/PERSIST'],
      },
    }),
});
```

#### API Service with Axios
```javascript
// api/axios.js
import axios from 'axios';
import { store } from '../store';
import { authSlice } from '../store/authSlice';

class ApiService {
  constructor() {
    this.api = axios.create({
      baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1',
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  setupInterceptors() {
    // Request interceptor
    this.api.interceptors.request.use(
      (config) => {
        const token = store.getState().auth.token;
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Response interceptor
    this.api.interceptors.response.use(
      (response: AxiosResponse) => {
        return response;
      },
      async (error) => {
        const originalRequest = error.config;

        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          try {
            const refreshToken = store.getState().auth.refreshToken;
            if (refreshToken) {
              const response = await this.refreshToken(refreshToken);
              const { accessToken } = response.data;
              
              store.dispatch(authSlice.actions.setToken(accessToken));
              originalRequest.headers.Authorization = `Bearer ${accessToken}`;
              
              return this.api(originalRequest);
            }
          } catch (refreshError) {
            store.dispatch(authSlice.actions.logout());
            window.location.href = '/login';
          }
        }

        return Promise.reject(error);
      }
    );
  }

  private async refreshToken(refreshToken: string) {
    return this.api.post('/auth/refresh', { refreshToken });
  }

  // Generic methods
  async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.api.get<T>(url, config);
    return response.data;
  }

  async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.api.post<T>(url, data, config);
    return response.data;
  }

  async put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.api.put<T>(url, data, config);
    return response.data;
  }

  async patch<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.api.patch<T>(url, data, config);
    return response.data;
  }

  async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.api.delete<T>(url, config);
    return response.data;
  }

  // File upload
  async uploadFile<T>(url: string, file: File, onProgress?: (progress: number) => void): Promise<T> {
    const formData = new FormData();
    formData.append('file', file);

    const config: AxiosRequestConfig = {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      onUploadProgress: (progressEvent) => {
        if (onProgress && progressEvent.total) {
          const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          onProgress(progress);
        }
      },
    };

    const response = await this.api.post<T>(url, formData, config);
    return response.data;
  }
}

export const apiService = new ApiService();
```

#### React Hook for API Calls
```typescript
// hooks/useJobs.ts
import { useState, useEffect, useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { jobsSlice } from '../store/jobsSlice';
import { apiService } from '../api/axios';
import { Job, JobCreateRequest, JobUpdateRequest, JobSearchCriteria } from '../types/job';

export const useJobs = () => {
  const dispatch = useDispatch();
  const { jobs, loading, error } = useSelector((state: RootState) => state.jobs);

  const fetchJobs = useCallback(async (criteria?: JobSearchCriteria, page = 0, size = 20) => {
    try {
      dispatch(jobsSlice.actions.setLoading(true));
      const params = new URLSearchParams({
        page: page.toString(),
        size: size.toString(),
        ...(criteria && Object.entries(criteria).reduce((acc, [key, value]) => {
          if (value !== undefined && value !== null && value !== '') {
            acc[key] = value.toString();
          }
          return acc;
        }, {} as Record<string, string>))
      });

      const response = await apiService.get<ApiResponse<Page<Job>>>(`/jobs?${params}`);
      dispatch(jobsSlice.actions.setJobs(response.data));
    } catch (err) {
      dispatch(jobsSlice.actions.setError(err.message));
    } finally {
      dispatch(jobsSlice.actions.setLoading(false));
    }
  }, [dispatch]);

  const createJob = useCallback(async (jobData: JobCreateRequest) => {
    try {
      dispatch(jobsSlice.actions.setLoading(true));
      const response = await apiService.post<ApiResponse<Job>>('/jobs', jobData);
      dispatch(jobsSlice.actions.addJob(response.data));
      return response.data;
    } catch (err) {
      dispatch(jobsSlice.actions.setError(err.message));
      throw err;
    } finally {
      dispatch(jobsSlice.actions.setLoading(false));
    }
  }, [dispatch]);

  const updateJob = useCallback(async (jobId: number, jobData: JobUpdateRequest) => {
    try {
      dispatch(jobsSlice.actions.setLoading(true));
      const response = await apiService.put<ApiResponse<Job>>(`/jobs/${jobId}`, jobData);
      dispatch(jobsSlice.actions.updateJob(response.data));
      return response.data;
    } catch (err) {
      dispatch(jobsSlice.actions.setError(err.message));
      throw err;
    } finally {
      dispatch(jobsSlice.actions.setLoading(false));
    }
  }, [dispatch]);

  const deleteJob = useCallback(async (jobId: number) => {
    try {
      dispatch(jobsSlice.actions.setLoading(true));
      await apiService.delete(`/jobs/${jobId}`);
      dispatch(jobsSlice.actions.removeJob(jobId));
    } catch (err) {
      dispatch(jobsSlice.actions.setError(err.message));
      throw err;
    } finally {
      dispatch(jobsSlice.actions.setLoading(false));
    }
  }, [dispatch]);

  useEffect(() => {
    fetchJobs();
  }, [fetchJobs]);

  return {
    jobs,
    loading,
    error,
    fetchJobs,
    createJob,
    updateJob,
    deleteJob,
  };
};
```

## 🔧 Performance Optimizations

### 1. Database Optimizations

#### Indexing Strategy (Multi-Tenant Optimized)
```sql
-- Multi-tenant composite indexes (CRITICAL)
CREATE INDEX idx_jobs_company_status_date ON jobs(company_id, job_status, created_at);
CREATE INDEX idx_jobs_company_published ON jobs(company_id, job_status, published_at) WHERE job_status = 'PUBLISHED';
CREATE INDEX idx_applications_company_status_date ON applications(company_id, status_id, applied_date);
CREATE INDEX idx_applications_company_job_status ON applications(company_id, job_id, status_id);
CREATE INDEX idx_interviews_company_scheduled ON interviews(company_id, scheduled_date, status);
CREATE INDEX idx_notifications_company_user_unread ON notifications(company_id, user_id, is_read);
CREATE INDEX idx_users_company_role_active ON users(company_id, role_id, is_active);
CREATE INDEX idx_audit_logs_company_entity ON audit_logs(company_id, entity_type, entity_id);

-- Single-column indexes
CREATE INDEX idx_jobs_deadline_status ON jobs(deadline_date, job_status);
CREATE INDEX idx_applications_assigned_status ON applications(assigned_to, status_id);
CREATE INDEX idx_user_sessions_token ON user_sessions(session_token);

-- Full-text search indexes
CREATE FULLTEXT INDEX idx_jobs_search ON jobs(title, position, job_description);
CREATE FULLTEXT INDEX idx_companies_search ON companies(name, description);
CREATE FULLTEXT INDEX idx_applications_search ON applications(candidate_name, candidate_email, notes);
```

#### Query Optimization
```java
// Using @EntityGraph for eager loading (multi-tenant)
@EntityGraph(attributePaths = {"company", "skills", "applications"})
@Query("SELECT j FROM Job j WHERE j.company.id = :companyId AND j.deletedAt IS NULL")
Page<Job> findByCompanyIdWithDetails(@Param("companyId") String companyId, Pageable pageable);

// Application queries với multi-tenant
@EntityGraph(attributePaths = {"job", "assignedTo", "statusHistory"})
@Query("SELECT a FROM Application a WHERE a.company.id = :companyId AND a.deletedAt IS NULL")
Page<Application> findByCompanyIdWithDetails(@Param("companyId") String companyId, Pageable pageable);

// Using @BatchSize for batch loading
@BatchSize(size = 20)
@OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private Set<Interview> interviews = new HashSet<>();
```

### 2. Caching Strategy

#### Redis Configuration
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory();
        factory.setHostName("localhost");
        factory.setPort(6379);
        return factory;
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
    
    @Bean
    public CacheManager cacheManager() {
        RedisCacheManager.Builder builder = RedisCacheManager
            .RedisCacheManagerBuilder
            .fromConnectionFactory(redisConnectionFactory())
            .cacheDefaults(cacheConfiguration(Duration.ofMinutes(10)));
        
        return builder.build();
    }
    
    private RedisCacheConfiguration cacheConfiguration(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(ttl)
            .serializeKeysWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}
```

#### Service Layer Caching
```java
@Service
public class JobService {
    
    @Cacheable(value = "jobs", key = "#companyId + '_' + #page + '_' + #size")
    public Page<JobResponse> getJobsByCompanyId(String companyId, int page, int size) {
        // Implementation với multi-tenant filtering
    }
    
    @CacheEvict(value = "jobs", allEntries = true)
    public JobResponse createJob(JobCreateRequest request, String userId, String companyId) {
        // Implementation với company validation
    }
    
    @Cacheable(value = "dashboard", key = "#companyId")
    public DashboardStatistics getDashboardStatistics(String companyId) {
        // Implementation với multi-tenant metrics
    }
}

@Service
public class ApplicationService {
    
    @Cacheable(value = "applications", key = "#companyId + '_' + #statusId + '_' + #page")
    public Page<ApplicationResponse> getApplicationsByCompanyId(String companyId, String statusId, int page, int size) {
        // Implementation với multi-tenant filtering
    }
    
    @CacheEvict(value = "applications", allEntries = true)
    public ApplicationResponse createApplication(ApplicationCreateRequest request, String companyId) {
        // Implementation với company validation
    }
}
```

### 3. Frontend Optimizations

#### Code Splitting
```typescript
// Lazy loading components
const DashboardPage = lazy(() => import('../pages/dashboard/DashboardPage'));
const JobsPage = lazy(() => import('../pages/jobs/JobsPage'));
const CompaniesPage = lazy(() => import('../pages/companies/CompaniesPage'));

// Route configuration with lazy loading
const AppRoutes = () => (
  <Routes>
    <Route path="/dashboard" element={
      <Suspense fallback={<Loading />}>
        <DashboardPage />
      </Suspense>
    } />
    <Route path="/jobs" element={
      <Suspense fallback={<Loading />}>
        <JobsPage />
      </Suspense>
    } />
  </Routes>
);
```

#### Memoization
```typescript
// Memoized components
const JobCard = memo(({ job, onEdit, onDelete }: JobCardProps) => {
  return (
    <Card>
      <CardHeader>
        <CardTitle>{job.title}</CardTitle>
        <CardDescription>{job.company.name}</CardDescription>
      </CardHeader>
      <CardContent>
        <Badge variant={getStatusVariant(job.status)}>
          {job.status}
        </Badge>
      </CardContent>
      <CardFooter>
        <Button onClick={() => onEdit(job.id)}>Edit</Button>
        <Button variant="destructive" onClick={() => onDelete(job.id)}>
          Delete
        </Button>
      </CardFooter>
    </Card>
  );
});

// Memoized selectors
const selectJobsByStatus = createSelector(
  [(state: RootState) => state.jobs.jobs],
  (jobs) => jobs.reduce((acc, job) => {
    acc[job.status] = (acc[job.status] || 0) + 1;
    return acc;
  }, {} as Record<JobStatus, number>)
);
```

## 📊 Monitoring & Observability

### 1. Application Metrics

#### Custom Metrics
```java
@Component
public class JobMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter jobCreatedCounter;
    private final Counter jobStatusChangedCounter;
    private final Timer jobProcessingTimer;
    
    public JobMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.jobCreatedCounter = Counter.builder("jobs.created")
            .description("Number of jobs created")
            .register(meterRegistry);
        this.jobStatusChangedCounter = Counter.builder("jobs.status.changed")
            .description("Number of job status changes")
            .register(meterRegistry);
        this.jobProcessingTimer = Timer.builder("jobs.processing.time")
            .description("Job processing time")
            .register(meterRegistry);
    }
    
    public void incrementJobCreated() {
        jobCreatedCounter.increment();
    }
    
    public void incrementJobStatusChanged(JobStatus from, JobStatus to) {
        jobStatusChangedCounter.increment(
            Tags.of("from", from.name(), "to", to.name()) // DRAFT → PUBLISHED, etc.
        );
    }
    
    public void incrementApplicationStatusChanged(String fromStatusId, String toStatusId) {
        Counter applicationStatusCounter = Counter.builder("applications.status.changed")
            .description("Number of application status changes")
            .register(meterRegistry);
        applicationStatusCounter.increment(
            Tags.of("from", fromStatusId, "to", toStatusId) // Status IDs from application_statuses table
        );
    }
    
    public void recordJobProcessingTime(Duration duration) {
        jobProcessingTimer.record(duration);
    }
}
```

### 2. Health Checks

#### Custom Health Indicators
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    private final DataSource dataSource;
    
    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up()
                    .withDetail("database", "MySQL")
                    .withDetail("validationQuery", "isValid")
                    .build();
            }
        } catch (SQLException e) {
            return Health.down()
                .withDetail("database", "MySQL")
                .withDetail("error", e.getMessage())
                .build();
        }
        
        return Health.down()
            .withDetail("database", "MySQL")
            .withDetail("error", "Connection validation failed")
            .build();
    }
}
```

## 🔒 Security Best Practices

### 1. Input Validation

#### Custom Validators
```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidJobStatusValidator.class)
public @interface ValidJobStatus {
    String message() default "Invalid job status";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class ValidJobStatusValidator implements ConstraintValidator<ValidJobStatus, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        
        try {
            JobStatus.valueOf(value.toUpperCase()); // DRAFT, PUBLISHED, PAUSED, CLOSED, FILLED
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

// ENUM Definitions
public enum JobStatus {
    DRAFT, PUBLISHED, PAUSED, CLOSED, FILLED
}

public enum JobType {
    FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE
}

// ApplicationStatus is now a lookup table entity, not an enum
// See ApplicationStatus.java entity definition below

public enum InterviewType {
    PHONE, VIDEO, IN_PERSON, TECHNICAL, HR, FINAL
}

public enum InterviewStatus {
    SCHEDULED, COMPLETED, CANCELLED, RESCHEDULED
}

public enum InterviewResult {
    PASSED, FAILED, PENDING
}
```

### 2. Rate Limiting

#### Rate Limiting Configuration
```java
@Configuration
public class RateLimitingConfig {
    
    @Bean
    public RedisTemplate<String, String> rateLimitRedisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
    
    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(10.0); // 10 requests per second
    }
}
```

## 📈 Scalability Considerations

### 1. Horizontal Scaling

#### Load Balancer Configuration
```nginx
upstream backend {
    least_conn;
    server backend1:8080 max_fails=3 fail_timeout=30s;
    server backend2:8080 max_fails=3 fail_timeout=30s;
    server backend3:8080 max_fails=3 fail_timeout=30s;
    keepalive 32;
}
```

### 2. Database Scaling

#### Read Replica Configuration
```yaml
# application-production.yml
spring:
  datasource:
    primary:
      url: jdbc:mysql://mysql-primary:3306/jobtracker
      username: jobtracker
      password: ${MYSQL_PASSWORD}
    replica:
      url: jdbc:mysql://mysql-replica:3306/jobtracker
      username: jobtracker
      password: ${MYSQL_PASSWORD}
```

## 🏗️ Base Entity Classes

### 📊 Audit Patterns Analysis

Dựa trên database schema, có **3 patterns chính** cho audit fields:

#### **Pattern 1: FULL AUDIT** (10 bảng)
```java
// Có: created_by, updated_by, created_at, updated_at
- Lookup Tables (2 bảng): roles, permissions (RBAC)
- Core Business Entities (8 bảng): users, companies, jobs, skills, interviews, applications, comments, attachments
// Note: Các lookup tables khác (job_statuses, job_types, etc.) đã chuyển sang ENUM
```

#### **Pattern 2: PARTIAL AUDIT** (1 bảng)  
```java
// Có: created_by, created_at, updated_at (không có updated_by)
- Junction Tables: job_skills
// Note: user_skills và job_resumes đã bỏ
```

#### **Pattern 3: SYSTEM / CONFIG TABLES** (6 bảng)
```java
// Có: created_at, updated_at (không có user tracking, không soft delete)
- System Tables: notifications, user_sessions, audit_logs
- Config/Billing Tables: subscription_plans, company_subscriptions, payments
```

### 📋 Base Class Mapping Table

| **Base Class** | **Tables** | **Audit Fields** | **Soft Delete** | **Count** |
|---|---|---|---|---|
| **BaseFullAuditEntity** | **Lookup Tables (2 bảng - chỉ RBAC)** | | | |
| | `roles` | ✅ created_by, updated_by, created_at, updated_at | ✅ deleted_at | 1 |
| | `permissions` | ✅ created_by, updated_by, created_at, updated_at | ✅ deleted_at | 2 |
| | **Core Business Entities (8 bảng)** | | | |
| | `users` | ✅ created_by, updated_by, created_at, updated_at | ✅ deleted_at | 3 |
| | `companies` | ✅ created_by, updated_by, created_at, updated_at | ✅ deleted_at | 4 |
| | `jobs` | ✅ created_by, updated_by, created_at, updated_at | ✅ deleted_at | 5 |
| | `skills` | ✅ created_by, updated_by, created_at, updated_at | ✅ deleted_at | 6 |
| | `interviews` | ✅ created_by, updated_by, created_at, updated_at | ✅ deleted_at | 7 |
| | `applications` ➕ | ✅ created_by, updated_by, created_at, updated_at | ✅ deleted_at | 8 |
| | `comments` ➕ | ✅ created_by, updated_by, created_at, updated_at | ✅ deleted_at | 9 |
| | `attachments` | ✅ created_by, updated_by, created_at, updated_at | ✅ deleted_at | 10 |
| **BasePartialAuditEntity** | **Junction Tables (1 bảng)** | | | |
| | `job_skills` | ✅ created_by, created_at, updated_at | ✅ is_deleted | 11 |
| **BaseSystemEntity** | **System / Config Tables (6 bảng)** | | | |
| | `notifications` | ✅ created_at, updated_at | ❌ No soft delete | 12 |
| | `user_sessions` | ✅ created_at, updated_at | ❌ No soft delete | 13 |
| | `audit_logs` | ✅ created_at | ❌ No soft delete | 14 |
| | `subscription_plans` ➕ | ✅ created_at, updated_at | ❌ No soft delete | 15 |
| | `company_subscriptions` ➕ | ✅ created_at, updated_at | ❌ No soft delete | 16 |
| | `payments` ➕ | ✅ created_at, updated_at | ❌ No soft delete | 17 |
| **Không có Base Class** | **History Tables (1 bảng)** | | | |
| | `application_status_history` ➕ | ❌ No audit fields | ❌ No soft delete | 18 |

### 🎯 Implementation Summary

#### **BaseFullAuditEntity** (10 bảng)
```java
// Extends: BaseSoftDeleteEntity
// Fields: created_by, updated_by, created_at, updated_at, deleted_at
// Usage: RBAC lookup tables (roles, permissions) + core business entities
// Note: Các lookup tables khác (job_statuses, job_types, etc.) đã chuyển sang ENUM
```

#### **BasePartialAuditEntity** (1 bảng)
```java
// Extends: BaseBooleanDeleteEntity  
// Fields: created_by, created_at, updated_at, is_deleted
// Usage: Junction tables only (job_skills)
// Note: user_skills và job_resumes đã bỏ
```

#### **BaseSystemEntity** (System / Config Tables)
```java
// No inheritance
// Fields: created_at, updated_at (audit_logs only has created_at)
// Usage: System-generated tables và config tables (subscription_plans, company_subscriptions)
```

### 🎯 Base Class Implementation

#### 1. BaseFullAuditEntity (Full Audit + Soft Delete)
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseFullAuditEntity extends BaseSoftDeleteEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
    
    // Getters, setters
}
```

#### 2. BasePartialAuditEntity (Partial Audit + Boolean Delete)
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BasePartialAuditEntity extends BaseBooleanDeleteEntity {
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    // Getters, setters
}
```

#### 3. BaseSystemEntity (System Tables + No Soft Delete)
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseSystemEntity {
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Getters, setters
}
```

#### 4. BaseSoftDeleteEntity (deleted_at)
```java
@MappedSuperclass
public abstract class BaseSoftDeleteEntity {
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Transient
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
    
    public void restore() {
        this.deletedAt = null;
    }
    
    // Getters, setters
}
```

#### 5. BaseBooleanDeleteEntity (is_deleted)
```java
@MappedSuperclass
public abstract class BaseBooleanDeleteEntity {
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public void softDelete() {
        this.isDeleted = true;
    }
    
    public void restore() {
        this.isDeleted = false;
    }
    
    // Getters, setters
}
```

### 📋 Entity Implementation Examples

#### Lookup Tables (2 bảng - chỉ RBAC)
```java
@Entity
@Table(name = "roles")
public class Role extends BaseFullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(nullable = false, unique = true)
    private String name; // COMPANY_ADMIN, RECRUITER, HIRING_MANAGER, INTERVIEWER
    
    private String description;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // Business fields only, audit fields inherited
}

@Entity
@Table(name = "permissions")
public class Permission extends BaseFullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(nullable = false, unique = true)
    private String name; // JOB_CREATE, APPLICATION_VIEW, etc.
    
    private String resource; // JOB, APPLICATION, INTERVIEW, etc.
    private String action; // CREATE, READ, UPDATE, DELETE
    
    // Business fields only, audit fields inherited
}
```

#### Core Business Entities (8 bảng)
```java
@Entity
@Table(name = "jobs")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = "string"))
@Filter(name = "tenantFilter", condition = "company_id = :tenantId")
public class Job extends BaseFullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // HR/Recruiter
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // Multi-tenant key
    
    // Business fields only, audit fields inherited
}

@Entity
@Table(name = "applications")
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = "string"))
@Filter(name = "tenantFilter", condition = "company_id = :tenantId")
public class Application extends BaseFullAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company; // Multi-tenant key
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private ApplicationStatus status; // Lookup table: application_statuses
    
    // Business fields only, audit fields inherited
}
```

#### Junction Tables (1 bảng)
```java
@Entity
@Table(name = "job_skills")
public class JobSkill extends BasePartialAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    
    // Business fields only, audit fields inherited
}
```

#### System Tables (3 bảng)
```java
@Entity
@Table(name = "notifications")
public class Notification extends BaseSystemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Business fields only, audit fields inherited
}
```

### 🎯 Benefits of Base Classes

#### Code Reusability
- ✅ Giảm code duplication
- ✅ Consistent audit implementation
- ✅ Easy maintenance

#### Type Safety
- ✅ Compile-time checking
- ✅ IDE support
- ✅ Refactoring safety

#### Performance
- ✅ JPA inheritance optimization
- ✅ Single table inheritance
- ✅ Efficient queries

#### Maintainability
- ✅ Centralized audit logic
- ✅ Easy to add new audit fields
- ✅ Consistent behavior

## 🚀 Future Enhancements

### 1. Microservices Migration

#### Service Boundaries
- **User Service**: Authentication, profile management
- **Job Service**: Job management, applications
- **Company Service**: Company information, reviews
- **Notification Service**: Email, SMS, push notifications
- **File Service**: File storage, processing
- **Analytics Service**: Reporting, dashboards

### 2. Advanced Features

#### AI Integration
- **CV Parsing**: AI-powered CV parsing và extraction
- **Application Matching**: ML-based matching applications với job requirements
- **Interview Preparation**: AI-generated interview questions
- **Salary Prediction**: ML-based salary estimates
- **Candidate Ranking**: AI-powered candidate ranking

#### Real-time Features
- **Live Chat**: Real-time communication giữa HR/Recruiter
- **Collaborative Comments**: Real-time comments trên applications
- **Live Notifications**: WebSocket-based real-time updates
- **Video Interviews**: Integrated video calling

### 3. Mobile Application

#### React Native Implementation
- **Cross-platform**: iOS and Android support
- **Offline Support**: Local data synchronization
- **Push Notifications**: Native push notifications
- **Biometric Authentication**: Fingerprint/Face ID login
