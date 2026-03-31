# 📝 Task Management REST API

A production-style RESTful API built with **Spring Boot + Kotlin** for managing tasks.  
This project demonstrates clean architecture, CRUD operations, pagination, DTO mapping, and global exception handling.

---

# 🚀 Features

- Create task
- Get task by ID
- Get all tasks (pagination + optional status filter)
- Update task status
- Delete task
- DTO-based request/response structure
- Global exception handling (404, 400, 500)
- Clean layered architecture

---

# 🛠 Tech Stack

- Kotlin
- Spring Boot
- Spring Web
- Spring JDBC / JPA (depending on setup)
- H2 Database (default) or PostgreSQL
- Gradle

---

# 📁 Project Structure

com.example.demo
│
├── controller # REST controllers
├── service # Business logic layer
├── repository # Database access layer
├── model # Entity classes
├── dto # Request / Response DTOs
├── mapper # Entity ↔ DTO conversion
├── exception # Global exception handling
├── common # API response wrapper (optional)

---

# ⚙️ Setup & Run


```bash
1. Clone repository
git clone <your-repo-url>
cd demo 


2. Build project
./gradlew clean build


3. Run application
./gradlew bootRun

Server runs at:

http://localhost:8080

📌 API ENDPOINTS

📍 Create Task
Request
POST /api/tasks
{
  "title": "My Task",
  "description": "Task description"
}
Response
{
  "success": true,
  "data": {
    "id": 1,
    "title": "My Task",
    "description": "Task description",
    "status": "TODO",
    "createdAt": "2026-03-28T20:00:00",
    "updatedAt": "2026-03-28T20:00:00"
  },
  "error": null
}
📍 Get Task by ID
Request
GET /api/tasks/{id}
Response (success)
{
  "success": true,
  "data": {
    "id": 1,
    "title": "My Task",
    "description": "Task description",
    "status": "TODO",
    "createdAt": "2026-03-28T20:00:00",
    "updatedAt": "2026-03-28T20:00:00"
  },
  "error": null
}
Response (not found)
{
  "error": "Task not found with id: 999"
}
📍 Get All Tasks
Request
GET /api/tasks?page=0&size=10&status=NEW
Response
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "Task 1",
      "description": "Example",
      "status": "TODO"
    }
  ],
  "error": null
}
📍 Update Task Status
Request
PUT /api/tasks/{id}/status?status=DONE
📍 Delete Task
Request
DELETE /api/tasks/{id}
❗ Error Handling

Global exception handling is implemented.

Standard Errors
Status	Meaning
400	Bad Request
404	Not Found
500	Internal Server Error
Example Error Response
{
  "error": "Task not found with id: 1"
}
🧠 Architecture
Controller → Service → Repository → Database
      ↓
Global Exception Handler
      ↓
Standard API Response
🔥 Key Design Points
DTO separates API layer from DB layer
Mapper handles conversion logic
Service contains business logic only
Controller handles request/response only
Global exception handler ensures consistent errors
📦 Build & Run
./gradlew clean build
./gradlew bootRun
🧪 Testing Tools
Postman
IntelliJ HTTP Client
Curl
🚀 Future Improvements
Add Swagger/OpenAPI documentation
Add JWT authentication (Spring Security)
Add Flyway/Liquibase migrations
Add unit/integration tests
Standardize API response wrapper fully
👨‍💻 Author

Spring Boot + Kotlin REST API project for learning and backend practice.

📄 License

For educational use only.


---

If you want next upgrade, I can help you build:

👉 Swagger UI (auto API docs)  
👉 JWT login system  
👉 real production folder architecture  
👉 Docker deployment  

Just say **“next level”** 🚀