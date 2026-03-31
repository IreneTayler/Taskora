# Postman Testing Guide for Task Management API

## 🚀 Quick Start

### 1. Import Collection
- Open Postman
- Click "Import" button
- Select `Task-Management-API.postman_collection.json`
- Collection will be imported with all test scenarios

### 2. Start the Application
```bash
./gradlew bootRun
```
Wait for: `Started TaskManagementApplicationKt in X.XXX seconds`

### 3. Run Tests

#### Option A: Run Entire Collection
1. Right-click on "Task Management API - Complete Test Suite"
2. Select "Run collection"
3. Click "Run Task Management API"
4. Watch all tests execute automatically

#### Option B: Run Individual Tests
1. Expand collection folders
2. Click on any request
3. Click "Send" button
4. View response and test results

## 📋 Test Scenarios Included

### ✅ Health Check
- **Welcome Endpoint**: `GET /` - Verify API is running

### ✅ Core CRUD Operations
- **Create Task**: `POST /api/tasks` - Create new task
- **Get All Tasks**: `GET /api/tasks?page=0&size=10` - List with pagination
- **Get Task by ID**: `GET /api/tasks/{id}` - Retrieve specific task
- **Update Task Status**: `PATCH /api/tasks/{id}/status` - Update task status only
- **Delete Task**: `DELETE /api/tasks/{id}` - Remove task

### ✅ Error Handling
- **404 Not Found**: Get non-existent task
- **400 Bad Request**: Create task with invalid data

### ✅ Bulk Operations
- **Multiple Tasks**: Create several tasks for testing pagination

## 🔧 Environment Variables

The collection uses these variables (auto-configured):
- `baseUrl`: http://localhost:8080
- `taskId`: Automatically set after creating a task

## 📊 Expected Results

### Successful Responses
- **Create Task**: `201 Created` with task data
- **Get Tasks**: `200 OK` with paginated results
- **Update Task**: `200 OK` with updated data
- **Delete Task**: `204 No Content`

### Error Responses
- **Not Found**: `404` with error message
- **Bad Request**: `400` with validation errors

## 🧪 Test Automation

Each request includes automated tests that verify:
- Correct HTTP status codes
- Response structure and required fields
- Data integrity after operations

## 🔍 Troubleshooting

### Port Already in Use
```bash
netstat -ano | findstr :8080
taskkill /PID [PID_NUMBER] /F
```

### Application Not Starting
```bash
./gradlew clean build
./gradlew bootRun
```

### Tests Failing
1. Ensure application is running on port 8080
2. Check console for any startup errors
3. Verify JSON format in request bodies
4. Check that Content-Type header is set to `application/json`

## 📝 Manual Testing Examples

### Create a Task
```http
POST http://localhost:8080/api/tasks
Content-Type: application/json

{
  "title": "My Test Task",
  "description": "Testing the API manually"
}
```

### Get All Tasks
```http
GET http://localhost:8080/api/tasks?page=0&size=10
```

### Update Task Status
```http
PATCH http://localhost:8080/api/tasks/1/status
Content-Type: application/json

{
  "status": "DONE"
}
```

## 🎯 Success Indicators

✅ All tests pass with green checkmarks  
✅ Response times under 1000ms  
✅ Proper JSON structure in all responses  
✅ Correct HTTP status codes  
✅ Task IDs are properly generated and used  

Happy Testing! 🚀