# PsTracker — ICPC Training Tracker

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![React](https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-6-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/Tailwind_CSS-3-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)

**A full-stack competitive programming training platform for ICPC teams.**  
Track Codeforces submissions, analyse performance trends, and coordinate with your team — all in one place.

</div>

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [API Reference](#api-reference)
- [Database Schema](#database-schema)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [Known Issues & Roadmap](#known-issues--roadmap)

---

## Overview

PsTracker is a training management tool built for ICPC teams. Coaches create teams, assign trainees, and monitor every member's Codeforces submission history — acceptance rates, runtime, verdict breakdowns, streaks, and more. Trainees see their own detailed analytics dashboard and can collaborate with teammates via a real-time chat and announcement system.

Submissions are **automatically synced** from the Codeforces API on a background schedule, so no manual entry is required.

---

## Features

### For Trainees
| Feature | Description |
|---|---|
| 📊 **Personal Dashboard** | Rating progress chart, verdict donut, consistency heatmap, streak tracker |
| 📋 **Submissions Page** | Full filterable/sortable history with per-period stats (Last Day / 7 Days / 30 Days / All Time / Custom range) |
| 🔥 **Performance Analytics** | Acceptance rate, avg. runtime, problems solved, longest streak, most active day |
| 👥 **Team Hub** | Real-time team chat, coach announcements, and training materials |

### For Coaches
| Feature | Description |
|---|---|
| 👁️ **Per-Trainee View** | Click "Stats" next to any team member to jump to their full submissions analytics |
| 📣 **Announcements** | Broadcast URGENT / UPDATE / INFO messages to the whole team |
| 📦 **Training Materials** | Share links, PDFs, and images with trainees |
| 🏆 **Team Management** | Create team, generate invite code, manage members |

### Platform
- JWT authentication with automatic refresh token rotation
- Dark / light mode with smooth transitions
- Fully responsive — mobile through desktop
- Real-time WebSocket team chat (STOMP over SockJS)
- Swagger / OpenAPI docs at `/swagger-ui.html`

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Browser Client                        │
│         React 19 + TypeScript + Vite + TailwindCSS          │
│                                                             │
│  Pages: TraineeDashboard · SubmissionsPage · TeamPage       │
│  Hooks: useSubmissions · useCurrentUser · useTeam · …       │
│  Services: submissionsService · teamService · …             │
└────────────────────────┬────────────────────────────────────┘
                         │  HTTP (Axios + JWT Bearer)
                         │  WebSocket (STOMP / SockJS)
┌────────────────────────▼────────────────────────────────────┐
│                    Spring Boot 4 API                         │
│                  http://localhost:8000/api                   │
│                                                             │
│  Controllers: Auth · User · Team · Submission · Problem …   │
│  Security: JWT Filter · Spring Security · Refresh Tokens    │
│  Background: Codeforces sync (@Scheduled, every 2 min)      │
└──────────┬─────────────────────────────┬───────────────────┘
           │ JPA / Hibernate             │ Spring Data Redis
┌──────────▼──────────┐       ┌──────────▼──────────────────┐
│    PostgreSQL DB     │       │      Redis Cache / Queue     │
│  localhost:5432      │       │      localhost:6379          │
└─────────────────────┘       └─────────────────────────────┘
                                         ▲
                    External API call ───┘
                    api.codeforces.com/api/user.status
```

---

## Tech Stack

### Backend (`/back`)
| Technology | Purpose |
|---|---|
| **Spring Boot 4.0.6** | REST API framework |
| **Spring Security** | Authentication & authorisation |
| **Spring WebSocket + STOMP** | Real-time team chat |
| **Spring Data JPA + Hibernate** | ORM / PostgreSQL persistence |
| **Spring Data Redis** | Caching layer |
| **JWT (JJWT)** | Stateless auth tokens + refresh |
| **Springdoc OpenAPI 3** | Swagger UI docs |
| **Lombok** | Boilerplate reduction |
| **Spring RestClient** | Codeforces API integration |
| **PostgreSQL** | Primary relational database |
| **Redis 7** | Token blacklist, session cache |

### Frontend (`/Front/icpc-tracker`)
| Technology | Purpose |
|---|---|
| **React 19** | UI framework |
| **TypeScript 6** | Type safety |
| **Vite 8** | Build tool & dev server |
| **TailwindCSS 3** | Utility-first styling |
| **React Router 7** | Client-side routing |
| **Axios** | HTTP client with JWT interceptors |
| **Lucide React** | Icon library |
| **Sonner** | Toast notifications |
| **Motion** | Animations |

---

## Project Structure

```
PsTracker/
├── back/                              # Spring Boot backend
│   └── src/main/java/com/TrainingTracker/TraingingTracker/
│       ├── Controllers/               # REST endpoints
│       │   ├── AuthController.java
│       │   ├── UserController.java
│       │   ├── TeamController.java
│       │   ├── SubmissionController.java
│       │   ├── ProblemController.java
│       │   ├── TagController.java
│       │   ├── AnnouncmentTeamController.java
│       │   ├── NotificationController.java
│       │   └── TeamMessageController.java
│       ├── BusinessLogic/
│       │   ├── InterfacesServiceLayer/ # Service interfaces
│       │   └── ImpServiceLayer/        # Service implementations
│       │       ├── Auth/               # Login, register, refresh
│       │       ├── Codeforces/         # Background CF sync
│       │       ├── User/
│       │       ├── Team/
│       │       ├── Submission/
│       │       ├── Problem/
│       │       ├── Tag/
│       │       ├── Announcment/
│       │       ├── Notification/
│       │       └── TeamMessage/
│       ├── DataAccessLayer/
│       │   ├── Entites/               # JPA entities
│       │   ├── Repositories/          # Spring Data repos
│       │   └── Dto/                   # Request / response DTOs
│       ├── Security/
│       │   ├── jwt/                   # JWT filter & service
│       │   ├── User/                  # UserDetails impl
│       │   └── WebSocket/             # WS auth interceptor
│       └── Config/
│           ├── Security/              # Spring Security config
│           ├── Redis/                 # Redis config
│           ├── WebSocket/             # STOMP config
│           └── SwaggerConfig/
│
├── Front/icpc-tracker/               # React frontend
│   └── src/
│       ├── pages/
│       │   ├── TraineeDashboard.tsx  # Trainee home
│       │   ├── SubmissionsPage.tsx   # Submissions analytics
│       │   ├── TeamPage.tsx          # Team Hub (chat/announcements/materials)
│       │   ├── CoachDashboard.tsx
│       │   ├── RootLayout.tsx        # Shared layout with sidebar
│       │   └── AuthPage.tsx
│       ├── components/
│       │   ├── Traineedashboard/     # Dashboard widgets
│       │   │   ├── ProfileCard.tsx
│       │   │   ├── RankCard.tsx
│       │   │   ├── StatsCard.tsx
│       │   │   ├── RatingProgressChart.tsx
│       │   │   ├── RecentVerdictsChart.tsx
│       │   │   ├── ConsistencyHeatmap.tsx
│       │   │   └── RecentSubmissions.tsx
│       │   ├── Submissions/          # Submissions page components
│       │   │   ├── PeriodSelector.tsx
│       │   │   ├── SubmissionStatsBar.tsx
│       │   │   ├── SubmissionVerdictsChart.tsx
│       │   │   ├── SubmissionActivityChart.tsx
│       │   │   └── SubmissionTable.tsx
│       │   └── shared/
│       │       ├── DashboardSidebar.tsx
│       │       ├── NavBar.tsx
│       │       ├── TeamChatPane.tsx
│       │       ├── AnnouncementsPanel.tsx
│       │       └── MaterialsPanel.tsx
│       ├── hooks/
│       │   ├── useSubmissions.ts     # API: GET /submissions/me or /user/:id
│       │   ├── useSubmissionsStats.ts # Pure stats computation hook
│       │   ├── useCurrentUser.ts     # API: GET /users/me
│       │   ├── useTeam.ts            # API: GET /teams/:id
│       │   ├── useTeamMembers.ts     # API: GET /users/team/:id
│       │   ├── useProblems.ts        # API: GET /problems
│       │   └── Auth/
│       │       ├── useLogin.ts
│       │       └── useRegister.ts
│       ├── services/
│       │   ├── submissionsService.tsx
│       │   ├── userService.ts
│       │   ├── teamService.ts
│       │   ├── problemService.ts     # Also covers /tags
│       │   ├── AuthService.tsx
│       │   └── ErrorService.tsx
│       ├── types/
│       │   ├── api.types.ts          # Mirrors all backend Java DTOs
│       │   ├── dashboard.types.ts    # Frontend-specific types
│       │   └── auth.types.ts
│       └── config/
│           └── api.tsx               # Axios instance + JWT interceptors
│
└── docker-compose.yml                # Redis container
```

---

## API Reference

Base URL: `http://localhost:8000/api`  
Interactive docs: `http://localhost:8000/swagger-ui.html`

### Authentication — `/api/auth`
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/auth/register` | ❌ | Register a new user (trainee or coach) |
| `POST` | `/auth/login` | ❌ | Login — returns JWT + refresh token |
| `POST` | `/auth/refresh` | Cookie | Refresh access token |
| `POST` | `/auth/logout` | ✅ | Invalidate tokens |

### Users — `/api/users`
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/users/me` | ✅ | Current user profile + stats |
| `GET` | `/users` | ✅ | All users |
| `GET` | `/users/:id` | ✅ | User by ID |
| `GET` | `/users/team/:teamId` | ✅ | All members of a team |

### Teams — `/api/teams`
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/teams` | ✅ Coach | Create team → returns `{ teamCode }` |
| `POST` | `/teams/join` | ✅ | Join team by code |
| `POST` | `/teams/leave/:teamId` | ✅ | Leave team |
| `GET` | `/teams/:id` | ✅ | Team details + member list |

### Submissions — `/api/submissions`
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/submissions/me` | ✅ | Current user's submissions |
| `GET` | `/submissions/user/:userId` | ✅ | Submissions for any user (coach) |
| `GET` | `/submissions/problem/:problemId` | ✅ | By problem |
| `GET` | `/submissions/:id` | ✅ | Single submission |
| `POST` | `/submissions/me` | ✅ | Manually record a submission |
| `PUT` | `/submissions/:id` | ✅ | Update verdict / runtime |
| `DELETE` | `/submissions/:id` | ✅ | Delete a submission |

### Problems — `/api/problems`
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/problems` | All problems (with tags + rating) |
| `GET` | `/problems/:id` | Problem by ID |
| `POST` | `/problems` | Create problem |
| `PUT` | `/problems/:id` | Update problem |
| `DELETE` | `/problems/:id` | Delete problem |

### Tags — `/api/tags`
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/tags` | All tags |
| `POST` | `/tags` | Create tag |
| `PUT` | `/tags/:id` | Update tag |
| `DELETE` | `/tags/:id` | Delete tag |

### Announcements — `/api/announcment-teams`
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/announcment-teams/team/:teamId` | All team announcements |
| `POST` | `/announcment-teams` | Send announcement to team |

> **WebSocket** (STOMP): connect to `ws://localhost:8000/ws` with JWT header for real-time team chat.

---

## Database Schema

```
User ──────────────┐
  id               │
  userName         │   1      N
  email            ├──────── Submission
  role (TRAINEE/COACH)        id
  codeforcesHandle │           userId  ──────── Problem
  team_id ─────────┤           problemId        id
  isCoach          │           verdict           name
                   │           timeConsumedMs    rating
Team ──────────────┘           memoryConsumedBytes  contestId
  id                           createdAt         tags[]
  teamName
  teamCode         ──── TeamMessage
  coachId              id
                       content
Announcement ────────  teamId
  id               ──  senderId
  type (URGENT/UPDATE/INFO)    createdAt
  content
  senderId (coach)
  receiverId (team)
  isTeamAnnouncement

Notification
  id
  userId
  title / message
  type
  isRead
  createdAt

RefreshToken
  id
  userId
  token
  expiryDate
```

---

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+
- Node.js 20+
- Docker & Docker Compose (for Redis)
- PostgreSQL (running locally or via Docker)

### 1 — Start infrastructure

```bash
# Start Redis (required for token management)
docker-compose up -d
```

### 2 — Configure the backend

Create `back/src/main/resources/application.properties` (or `.yml`):

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/pstracker
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# JWT
app.jwt.secret=your_256_bit_secret_here
app.jwt.expiration-ms=900000
app.jwt.refresh-expiration-ms=604800000

# Server
server.port=8000
```

### 3 — Run the backend

```bash
cd back
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8000/api`.  
Swagger UI: `http://localhost:8000/swagger-ui.html`

### 4 — Run the frontend

```bash
cd Front/icpc-tracker
npm install
npm run dev
```

Open `http://localhost:5173` in your browser.

---

## Environment Variables

### Backend (`application.properties`)
| Key | Description | Example |
|---|---|---|
| `spring.datasource.url` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/pstracker` |
| `spring.datasource.username` | DB username | `postgres` |
| `spring.datasource.password` | DB password | `secret` |
| `spring.data.redis.host` | Redis host | `localhost` |
| `app.jwt.secret` | 256-bit JWT signing secret | `myVeryLongSecretKey...` |
| `app.jwt.expiration-ms` | Access token TTL (ms) | `900000` (15 min) |
| `app.jwt.refresh-expiration-ms` | Refresh token TTL (ms) | `604800000` (7 days) |

### Frontend
The API base URL is configured in [`src/config/api.tsx`](Front/icpc-tracker/src/config/api.tsx):
```ts
baseURL: "http://localhost:8000/api"
```

---

## Known Issues & Roadmap

### 🚨 Active Bugs (Backend)

| # | File | Issue |
|---|---|---|
| 1 | `ImpCfService.java` | Background sync thread uses `SecurityContextHolder` which is empty on scheduled threads → crashes every 2 min |
| 2 | `SubmissionMapper.java` | Uses `SecurityContextHolder` inside a mapper called by the sync task → same crash |
| 3 | `ImpCfService.java` | Transient `Problem` entity passed to submission save → potential Hibernate constraint violation |

### ⚠️ Frontend — In Progress
- [ ] Replace all `mockProfile.ts` usage with real API calls (hooks are wired, pages still consume mock data in some places)
- [ ] Implement WebSocket client (`@stomp/stompjs` + `sockjs-client`) for real-time team chat
- [ ] Connect `TeamPage` to real API (`useTeam`, `useTeamMembers`)
- [ ] Coach dashboard page (currently a placeholder)

### 🗺️ Planned Features
- [ ] Leaderboard — team ranking by acceptance rate / problems solved
- [ ] Contest scheduler & upcoming contest tracker
- [ ] Problem recommendation engine based on weak tags
- [ ] Notification bell with real-time push (WebSocket)
- [ ] Export submissions data to CSV

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m "feat: add my feature"`
4. Push and open a Pull Request

---

<div align="center">
  Built with ❤️ for the competitive programming community
</div>
