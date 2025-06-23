package darak.community.controller.home;

import darak.community.domain.member.Member;
import darak.community.dto.LoginForm;
import darak.community.exception.PasswordFailedExceededException;
import darak.community.service.LoginService;
import darak.community.web.argumentresolver.Login;
import darak.community.web.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

    @GetMapping("/login")
    public String loginForm(@Login Member member, @ModelAttribute("loginForm") LoginForm form, Model model) {
        if (member != null) {
            model.addAttribute(member);
            return "redirect:/";
        }
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult,
                        @RequestParam(defaultValue = "/") String redirectURL, HttpServletRequest request)
            throws PasswordFailedExceededException {

        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        if (loginMember == null) {
            bindingResult.reject("member.password.fail", "ID 또는 비밀번호가 일치하지 않습니다");
            return "login/loginForm";
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        if (loginMember.isPasswordExpired()) {
            return "redirect:/members/expired-password?redirectURL=" + redirectURL;
        }

        return "redirect:" + redirectURL;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            session.invalidate();
        }

        return "redirect:/";
    }
}
