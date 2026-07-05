# ICPC Tracker Frontend

React + TypeScript frontend for the ICPC training tracker.

## Backend API

The frontend is configured to call the backend through:

```txt
http://localhost:8080/api
```

All URLs below are relative to that base URL. The Axios client sends JSON requests with:

```http
Content-Type: application/json
```

After login/register, the access token is stored in `localStorage` and sent on authenticated requests as:

```http
Authorization: Bearer <token>
```

The client also sends cookies because `withCredentials` is enabled. When an authenticated request receives `401`, the client calls `POST /auth/refresh` and retries the original request with the new token.

### Common Response Models

#### Auth User

Used by `POST /auth/login` and `POST /auth/register` in the current frontend service.

```json
{
  "user": {
    "id": 1,
    "userName": "trainee_user",
    "token": "jwt-access-token",
    "isCoach": false
  }
}
```

`src/types/api.types.ts` also contains a flat auth response type:

```json
{
  "token": "jwt-access-token",
  "userId": 1,
  "userName": "trainee_user",
  "isCoach": false
}
```

#### TraineeResponse

```json
{
  "id": 1,
  "userName": "trainee_user",
  "role": "Trainee",
  "email": "trainee@example.com",
  "teamId": 10,
  "teamName": "Team A",
  "numberOfSolveProblems": 42,
  "totalSumbissions": 100,
  "numberOfTimeLimitVerdict": 3,
  "numberOfMemoryLimitVerdict": 1,
  "numberOfWrongAnswerVerdict": 12
}
```

#### TeamResponse

```json
{
  "id": 10,
  "teamName": "Team A",
  "teamCode": "ABC123",
  "coachId": 2,
  "coachUsername": "coach_user",
  "trainees": [
    {
      "id": 1,
      "userName": "trainee_user",
      "role": "Trainee",
      "email": "trainee@example.com",
      "teamId": 10,
      "teamName": "Team A",
      "numberOfSolveProblems": 42,
      "totalSumbissions": 100,
      "numberOfTimeLimitVerdict": 3,
      "numberOfMemoryLimitVerdict": 1,
      "numberOfWrongAnswerVerdict": 12
    }
  ]
}
```

#### ProblemResponse

```json
{
  "id": 100,
  "problemIndex": "A",
  "name": "Watermelon",
  "contestId": 4,
  "rating": 800,
  "tags": ["math", "implementation"]
}
```

#### SubmissionResponse

```json
{
  "id": 500,
  "userId": 1,
  "userName": "trainee_user",
  "problemId": 100,
  "problemName": "Watermelon",
  "verdict": "AC",
  "timeConsumedMs": 62,
  "memoryConsumedBytes": 102400,
  "createdAt": "2026-06-29T10:30:00"
}
```

#### TagResponse

```json
{
  "id": 1,
  "tagName": "dp"
}
```

### Auth API

#### `POST /auth/login`

Full URL: `http://localhost:8080/api/auth/login`

Request:

```json
{
  "email": "trainee@example.com",
  "password": "password123"
}
```

Response:

```json
{
  "user": {
    "id": 1,
    "userName": "trainee_user",
    "token": "jwt-access-token",
    "isCoach": false
  }
}
```

#### `POST /auth/register`

Full URL: `http://localhost:8080/api/auth/register`

Request:

```json
{
  "username": "trainee_user",
  "email": "trainee@example.com",
  "password": "password123",
  "codeforcesHandle": "tourist",
  "isCoach": false
}
```

Response:

```json
{
  "user": {
    "id": 1,
    "userName": "trainee_user",
    "token": "jwt-access-token",
    "isCoach": false
  }
}
```

#### `POST /auth/logout`

Full URL: `http://localhost:8080/api/auth/logout`

Request: no body.

Response: backend-defined logout response. The frontend clears local auth state after this call succeeds.

#### `POST /auth/refresh`

Full URL: `http://localhost:8080/api/auth/refresh`

Request: no JSON body. The refresh cookie is sent with the request.

Response:

```json
{
  "token": "new-jwt-access-token"
}
```

### Users API

#### `GET /users/me`

Full URL: `http://localhost:8080/api/users/me`

Request: no body.

Response: `TraineeResponse`.

#### `GET /users`

Full URL: `http://localhost:8080/api/users`

Request: no body.

Response:

```json
[
  {
    "id": 1,
    "userName": "trainee_user",
    "role": "Trainee",
    "email": "trainee@example.com",
    "teamId": 10,
    "teamName": "Team A",
    "numberOfSolveProblems": 42,
    "totalSumbissions": 100,
    "numberOfTimeLimitVerdict": 3,
    "numberOfMemoryLimitVerdict": 1,
    "numberOfWrongAnswerVerdict": 12
  }
]
```

#### `GET /users/team/{teamId}`

Full URL: `http://localhost:8080/api/users/team/{teamId}`

Request: no body.

Response: `TraineeResponse[]`.

#### `GET /users/{id}`

Full URL: `http://localhost:8080/api/users/{id}`

Request: no body.

Response: `TraineeResponse`.

