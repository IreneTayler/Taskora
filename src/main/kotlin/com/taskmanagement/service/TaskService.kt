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
class TaskService(private val taskRepository: TaskRepository) {

    fun createTask(request: TaskRequest): Mono<TaskResponse> {
        return Mono.fromCallable {
            val task = Task(
                title = request.title,
                description = request.description
            )
            val savedTask = taskRepository.save(task)
            mapToResponse(savedTask)
        }.subscribeOn(Schedulers.boundedElastic())
    }

    fun getTaskById(id: Long): Mono<TaskResponse> {
        return Mono.fromCallable {
            taskRepository.findById(id)
                ?: throw TaskNotFoundException("Task not found with id: $id")
        }
        .map { mapToResponse(it) }
        .subscribeOn(Schedulers.boundedElastic())
    }

    fun getTasks(page: Int, size: Int, status: TaskStatus?): Mono<PageResponse<TaskResponse>> {
        return Mono.fromCallable {
            val tasks = taskRepository.findAll(page, size, status)
            val totalElements = taskRepository.count(status)
            val totalPages = ((totalElements + size - 1) / size).toInt()

            PageResponse(
                content = tasks.map { mapToResponse(it) },
                page = page,
                size = size,
                totalElements = totalElements,
                totalPages = totalPages
            )
        }.subscribeOn(Schedulers.boundedElastic())
    }

    fun updateTaskStatus(id: Long, status: TaskStatus): Mono<TaskResponse> {
        return Mono.fromCallable {
            val updated = taskRepository.updateStatus(id, status)
            if (!updated) {
                throw TaskNotFoundException("Task not found with id: $id")
            }
            taskRepository.findById(id)!!
        }
        .map { mapToResponse(it) }
        .subscribeOn(Schedulers.boundedElastic())
    }

    fun deleteTask(id: Long): Mono<Void> {
        return Mono.fromCallable {
            val deleted = taskRepository.deleteById(id)
            if (!deleted) {
                throw TaskNotFoundException("Task not found with id: $id")
            }
        }
        .then()
        .subscribeOn(Schedulers.boundedElastic())
    }

    private fun mapToResponse(task: Task): TaskResponse {
        return TaskResponse(
            id = task.id!!,
            title = task.title,
            description = task.description,
            status = task.status,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt
        )
    }
}