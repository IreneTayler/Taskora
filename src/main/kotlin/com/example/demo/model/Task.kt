package com.example.demo.model

import java.time.LocalDateTime

data class Task(
    val id: Long? = null,
    val title: String,
    val description: String,
    val status: TaskStatus = TaskStatus.TODO,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)