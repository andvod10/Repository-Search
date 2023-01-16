package com.tui.vcsrepositorysearch

import com.tui.vcsrepositorysearch.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.web.HttpMediaTypeException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.io.IOException

@RestControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleExceptionGeneralUnhandledExceptions(ex: Exception): ErrorResponse<Any> {
        return ErrorResponse(
            message = ex.localizedMessage,
            status = HttpStatus.BAD_REQUEST,
            code = HttpStatus.BAD_REQUEST.value()
        )
    }

    @ExceptionHandler(IOException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleExceptionInternalServerError(ex: Exception): ErrorResponse<Any> {
        return ErrorResponse(
            message = ex.localizedMessage,
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            code = HttpStatus.INTERNAL_SERVER_ERROR.value()
        )
    }

    @ExceptionHandler(HttpMediaTypeException::class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    fun handleExceptionHttpMediaExceptions(ex: Exception): ErrorResponse<Any> {
        return ErrorResponse(
            message = ex.localizedMessage,
            status = HttpStatus.NOT_ACCEPTABLE,
            code = HttpStatus.NOT_ACCEPTABLE.value()
        )
    }
}
