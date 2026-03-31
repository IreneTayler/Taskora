package com.taskmanagement.service

import com.taskmanagement.dto.TaskRequest
import com.taskmanagement.exception.TaskNotFoundException
import com.taskmanagement.model.Task
import com.taskmanagement.model.TaskStatus
import com.taskmanagement.repository.TaskRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.test.StepVerifier
import java.time.LocalDateTime

class TaskServiceTest {

    private val taskRepository = mockk<TaskRepository>()
    private val taskService = TaskService(taskRepository)

    @Test
    fun `createTask should return TaskResponse when successful`() {
        // Given
        val request = TaskRequest("Test Task", "Test Description")
        val savedTask = Task(
            id = 1L,
            title = "Test Task",
            description = "Test Description",
            status = TaskStatus.NEW,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { taskRepository.save(any()) } returns savedTask

        // When & Then
        StepVerifier.create(taskService.createTask(request))
            .expectNextMatches { response ->
                response.id == 1L &&
                response.title == "Test Task" &&
                response.description == "Test Description" &&
                response.status == TaskStatus.NEW
            }
            .verifyComplete()

        verify { taskRepository.save(any()) }
    }

    @Test
    fun `getTaskById should return TaskResponse when task exists`() {
        // Given
        val taskId = 1L
        val task = Task(
            id = taskId,
            title = "Test Task",
            description = "Test Description",
            status = TaskStatus.NEW,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { taskRepository.findById(taskId) } returns task

        // When & Then
        StepVerifier.create(taskService.getTaskById(taskId))
            .expectNextMatches { response ->
                response.id == taskId &&
                response.title == "Test Task"
            }
            .verifyComplete()

        verify { taskRepository.findById(taskId) }
    }

    @Test
    fun `getTaskById should throw TaskNotFoundException when task does not exist`() {
        // Given
        val taskId = 999L
        every { taskRepository.findById(taskId) } returns null

        // When & Then
        StepVerifier.create(taskService.getTaskById(taskId))
            .expectError(TaskNotFoundException::class.java)
            .verify()

        verify { taskRepository.findById(taskId) }
    }

    @Test
    fun `updateTaskStatus should return updated TaskResponse when successful`() {
        // Given
        val taskId = 1L
        val newStatus = TaskStatus.DONE
        val updatedTask = Task(
            id = taskId,
            title = "Test Task",
            description = "Test Description",
            status = newStatus,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { taskRepository.updateStatus(taskId, newStatus) } returns true
        every { taskRepository.findById(taskId) } returns updatedTask

        // When & Then
        StepVerifier.create(taskService.updateTaskStatus(taskId, newStatus))
            .expectNextMatches { response ->
                response.id == taskId &&
                response.status == newStatus
            }
            .verifyComplete()

        verify { taskRepository.updateStatus(taskId, newStatus) }
        verify { taskRepository.findById(taskId) }
    }

    @Test
    fun `updateTaskStatus should throw TaskNotFoundException when task does not exist`() {
        // Given
        val taskId = 999L
        val newStatus = TaskStatus.DONE

        every { taskRepository.updateStatus(taskId, newStatus) } returns false

        // When & Then
        StepVerifier.create(taskService.updateTaskStatus(taskId, newStatus))
            .expectError(TaskNotFoundException::class.java)
            .verify()

        verify { taskRepository.updateStatus(taskId, newStatus) }
    }

    @Test
    fun `deleteTask should complete when task exists`() {
        // Given
        val taskId = 1L
        every { taskRepository.deleteById(taskId) } returns true

        // When & Then
        StepVerifier.create(taskService.deleteTask(taskId))
            .verifyComplete()

        verify { taskRepository.deleteById(taskId) }
    }

    @Test
    fun `deleteTask should throw TaskNotFoundException when task does not exist`() {
        // Given
        val taskId = 999L
        every { taskRepository.deleteById(taskId) } returns false

        // When & Then
        StepVerifier.create(taskService.deleteTask(taskId))
            .expectError(TaskNotFoundException::class.java)
            .verify()

        verify { taskRepository.deleteById(taskId) }
    }

    @Test
    fun `getTasks should return PageResponse with filtered results`() {
        // Given
        val page = 0
        val size = 10
        val status = TaskStatus.NEW
        val tasks = listOf(
            Task(
                id = 1L,
                title = "Task 1",
                description = "Description 1",
                status = TaskStatus.NEW,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )

        every { taskRepository.findAll(page, size, status) } returns tasks
        every { taskRepository.count(status) } returns 1L

        // When & Then
        StepVerifier.create(taskService.getTasks(page, size, status))
            .expectNextMatches { response ->
                response.content.size == 1 &&
                response.page == page &&
                response.size == size &&
                response.totalElements == 1L &&
                response.totalPages == 1
            }
            .verifyComplete()

        verify { taskRepository.findAll(page, size, status) }
        verify { taskRepository.count(status) }
    }
}