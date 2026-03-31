# 🔄 Workflow Orchestrator API

A hexagonal architecture-based RESTful service built with **Spring Boot + Kotlin** for orchestrating workflow items.  
This project demonstrates ports-adapters pattern, domain-driven design, pagination, contract mapping, and centralized error handling.

---

# ⚡ Capabilities

- Initiate workflow item
- Locate workflow item by identifier
- Enumerate all workflow items (pagination + optional phase filter)
- Transition workflow item phase
- Eliminate workflow item
- Contract-based request/response structure
- Centralized exception handling (404, 400, 500)
- Hexagonal architecture implementation

---

# 🛠 Technology Stack

- Kotlin
- Spring Boot
- Spring Web
- Spring JDBC
- H2 Database (in-memory) or PostgreSQL
- Gradle

---

# 📁 Architecture Structure

io.nexus.orchestrator
│
├── domain
│   ├── entities # Core business entities
│   ├── ports # Repository interfaces
│   └── services # Business logic layer
├── application
│   ├── contracts # Request/Response contracts
│   └── transformers # Entity ↔ Contract conversion
└── infrastructure
    ├── web # REST endpoints
    ├── persistence # Database adapters
    └── exceptions # Error handling

---

# ⚙️ Setup & Execution

```bash
1. Clone repository
git clone <your-repo-url>
cd workflow-orchestrator

2. Build project
./gradlew clean build

3. Execute application
./gradlew bootRun
```

Service runs at: http://localhost:8080

---

# 📌 API ENDPOINTS

## 📍 Initiate Workflow Item
**Request**
```
POST /orchestrator/workflow-items
{
  "headline": "My Workflow Item",
  "narrative": "Item narrative"
}
```

**Response**
```json
{
  "identifier": 1,
  "headline": "My Workflow Item",
  "narrative": "Item narrative",
  "phase": "PENDING",
  "initiatedAt": "2026-03-31T20:00:00Z",
  "modifiedAt": "2026-03-31T20:00:00Z"
}
```

## 📍 Locate Workflow Item by Identifier
**Request**
```
GET /orchestrator/workflow-items/{identifier}
```

**Response (success)**
```json
{
  "identifier": 1,
  "headline": "My Workflow Item",
  "narrative": "Item narrative",
  "phase": "PENDING",
  "initiatedAt": "2026-03-31T20:00:00Z",
  "modifiedAt": "2026-03-31T20:00:00Z"
}
```

**Response (not found)**
```json
{
  "error": "Work item not found with identifier: 999"
}
```

## 📍 Enumerate All Workflow Items
**Request**
```
GET /orchestrator/workflow-items?pageIndex=0&pageCapacity=10&phase=ACTIVE
```

**Response**
```json
[
  {
    "identifier": 1,
    "headline": "Item 1",
    "narrative": "Example",
    "phase": "ACTIVE"
  }
]
```

## 📍 Transition Workflow Item Phase
**Request**
```
PATCH /orchestrator/workflow-items/{identifier}/phase?phase=COMPLETED
```

## 📍 Eliminate Workflow Item
**Request**
```
DELETE /orchestrator/workflow-items/{identifier}
```

---

# ❗ Error Handling

Centralized exception handling is implemented.

**Standard Errors**
| Status | Meaning |
|--------|---------|
| 400 | Bad Request |
| 404 | Not Found |
| 500 | Internal Server Error |

**Example Error Response**
```json
{
  "error": "Work item not found with identifier: 1"
}
```

---

# 🏗 Architecture Pattern

```
Domain Entities ← → Domain Services ← → Repository Ports
                                            ↓
Infrastructure Web ← → Application Contracts ← → Infrastructure Persistence
                                            ↓
                                    System Exception Handler
```

---

# 🔥 Key Design Principles

- Hexagonal architecture separates core domain from infrastructure
- Contracts isolate API layer from domain layer
- Transformers handle conversion logic
- Domain services contain pure business logic
- Web endpoints handle HTTP concerns only
- Centralized exception handler ensures consistent errors

---

# 📦 Build & Execute

```bash
./gradlew clean build
./gradlew bootRun
```

---

# 🧪 Testing Tools

- Postman
- IntelliJ HTTP Client
- Curl

---

# 🚀 Future Enhancements

- Add OpenAPI/Swagger documentation
- Add Spring Security with JWT
- Add database migrations
- Add comprehensive testing suite
- Implement event sourcing

---

# 👨‍💻 Author

Spring Boot + Kotlin hexagonal architecture project for advanced backend development.

---

# 📄 License

For educational and research purposes only.

---

If you want advanced features, I can help you implement:

👉 Event-driven architecture  
👉 CQRS pattern implementation  
👉 Microservices decomposition  
👉 Kubernetes deployment  

Just say **"advanced patterns"** 🚀