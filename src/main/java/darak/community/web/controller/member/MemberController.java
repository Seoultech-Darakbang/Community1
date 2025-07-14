package darak.community.web.controller.member;

import darak.community.core.argumentresolver.Login;
import darak.community.core.exception.PasswordFailedExceededException;
import darak.community.core.session.dto.LoginMember;
import darak.community.service.member.MemberService;
import darak.community.web.dto.MemberCreateForm;
import darak.community.web.dto.PasswordForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(@Login LoginMember loginMember, @ModelAttribute("memberCreateForm") MemberCreateForm form,
                             Model model) {
        if (loginMember != null) {
            return "redirect:/";
        }
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Validated @ModelAttribute MemberCreateForm form, BindingResult bindingResult,
                         RedirectAttributes redirectAttributes, Model model) {

        if (bindingResult.hasErrors()) {
            return "members/createMemberForm";
        }

        memberService.join(form.toServiceRequest());
        log.info("loginId: {}, name: {} 님의 회원 가입", form.getLoginId(), form.getName());
        redirectAttributes.addAttribute("memberJoinStatus", true);
        return "redirect:/login";
    }

    @GetMapping("/members/expired-password")
    public String expiredPasswordForm(@Login LoginMember loginMember,
                                      @ModelAttribute("passwordForm") PasswordForm form) {
        if (loginMember == null) {
            return "redirect:/login";
        }

        return "/members/expired-password";
    }

    @PostMapping("/members/password")
    public String changePassword(@Login LoginMember loginMember,
                                 @Validated @ModelAttribute("passwordForm") PasswordForm form,
                                 BindingResult bindingResult) throws PasswordFailedExceededException {
        if (loginMember == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            return "members/expired-password";
        }

        if (!form.isNewPasswordMatch()) {
            bindingResult.reject("change.member.password.confirmFail", "새로운 비밀번호를 다시 확인해주세요.");
            return "members/expired-password";
        }

        memberService.changePassword(form.toServiceRequest(loginMember.getId()));
        return "redirect:/";
    }

}
