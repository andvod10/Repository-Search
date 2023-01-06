package com.tui.vcsrepositorysearch

import com.tui.vcsrepositorysearch.application.dto.RsErrorResponse
import com.tui.vcsrepositorysearch.service.exception.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpMediaTypeException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.io.IOException

@RestControllerAdvice
class ControllerAdvisor {
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleExceptionGeneralUnhandledExceptions(ex: Exception): ResponseEntity<RsErrorResponse<Any>> {
        val response = RsErrorResponse<Any>(
            message = ex.localizedMessage,
            status = HttpStatus.BAD_REQUEST,
            code = HttpStatus.BAD_REQUEST.value()
        )
        return wrapErrorResponse(response)
    }

    @ExceptionHandler(IOException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleExceptionInternalServerError(ex: Exception): ResponseEntity<RsErrorResponse<Any>> {
        val response = RsErrorResponse<Any>(
            message = ex.localizedMessage,
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            code = HttpStatus.INTERNAL_SERVER_ERROR.value()
        )
        return wrapErrorResponse(response)
    }

    @ExceptionHandler(HttpMediaTypeException::class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    fun handleExceptionHttpMediaExceptions(ex: Exception): ResponseEntity<RsErrorResponse<Any>> {
        val response = RsErrorResponse<Any>(
            message = ex.localizedMessage,
            status = HttpStatus.NOT_ACCEPTABLE,
            code = HttpStatus.NOT_ACCEPTABLE.value()
        )
        return wrapErrorResponse(response)
    }

    @ExceptionHandler(
        *[
            EntityNotFoundException::class
        ]
    )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleExceptionNotFoundExceptions(ex: Exception): ResponseEntity<RsErrorResponse<Any>> {
        val response = RsErrorResponse<Any>(
            message = ex.localizedMessage,
            status = HttpStatus.NOT_FOUND,
            code = HttpStatus.NOT_FOUND.value()
        )
        return wrapErrorResponse(response)
    }

    private fun wrapErrorResponse(response: RsErrorResponse<Any>): ResponseEntity<RsErrorResponse<Any>> {
        return ResponseEntity(response, response.status)
    }
}
