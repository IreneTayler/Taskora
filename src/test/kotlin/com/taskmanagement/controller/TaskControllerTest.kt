package com.taskmanagement.controller

import com.fasterxml.jackson.databind.ObjectMapper
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@WebMvcTest(TaskController::class)
class TaskControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var taskService: TaskService

    @Test
    fun `createTask should return 201 and TaskResponse when valid request`() {
        // Given
        val request = TaskRequest("Test Task", "Test Description")
        val response = TaskResponse(
            id = 1L,
            title = "Test Task",
            description = "Test Description",
            status = TaskStatus.NEW,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { taskService.createTask(request) } returns Mono.just(response)

        // When & Then
        mockMvc.perform(
            post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Test Task"))
            .andExpect(jsonPath("$.status").value("NEW"))
    }

    @Test
    fun `createTask should return 400 when title is empty`() {
        // Given
        val request = TaskRequest("", "Test Description")

        // When & Then
        mockMvc.perform(
            post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `createTask should return 400 when title is too short`() {
        // Given
        val request = TaskRequest("ab", "Test Description")

        // When & Then
        mockMvc.perform(
            post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `getTaskById should return 200 and TaskResponse when task exists`() {
        // Given
        val taskId = 1L
        val response = TaskResponse(
            id = taskId,
            title = "Test Task",
            description = "Test Description",
            status = TaskStatus.NEW,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { taskService.getTaskById(taskId) } returns Mono.just(response)

        // When & Then
        mockMvc.perform(get("/api/tasks/{id}", taskId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(taskId))
            .andExpect(jsonPath("$.title").value("Test Task"))
    }

    @Test
    fun `getTaskById should return 404 when task does not exist`() {
        // Given
        val taskId = 999L
        every { taskService.getTaskById(taskId) } returns Mono.error(TaskNotFoundException("Task not found"))

        // When & Then
        mockMvc.perform(get("/api/tasks/{id}", taskId))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `getTasks should return 200 and PageResponse`() {
        // Given
        val page = 0
        val size = 10
        val status = TaskStatus.NEW
        val taskResponse = TaskResponse(
            id = 1L,
            title = "Test Task",
            description = "Test Description",
            status = TaskStatus.NEW,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val pageResponse = PageResponse(
            content = listOf(taskResponse),
            page = page,
            size = size,
            totalElements = 1L,
            totalPages = 1
        )

        every { taskService.getTasks(page, size, status) } returns Mono.just(pageResponse)

        // When & Then
        mockMvc.perform(
            get("/api/tasks")
                .param("page", page.toString())
                .param("size", size.toString())
                .param("status", status.name)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content").isArray)
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.page").value(page))
            .andExpect(jsonPath("$.size").value(size))
            .andExpect(jsonPath("$.totalElements").value(1))
    }

    @Test
    fun `updateTaskStatus should return 200 and updated TaskResponse`() {
        // Given
        val taskId = 1L
        val request = UpdateStatusRequest(TaskStatus.DONE)
        val response = TaskResponse(
            id = taskId,
            title = "Test Task",
            description = "Test Description",
            status = TaskStatus.DONE,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { taskService.updateTaskStatus(taskId, TaskStatus.DONE) } returns Mono.just(response)

        // When & Then
        mockMvc.perform(
            patch("/api/tasks/{id}/status", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(taskId))
            .andExpect(jsonPath("$.status").value("DONE"))
    }

    @Test
    fun `updateTaskStatus should return 404 when task does not exist`() {
        // Given
        val taskId = 999L
        val request = UpdateStatusRequest(TaskStatus.DONE)

        every { taskService.updateTaskStatus(taskId, TaskStatus.DONE) } returns 
            Mono.error(TaskNotFoundException("Task not found"))

        // When & Then
        mockMvc.perform(
            patch("/api/tasks/{id}/status", taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteTask should return 204 when successful`() {
        // Given
        val taskId = 1L
        every { taskService.deleteTask(taskId) } returns Mono.empty()

        // When & Then
        mockMvc.perform(delete("/api/tasks/{id}", taskId))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteTask should return 404 when task does not exist`() {
        // Given
        val taskId = 999L
        every { taskService.deleteTask(taskId) } returns Mono.error(TaskNotFoundException("Task not found"))

        // When & Then
        mockMvc.perform(delete("/api/tasks/{id}", taskId))
            .andExpect(status().isNotFound)
    }
}