package darak.community.web.controllerAdvice.exceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import darak.community.web.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "darak.community.web.api")
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ApiResponse<?> handleBadRequestExceptions(RuntimeException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ApiResponse.error(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleAllExceptions(Exception ex) {
        log.error("Internal server error: ", ex);
        return ApiResponse.error("서버 내부 오류가 발생했습니다.");
    }

}