package darak.community.controller;

import darak.community.domain.member.Member;
import darak.community.dto.MemberCreateForm;
import darak.community.service.MemberService;
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
    public String createForm(@ModelAttribute("memberCreateForm") MemberCreateForm form, Model model) {
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
}
