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
import reactor.test.StepVerifier
import java.time.LocalDateTime

class TaskServiceTest {

    private val repository = mockk<TaskRepository>()
    private val service = TaskService(repository)

    @Test
    fun `should create task successfully`() {
        val request = TaskRequest("Buy groceries", "Milk, bread, eggs")
        val saved = Task(
            id = 1L,
            title = "Buy groceries",
            description = "Milk, bread, eggs",
            status = TaskStatus.NEW,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { repository.save(any()) } returns saved

        StepVerifier.create(service.createTask(request))
            .expectNextMatches { 
                it.id == 1L && it.title == "Buy groceries" && it.status == TaskStatus.NEW
            }
            .verifyComplete()

        verify { repository.save(any()) }
    }

    @Test
    fun `should return task when found`() {
        val task = Task(
            id = 1L,
            title = "Review PR",
            description = "Check the new feature implementation",
            status = TaskStatus.IN_PROGRESS,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { repository.findById(1L) } returns task

        StepVerifier.create(service.getTaskById(1L))
            .expectNextMatches { it.id == 1L && it.title == "Review PR" }
            .verifyComplete()
    }

    @Test
    fun `should throw exception when task not found`() {
        every { repository.findById(999L) } returns null

        StepVerifier.create(service.getTaskById(999L))
            .expectError(TaskNotFoundException::class.java)
            .verify()
    }

    @Test
    fun `should update task status`() {
        val updated = Task(
            id = 1L,
            title = "Deploy app",
            description = "Production deployment",
            status = TaskStatus.DONE,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { repository.updateStatus(1L, TaskStatus.DONE) } returns true
        every { repository.findById(1L) } returns updated

        StepVerifier.create(service.updateTaskStatus(1L, TaskStatus.DONE))
            .expectNextMatches { it.status == TaskStatus.DONE }
            .verifyComplete()
    }

    @Test
    fun `should fail to update non-existent task`() {
        every { repository.updateStatus(999L, TaskStatus.DONE) } returns false

        StepVerifier.create(service.updateTaskStatus(999L, TaskStatus.DONE))
            .expectError(TaskNotFoundException::class.java)
            .verify()
    }

    @Test
    fun `should delete task successfully`() {
        every { repository.deleteById(1L) } returns true

        StepVerifier.create(service.deleteTask(1L))
            .verifyComplete()
    }

    @Test
    fun `should fail to delete non-existent task`() {
        every { repository.deleteById(999L) } returns false

        StepVerifier.create(service.deleteTask(999L))
            .expectError(TaskNotFoundException::class.java)
            .verify()
    }

    @Test
    fun `should return paginated tasks`() {
        val tasks = listOf(
            Task(1L, "Task 1", "Desc 1", TaskStatus.NEW, LocalDateTime.now(), LocalDateTime.now()),
            Task(2L, "Task 2", "Desc 2", TaskStatus.NEW, LocalDateTime.now(), LocalDateTime.now())
        )

        every { repository.findAll(0, 10, TaskStatus.NEW) } returns tasks
        every { repository.count(TaskStatus.NEW) } returns 2L

        StepVerifier.create(service.getTasks(0, 10, TaskStatus.NEW))
            .expectNextMatches { 
                it.content.size == 2 && it.totalElements == 2L && it.totalPages == 1
            }
            .verifyComplete()
    }
}