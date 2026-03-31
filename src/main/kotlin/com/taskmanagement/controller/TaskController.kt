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
class TaskController(private val service: TaskService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: TaskRequest): Mono<TaskResponse> {
        return service.createTask(request)
    }

    @GetMapping
    fun list(
        @RequestParam page: Int,
        @RequestParam size: Int,
        @RequestParam(required = false) status: TaskStatus?
    ): Mono<PageResponse<TaskResponse>> {
        return service.getTasks(page, size, status)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): Mono<TaskResponse> {
        return service.getTaskById(id)
    }

    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateStatusRequest
    ): Mono<TaskResponse> {
        return service.updateTaskStatus(id, request.status)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long): Mono<Void> {
        return service.deleteTask(id)
    }
}