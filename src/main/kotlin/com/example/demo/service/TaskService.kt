package com.example.demo.service

import com.example.demo.model.Task
import com.example.demo.model.TaskStatus
import com.example.demo.repository.TaskRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TaskService(private val repository: TaskRepository) {

    fun create(task: Task): Task {
        return repository.save(task)
    }

    fun getById(id: Long): Task? {
        return repository.findById(id)
    }

    fun getAll(page: Int, size: Int, status: TaskStatus?): List<Task> {
        return repository.findAll(page, size, status)
    }

    fun count(status: TaskStatus?): Long {
        return repository.count(status)
    }

    fun updateStatus(id: Long, status: TaskStatus): Int {
        return repository.updateStatus(id, status, LocalDateTime.now())
    }

    fun deleteById(id: Long): Int {
        return repository.deleteById(id)
    }
}