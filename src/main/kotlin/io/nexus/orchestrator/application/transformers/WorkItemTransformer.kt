package io.nexus.orchestrator.application.transformers

import io.nexus.orchestrator.application.contracts.WorkItemCreationRequest
import io.nexus.orchestrator.application.contracts.WorkItemRepresentation
import io.nexus.orchestrator.domain.entities.WorkItem
import io.nexus.orchestrator.domain.entities.WorkPhase

object WorkItemTransformer {

    fun fromCreationRequest(request: WorkItemCreationRequest): WorkItem {
        return WorkItem(
            identifier = null,
            headline = request.headline,
            narrative = request.narrative,
            phase = WorkPhase.PENDING
        )
    }

    fun toRepresentation(workItem: WorkItem): WorkItemRepresentation {
        return WorkItemRepresentation(
            identifier = workItem.identifier ?: 0L,
            headline = workItem.headline,
            narrative = workItem.narrative,
            phase = workItem.phase,
            initiatedAt = workItem.initiatedAt,
            modifiedAt = workItem.modifiedAt
        )
    }
}