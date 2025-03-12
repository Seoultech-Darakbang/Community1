package darak.community.controller;

import darak.community.domain.member.Member;
import darak.community.dto.MemberCreateForm;
import darak.community.dto.ResponseDto;
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

        try {
            Member newMember = form.toEntity();
            memberService.join(newMember);
        } catch (IllegalStateException e) {
            log.info("loginId {}에서 중복 발생", form.getLoginId());
            bindingResult.rejectValue("loginId", "duplicated.member.loginId", "회원 ID가 중복되었습니다.");
            return "members/createMemberForm";
        }

        log.info("loginId: {}, name: {} 님의 회원 가입", form.getLoginId(), form.getName());
        redirectAttributes.addAttribute("memberJoinStatus", true);
        return "redirect:/login";
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

        try {
            memberService.validateDuplicateMember(loginId);
        } catch (IllegalStateException e) {
            return new ResponseDto<>(-1, "중복된 회원 ID 입니다.", null);
        }

        return new ResponseDto<>(1, "사용 가능한 회원 ID 입니다.", null);
    }
}
