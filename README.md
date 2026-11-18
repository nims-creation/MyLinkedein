<p align="center">
  <img src="docs/logo.svg" alt="MyLinkedin" width="80" />
</p>

<h1 align="center">MyLinkedin</h1>

<p align="center">
  <b>A production-grade LinkedIn clone built with Microservices Architecture</b>
  <br/>
  <sub>Spring Boot 4 · React 18 · Kafka · Neo4j · PostgreSQL · Redis · Docker · Kubernetes</sub>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.x-6DB33F?logo=springboot" alt="Spring Boot 4" />
  <img src="https://img.shields.io/badge/React-18-61DAFB?logo=react" alt="React 18" />
  <img src="https://img.shields.io/badge/Kafka-KRaft-231F20?logo=apachekafka" alt="Kafka" />
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker" alt="Docker" />
  <img src="https://img.shields.io/badge/Kubernetes-Ready-326CE5?logo=kubernetes" alt="K8s" />
</p>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [System Architecture](#-system-architecture)
- [Tech Stack](#-tech-stack)
- [Microservices](#-microservices)
- [API Reference](#-api-reference)
- [Event-Driven Architecture](#-event-driven-architecture)
- [Database Design](#-database-design)
- [Getting Started](#-getting-started)
- [Docker Deployment](#-docker-deployment)
- [Kubernetes Deployment](#-kubernetes-deployment)
- [Project Structure](#-project-structure)
- [Future Roadmap](#-future-roadmap)
- [Contributing](#-contributing)

---

## 🎯 Overview

**MyLinkedin** is a full-stack professional networking platform inspired by LinkedIn, built from scratch using **microservices architecture**. The system is designed to be scalable, resilient, and production-ready with event-driven communication, graph-based social connections, and containerized deployment.

### Key Highlights

- 🏗️ **7 Independent Microservices** — each service owns its data, deployable independently
- 📡 **Event-Driven Architecture** — Kafka for async communication between services
- 🕸️ **Graph Database** — Neo4j for social connections (1st-degree, 2nd-degree, recommendations)
- 🔐 **JWT Authentication** — secure, stateless auth with token versioning & password reset
- ⚡ **Redis Caching** — session management and rate limiting at API Gateway
- ☁️ **Cloud-Native** — fully containerized with Docker Compose & Kubernetes manifests
- 🔄 **Service Discovery** — Netflix Eureka for dynamic service registration & load balancing

---

## 🏛️ System Architecture

### High-Level Architecture

<p align="center">
  <img src="docs/system-design.png" alt="System Design" width="800" />
</p>

### Architecture Flow (Mermaid)

```mermaid
flowchart TB
    subgraph Client
        APP["🌐 React SPA<br/>Port 80"]
    end

    subgraph Gateway["API Gateway Layer"]
        GW["🚪 API Gateway<br/>Port 8080<br/>JWT Auth · Rate Limit · Routing"]
    end

    subgraph Discovery["Service Discovery"]
        EUR["📡 Eureka Server<br/>Port 8761<br/>Service Registry · Load Balancing"]
    end

    subgraph Services["Microservices (Private Network)"]
        US["👤 User Service<br/>Port 9020"]
        PS["📝 Post Service<br/>Port 9010"]
        CS["🤝 Connection Service<br/>Port 9030"]
        NS["🔔 Notification Service<br/>Port 9040"]
        UL["📁 Uploader Service<br/>Port 9050"]
    end

    subgraph Messaging["Event Bus"]
        KF["📨 Apache Kafka<br/>KRaft Mode"]
    end

    subgraph Databases["Data Stores"]
        PG["🐘 PostgreSQL<br/>userDb · postDb · notification_db"]
        N4["🕸️ Neo4j<br/>Graph DB"]
        RD["⚡ Redis<br/>Cache · Sessions"]
        CL["☁️ Cloudinary<br/>Media CDN"]
    end

    APP -->|"HTTPS"| GW
    GW -->|"lb://"| US & PS & CS & NS & UL
    US & PS & CS & NS & UL -->|"Register"| EUR
    GW -->|"Discover"| EUR

    PS -->|"post_created"| KF
    PS -->|"post_liked"| KF
    US -->|"user_created"| KF
    KF -->|"Consume"| NS
    KF -->|"Consume"| CS

    US --> PG
    PS --> PG
    NS --> PG
    CS --> N4
    CS --> RD
    GW --> RD
    UL --> CL
```

---

## 🛠️ Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Frontend** | React 18, Vite, React Router 7 | Single Page Application |
| **API Gateway** | Spring Cloud Gateway | Routing, JWT Auth, Rate Limiting |
| **Backend** | Spring Boot 4.x, Java 21 | Microservices |
| **Service Discovery** | Netflix Eureka | Registration & Load Balancing |
| **Messaging** | Apache Kafka (KRaft) | Async Event-Driven Communication |
| **Relational DB** | PostgreSQL 16 | Users, Posts, Notifications |
| **Graph DB** | Neo4j 5 | Social Connections & Relationships |
| **Cache** | Redis 7 | Sessions, Rate Limiting |
| **Media Storage** | Cloudinary | Image & File CDN |
| **Containerization** | Docker, Docker Compose | Multi-container orchestration |
| **Orchestration** | Kubernetes | Production deployment manifests |
| **Build Tool** | Maven (mvnw) | Dependency management & build |

---

## 🧩 Microservices

### Service Overview

| Service | Port | Database | Responsibilities |
|---------|------|----------|-----------------|
| **discovery-server** | 8761 | — | Eureka service registry & health dashboard |
| **api-gateway** | 8080 | Redis | JWT authentication, request routing, rate limiting |
| **user-service** | 9020 | PostgreSQL (`userDb`) | Sign up, login, password reset, JWT tokens |
| **post-service** | 9010 | PostgreSQL (`postDb`) | Create/read posts, like/unlike, Kafka events |
| **connection-service** | 9030 | Neo4j + Redis | Send/accept/reject connection requests, graph queries |
| **notification-service** | 9040 | PostgreSQL (`notification_db`) | Kafka consumer, notification persistence |
| **uploader-service** | 9050 | Cloudinary | File upload to Cloudinary CDN |
| **frontend** | 80 | — | React SPA served by Nginx |

### Inter-Service Communication

```mermaid
sequenceDiagram
    participant U as User
    participant GW as API Gateway
    participant US as User Service
    participant PS as Post Service
    participant K as Kafka
    participant NS as Notification Service
    participant CS as Connection Service

    U->>GW: POST /api/v1/users/auth/signup
    GW->>US: Forward (no auth)
    US->>K: Publish "user_created"
    K->>CS: Create Person node in Neo4j

    U->>GW: POST /api/v1/posts/core
    GW->>GW: Validate JWT
    GW->>PS: Forward (authenticated)
    PS->>K: Publish "post_created"
    K->>NS: Store notification

    U->>GW: POST /api/v1/posts/likes/{id}
    GW->>PS: Forward (authenticated)
    PS->>K: Publish "post_liked"
    K->>NS: "User X liked your post"
```

---

## 📡 API Reference

### 🔐 Authentication (`/api/v1/users`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/auth/signup` | ❌ | Register new user |
| `POST` | `/auth/login` | ❌ | Login → returns JWT token |
| `POST` | `/auth/forgot-password` | ❌ | Request password reset link |
| `POST` | `/auth/reset-password` | ❌ | Reset password with token |

### 📝 Posts (`/api/v1/posts`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/core` | ✅ | Create a new post |
| `GET` | `/core/{postId}` | ✅ | Get post by ID |
| `GET` | `/core/users/{userId}/allPosts` | ✅ | Get all posts of a user |
| `POST` | `/likes/{postId}` | ✅ | Like a post |
| `DELETE` | `/likes/{postId}` | ✅ | Unlike a post |

### 🤝 Connections (`/api/v1/connections`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `GET` | `/core/{userId}/first-degree` | ✅ | Get 1st-degree connections |
| `POST` | `/core/request/{userId}` | ✅ | Send connection request |
| `POST` | `/core/accept/{userId}` | ✅ | Accept connection request |
| `POST` | `/core/reject/{userId}` | ✅ | Reject connection request |

### 📁 Uploader (`/api/v1/upload`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/file` | ✅ | Upload file → returns CDN URL |

---

## 📨 Event-Driven Architecture

MyLinkedin uses **Apache Kafka** for asynchronous, decoupled communication between services.

### Kafka Events

| Event | Producer | Consumer(s) | Payload |
|-------|----------|-------------|---------|
| `user_created` | user-service | connection-service | `{ userId, name }` |
| `post_created` | post-service | notification-service | `{ ownerUserId, postId, userId, content }` |
| `post_liked` | post-service | notification-service | `{ postId, ownerUserId, likeByUserId }` |

### Event Flow

```mermaid
flowchart LR
    US["👤 User Service"] -->|"user_created"| K["📨 Kafka"]
    PS["📝 Post Service"] -->|"post_created"| K
    PS -->|"post_liked"| K
    K -->|"Consume"| CS["🤝 Connection Service"]
    K -->|"Consume"| NS["🔔 Notification Service"]

    CS -->|"Create Person Node"| N4["🕸️ Neo4j"]
    NS -->|"Store Notification"| PG["🐘 PostgreSQL"]
```

---

## 🗄️ Database Design

### PostgreSQL — `userDb`

```
┌──────────────────────┐
│       users          │
├──────────────────────┤
│ id           BIGINT  │ PK, AUTO_INCREMENT
│ name         VARCHAR │ NOT NULL
│ email        VARCHAR │ NOT NULL, UNIQUE
│ password     VARCHAR │ NOT NULL (BCrypt)
│ tokenVersion INTEGER │ DEFAULT 0
└──────────────────────┘

┌──────────────────────────┐
│   password_reset_token   │
├──────────────────────────┤
│ id           BIGINT      │ PK
│ token        VARCHAR     │
│ user_id      BIGINT      │ FK → users
│ expiry_date  TIMESTAMP   │
└──────────────────────────┘
```

### PostgreSQL — `postDb`

```
┌──────────────────────┐       ┌──────────────────────┐
│       post           │       │     post_like        │
├──────────────────────┤       ├──────────────────────┤
│ id         BIGINT    │ PK    │ id         BIGINT    │ PK
│ content    TEXT       │       │ post_id    BIGINT    │ FK
│ user_id    BIGINT    │       │ user_id    BIGINT    │
│ created_at TIMESTAMP │       │ created_at TIMESTAMP │
└──────────────────────┘       └──────────────────────┘
```

### Neo4j — Graph Model

```
(:Person {userId, name})
        |
   [:CONNECTED_TO]  ←── 1st degree connection
        |
(:Person {userId, name})
```

### PostgreSQL — `notification_db`

```
┌──────────────────────┐
│    notification      │
├──────────────────────┤
│ id         BIGINT    │ PK
│ user_id    BIGINT    │
│ message    TEXT      │
│ created_at TIMESTAMP │
└──────────────────────┘
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** (JDK)
- **Docker Desktop** (latest)
- **Git**

### Quick Start (Docker Compose — Recommended)

```bash
# 1. Clone the repository
git clone https://github.com/yourusername/MyLinkedin.git
cd MyLinkedin

# 2. Create environment file
cp .env.example .env
# Edit .env with your values (DB passwords, Cloudinary keys, JWT secret)

# 3. Build and start everything
docker compose up --build -d

# 4. Wait ~90 seconds for all services to start, then verify
docker compose ps
```

### Access Points

| Service | URL |
|---------|-----|
| 🌐 **Frontend** | [http://localhost](http://localhost) |
| 📡 **Eureka Dashboard** | [http://localhost:8761](http://localhost:8761) |
| 🚪 **API Gateway** | [http://localhost:8080](http://localhost:8080) |
| 🕸️ **Neo4j Browser** | [http://localhost:7474](http://localhost:7474) |

### Environment Variables (`.env`)

```env
# Database
DB_PASSWORD=your_db_password

# Redis
REDIS_PASSWORD=your_redis_password

# Neo4j
NEO4J_PASSWORD=your_neo4j_password

# JWT
JWT_SECRET_KEY=your_jwt_secret

# Cloudinary
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

---

## 🐳 Docker Deployment

### Architecture in Docker

All services run as isolated containers connected via Docker networking:

```
┌─────────────────────────────────────────────────────┐
│                 Docker Network                       │
│                                                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │ Postgres │  │  Neo4j   │  │  Kafka   │          │
│  │  :5432   │  │  :7687   │  │  :9092   │          │
│  └──────────┘  └──────────┘  └──────────┘          │
│  ┌──────────┐  ┌──────────┐                         │
│  │  Redis   │  │ Eureka   │                         │
│  │  :6379   │  │  :8761   │                         │
│  └──────────┘  └──────────┘                         │
│       ↕              ↕                               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │ Gateway  │  │ User Svc │  │ Post Svc │          │
│  │  :8080   │  │  :9020   │  │  :9010   │          │
│  └──────────┘  └──────────┘  └──────────┘          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐          │
│  │ Conn Svc │  │Notif Svc │  │Upload Svc│          │
│  │  :9030   │  │  :9040   │  │  :9050   │          │
│  └──────────┘  └──────────┘  └──────────┘          │
│  ┌────────────────────────────────────────┐         │
│  │          Frontend (Nginx :80)          │         │
│  └────────────────────────────────────────┘         │
└─────────────────────────────────────────────────────┘
```

### Docker Commands

```bash
# Build all images
docker compose build

# Start all services
docker compose up -d

# View logs
docker compose logs -f [service-name]

# Stop everything
docker compose down

# Full reset (delete volumes)
docker compose down -v
```

---

## ☸️ Kubernetes Deployment

Production-ready K8s manifests are included in the `k8s/` directory.

### Directory Structure

```
k8s/
├── namespace.yaml                    # mylinkedin namespace
├── deploy.sh                         # One-click deploy script
├── secrets/
│   └── app-secrets.yaml              # Base64 encoded secrets
├── configmaps/
│   └── app-config.yaml               # Service URLs, ports
├── infrastructure/
│   ├── postgres.yaml                  # StatefulSet + PVC
│   ├── redis.yaml                     # StatefulSet + PVC
│   ├── neo4j.yaml                     # StatefulSet + PVC
│   ├── kafka.yaml                     # StatefulSet + PVC
│   └── postgres-init-configmap.yaml   # Multi-DB init script
├── services/
│   ├── discovery-server.yaml          # Deployment (1 replica)
│   ├── api-gateway.yaml               # Deployment (2 replicas)
│   ├── user-service.yaml              # Deployment (2 replicas)
│   ├── post-service.yaml              # Deployment (2 replicas)
│   ├── connection-service.yaml        # Deployment (2 replicas)
│   ├── notification-service.yaml      # Deployment (2 replicas)
│   └── uploader-service.yaml          # Deployment (2 replicas)
├── frontend/
│   └── frontend.yaml                  # Deployment (2 replicas)
└── ingress/
    └── ingress.yaml                   # ALB Ingress (AWS)
```

### Deploy to Kubernetes

```bash
# Apply all manifests in order
bash k8s/deploy.sh

# Or manually:
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secrets/
kubectl apply -f k8s/configmaps/
kubectl apply -f k8s/infrastructure/
kubectl apply -f k8s/services/
kubectl apply -f k8s/frontend/
kubectl apply -f k8s/ingress/

# Check status
kubectl get all -n mylinkedin
```

---

## 📂 Project Structure

```
MyLinkedin/
├── 📁 api-gateway/              # Spring Cloud Gateway (JWT + Routing)
│   └── src/main/
├── 📁 discovery-server/         # Netflix Eureka Server
│   └── src/main/
├── 📁 user-service/             # Authentication & User Management
│   └── src/main/
├── 📁 post-service/             # Posts CRUD + Likes + Kafka Producer
│   └── src/main/
├── 📁 ConnectionService/        # Neo4j Graph-based Connections
│   └── src/main/
├── 📁 notification-service/     # Kafka Consumer + Notifications
│   └── src/main/
├── 📁 uploader-service/         # Cloudinary File Upload
│   └── src/main/
├── 📁 mylinkedin-frontend/      # React 18 + Vite SPA
│   └── src/
├── 📁 infrastructure/           # Docker infra configs
│   └── postgres/init-multiple-dbs.sh
├── 📁 k8s/                      # Kubernetes manifests
│   ├── namespace.yaml
│   ├── deploy.sh
│   ├── secrets/
│   ├── configmaps/
│   ├── infrastructure/
│   ├── services/
│   ├── frontend/
│   └── ingress/
├── 📁 docs/                     # Architecture diagrams
│   └── system-design.png
├── 📄 docker-compose.yml        # Full-stack Docker Compose
├── 📄 .env.example              # Environment template
├── 📄 .gitignore
└── 📄 README.md                 # ← You are here
```

---

## 🗺️ Future Roadmap

- [ ] **Observability** — Zipkin distributed tracing + ELK centralized logging
- [ ] **Config Server** — Spring Cloud Config for centralized configuration
- [ ] **CI/CD Pipeline** — GitHub Actions for automated build, test & deploy
- [ ] **Search Service** — Elasticsearch for user/post search
- [ ] **Messaging Service** — Real-time chat using WebSockets
- [ ] **Feed Algorithm** — Personalized feed based on connections & interactions
- [ ] **Profile Photos** — Integrate uploader-service with user profiles
- [ ] **Comments** — Comment on posts with threaded replies
- [ ] **Email Notifications** — SMTP integration for password reset & alerts

---

## 🧑‍💻 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📜 License

This project is open source and available under the [MIT License](LICENSE).

---

<p align="center">
  Built with ❤️ by <a href="https://github.com/yourusername"><b>NimsCreation</b></a>
</p>
