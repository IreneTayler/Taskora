package com.example.demo.mapper

import com.example.demo.dto.TaskRequest
import com.example.demo.dto.TaskResponse
import com.example.demo.model.Task
import com.example.demo.model.TaskStatus

object TaskMapper {

    fun toEntity(dto: TaskRequest): Task {
        return Task(
            id = null,
            title = dto.title,
            description = dto.description,
            status = TaskStatus.TODO
        )
    }

    fun toResponse(task: Task): TaskResponse {
        return TaskResponse(
            id = task.id ?: 0L,
            title = task.title,
            description = task.description,
            status = task.status,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt
        )
    }
}