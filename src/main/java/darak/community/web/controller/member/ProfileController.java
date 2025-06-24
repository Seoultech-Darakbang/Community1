package darak.community.web.controller.member;

import darak.community.core.argumentresolver.Login;
import darak.community.core.session.constant.SessionConst;
import darak.community.domain.member.Member;
import darak.community.dto.MyCommentDto;
import darak.community.dto.MyPostDto;
import darak.community.dto.ProfileDto;
import darak.community.service.board.BoardCategoryService;
import darak.community.service.member.MemberService;
import darak.community.service.member.ProfileService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final BoardCategoryService boardCategoryService;
    private final MemberService memberService;

    @ModelAttribute
    public void addAttributes(@Login Member member, Model model) {
        addBoardInformation(model);
        model.addAttribute("member", member);
    }

    @GetMapping
    public String profile(@Login Member member, Model model) {

        ProfileDto profile = profileService.getProfile(member.getId());
        model.addAttribute("profile", profile);
        model.addAttribute("active", "profile");

        return "community/profile/memberProfile";
    }

    @GetMapping("/{memberId}")
    public String memberProfile(@PathVariable Long memberId, Model model) {

        ProfileDto profile = profileService.getProfile(memberId);
        model.addAttribute("profile", profile);
        model.addAttribute("active", "profile");

        return "community/profile/memberProfile";
    }

    @GetMapping("/posts")
    public String myPosts(@Login Member member,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String boardName,
                          Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MyPostDto> posts = getMyPostDtos(member, keyword, boardName, pageable);

        model.addAttribute("posts", posts);
        model.addAttribute("keyword", keyword);
        model.addAttribute("boardName", boardName);
        model.addAttribute("active", "posts");

        return "community/profile/myPosts";
    }

    private Page<MyPostDto> getMyPostDtos(Member member, String keyword, String boardName, Pageable pageable) {
        if ((keyword != null && !keyword.trim().isEmpty()) ||
                (boardName != null && !boardName.trim().isEmpty())) {
            return profileService.searchMyPosts(member.getId(), keyword, boardName, pageable);
        }
        return profileService.getMyPosts(member.getId(), pageable);
    }

    @GetMapping("/comments")
    public String myComments(@Login Member member,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String boardName,
                             Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MyCommentDto> comments = getMyCommentDtos(member, keyword, boardName, pageable);

        model.addAttribute("comments", comments);
        model.addAttribute("keyword", keyword);
        model.addAttribute("boardName", boardName);
        model.addAttribute("active", "comments");

        return "community/profile/myComments";
    }

    private Page<MyCommentDto> getMyCommentDtos(Member member, String keyword, String boardName, Pageable pageable) {
        Page<MyCommentDto> comments;
        if ((keyword != null && !keyword.trim().isEmpty()) ||
                (boardName != null && !boardName.trim().isEmpty())) {
            return profileService.searchMyComments(member.getId(), keyword, boardName, pageable);
        }
        return profileService.getMyComments(member.getId(), pageable);
    }

    @GetMapping("/liked-posts")
    public String likedPosts(@Login Member member,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<MyPostDto> likedPosts = profileService.getLikedPosts(member.getId(), pageable);

        model.addAttribute("posts", likedPosts);
        model.addAttribute("active", "liked-posts");

        return "community/profile/likedPosts";
    }

    @GetMapping("/liked-comments")
    public String likedComments(@Login Member member,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model) {
        if (member == null) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<MyCommentDto> likedComments = profileService.getLikedComments(member.getId(), pageable);

        model.addAttribute("comments", likedComments);
        model.addAttribute("active", "liked-comments");

        return "community/profile/likedComments";
    }

    @GetMapping("/edit")
    public String editProfile(@Login Member member, Model model) {

        model.addAttribute("member", member);
        model.addAttribute("active", "edit");

        return "community/profile/editProfile";
    }

    @PostMapping("/edit")
    public String updateProfile(@Login Member member,
                                @RequestParam String email,
                                @RequestParam(required = false) String phone,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (member == null) {
            return "redirect:/login";
        }

        try {
            profileService.updateProfile(member.getId(), email, phone);

            Member updatedMember = memberService.findById(member.getId());
            session.setAttribute(SessionConst.LOGIN_MEMBER_ID, updatedMember);

            redirectAttributes.addFlashAttribute("message", "프로필이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "프로필 수정 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "redirect:/profile/edit";
    }

    @PostMapping("/change-password")
    public String changePassword(@Login Member member,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "새 비밀번호가 일치하지 않습니다.");
            return "redirect:/profile/edit";
        }

        try {
            memberService.changePassword(member.getId(), newPassword, currentPassword);
            redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "비밀번호 변경 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "redirect:/profile/edit";
    }

    private void addBoardInformation(Model model) {
        model.addAttribute("boardCategories", boardCategoryService.findAll());
    }
} 