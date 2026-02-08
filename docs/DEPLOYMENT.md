# 🚀 JobTracker ATS Deployment Guide

## 📋 Tổng quan Deployment

JobTracker ATS được thiết kế để deploy dễ dàng với Docker và Docker Compose, hỗ trợ cả môi trường development và production cho kiến trúc **multi-tenant**.

## 🏗️ Kiến trúc Deployment

### Development Environment
```
┌─────────────────────────────────────────────────────────────┐
│                    Developer Machine                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   React     │ │   Spring    │ │   MySQL     │           │
│  │   Dev       │ │   Boot      │ │   Local     │           │
│  │   Server    │ │   App       │ │   Database  │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
```

### Production Environment
```
┌─────────────────────────────────────────────────────────────┐
│                    Load Balancer (Nginx)                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Application Servers                      │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   Spring    │ │   Spring    │ │   Spring    │           │
│  │   Boot      │ │   Boot      │ │   Boot      │           │
│  │   App 1     │ │   App 2     │ │   App 3     │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Database Cluster                         │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   MySQL     │ │   MySQL     │ │   Redis     │           │
│  │   Primary   │ │   Replica   │ │   Cache     │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
```

## 🐳 Docker Configuration

### 1. Backend Dockerfile

```dockerfile
# backend/Dockerfile
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src/ src/

# Build application
RUN ./mvnw clean package -DskipTests

# Create non-root user
RUN addgroup --system spring && adduser --system spring --ingroup spring
RUN chown -R spring:spring /app
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
CMD ["java", "-jar", "target/jobtracker-0.0.1-SNAPSHOT.jar"]
```

### 2. Frontend Dockerfile (Create React App)

```dockerfile
# frontend/Dockerfile
FROM node:18-alpine AS builder

# Set working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build application
RUN npm run build

# Production stage
FROM nginx:alpine

# Copy built files
COPY --from=builder /app/build /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost/ || exit 1

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
```

### 3. Docker Compose - Development

```yaml
# docker-compose.dev.yml
version: '3.8'

services:
  # MySQL Database
  mysql:
    image: mysql:8.0
    container_name: jobtracker-mysql-dev
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: jobtracker
      MYSQL_USER: jobtracker
      MYSQL_PASSWORD: password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./scripts/init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - jobtracker-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  # Redis Cache
  redis:
    image: redis:7-alpine
    container_name: jobtracker-redis-dev
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - jobtracker-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      timeout: 3s
      retries: 5

  # Spring Boot Application
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: jobtracker-backend-dev
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/jobtracker?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: jobtracker
      SPRING_DATASOURCE_PASSWORD: password
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PORT: 6379
      JWT_SECRET: your-secret-key-here
      CLOUDINARY_CLOUD_NAME: your-cloudinary-cloud-name
      CLOUDINARY_API_KEY: your-cloudinary-api-key
      CLOUDINARY_API_SECRET: your-cloudinary-api-secret
      BREVO_API_KEY: your-brevo-api-key
      GOOGLE_CLIENT_ID: your-google-client-id
      GOOGLE_CLIENT_SECRET: your-google-client-secret
    ports:
      - "8080:8080"
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - jobtracker-network
    volumes:
      - ./logs:/app/logs
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      timeout: 10s
      retries: 5

  # React Frontend
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: jobtracker-frontend-dev
    ports:
      - "3000:80"
    depends_on:
      - backend
    networks:
      - jobtracker-network
    environment:
      REACT_APP_API_URL: http://localhost:8080/api/v1
      REACT_APP_WS_URL: ws://localhost:8080/ws

  # Nginx Reverse Proxy
  nginx:
    image: nginx:alpine
    container_name: jobtracker-nginx-dev
    ports:
      - "80:80"
    volumes:
      - ./nginx/nginx.dev.conf:/etc/nginx/nginx.conf
    depends_on:
      - frontend
      - backend
    networks:
      - jobtracker-network

volumes:
  mysql_data:
  redis_data:

networks:
  jobtracker-network:
    driver: bridge
```

