package darak.community.web.controllerAdvice.exceptionHandler;

import darak.community.web.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "darak.community.api")
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<?>> handleBadRequestExceptions(RuntimeException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllExceptions(Exception ex) {
        log.error("Internal server error: ", ex);
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error("서버 내부 오류가 발생했습니다."));
    }

}