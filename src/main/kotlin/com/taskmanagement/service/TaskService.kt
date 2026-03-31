package com.taskmanagement.service

import com.taskmanagement.dto.PageResponse
import com.taskmanagement.dto.TaskRequest
import com.taskmanagement.dto.TaskResponse
import com.taskmanagement.exception.TaskNotFoundException
import com.taskmanagement.model.Task
import com.taskmanagement.model.TaskStatus
import com.taskmanagement.repository.TaskRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class TaskService(private val repository: TaskRepository) {

    fun createTask(request: TaskRequest): Mono<TaskResponse> {
        return Mono.fromCallable {
            val newTask = Task(title = request.title, description = request.description)
            repository.save(newTask).toResponse()
        }.subscribeOn(Schedulers.boundedElastic())
    }

    fun getTaskById(id: Long): Mono<TaskResponse> {
        return Mono.fromCallable {
            repository.findById(id)?.toResponse() 
                ?: throw TaskNotFoundException("Task with id $id not found")
        }.subscribeOn(Schedulers.boundedElastic())
    }

    fun getTasks(page: Int, size: Int, status: TaskStatus?): Mono<PageResponse<TaskResponse>> {
        return Mono.fromCallable {
            val tasks = repository.findAll(page, size, status)
            val total = repository.count(status)
            
            PageResponse(
                content = tasks.map { it.toResponse() },
                page = page,
                size = size,
                totalElements = total,
                totalPages = (total + size - 1).toInt() / size
            )
        }.subscribeOn(Schedulers.boundedElastic())
    }

    fun updateTaskStatus(id: Long, newStatus: TaskStatus): Mono<TaskResponse> {
        return Mono.fromCallable {
            if (!repository.updateStatus(id, newStatus)) {
                throw TaskNotFoundException("Task with id $id not found")
            }
            repository.findById(id)!!.toResponse()
        }.subscribeOn(Schedulers.boundedElastic())
    }

    fun deleteTask(id: Long): Mono<Void> {
        return Mono.fromCallable {
            if (!repository.deleteById(id)) {
                throw TaskNotFoundException("Task with id $id not found")
            }
        }.then().subscribeOn(Schedulers.boundedElastic())
    }

    private fun Task.toResponse() = TaskResponse(
        id = id!!,
        title = title,
        description = description,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}