package com.taskmanagement.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WelcomeController {

    @GetMapping("/")
    fun welcome(): Map<String, Any> {
        return mapOf(
            "service" to "Task Management API",
            "version" to "1.0.0",
            "status" to "Running",
            "endpoints" to mapOf(
                "tasks" to "/api/tasks",
                "h2-console" to "/h2-console"
            ),
            "sample_requests" to listOf(
                "GET /api/tasks?page=0&size=10",
                "POST /api/tasks",
                "GET /api/tasks/{id}",
                "PATCH /api/tasks/{id}/status",
                "DELETE /api/tasks/{id}"
            )
        )
    }
}