package com.tui.vcsrepositorysearch

import com.tui.vcsrepositorysearch.application.dto.RsErrorResponse
import com.tui.vcsrepositorysearch.service.exception.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.io.IOException

@RestControllerAdvice
class ControllerAdvisor {
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<RsErrorResponse<Nothing>> {
        val response = when (ex) {
            is IOException -> {
                RsErrorResponse(
                    message = ex.localizedMessage,
                    status = HttpStatus.INTERNAL_SERVER_ERROR,
                    code = HttpStatus.INTERNAL_SERVER_ERROR.value()
                )
            }
            is EntityNotFoundException -> {
                RsErrorResponse(
                    message = ex.localizedMessage,
                    status = HttpStatus.NOT_FOUND,
                    code = HttpStatus.NOT_FOUND.value()
                )
            }
            is HttpMediaTypeNotSupportedException -> {
                RsErrorResponse(
                    message = ex.localizedMessage,
                    status = HttpStatus.NOT_ACCEPTABLE,
                    code = HttpStatus.NOT_ACCEPTABLE.value()
                )
            }
            else -> {
                RsErrorResponse<Nothing>(
                    message = ex.localizedMessage,
                    status = HttpStatus.BAD_REQUEST
                )
            }
        }
        return ResponseEntity(response, response.status)
    }
}
