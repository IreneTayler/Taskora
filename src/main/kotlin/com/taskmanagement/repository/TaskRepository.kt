package com.taskmanagement.repository

import com.taskmanagement.model.Task
import com.taskmanagement.model.TaskStatus
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime

@Repository
class TaskRepository(private val client: JdbcClient) {

    fun save(task: Task): Task {
        val keyHolder = GeneratedKeyHolder()
        
        client.sql("""
            INSERT INTO tasks (title, description, status, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
        """)
            .params(task.title, task.description, task.status.name, task.createdAt, task.updatedAt)
            .update(keyHolder)

        val id = keyHolder.key?.toLong() ?: error("Could not retrieve generated ID")
        return task.copy(id = id)
    }

    fun findById(id: Long): Task? {
        return client.sql("SELECT * FROM tasks WHERE id = ?")
            .param(id)
            .query(::mapTask)
            .optional()
            .orElse(null)
    }

    fun findAll(page: Int, size: Int, status: TaskStatus?): List<Task> {
        val offset = page * size
        
        return if (status != null) {
            client.sql("""
                SELECT * FROM tasks 
                WHERE status = ? 
                ORDER BY created_at DESC 
                LIMIT ? OFFSET ?
            """)
                .params(status.name, size, offset)
                .query(::mapTask)
                .list()
        } else {
            client.sql("""
                SELECT * FROM tasks 
                ORDER BY created_at DESC 
                LIMIT ? OFFSET ?
            """)
                .params(size, offset)
                .query(::mapTask)
                .list()
        }
    }

    fun count(status: TaskStatus?): Long {
        return if (status != null) {
            client.sql("SELECT COUNT(*) FROM tasks WHERE status = ?")
                .param(status.name)
                .query(Long::class.java)
                .single()
        } else {
            client.sql("SELECT COUNT(*) FROM tasks")
                .query(Long::class.java)
                .single()
        }
    }

    fun updateStatus(id: Long, status: TaskStatus): Boolean {
        val rows = client.sql("UPDATE tasks SET status = ?, updated_at = ? WHERE id = ?")
            .params(status.name, LocalDateTime.now(), id)
            .update()
        return rows > 0
    }

    fun deleteById(id: Long): Boolean {
        val rows = client.sql("DELETE FROM tasks WHERE id = ?")
            .param(id)
            .update()
        return rows > 0
    }

    private fun mapTask(rs: ResultSet, @Suppress("UNUSED_PARAMETER") rowNum: Int): Task {
        return Task(
            id = rs.getLong("id"),
            title = rs.getString("title"),
            description = rs.getString("description"),
            status = TaskStatus.valueOf(rs.getString("status")),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
            updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
        )
    }
}