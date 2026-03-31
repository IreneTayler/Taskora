package com.example.demo.repository

import com.example.demo.model.Task
import com.example.demo.model.TaskStatus
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class TaskRepository(private val jdbcClient: JdbcClient) {

    private fun rowMapper(rs: java.sql.ResultSet) = Task(
        id = rs.getLong("id"),
        title = rs.getString("title"),
        description = rs.getString("description"),
        status = TaskStatus.valueOf(rs.getString("status")),
        createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
        updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
    )

    fun save(task: Task): Task {
        jdbcClient.sql("""
            INSERT INTO tasks(title, description, status, created_at, updated_at)
            VALUES (:title, :description, :status, :createdAt, :updatedAt)
        """)
            .param("title", task.title)
            .param("description", task.description)
            .param("status", task.status.name)
            .param("createdAt", task.createdAt)
            .param("updatedAt", task.updatedAt)
            .update()

        return jdbcClient.sql("SELECT * FROM tasks ORDER BY id DESC LIMIT 1")
            .query { rs, _ -> rowMapper(rs) }
            .single()
    }

    fun findById(id: Long): Task? =
        jdbcClient.sql("SELECT * FROM tasks WHERE id = :id")
            .param("id", id)
            .query { rs, _ -> rowMapper(rs) }
            .optional()
            .orElse(null)

    fun findAll(page: Int, size: Int, status: TaskStatus?): List<Task> =
        if (status != null) {
            jdbcClient.sql("""
                SELECT * FROM tasks
                WHERE status = :status
                ORDER BY created_at DESC
                LIMIT :limit OFFSET :offset
            """)
                .param("status", status.name)
                .param("limit", size)
                .param("offset", page * size)
                .query { rs, _ -> rowMapper(rs) }
                .list()
        } else {
            jdbcClient.sql("""
                SELECT * FROM tasks
                ORDER BY created_at DESC
                LIMIT :limit OFFSET :offset
            """)
                .param("limit", size)
                .param("offset", page * size)
                .query { rs, _ -> rowMapper(rs) }
                .list()
        }

    fun count(status: TaskStatus?): Long =
        if (status != null) {
            jdbcClient.sql("SELECT COUNT(*) FROM tasks WHERE status = :status")
                .param("status", status.name)
                .query(Long::class.java)
                .single()
        } else {
            jdbcClient.sql("SELECT COUNT(*) FROM tasks")
                .query(Long::class.java)
                .single()
        }

    fun updateStatus(id: Long, status: TaskStatus, updatedAt: LocalDateTime): Int =
        jdbcClient.sql("""
            UPDATE tasks
            SET status = :status, updated_at = :updatedAt
            WHERE id = :id
        """)
            .param("status", status.name)
            .param("updatedAt", updatedAt)
            .param("id", id)
            .update()

    fun deleteById(id: Long): Int =
        jdbcClient.sql("DELETE FROM tasks WHERE id = :id")
            .param("id", id)
            .update()
}