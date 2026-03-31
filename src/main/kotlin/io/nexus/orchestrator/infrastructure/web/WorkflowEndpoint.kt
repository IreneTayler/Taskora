package io.nexus.orchestrator.infrastructure.web

import io.nexus.orchestrator.application.contracts.WorkItemCreationRequest
import io.nexus.orchestrator.application.contracts.WorkItemRepresentation
import io.nexus.orchestrator.application.transformers.WorkItemTransformer
import io.nexus.orchestrator.domain.entities.WorkPhase
import io.nexus.orchestrator.domain.services.WorkflowManager
import io.nexus.orchestrator.infrastructure.exceptions.WorkItemMissingException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orchestrator/workflow-items")
class WorkflowEndpoint(
    private val workflowManager: WorkflowManager
) {

    // =========================
    // INITIATE WORK ITEM
    // =========================
    @PostMapping
    fun initiate(@RequestBody request: WorkItemCreationRequest): WorkItemRepresentation {
        val workItem = WorkItemTransformer.fromCreationRequest(request)
        val persisted = workflowManager.initiateWorkItem(workItem)
        return WorkItemTransformer.toRepresentation(persisted)
    }

    // =========================
    // ENUMERATE ALL WORK ITEMS (pagination + optional filter)
    // =========================
    @GetMapping
    fun enumerate(
        @RequestParam(defaultValue = "0") pageIndex: Int,
        @RequestParam(defaultValue = "10") pageCapacity: Int,
        @RequestParam(required = false) phase: WorkPhase?
    ): List<WorkItemRepresentation> {

        val workItems = workflowManager.enumerateWorkItems(pageIndex, pageCapacity, phase)
        return workItems.map { WorkItemTransformer.toRepresentation(it) }
    }

    // =========================
    // LOCATE WORK ITEM BY IDENTIFIER
    // =========================
    @GetMapping("/{identifier}")
    fun locate(@PathVariable identifier: Long): WorkItemRepresentation {
        val workItem = workflowManager.locateWorkItem(identifier)
            ?: throw WorkItemMissingException("Work item not found with identifier: $identifier")

        return WorkItemTransformer.toRepresentation(workItem)
    }

    // =========================
    // TRANSITION PHASE ONLY
    // =========================
    @PatchMapping("/{identifier}/phase")
    fun transitionPhase(
        @PathVariable identifier: Long,
        @RequestParam phase: WorkPhase
    ): String {

        val modifiedRows = workflowManager.transitionPhase(identifier, phase)

        if (modifiedRows == 0) {
            throw RuntimeException("Work item not found with identifier: $identifier")
        }

        return "Phase transitioned successfully"
    }

    // =========================
    // ELIMINATE WORK ITEM
    // =========================
    @DeleteMapping("/{identifier}")
    fun eliminate(@PathVariable identifier: Long): String {

        val eliminatedRows = workflowManager.eliminateWorkItem(identifier)

        if (eliminatedRows == 0) {
            throw RuntimeException("Work item not found with identifier: $identifier")
        }

        return "Work item eliminated successfully"
    }
}