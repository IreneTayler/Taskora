package io.nexus.orchestrator.application.contracts

data class PaginatedCollection<T>(
    val elements: List<T>,
    val pageIndex: Int,
    val pageCapacity: Int,
    val totalElements: Long,
    val totalPages: Int
)