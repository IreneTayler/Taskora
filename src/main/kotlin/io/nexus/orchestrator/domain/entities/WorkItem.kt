package io.nexus.orchestrator.domain.entities

import java.time.Instant

data class WorkItem(
    val identifier: Long? = null,
    val headline: String,
    val narrative: String,
    val phase: WorkPhase = WorkPhase.PENDING,
    val initiatedAt: Instant = Instant.now(),
    val modifiedAt: Instant = Instant.now()
)