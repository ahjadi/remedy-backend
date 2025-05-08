package com.alialjadi.remedybackend.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.MethodArgumentNotValidException
import  java.time.format.DateTimeParseException
import  jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import  jakarta.validation.ConstraintViolationException
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String?>> {
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }
        return ResponseEntity.badRequest().body(errors)
    }

    @ExceptionHandler(DateTimeParseException::class)
    fun handleDateParseError(ex: DateTimeParseException): ResponseEntity<Map<String, String?>> {
        return ResponseEntity.badRequest().body(mapOf("error" to "Invalid date format. Use dd-MM-yyyy."))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<Map<String, String?>> {
        return ResponseEntity.badRequest().body(mapOf("error" to (ex.message ?: "Bad request")))
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(ex: EntityNotFoundException): ResponseEntity<Map<String, String?>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to (ex.message ?: "Entity not found")))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<Map<String, String>> {
        val errors = ex.constraintViolations.associate {
            it.propertyPath.toString() to (it.message ?: "Invalid value")
        }
        return ResponseEntity.badRequest().body(errors)
    }
}