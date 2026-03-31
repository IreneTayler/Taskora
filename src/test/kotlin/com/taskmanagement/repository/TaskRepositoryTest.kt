package com.taskmanagement.repository

import com.taskmanagement.model.Task
import com.taskmanagement.model.TaskStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.test.context.jdbc.Sql

@JdbcTest
@Sql("/test-schema.sql")
@Sql(statements = ["DELETE FROM tasks"])
class TaskRepositoryTest {

    @Autowired
    private lateinit var client: JdbcClient

    private val repository by lazy { TaskRepository(client) }

    @Test
    fun `should save task with generated id`() {
        val task = Task(title = "Setup CI pipeline", description = "Configure GitHub Actions")

        val saved = repository.save(task)

        assertNotNull(saved.id)
        assertEquals("Setup CI pipeline", saved.title)
        assertEquals("Configure GitHub Actions", saved.description)
        assertEquals(TaskStatus.NEW, saved.status)
    }

    @Test
    fun `should find existing task by id`() {
        val task = Task(title = "Database migration", description = "Add user preferences table")
        val saved = repository.save(task)

        val found = repository.findById(saved.id!!)

        assertNotNull(found)
        assertEquals(saved.id, found!!.id)
        assertEquals("Database migration", found.title)
    }

    @Test
    fun `should return null for non-existent task`() {
        val found = repository.findById(999L)
        assertNull(found)
    }

    @Test
    fun `should paginate results correctly`() {
        repeat(15) { i ->
            repository.save(Task(title = "Task ${i + 1}", description = "Description ${i + 1}"))
        }

        val firstPage = repository.findAll(0, 10, null)
        assertEquals(10, firstPage.size)

        val secondPage = repository.findAll(1, 10, null)
        assertEquals(5, secondPage.size)
    }

    @Test
    fun `should filter tasks by status`() {
        repository.save(Task(title = "Bug fix", status = TaskStatus.NEW))
        repository.save(Task(title = "Feature work", status = TaskStatus.IN_PROGRESS))
        repository.save(Task(title = "Documentation", status = TaskStatus.NEW))

        val newTasks = repository.findAll(0, 10, TaskStatus.NEW)

        assertEquals(2, newTasks.size)
        assertTrue(newTasks.all { it.status == TaskStatus.NEW })
    }

    @Test
    fun `should count all tasks`() {
        repeat(3) { i ->
            repository.save(Task(title = "Task ${i + 1}"))
        }

        val count = repository.count(null)
        assertEquals(3L, count)
    }

    @Test
    fun `should count tasks by status`() {
        repository.save(Task(title = "Task 1", status = TaskStatus.NEW))
        repository.save(Task(title = "Task 2", status = TaskStatus.IN_PROGRESS))
        repository.save(Task(title = "Task 3", status = TaskStatus.NEW))

        val newCount = repository.count(TaskStatus.NEW)
        assertEquals(2L, newCount)
    }

    @Test
    fun `should update task status successfully`() {
        val task = repository.save(Task(title = "Code review", status = TaskStatus.NEW))

        val updated = repository.updateStatus(task.id!!, TaskStatus.DONE)
        assertTrue(updated)
        
        val found = repository.findById(task.id!!)
        assertEquals(TaskStatus.DONE, found?.status)
    }

    @Test
    fun `should fail to update non-existent task`() {
        val updated = repository.updateStatus(999L, TaskStatus.DONE)
        assertFalse(updated)
    }

    @Test
    fun `should delete existing task`() {
        val task = repository.save(Task(title = "Cleanup logs"))

        val deleted = repository.deleteById(task.id!!)
        assertTrue(deleted)
        assertNull(repository.findById(task.id!!))
    }

    @Test
    fun `should fail to delete non-existent task`() {
        val deleted = repository.deleteById(999L)
        assertFalse(deleted)
    }
}