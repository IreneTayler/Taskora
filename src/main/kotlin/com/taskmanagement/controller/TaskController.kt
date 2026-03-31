package com.taskmanagement.controller

import com.taskmanagement.dto.PageResponse
import com.taskmanagement.dto.TaskRequest
import com.taskmanagement.dto.TaskResponse
import com.taskmanagement.dto.UpdateStatusRequest
import com.taskmanagement.model.TaskStatus
import com.taskmanagement.service.TaskService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val taskService: TaskService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTask(@Valid @RequestBody request: TaskRequest): Mono<TaskResponse> {
        return taskService.createTask(request)
    }

    @GetMapping
    fun getTasks(
        @RequestParam page: Int,
        @RequestParam size: Int,
        @RequestParam(required = false) status: TaskStatus?
    ): Mono<PageResponse<TaskResponse>> {
        return taskService.getTasks(page, size, status)
    }

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Long): Mono<TaskResponse> {
        return taskService.getTaskById(id)
    }

    @PatchMapping("/{id}/status")
    fun updateTaskStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateStatusRequest
    ): Mono<TaskResponse> {
        return taskService.updateTaskStatus(id, request.status)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTask(@PathVariable id: Long): Mono<Void> {
        return taskService.deleteTask(id)
    }
}