package io.nexus.orchestrator.infrastructure.persistence

import io.nexus.orchestrator.domain.entities.WorkItem
import io.nexus.orchestrator.domain.entities.WorkPhase
import io.nexus.orchestrator.domain.ports.WorkItemRepository
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
class JdbcWorkItemRepository(private val jdbcClient: JdbcClient) : WorkItemRepository {

    private fun recordMapper(resultSet: java.sql.ResultSet) = WorkItem(
        identifier = resultSet.getLong("identifier"),
        headline = resultSet.getString("headline"),
        narrative = resultSet.getString("narrative"),
        phase = WorkPhase.valueOf(resultSet.getString("phase")),
        initiatedAt = resultSet.getTimestamp("initiated_at").toInstant(),
        modifiedAt = resultSet.getTimestamp("modified_at").toInstant()
    )

    override fun persist(workItem: WorkItem): WorkItem {
        jdbcClient.sql("""
            INSERT INTO workflow_items(headline, narrative, phase, initiated_at, modified_at)
            VALUES (:headline, :narrative, :phase, :initiatedAt, :modifiedAt)
        """)
            .param("headline", workItem.headline)
            .param("narrative", workItem.narrative)
            .param("phase", workItem.phase.name)
            .param("initiatedAt", workItem.initiatedAt)
            .param("modifiedAt", workItem.modifiedAt)
            .update()

        return jdbcClient.sql("SELECT * FROM workflow_items ORDER BY identifier DESC LIMIT 1")
            .query { rs, _ -> recordMapper(rs) }
            .single()
    }

    override fun retrieveByIdentifier(identifier: Long): WorkItem? =
        jdbcClient.sql("SELECT * FROM workflow_items WHERE identifier = :identifier")
            .param("identifier", identifier)
            .query { rs, _ -> recordMapper(rs) }
            .optional()
            .orElse(null)

    override fun retrieveAll(pageIndex: Int, pageCapacity: Int, phaseFilter: WorkPhase?): List<WorkItem> =
        if (phaseFilter != null) {
            jdbcClient.sql("""
                SELECT * FROM workflow_items
                WHERE phase = :phase
                ORDER BY initiated_at DESC
                LIMIT :limit OFFSET :offset
            """)
                .param("phase", phaseFilter.name)
                .param("limit", pageCapacity)
                .param("offset", pageIndex * pageCapacity)
                .query { rs, _ -> recordMapper(rs) }
                .list()
        } else {
            jdbcClient.sql("""
                SELECT * FROM workflow_items
                ORDER BY initiated_at DESC
                LIMIT :limit OFFSET :offset
            """)
                .param("limit", pageCapacity)
                .param("offset", pageIndex * pageCapacity)
                .query { rs, _ -> recordMapper(rs) }
                .list()
        }

    override fun countTotal(phaseFilter: WorkPhase?): Long =
        if (phaseFilter != null) {
            jdbcClient.sql("SELECT COUNT(*) FROM workflow_items WHERE phase = :phase")
                .param("phase", phaseFilter.name)
                .query(Long::class.java)
                .single()
        } else {
            jdbcClient.sql("SELECT COUNT(*) FROM workflow_items")
                .query(Long::class.java)
                .single()
        }

    override fun modifyPhase(identifier: Long, newPhase: WorkPhase): Int =
        jdbcClient.sql("""
            UPDATE workflow_items
            SET phase = :phase, modified_at = :modifiedAt
            WHERE identifier = :identifier
        """)
            .param("phase", newPhase.name)
            .param("modifiedAt", Instant.now())
            .param("identifier", identifier)
            .update()

    override fun removeByIdentifier(identifier: Long): Int =
        jdbcClient.sql("DELETE FROM workflow_items WHERE identifier = :identifier")
            .param("identifier", identifier)
            .update()
}