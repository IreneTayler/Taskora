# Task Management API

RESTful task management service built with Kotlin, Spring Boot, and reactive programming.

## Features

- Create, read, update, delete tasks
- Pagination and filtering
- Task status management (NEW → IN_PROGRESS → DONE → CANCELLED)
- Reactive programming with Mono/Flux
- Native SQL queries with JdbcClient
- Input validation and error handling

## Tech Stack

- Kotlin
- Spring Boot 3.2.0
- Spring WebFlux (reactive)
- Project Reactor (Mono/Flux)
- JdbcClient with native SQL
- H2 Database (in-memory)
- Jakarta Validation
- JUnit 5 + MockK

## Quick Start

### Prerequisites
- Java 17+

### Run
```bash
./gradlew bootRun
```
Application starts at http://localhost:8080

## API Endpoints

### Create Task
```http
POST /api/tasks
Content-Type: application/json

{
  "title": "Prepare monthly report",
  "description": "Create financial report for March"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "Prepare monthly report",
  "description": "Create financial report for March",
  "status": "NEW",
  "createdAt": "2026-03-31T12:00:00",
  "updatedAt": "2026-03-31T12:00:00"
}
```

### Get All Tasks (with Pagination)
```http
GET /api/tasks?page=0&size=10&status=NEW
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Prepare monthly report",
      "description": "Create financial report for March",
      "status": "NEW",
      "createdAt": "2026-03-31T12:00:00",
      "updatedAt": "2026-03-31T12:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

### Get Task by ID
```http
GET /api/tasks/1
```

### Update Task Status
```http
PATCH /api/tasks/1/status
Content-Type: application/json

{
  "status": "IN_PROGRESS"
}
```

### Delete Task
```http
DELETE /api/tasks/1
```
**Response: 204 No Content**

## Testing

Run tests:
```bash
./gradlew test
```

## Task Status Values
- `NEW` - Initial status
- `IN_PROGRESS` - Being worked on  
- `DONE` - Completed
- `CANCELLED` - Cancelled

## Validation Rules
- Title: Required, 3-100 characters
- Description: Optional
- Status: Valid enum value