### 4. Docker Compose - Production

```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  # MySQL Database
  mysql:
    image: mysql:8.0
    container_name: jobtracker-mysql-prod
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: jobtracker
      MYSQL_USER: jobtracker
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
    volumes:
      - mysql_data:/var/lib/mysql
      - ./scripts/init.sql:/docker-entrypoint-initdb.d/init.sql
      - ./mysql/conf.d:/etc/mysql/conf.d
    networks:
      - jobtracker-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  # Redis Cache
  redis:
    image: redis:7-alpine
    container_name: jobtracker-redis-prod
    volumes:
      - redis_data:/data
      - ./redis/redis.conf:/usr/local/etc/redis/redis.conf
    networks:
      - jobtracker-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      timeout: 3s
      retries: 5

  # Spring Boot Application
  backend:
    image: jobtracker/backend:latest
    container_name: jobtracker-backend-prod
    environment:
      SPRING_PROFILES_ACTIVE: production
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/jobtracker?useSSL=true&serverTimezone=UTC
      SPRING_DATASOURCE_USERNAME: jobtracker
      SPRING_DATASOURCE_PASSWORD: ${MYSQL_PASSWORD}
      SPRING_REDIS_HOST: redis
      SPRING_REDIS_PORT: 6379
      JWT_SECRET: ${JWT_SECRET}
      CLOUDINARY_CLOUD_NAME: ${CLOUDINARY_CLOUD_NAME}
      CLOUDINARY_API_KEY: ${CLOUDINARY_API_KEY}
      CLOUDINARY_API_SECRET: ${CLOUDINARY_API_SECRET}
      BREVO_API_KEY: ${BREVO_API_KEY}
      GOOGLE_CLIENT_ID: ${GOOGLE_CLIENT_ID}
      GOOGLE_CLIENT_SECRET: ${GOOGLE_CLIENT_SECRET}
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - jobtracker-network
    restart: unless-stopped
    volumes:
      - ./logs:/app/logs
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      timeout: 10s
      retries: 5
    deploy:
      replicas: 3
      resources:
        limits:
          memory: 1G
          cpus: '0.5'
        reservations:
          memory: 512M
          cpus: '0.25'

  # React Frontend
  frontend:
    image: jobtracker/frontend:latest
    container_name: jobtracker-frontend-prod
    depends_on:
      - backend
    networks:
      - jobtracker-network
    restart: unless-stopped
    environment:
      REACT_APP_API_URL: https://api.jobtracker.com/api/v1
      REACT_APP_WS_URL: wss://api.jobtracker.com/ws

  # Nginx Reverse Proxy
  nginx:
    image: nginx:alpine
    container_name: jobtracker-nginx-prod
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.prod.conf:/etc/nginx/nginx.conf
      - ./nginx/ssl:/etc/nginx/ssl
      - ./logs/nginx:/var/log/nginx
    depends_on:
      - frontend
      - backend
    networks:
      - jobtracker-network
    restart: unless-stopped

volumes:
  mysql_data:
  redis_data:

networks:
  jobtracker-network:
    driver: bridge
```

## ⚙️ Configuration Files

### 1. Nginx Configuration - Development

```nginx
# nginx/nginx.dev.conf
events {
    worker_connections 1024;
}

http {
    upstream backend {
        server backend:8080;
    }

    upstream frontend {
        server frontend:80;
    }

    server {
        listen 80;
        server_name localhost;

        # Frontend
        location / {
            proxy_pass http://frontend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Backend API
        location /api/ {
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # WebSocket
        location /ws/ {
            proxy_pass http://backend;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Health check
        location /health {
            proxy_pass http://backend/actuator/health;
        }
    }
}
```

### 2. Nginx Configuration - Production

