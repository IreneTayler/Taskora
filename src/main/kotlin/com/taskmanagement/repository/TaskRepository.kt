package com.taskmanagement.repository

import com.taskmanagement.model.Task
import com.taskmanagement.model.TaskStatus
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class TaskRepository(private val jdbcClient: JdbcClient) {

    fun save(task: Task): Task {
        val keyHolder = GeneratedKeyHolder()
        
        jdbcClient.sql("""
            INSERT INTO tasks (title, description, status, created_at, updated_at)
            VALUES (:title, :description, :status, :createdAt, :updatedAt)
        """)
            .param("title", task.title)
            .param("description", task.description)
            .param("status", task.status.name)
            .param("createdAt", task.createdAt)
            .param("updatedAt", task.updatedAt)
            .update(keyHolder)

        val generatedId = keyHolder.key?.toLong() ?: throw RuntimeException("Failed to generate ID")
        return task.copy(id = generatedId)
    }

    fun findById(id: Long): Task? {
        return jdbcClient.sql("SELECT * FROM tasks WHERE id = :id")
            .param("id", id)
            .query { rs, _ ->
                Task(
                    id = rs.getLong("id"),
                    title = rs.getString("title"),
                    description = rs.getString("description"),
                    status = TaskStatus.valueOf(rs.getString("status")),
                    createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                    updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
                )
            }
            .optional()
            .orElse(null)
    }

    fun findAll(page: Int, size: Int, status: TaskStatus?): List<Task> {
        val sql = if (status != null) {
            """
            SELECT * FROM tasks 
            WHERE status = :status 
            ORDER BY created_at DESC 
            LIMIT :size OFFSET :offset
            """
        } else {
            """
            SELECT * FROM tasks 
            ORDER BY created_at DESC 
            LIMIT :size OFFSET :offset
            """
        }

        val query = jdbcClient.sql(sql)
            .param("size", size)
            .param("offset", page * size)

        if (status != null) {
            query.param("status", status.name)
        }

        return query.query { rs, _ ->
            Task(
                id = rs.getLong("id"),
                title = rs.getString("title"),
                description = rs.getString("description"),
                status = TaskStatus.valueOf(rs.getString("status")),
                createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
                updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
            )
        }.list()
    }

    fun count(status: TaskStatus?): Long {
        return if (status != null) {
            jdbcClient.sql("SELECT COUNT(*) FROM tasks WHERE status = :status")
                .param("status", status.name)
                .query(Long::class.java)
                .single()
        } else {
            jdbcClient.sql("SELECT COUNT(*) FROM tasks")
                .query(Long::class.java)
                .single()
        }
    }

    fun updateStatus(id: Long, status: TaskStatus): Boolean {
        val updatedRows = jdbcClient.sql("""
            UPDATE tasks 
            SET status = :status, updated_at = :updatedAt 
            WHERE id = :id
        """)
            .param("status", status.name)
            .param("updatedAt", LocalDateTime.now())
            .param("id", id)
            .update()

        return updatedRows > 0
    }

    fun deleteById(id: Long): Boolean {
        val deletedRows = jdbcClient.sql("DELETE FROM tasks WHERE id = :id")
            .param("id", id)
            .update()

        return deletedRows > 0
    }
}