package io.nexus.orchestrator.application.contracts

import io.nexus.orchestrator.domain.entities.WorkPhase
import java.time.Instant

data class WorkItemRepresentation(
    val identifier: Long,
    val headline: String,
    val narrative: String,
    val phase: WorkPhase,
    val initiatedAt: Instant,
    val modifiedAt: Instant
)