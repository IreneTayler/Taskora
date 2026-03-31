package com.taskmanagement.dto

import com.taskmanagement.model.TaskStatus
import jakarta.validation.constraints.NotNull

data class UpdateStatusRequest(
    @field:NotNull(message = "Status cannot be null")
    val status: TaskStatus
)