```nginx
# nginx/nginx.prod.conf
events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    # Logging
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;
    error_log /var/log/nginx/error.log warn;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript application/javascript application/xml+rss application/json;

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;
    limit_req_zone $binary_remote_addr zone=login:10m rate=5r/m;

    # Upstream servers
    upstream backend {
        server backend:8080 max_fails=3 fail_timeout=30s;
        keepalive 32;
    }

    upstream frontend {
        server frontend:80 max_fails=3 fail_timeout=30s;
        keepalive 32;
    }

    # HTTP to HTTPS redirect
    server {
        listen 80;
        server_name jobtracker.com www.jobtracker.com;
        return 301 https://$server_name$request_uri;
    }

    # HTTPS server
    server {
        listen 443 ssl http2;
        server_name jobtracker.com www.jobtracker.com;

        # SSL configuration
        ssl_certificate /etc/nginx/ssl/cert.pem;
        ssl_certificate_key /etc/nginx/ssl/key.pem;
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
        ssl_prefer_server_ciphers off;
        ssl_session_cache shared:SSL:10m;
        ssl_session_timeout 10m;

        # Security headers
        add_header X-Frame-Options DENY;
        add_header X-Content-Type-Options nosniff;
        add_header X-XSS-Protection "1; mode=block";
        add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;

        # Frontend
        location / {
            proxy_pass http://frontend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            
            # Cache static assets
            location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
                expires 1y;
                add_header Cache-Control "public, immutable";
            }
        }

        # Backend API
        location /api/ {
            limit_req zone=api burst=20 nodelay;
            
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            
            # Timeouts
            proxy_connect_timeout 30s;
            proxy_send_timeout 30s;
            proxy_read_timeout 30s;
        }

        # Login endpoint with stricter rate limiting
        location /api/v1/auth/login {
            limit_req zone=login burst=5 nodelay;
            
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # WebSocket
        location /ws/ {
            proxy_pass http://backend;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection "upgrade";
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            
            # WebSocket timeouts
            proxy_read_timeout 86400;
            proxy_send_timeout 86400;
        }

        # Health check
        location /health {
            proxy_pass http://backend/actuator/health;
            access_log off;
        }

        # Metrics (restricted access)
        location /actuator/ {
            allow 10.0.0.0/8;
            allow 172.16.0.0/12;
            allow 192.168.0.0/16;
            deny all;
            
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

### 3. Application Properties - Production

```yaml
# backend/src/main/resources/application-production.yml
spring:
  profiles:
    active: production
  
  datasource:
    url: jdbc:mysql://mysql:3306/jobtracker?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: ${SPRING_DATASOURCE_USERNAME:jobtracker}
    password: ${SPRING_DATASOURCE_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: false
        use_sql_comments: false
        jdbc:
          batch_size: 25
        order_inserts: true
        order_updates: true
        batch_versioned_data: true
        connection:
          provider_disables_autocommit: true
  
  redis:
    host: ${SPRING_REDIS_HOST:redis}
    port: ${SPRING_REDIS_PORT:6379}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
        max-wait: 2000ms
  
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope:
              - email
              - profile

# JWT Configuration
jwt:
  secret: ${JWT_SECRET}
  expiration: 3600000 # 1 hour
  refresh-expiration: 604800000 # 7 days

# Dropbox Configuration
dropbox:
  access-token: ${DROPBOX_ACCESS_TOKEN}
  app-key: ${DROPBOX_APP_KEY}
  app-secret: ${DROPBOX_APP_SECRET}

# Application Configuration
app:
  cors:
    allowed-origins: https://jobtracker.com,https://www.jobtracker.com
    allowed-methods: GET,POST,PUT,PATCH,DELETE,OPTIONS
    allowed-headers: "*"
    allow-credentials: true
    max-age: 3600
  
  file:
    max-size: 52428800 # 50MB
    allowed-types: application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document
  
  notification:
    email:
      from: noreply@jobtracker.com
      reply-to: noreply@jobtracker.com
    reminder:
      days-before: 3
      enabled: true

# Logging Configuration
logging:
  level:
    com.jobtracker: INFO
    org.springframework.security: WARN
    org.hibernate.SQL: WARN
    org.hibernate.type.descriptor.sql.BasicBinder: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: /app/logs/jobtracker.log
    max-size: 100MB
    max-history: 30

# Management endpoints
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true
```

## 🔧 Environment Variables

### Development (.env.dev)
```bash
# Database
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_PASSWORD=password

# JWT
JWT_SECRET=your-development-secret-key-here

# Cloudinary (File Storage)
CLOUDINARY_CLOUD_NAME=your-cloudinary-cloud-name
CLOUDINARY_API_KEY=your-cloudinary-api-key
CLOUDINARY_API_SECRET=your-cloudinary-api-secret

# Brevo (Email Service)
BREVO_API_KEY=your-brevo-api-key

# Google OAuth
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

### Production (.env.prod)
```bash
# Database
MYSQL_ROOT_PASSWORD=super-secure-root-password
MYSQL_PASSWORD=super-secure-password

# JWT
JWT_SECRET=super-secure-jwt-secret-key-256-bits

# Cloudinary (File Storage)
CLOUDINARY_CLOUD_NAME=production-cloudinary-cloud-name
CLOUDINARY_API_KEY=production-cloudinary-api-key
CLOUDINARY_API_SECRET=production-cloudinary-api-secret

# Brevo (Email Service)
BREVO_API_KEY=production-brevo-api-key

# Google OAuth
GOOGLE_CLIENT_ID=production-google-client-id
GOOGLE_CLIENT_SECRET=production-google-client-secret
```

## 🚀 Deployment Scripts

### 1. Development Setup Script

```bash
#!/bin/bash
# scripts/setup-dev.sh

echo "🚀 Setting up JobTracker Development Environment..."

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Create environment file if it doesn't exist
if [ ! -f .env.dev ]; then
    echo "📝 Creating .env.dev file..."
    cp .env.example .env.dev
    echo "⚠️  Please update .env.dev with your actual values"
fi

# Create necessary directories
echo "📁 Creating directories..."
mkdir -p logs/nginx
mkdir -p mysql/conf.d
mkdir -p redis
mkdir -p nginx/ssl

# Build and start services
echo "🔨 Building and starting services..."
docker-compose -f docker-compose.dev.yml up --build -d

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 30

# Check service health
echo "🏥 Checking service health..."
docker-compose -f docker-compose.dev.yml ps

echo "✅ Development environment is ready!"
echo "🌐 Frontend: http://localhost:3000"
echo "🔌 Backend API: http://localhost:8080"
echo "📊 Swagger UI: http://localhost:8080/swagger-ui.html"
echo "🗄️  MySQL: localhost:3306"
echo "🔴 Redis: localhost:6379"
```

### 2. Production Deployment Script

```bash
#!/bin/bash
# scripts/deploy-prod.sh

echo "🚀 Deploying JobTracker ATS to Production..."

# Check if environment file exists
if [ ! -f .env.prod ]; then
    echo "❌ .env.prod file not found. Please create it first."
    exit 1
fi

# Load environment variables
export $(cat .env.prod | xargs)

# Build production images
echo "🔨 Building production images..."
docker build -t jobtracker/backend:latest ./backend
docker build -t jobtracker/frontend:latest ./frontend

# Stop existing services
echo "🛑 Stopping existing services..."
docker-compose -f docker-compose.prod.yml down

# Start production services
echo "🚀 Starting production services..."
docker-compose -f docker-compose.prod.yml up -d

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
sleep 60

# Check service health
echo "🏥 Checking service health..."
docker-compose -f docker-compose.prod.yml ps

# Run database migrations
echo "🗄️  Running database migrations..."
docker-compose -f docker-compose.prod.yml exec backend ./mvnw flyway:migrate

echo "✅ Production deployment completed!"
echo "🌐 Application: https://jobtracker.com"
echo "🔌 API: https://api.jobtracker.com"
```

### 3. Backup Script

```bash
#!/bin/bash
# scripts/backup.sh

echo "💾 Creating backup..."

# Create backup directory
BACKUP_DIR="backups/$(date +%Y%m%d_%H%M%S)"
mkdir -p $BACKUP_DIR

# Database backup
echo "🗄️  Backing up database..."
docker-compose -f docker-compose.prod.yml exec mysql mysqldump -u root -p$MYSQL_ROOT_PASSWORD jobtracker > $BACKUP_DIR/database.sql

# Redis backup
echo "🔴 Backing up Redis..."
docker-compose -f docker-compose.prod.yml exec redis redis-cli BGSAVE
docker cp $(docker-compose -f docker-compose.prod.yml ps -q redis):/data/dump.rdb $BACKUP_DIR/redis.rdb

# Application logs
echo "📝 Backing up logs..."
cp -r logs $BACKUP_DIR/

# Compress backup
echo "📦 Compressing backup..."
tar -czf $BACKUP_DIR.tar.gz -C backups $(basename $BACKUP_DIR)
rm -rf $BACKUP_DIR

echo "✅ Backup created: $BACKUP_DIR.tar.gz"
```

## 📊 Monitoring & Health Checks

### 1. Health Check Endpoints

```bash
# Application health
curl http://localhost:8080/actuator/health

# Database health
curl http://localhost:8080/actuator/health/db

# Redis health
curl http://localhost:8080/actuator/health/redis

# Disk space
curl http://localhost:8080/actuator/health/diskSpace

# Custom health check
curl http://localhost:8080/actuator/health/custom
```

### 2. Monitoring Script

```bash
#!/bin/bash
# scripts/monitor.sh

echo "📊 JobTracker ATS Monitoring Dashboard"
echo "=================================="

# Check Docker containers
echo "🐳 Docker Containers:"
docker-compose -f docker-compose.prod.yml ps

echo ""

# Check application health
echo "🏥 Application Health:"
curl -s http://localhost:8080/actuator/health | jq '.'

echo ""

# Check database connections
echo "🗄️  Database Status:"
docker-compose -f docker-compose.prod.yml exec mysql mysqladmin -u root -p$MYSQL_ROOT_PASSWORD status

echo ""

# Check Redis status
echo "🔴 Redis Status:"
docker-compose -f docker-compose.prod.yml exec redis redis-cli ping

echo ""

# Check disk usage
echo "💾 Disk Usage:"
df -h

echo ""

# Check memory usage
echo "🧠 Memory Usage:"
free -h

echo ""

# Check application logs
echo "📝 Recent Application Logs:"
docker-compose -f docker-compose.prod.yml logs --tail=20 backend
```

## 🔒 Security Considerations

### 1. SSL/TLS Configuration

```bash
# Generate SSL certificate with Let's Encrypt
certbot certonly --standalone -d jobtracker.com -d www.jobtracker.com

# Copy certificates to nginx directory
cp /etc/letsencrypt/live/jobtracker.com/fullchain.pem nginx/ssl/cert.pem
cp /etc/letsencrypt/live/jobtracker.com/privkey.pem nginx/ssl/key.pem
```

### 2. Firewall Configuration

```bash
# UFW firewall rules
ufw allow 22/tcp    # SSH
ufw allow 80/tcp    # HTTP
ufw allow 443/tcp   # HTTPS
ufw deny 3306/tcp   # MySQL (internal only)
ufw deny 6379/tcp   # Redis (internal only)
ufw deny 8080/tcp   # Spring Boot (internal only)
ufw enable
```

### 3. Database Security

```sql
-- Create application user with limited privileges
CREATE USER 'jobtracker'@'%' IDENTIFIED BY 'secure_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON jobtracker.* TO 'jobtracker'@'%';
FLUSH PRIVILEGES;

-- Remove root remote access
DELETE FROM mysql.user WHERE User='root' AND Host='%';
FLUSH PRIVILEGES;
```

## 📈 Performance Optimization

### 1. MySQL Configuration

```ini
# mysql/conf.d/mysql.cnf
[mysqld]
# Performance tuning
innodb_buffer_pool_size = 1G
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 2
innodb_flush_method = O_DIRECT

# Connection settings
max_connections = 200
max_connect_errors = 1000

# Query cache
query_cache_type = 1
query_cache_size = 64M
query_cache_limit = 2M

# Slow query log
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2
```

### 2. Redis Configuration

```conf
# redis/redis.conf
# Memory management
maxmemory 256mb
maxmemory-policy allkeys-lru

# Persistence
save 900 1
save 300 10
save 60 10000

# Network
tcp-keepalive 300
timeout 0

# Security
requirepass your_redis_password
```

## 🔄 CI/CD Pipeline

### 1. GitHub Actions Workflow

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Cache Maven dependencies
        uses: actions/cache@v3
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
      
      - name: Run tests
        run: ./mvnw test
      
      - name: Run integration tests
        run: ./mvnw verify
        env:
          SPRING_PROFILES_ACTIVE: test

  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Build Docker images
        run: |
          docker build -t jobtracker/backend:${{ github.sha }} ./backend
          docker build -t jobtracker/frontend:${{ github.sha }} ./frontend
      
      - name: Push to registry
        run: |
          echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
          docker push jobtracker/backend:${{ github.sha }}
          docker push jobtracker/frontend:${{ github.sha }}

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - uses: actions/checkout@v3
      
      - name: Deploy to production
        run: |
          echo "${{ secrets.PRODUCTION_SSH_KEY }}" > ssh_key
          chmod 600 ssh_key
          ssh -i ssh_key -o StrictHostKeyChecking=no ${{ secrets.PRODUCTION_USER }}@${{ secrets.PRODUCTION_HOST }} '
            cd /opt/jobtracker &&
            git pull origin main &&
            ./scripts/deploy-prod.sh
          '
```

## 📋 Deployment Checklist

### Pre-deployment
- [ ] Environment variables configured
- [ ] SSL certificates generated
- [ ] Database migrations tested
- [ ] Security scan completed
- [ ] Performance tests passed
- [ ] Backup strategy implemented

### Deployment
- [ ] Stop existing services
- [ ] Pull latest images
- [ ] Run database migrations
- [ ] Start new services
- [ ] Verify health checks
- [ ] Test critical functionality

### Post-deployment
- [ ] Monitor application logs
- [ ] Check performance metrics
- [ ] Verify SSL certificates
- [ ] Test email notifications (Brevo)
- [ ] Validate file uploads (Cloudinary)
- [ ] Test multi-tenant data isolation
- [ ] Check WebSocket connections

## 🆘 Troubleshooting

### Common Issues

#### 1. Database Connection Issues
```bash
# Check MySQL container
docker-compose logs mysql

# Test connection
docker-compose exec backend ./mvnw flyway:info
```

#### 2. Redis Connection Issues
```bash
# Check Redis container
docker-compose logs redis

# Test connection
docker-compose exec redis redis-cli ping
```

#### 3. Application Startup Issues
```bash
# Check application logs
docker-compose logs backend

# Check health endpoint
curl http://localhost:8080/actuator/health
```

#### 4. Frontend Build Issues
```bash
# Check frontend logs
docker-compose logs frontend

# Rebuild frontend
docker-compose build frontend
```

### Log Locations
- **Application Logs**: `./logs/jobtracker.log`
- **Nginx Logs**: `./logs/nginx/`
- **MySQL Logs**: `docker-compose logs mysql`
- **Redis Logs**: `docker-compose logs redis`