### Teams API

#### `POST /teams`

Full URL: `http://localhost:8080/api/teams`

Request:

```json
{
  "teamName": "Team A"
}
```

Response:

```json
{
  "teamCode": "ABC123"
}
```

#### `POST /teams/join`

Full URL: `http://localhost:8080/api/teams/join`

Request:

```json
{
  "teamCode": "ABC123"
}
```

Response: no response body expected by the frontend.

#### `POST /teams/leave/{teamId}`

Full URL: `http://localhost:8080/api/teams/leave/{teamId}`

Request: no body.

Response: no response body expected by the frontend.

#### `GET /teams/{id}`

Full URL: `http://localhost:8080/api/teams/{id}`

Request: no body.

Response: `TeamResponse`.

#### `GET /teams/coach/me`

Full URL: `http://localhost:8080/api/teams/coach/me`

Request: no body.

Response: `TeamResponse[]`.

### Problems API

#### `GET /problems`

Full URL: `http://localhost:8080/api/problems`

Request: no body.

Response: `ProblemResponse[]`.

#### `GET /problems/{id}`

Full URL: `http://localhost:8080/api/problems/{id}`

Request: no body.

Response: `ProblemResponse`.

#### `POST /problems`

Full URL: `http://localhost:8080/api/problems`

Request:

```json
{
  "problemIndex": "A",
  "name": "Watermelon",
  "contestId": 4,
  "rating": 800,
  "tags": ["math", "implementation"]
}
```

Response: `ProblemResponse`.

#### `PUT /problems/{id}`

Full URL: `http://localhost:8080/api/problems/{id}`

Request:

```json
{
  "problemIndex": "B",
  "name": "Before an Exam",
  "contestId": 4,
  "rating": 1200,
  "tags": ["greedy"]
}
```

Response: `ProblemResponse`.

#### `DELETE /problems/{id}`

Full URL: `http://localhost:8080/api/problems/{id}`

Request: no body.

Response: no response body expected by the frontend.

### Tags API

#### `GET /tags`

Full URL: `http://localhost:8080/api/tags`

Request: no body.

Response: `TagResponse[]`.

#### `GET /tags/{id}`

Full URL: `http://localhost:8080/api/tags/{id}`

Request: no body.

Response: `TagResponse`.

#### `POST /tags`

Full URL: `http://localhost:8080/api/tags`

Request:

```json
{
  "tagName": "dp"
}
```

Response: `TagResponse`.

#### `PUT /tags/{id}`

Full URL: `http://localhost:8080/api/tags/{id}`

Request:

```json
{
  "tagName": "dynamic programming"
}
```

Response: `TagResponse`.

#### `DELETE /tags/{id}`

Full URL: `http://localhost:8080/api/tags/{id}`

Request: no body.

Response: no response body expected by the frontend.

### Submissions API

#### `GET /submissions`

Full URL: `http://localhost:8080/api/submissions`

Request: no body.

Response: `SubmissionResponse[]`.

#### `GET /submissions/me`

Full URL: `http://localhost:8080/api/submissions/me`

Request: no body.

Response: `SubmissionResponse[]`.

#### `GET /submissions/user/{userId}`

Full URL: `http://localhost:8080/api/submissions/user/{userId}`

Request: no body.

Response: `SubmissionResponse[]`.

#### `GET /submissions/problem/{problemId}`

Full URL: `http://localhost:8080/api/submissions/problem/{problemId}`

Request: no body.

Response: `SubmissionResponse[]`.

#### `GET /submissions/{id}`

Full URL: `http://localhost:8080/api/submissions/{id}`

Request: no body.

Response: `SubmissionResponse`.

#### `POST /submissions`

Full URL: `http://localhost:8080/api/submissions`

Request:

```json
{
  "userId": 1,
  "problemId": 100,
  "verdict": "AC",
  "timeConsumedMs": 62,
  "memoryConsumedBytes": 102400
}
```

Response: `SubmissionResponse`.

#### `POST /submissions/me`

Full URL: `http://localhost:8080/api/submissions/me`

Request:

```json
{
  "userId": 1,
  "problemId": 100,
  "verdict": "AC",
  "timeConsumedMs": 62,
  "memoryConsumedBytes": 102400
}
```

Response: `SubmissionResponse`.

#### `PUT /submissions/{id}`

Full URL: `http://localhost:8080/api/submissions/{id}`

Request:

```json
{
  "verdict": "WA",
  "timeConsumedMs": 1000,
  "memoryConsumedBytes": 204800
}
```

Response: `SubmissionResponse`.

#### `DELETE /submissions/{id}`

Full URL: `http://localhost:8080/api/submissions/{id}`

Request: no body.

Response: no response body expected by the frontend.

## Source Files Used

The API list above is derived from:

- `src/config/api.tsx`
- `src/services/AuthService.tsx`
- `src/services/userService.ts`
- `src/services/teamService.ts`
- `src/services/problemService.ts`
- `src/services/submissionsService.tsx`
- `src/types/api.types.ts`
- `src/types/auth.types.ts`
