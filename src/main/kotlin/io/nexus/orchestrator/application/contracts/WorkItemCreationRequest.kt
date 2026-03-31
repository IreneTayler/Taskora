package io.nexus.orchestrator.application.contracts

data class WorkItemCreationRequest(
    val headline: String,
    val narrative: String
)