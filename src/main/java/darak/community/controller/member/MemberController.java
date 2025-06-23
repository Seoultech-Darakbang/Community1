package darak.community.controller.member;

import darak.community.domain.member.Member;
import darak.community.dto.MemberCreateForm;
import darak.community.dto.PasswordForm;
import darak.community.dto.ResponseDto;
import darak.community.exception.PasswordFailedExceededException;
import darak.community.service.MemberService;
import darak.community.web.argumentresolver.Login;
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
    public String createForm(@Login Member member, @ModelAttribute("memberCreateForm") MemberCreateForm form,
                             Model model) {
        if (member != null) {
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

        Member newMember = form.toEntity();
        memberService.join(newMember);

        log.info("loginId: {}, name: {} 님의 회원 가입", form.getLoginId(), form.getName());
        redirectAttributes.addAttribute("memberJoinStatus", true);
        return "redirect:/login";
    }

    @GetMapping("/members/expired-password")
    public String expiredPasswordForm(@Login Member member, @ModelAttribute("passwordForm") PasswordForm form) {
        if (member == null) {
            return "redirect:/login";
        }
        if (!member.isPasswordExpired()) {
            return "redirect:/";
        }

        return "/members/expired-password";
    }

    @PostMapping("/members/password")
    public String changePassword(@Login Member member,
                                 @Validated @ModelAttribute("passwordForm") PasswordForm form,
                                 BindingResult bindingResult) throws PasswordFailedExceededException {
        if (member == null) {
            return "redirect:/login";
        }
        if (!member.isPasswordExpired()) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            return "members/expired-password";
        }

        if (!form.getNewPassword().equals(form.getNewPasswordConfirm())) {
            bindingResult.reject("change.member.password.confirmFail", "새로운 비밀번호를 다시 확인해주세요.");
            return "members/expired-password";
        }

        memberService.changePassword(member.getId(), form.getNewPassword(), form.getOldPassword());
        return "redirect:/";
    }


    @GetMapping("/members/new/confirmLoginId")
    @ResponseBody
    public ResponseDto<?> checkDuplicatedLoginId(@RequestParam String loginId) {

        log.info("loginID 중복 검사 = {}", loginId);
        if (loginId.isEmpty()) {
            return new ResponseDto<>(-1, "아이디를 입력해주세요", null);
        }

        if (loginId.length() < 4 || loginId.length() > 20) {
            return new ResponseDto<>(-1, "아이디는 4자 이상, 20자 이하입니다", null);
        }

        memberService.validateDuplicateMember(loginId);
        return new ResponseDto<>(1, "사용 가능한 회원 ID 입니다.", null);
    }
}
