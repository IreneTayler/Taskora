package com.taskmanagement.controller

import com.ninjasquad.springmockk.MockkBean
import com.taskmanagement.dto.PageResponse
import com.taskmanagement.dto.TaskRequest
import com.taskmanagement.dto.TaskResponse
import com.taskmanagement.dto.UpdateStatusRequest
import com.taskmanagement.exception.TaskNotFoundException
import com.taskmanagement.model.TaskStatus
import com.taskmanagement.service.TaskService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@WebFluxTest(TaskController::class)
class TaskControllerTest {

    @Autowired
    private lateinit var client: WebTestClient

    @MockkBean
    private lateinit var service: TaskService

    @Test
    fun `should create task and return 201`() {
        val request = TaskRequest("Fix login bug", "Users can't login with special chars")
        val response = TaskResponse(
            id = 1L,
            title = "Fix login bug",
            description = "Users can't login with special chars",
            status = TaskStatus.NEW,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { service.createTask(request) } returns Mono.just(response)

        client.post()
            .uri("/api/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.title").isEqualTo("Fix login bug")
            .jsonPath("$.status").isEqualTo("NEW")
    }

    @Test
    fun `should return task by id`() {
        val response = TaskResponse(
            id = 1L,
            title = "Deploy to staging",
            description = "Deploy version 2.1.0",
            status = TaskStatus.IN_PROGRESS,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { service.getTaskById(1L) } returns Mono.just(response)

        client.get()
            .uri("/api/tasks/1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.title").isEqualTo("Deploy to staging")
    }

    @Test
    fun `should return 404 for missing task`() {
        every { service.getTaskById(999L) } returns Mono.error(TaskNotFoundException("Not found"))

        client.get()
            .uri("/api/tasks/999")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `should return paginated tasks`() {
        val task = TaskResponse(
            id = 1L,
            title = "Write tests",
            description = "Add unit tests for service layer",
            status = TaskStatus.NEW,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val page = PageResponse(
            content = listOf(task),
            page = 0,
            size = 10,
            totalElements = 1L,
            totalPages = 1
        )

        every { service.getTasks(0, 10, TaskStatus.NEW) } returns Mono.just(page)

        client.get()
            .uri("/api/tasks?page=0&size=10&status=NEW")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.content").isArray
            .jsonPath("$.content[0].title").isEqualTo("Write tests")
            .jsonPath("$.totalElements").isEqualTo(1)
    }

    @Test
    fun `should update task status`() {
        val request = UpdateStatusRequest(TaskStatus.DONE)
        val response = TaskResponse(
            id = 1L,
            title = "Code review",
            description = "Review PR #42",
            status = TaskStatus.DONE,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { service.updateTaskStatus(1L, TaskStatus.DONE) } returns Mono.just(response)

        client.patch()
            .uri("/api/tasks/1/status")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.status").isEqualTo("DONE")
    }

    @Test
    fun `should return 404 when updating missing task`() {
        val request = UpdateStatusRequest(TaskStatus.DONE)

        every { service.updateTaskStatus(999L, TaskStatus.DONE) } returns 
            Mono.error(TaskNotFoundException("Not found"))

        client.patch()
            .uri("/api/tasks/999/status")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `should delete task and return 204`() {
        every { service.deleteTask(1L) } returns Mono.empty()

        client.delete()
            .uri("/api/tasks/1")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `should return 404 when deleting missing task`() {
        every { service.deleteTask(999L) } returns Mono.error(TaskNotFoundException("Not found"))

        client.delete()
            .uri("/api/tasks/999")
            .exchange()
            .expectStatus().isNotFound
    }
}