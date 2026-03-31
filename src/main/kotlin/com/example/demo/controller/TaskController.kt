package com.example.demo.controller

import com.example.demo.dto.TaskRequest
import com.example.demo.dto.TaskResponse
import com.example.demo.mapper.TaskMapper
import com.example.demo.model.TaskStatus
import com.example.demo.service.TaskService
import org.springframework.web.bind.annotation.*
import com.example.demo.exception.TaskNotFoundException


@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val taskService: TaskService
) {

    // =========================
    // CREATE TASK
    // =========================
    @PostMapping
    fun create(@RequestBody request: TaskRequest): TaskResponse {
        val task = TaskMapper.toEntity(request)
        val saved = taskService.create(task)
        return TaskMapper.toResponse(saved)
    }

    // =========================
    // GET ALL TASKS (pagination + optional filter)
    // =========================
    @GetMapping
    fun getAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) status: TaskStatus?
    ): List<TaskResponse> {

        val tasks = taskService.getAll(page, size, status)
        return tasks.map { TaskMapper.toResponse(it) }
    }

    // =========================
    // GET TASK BY ID
    // =========================
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): TaskResponse {
        val task = taskService.getById(id)
            ?: throw TaskNotFoundException("Task not found with id: $id")

        return TaskMapper.toResponse(task)
    }


    // =========================
    // UPDATE STATUS ONLY
    // =========================
    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestParam status: TaskStatus
    ): String {

        val updatedRows = taskService.updateStatus(id, status)

        if (updatedRows == 0) {
            throw RuntimeException("Task not found with id: $id")
        }

        return "Status updated successfully"
    }

    // =========================
    // DELETE TASK
    // =========================
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): String {

        val deletedRows = taskService.deleteById(id)

        if (deletedRows == 0) {
            throw RuntimeException("Task not found with id: $id")
        }

        return "Task deleted successfully"
    }
}