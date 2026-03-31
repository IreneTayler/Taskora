#!/bin/bash

echo "🚀 Testing Task Management API..."
echo "Make sure the application is running on localhost:8080"
echo ""

# Test 1: Create a task
echo "1. Creating a new task..."
TASK_ID=$(curl -s -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"API Test Task","description":"Testing via script"}' \
  | grep -o '"id":[0-9]*' | grep -o '[0-9]*')

if [ ! -z "$TASK_ID" ]; then
  echo "✅ Task created with ID: $TASK_ID"
else
  echo "❌ Failed to create task"
  exit 1
fi

# Test 2: Get the task
echo ""
echo "2. Getting task by ID..."
curl -s http://localhost:8080/api/tasks/$TASK_ID | jq '.'

# Test 3: Update status
echo ""
echo "3. Updating task status..."
curl -s -X PATCH http://localhost:8080/api/tasks/$TASK_ID/status \
  -H "Content-Type: application/json" \
  -d '{"status":"DONE"}' | jq '.'

# Test 4: Get all tasks
echo ""
echo "4. Getting all tasks..."
curl -s "http://localhost:8080/api/tasks?page=0&size=5" | jq '.content | length'

# Test 5: Delete task
echo ""
echo "5. Deleting task..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE http://localhost:8080/api/tasks/$TASK_ID)

if [ "$HTTP_CODE" = "204" ]; then
  echo "✅ Task deleted successfully"
else
  echo "❌ Failed to delete task (HTTP $HTTP_CODE)"
fi

echo ""
echo "🎉 API testing completed!"