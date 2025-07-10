package darak.community.web.controller.member;

import darak.community.core.argumentresolver.Login;
import darak.community.core.exception.PasswordFailedExceededException;
import darak.community.core.session.dto.LoginMember;
import darak.community.dto.ApiResponse;
import darak.community.dto.MemberCreateForm;
import darak.community.dto.PasswordForm;
import darak.community.service.member.MemberService;
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
import org.springframework.web.bind.annotation.ResponseBody;
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


    @GetMapping("/members/new/confirmLoginId")
    @ResponseBody
    public ApiResponse<?> checkDuplicatedLoginId(@RequestParam String loginId) {

        if (loginId.isEmpty()) {
            return ApiResponse.error("아이디를 입력해주세요");
        }

        if (loginId.length() < 4 || loginId.length() > 20) {
            return ApiResponse.error("아이디는 4자 이상, 20자 이하입니다");
        }

        memberService.validateDuplicateMember(loginId);
        return ApiResponse.successWithNoData("사용 가능한 회원 ID 입니다.");
    }
}
