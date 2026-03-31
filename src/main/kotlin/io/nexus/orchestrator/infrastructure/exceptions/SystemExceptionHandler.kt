package io.nexus.orchestrator.infrastructure.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class SystemExceptionHandler {

    // ✅ Work item missing → 404
    @ExceptionHandler(WorkItemMissingException::class)
    fun handleWorkItemMissing(exception: WorkItemMissingException): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("error" to exception.message!!))
    }

    // ✅ fallback (any unexpected error → 500)
    @ExceptionHandler(Exception::class)
    fun handleSystemFailure(exception: Exception): ResponseEntity<Map<String, String>> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf("error" to (exception.message ?: "System failure occurred")))
    }
}