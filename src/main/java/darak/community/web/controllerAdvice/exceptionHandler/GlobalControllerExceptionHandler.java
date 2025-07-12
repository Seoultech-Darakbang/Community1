package darak.community.web.controllerAdvice.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "darak.community.controller")
@Slf4j
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(IllegalAccessError.class)
    public String handleIllegalAccessError(IllegalAccessError ex, Model model) {
        log.warn("접근 권한 오류: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/403";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        String message = ex.getMessage();

        model.addAttribute("errorMessage", "잘못된 요청입니다: " + message);
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model) {
        log.error("예상치 못한 오류 발생", ex);
        model.addAttribute("errorMessage", "시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        return "error/error";
    }
}