package darak.community.web.controller.home;

import darak.community.core.argumentresolver.Login;
import darak.community.core.exception.PasswordExpiredException;
import darak.community.core.exception.PasswordFailedExceededException;
import darak.community.core.exception.PasswordMismatchException;
import darak.community.core.session.SessionManager;
import darak.community.core.session.dto.LoginMember;
import darak.community.dto.LoginForm;
import darak.community.service.login.LoginService;
import darak.community.service.login.response.MemberLoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@Login LoginMember member, @ModelAttribute("loginForm") LoginForm form, Model model) {
        if (member != null) {
            model.addAttribute(member);
            return "redirect:/";
        }
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult,
                        @RequestParam(defaultValue = "/") String redirectURL, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        try {
            MemberLoginResponse loginResponse = loginService.login(loginForm.toServiceRequest());
            sessionManager.login(request.getSession(),
                    LoginMember.of(loginResponse.getMemberId(), loginResponse.getMemberGrade()));
            return "redirect:" + redirectURL;
        } catch (PasswordMismatchException e) {
            bindingResult.reject("member.password.fail", "ID 또는 비밀번호가 일치하지 않습니다");
            return "login/loginForm";
        } catch (PasswordFailedExceededException e) {
            bindingResult.reject("member.password.fail", "비밀번호 입력 실패 횟수를 초과했습니다. 관리자에게 문의하세요.");
            return "login/loginForm";
        } catch (PasswordExpiredException e) {
            return "redirect:/members/expired-password?redirectURL=" + redirectURL;
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        sessionManager.logout(request.getSession());
        return "redirect:/";
    }
}
