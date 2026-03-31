package io.nexus.orchestrator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WorkflowOrchestratorApplication

fun main(arguments: Array<String>) {
    runApplication<WorkflowOrchestratorApplication>(*arguments)
}