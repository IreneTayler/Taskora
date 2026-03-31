package io.nexus.orchestrator.application.contracts

import io.nexus.orchestrator.domain.entities.WorkPhase
import jakarta.validation.constraints.NotNull

data class PhaseTransitionRequest(
    @field:NotNull(message = "Phase must not be null")
    val phase: WorkPhase
)