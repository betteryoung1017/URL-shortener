package org.example.short_code.exception;

import org.example.short_code.pojo.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleIllegalArgumentException(IllegalArgumentException e) {
        return Result.error(400, e.getMessage());
    }
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleRuntimeException(RuntimeException e) {
        return Result.error(500,"服务器内部错误:"+ e.getMessage());
    }
    @ExceptionHandler(ShortLinkNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<String> handleShortLinkNotFoundException(ShortLinkNotFoundException e) {
        return Result.error(404, e.getMessage());
    }
}
