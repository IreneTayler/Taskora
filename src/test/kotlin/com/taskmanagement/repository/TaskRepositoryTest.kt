package com.taskmanagement.repository

import com.taskmanagement.model.Task
import com.taskmanagement.model.TaskStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDateTime

@JdbcTest
@Sql("/test-schema.sql")
@Sql(statements = ["DELETE FROM tasks"])
class TaskRepositoryTest {

    @Autowired
    private lateinit var jdbcClient: JdbcClient

    private val taskRepository by lazy { TaskRepository(jdbcClient) }

    @Test
    fun `save should persist task and return with generated id`() {
        // Given
        val task = Task(
            title = "Test Task",
            description = "Test Description",
            status = TaskStatus.NEW
        )

        // When
        val savedTask = taskRepository.save(task)

        // Then
        assertNotNull(savedTask.id)
        assertEquals("Test Task", savedTask.title)
        assertEquals("Test Description", savedTask.description)
        assertEquals(TaskStatus.NEW, savedTask.status)
    }

    @Test
    fun `findById should return task when exists`() {
        // Given
        val task = Task(title = "Test Task", description = "Test Description")
        val savedTask = taskRepository.save(task)

        // When
        val foundTask = taskRepository.findById(savedTask.id!!)

        // Then
        assertNotNull(foundTask)
        assertEquals(savedTask.id, foundTask!!.id)
        assertEquals("Test Task", foundTask.title)
    }

    @Test
    fun `findById should return null when task does not exist`() {
        // When
        val foundTask = taskRepository.findById(999L)

        // Then
        assertNull(foundTask)
    }

    @Test
    fun `findAll should return paginated results`() {
        // Given
        repeat(15) { i ->
            taskRepository.save(Task(title = "Task $i", description = "Description $i"))
        }

        // When
        val tasks = taskRepository.findAll(page = 0, size = 10, status = null)

        // Then
        assertEquals(10, tasks.size)
    }

    @Test
    fun `findAll should filter by status`() {
        // Given
        taskRepository.save(Task(title = "Task 1", status = TaskStatus.NEW))
        taskRepository.save(Task(title = "Task 2", status = TaskStatus.IN_PROGRESS))
        taskRepository.save(Task(title = "Task 3", status = TaskStatus.NEW))

        // When
        val newTasks = taskRepository.findAll(page = 0, size = 10, status = TaskStatus.NEW)

        // Then
        assertEquals(2, newTasks.size)
        assertTrue(newTasks.all { it.status == TaskStatus.NEW })
    }

    @Test
    fun `count should return total count`() {
        // Given
        repeat(5) { i ->
            taskRepository.save(Task(title = "Task $i"))
        }

        // When
        val count = taskRepository.count(null)

        // Then
        assertEquals(5L, count)
    }

    @Test
    fun `count should return filtered count`() {
        // Given
        taskRepository.save(Task(title = "Task 1", status = TaskStatus.NEW))
        taskRepository.save(Task(title = "Task 2", status = TaskStatus.IN_PROGRESS))
        taskRepository.save(Task(title = "Task 3", status = TaskStatus.NEW))

        // When
        val newCount = taskRepository.count(TaskStatus.NEW)

        // Then
        assertEquals(2L, newCount)
    }

    @Test
    fun `updateStatus should update task status and return true when task exists`() {
        // Given
        val task = taskRepository.save(Task(title = "Test Task", status = TaskStatus.NEW))

        // When
        val updated = taskRepository.updateStatus(task.id!!, TaskStatus.DONE)

        // Then
        assertTrue(updated)
        
        val updatedTask = taskRepository.findById(task.id!!)
        assertEquals(TaskStatus.DONE, updatedTask?.status)
    }

    @Test
    fun `updateStatus should return false when task does not exist`() {
        // When
        val updated = taskRepository.updateStatus(999L, TaskStatus.DONE)

        // Then
        assertEquals(false, updated)
    }

    @Test
    fun `deleteById should delete task and return true when task exists`() {
        // Given
        val task = taskRepository.save(Task(title = "Test Task"))

        // When
        val deleted = taskRepository.deleteById(task.id!!)

        // Then
        assertTrue(deleted)
        assertNull(taskRepository.findById(task.id!!))
    }

    @Test
    fun `deleteById should return false when task does not exist`() {
        // When
        val deleted = taskRepository.deleteById(999L)

        // Then
        assertEquals(false, deleted)
    }
}