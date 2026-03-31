package io.nexus.orchestrator.domain.ports

import io.nexus.orchestrator.domain.entities.WorkItem
import io.nexus.orchestrator.domain.entities.WorkPhase

interface WorkItemRepository {
    fun persist(workItem: WorkItem): WorkItem
    fun retrieveByIdentifier(identifier: Long): WorkItem?
    fun retrieveAll(pageIndex: Int, pageCapacity: Int, phaseFilter: WorkPhase?): List<WorkItem>
    fun countTotal(phaseFilter: WorkPhase?): Long
    fun modifyPhase(identifier: Long, newPhase: WorkPhase): Int
    fun removeByIdentifier(identifier: Long): Int
}