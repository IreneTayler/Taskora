package io.nexus.orchestrator.domain.services

import io.nexus.orchestrator.domain.entities.WorkItem
import io.nexus.orchestrator.domain.entities.WorkPhase
import io.nexus.orchestrator.domain.ports.WorkItemRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class WorkflowManager(private val workItemRepository: WorkItemRepository) {

    fun initiateWorkItem(workItem: WorkItem): WorkItem {
        return workItemRepository.persist(workItem)
    }

    fun locateWorkItem(identifier: Long): WorkItem? {
        return workItemRepository.retrieveByIdentifier(identifier)
    }

    fun enumerateWorkItems(pageIndex: Int, pageCapacity: Int, phaseFilter: WorkPhase?): List<WorkItem> {
        return workItemRepository.retrieveAll(pageIndex, pageCapacity, phaseFilter)
    }

    fun calculateTotal(phaseFilter: WorkPhase?): Long {
        return workItemRepository.countTotal(phaseFilter)
    }

    fun transitionPhase(identifier: Long, newPhase: WorkPhase): Int {
        return workItemRepository.modifyPhase(identifier, newPhase)
    }

    fun eliminateWorkItem(identifier: Long): Int {
        return workItemRepository.removeByIdentifier(identifier)
    }
}