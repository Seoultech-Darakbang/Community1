package darak.community.web.exceptionHandler;

import darak.community.core.exception.DuplicateMemberException;
import darak.community.core.exception.LoginFailedException;
import darak.community.core.exception.PasswordFailedExceededException;
import darak.community.core.exception.PasswordMismatchException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "darak.community.controller")
@Slf4j
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(DuplicateMemberException.class)
    public String handleDuplicateMemberException(DuplicateMemberException ex,
                                                 HttpServletRequest request,
                                                 Model model) {
        log.warn("회원가입 중 중복 오류: {}", ex.getMessage());

        model.addAttribute("duplicateError", true);
        model.addAttribute("errorMessage", ex.getMessage());

        return "members/createMemberForm";
    }

    @ExceptionHandler(LoginFailedException.class)
    public String handleLoginFailedException(LoginFailedException ex, Model model) {
        log.warn("로그인 실패: {}", ex.getMessage());
        model.addAttribute("loginError", true);
        model.addAttribute("errorMessage", ex.getMessage());
        return "login/loginForm";
    }

    @ExceptionHandler(PasswordFailedExceededException.class)
    public String handlePasswordFailedExceededException(PasswordFailedExceededException ex,
                                                        HttpServletRequest request,
                                                        Model model) {
        log.warn("비밀번호 시도 횟수 초과: {}", ex.getMessage());

        String requestURI = request.getRequestURI();
        model.addAttribute("passwordExceededError", true);
        model.addAttribute("errorMessage", ex.getMessage());

        if (requestURI.contains("/login")) {
            return "login/loginForm";
        } else if (requestURI.contains("/members/password")) {
            return "members/expired-password";
        }

        return "error/error";
    }

    @ExceptionHandler(PasswordMismatchException.class)
    public String handlePasswordMismatchException(PasswordMismatchException ex, Model model) {
        log.warn("비밀번호 불일치: {}", ex.getMessage());
        model.addAttribute("passwordMismatchError", true);
        model.addAttribute("errorMessage", ex.getMessage());
        return "members/expired-password";
    }

    @ExceptionHandler(IllegalAccessError.class)
    public String handleIllegalAccessError(IllegalAccessError ex, Model model) {
        log.warn("접근 권한 오류: {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model) {
        log.error("예상치 못한 오류 발생", ex);
        model.addAttribute("errorMessage", "시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        return "error/error";
    }
}