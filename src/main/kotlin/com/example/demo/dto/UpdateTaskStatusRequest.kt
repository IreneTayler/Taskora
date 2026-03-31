package com.example.demo.dto

import com.example.demo.model.TaskStatus
import jakarta.validation.constraints.NotNull

data class UpdateTaskStatusRequest(
    @field:NotNull(message = "Status must not be null")
    val status: TaskStatus
)
