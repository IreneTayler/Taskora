# Task Management Service

A RESTful task management service built with **Kotlin**, **Spring Boot**, **Reactor**, and **JdbcClient** using native SQL queries. The service implements a reactive architecture with proper layered separation (Controller → Service → Repository).

## 🚀 Features

- **Create tasks** with title and description
- **Retrieve tasks** by ID or list with pagination and filtering
- **Update task status** (NEW → IN_PROGRESS → DONE → CANCELLED)
- **Delete tasks** by ID
- **Reactive programming** with Mono/Flux throughout the service layer
- **Native SQL** queries with JdbcClient (no ORM)
- **Comprehensive validation** and error handling
- **Unit tests** for all layers with high coverage

## 🛠 Tech Stack

- **Kotlin** - Primary language
- **Spring Boot 3.2.0** - Framework
- **Spring WebFlux** - Reactive web framework
- **Project Reactor** - Reactive streams (Mono/Flux)
- **JdbcClient** - Database access with native SQL
- **H2 Database** - In-memory database for development
- **Jakarta Validation** - Request validation
- **JUnit 5** - Testing framework
- **MockK** - Mocking for Kotlin

## 📁 Project Structure

```
src/main/kotlin/com/taskmanagement/
├── controller/          # REST endpoints
├── service/            # Business logic (reactive)
├── repository/         # Data access (JdbcClient + SQL)
├── model/              # Domain entities
├── dto/                # Request/Response DTOs
└── exception/          # Error handling

src/test/kotlin/com/taskmanagement/
├── controller/         # Controller unit tests
├── service/           # Service unit tests
├── repository/        # Repository integration tests
└── TaskManagementApplicationTests.kt
```

## 🏗 Architecture

The service follows a clean layered architecture with reactive programming:

```
Controller (HTTP) → Service (Reactive) → Repository (JDBC) → Database
                         ↓
                 Global Exception Handler
```

**Key Design Principles:**
- **Reactive Service Layer**: All service methods return `Mono<T>` or `Flux<T>`
- **Native SQL**: Repository uses JdbcClient with hand-written SQL queries
- **DTO Separation**: Clear separation between API contracts and domain models
- **Validation**: Input validation with Jakarta Bean Validation
- **Error Handling**: Centralized exception handling with proper HTTP status codes

## ⚙️ How to Run

### 1. Prerequisites
- Java 21 or higher
- No additional setup required (uses embedded H2 database)

### 2. Build the Project
```bash
./gradlew clean build -x test
```

### 3. Run the Application
```bash
./gradlew bootRun
```

The service will start on **http://localhost:8080**

### 4. Access H2 Console (Optional)
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:taskdb`
- Username: `sa`
- Password: (empty)

## 📌 API Endpoints

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

## 🧪 Testing

### Run All Tests
```bash
./gradlew test
```

### Test Coverage
The project includes comprehensive tests:

- **Service Tests**: Mock repository, test reactive flows
- **Controller Tests**: Test HTTP endpoints, validation, status codes
- **Repository Tests**: Integration tests with H2 database
- **Error Scenarios**: 404 Not Found, validation errors, etc.

### Manual API Testing

Use the provided `test-api.http` file with your IDE's HTTP client, or use curl:

```bash
# Create a task
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Task","description":"Testing the API"}'

# Get all tasks
curl "http://localhost:8080/api/tasks?page=0&size=10"

# Get task by ID
curl http://localhost:8080/api/tasks/1

# Update status
curl -X PATCH http://localhost:8080/api/tasks/1/status \
  -H "Content-Type: application/json" \
  -d '{"status":"DONE"}'

# Delete task
curl -X DELETE http://localhost:8080/api/tasks/1
```

## 🔧 Configuration

### Database Configuration (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:taskdb
    username: sa
    password: 
    driver-class-name: org.h2.Driver
```

### Task Status Values
- `NEW` - Initial status for new tasks
- `IN_PROGRESS` - Task is being worked on
- `DONE` - Task completed successfully
- `CANCELLED` - Task was cancelled

## ✅ Validation Rules

- **Title**: Required, 3-100 characters
- **Description**: Optional
- **Status**: Must be valid enum value

## 🚨 Error Handling

The service provides consistent error responses:

**404 Not Found:**
```json
{
  "message": "Task not found with id: 999"
}
```

**400 Bad Request (Validation):**
```json
{
  "message": "Validation failed: title: Title must be between 3 and 100 characters"
}
```

## 🎯 Key Implementation Details

### Reactive Service Layer
All service methods use Project Reactor:
```kotlin
fun createTask(request: TaskRequest): Mono<TaskResponse>
fun getTaskById(id: Long): Mono<TaskResponse>
fun getTasks(page: Int, size: Int, status: TaskStatus?): Mono<PageResponse<TaskResponse>>
```

### Native SQL with JdbcClient
```kotlin
fun findAll(page: Int, size: Int, status: TaskStatus?): List<Task> {
    val sql = """
        SELECT * FROM tasks 
        WHERE status = :status 
        ORDER BY created_at DESC 
        LIMIT :size OFFSET :offset
    """
    return jdbcClient.sql(sql)
        .param("status", status?.name)
        .param("size", size)
        .param("offset", page * size)
        .query { rs, _ -> mapRowToTask(rs) }
        .list()
}
```

### Reactive Error Handling
```kotlin
fun getTaskById(id: Long): Mono<TaskResponse> {
    return Mono.fromCallable {
        taskRepository.findById(id)
            ?: throw TaskNotFoundException("Task not found with id: $id")
    }
    .map { mapToResponse(it) }
    .subscribeOn(Schedulers.boundedElastic())
}
```

## 📈 Performance Considerations

- **Reactive Streams**: Non-blocking I/O with Reactor
- **Connection Pooling**: HikariCP for database connections
- **Pagination**: Efficient LIMIT/OFFSET queries
- **Bounded Elastic Scheduler**: For blocking database operations

## 🔄 Future Enhancements

- Add Redis caching for frequently accessed tasks
- Implement task assignment to users
- Add task priorities and due dates
- Implement audit logging
- Add metrics and monitoring
- Database migrations with Flyway

---

**Author**: Task Management Service  
**License**: MIT  
**Version**: 1.0.0