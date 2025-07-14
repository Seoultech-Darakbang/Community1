package darak.community.web.controller.community.admin;

import darak.community.core.auth.WebAuth;
import darak.community.domain.member.MemberGrade;
import darak.community.service.board.BoardService;
import darak.community.service.board.request.BoardCreateServiceRequest;
import darak.community.service.board.request.BoardUpdateServiceRequest;
import darak.community.service.board.response.BoardAdminResponse;
import darak.community.service.boardcategory.BoardCategoryService;
import darak.community.service.boardcategory.request.BoardCategoryCreateServiceRequest;
import darak.community.service.boardcategory.request.BoardCategoryUpdateServiceRequest;
import darak.community.service.comment.CommentService;
import darak.community.service.member.MemberService;
import darak.community.service.member.response.MemberResponse;
import darak.community.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@WebAuth(MemberGrade.ADMIN)
public class AdminController {

    private final BoardCategoryService boardCategoryService;
    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final BoardService boardService;

    @GetMapping
    public String adminHome(Model model) {
        model.addAttribute("totalMembers", memberService.getTotalMemberCount());
        model.addAttribute("totalPosts", postService.getTotalPostCount());
        model.addAttribute("totalComments", commentService.getTotalCommentCount());
        model.addAttribute("totalCategories", boardCategoryService.getTotalCategoryCount());
        model.addAttribute("totalBoards", boardService.getTotalBoardCount());
        model.addAttribute("active", "dashboard");

        return "admin/dashboard";
    }

    @GetMapping("/categories")
    public String categoryList(Model model) {
        model.addAttribute("categories", boardCategoryService.findAll());
        model.addAttribute("active", "categories");
        return "admin/categories/list";
    }

    @GetMapping("/categories/new")
    public String categoryCreateForm(Model model) {
        model.addAttribute("active", "categories");
        return "admin/categories/form";
    }

    @PostMapping("/categories")
    public String createCategory(@RequestParam String name,
                                 @RequestParam Integer priority,
                                 RedirectAttributes redirectAttributes) {
        try {
            boardCategoryService.createBoardCategory(BoardCategoryCreateServiceRequest.of(name, priority));
            redirectAttributes.addFlashAttribute("message", "카테고리가 성공적으로 생성되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "카테고리 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/{id}/edit")
    public String categoryEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", boardCategoryService.findById(id));
        model.addAttribute("boards", boardService.findBoardsBy(id));
        model.addAttribute("active", "categories");
        return "admin/categories/edit";
    }

    @PostMapping("/categories/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @RequestParam String name,
                                 @RequestParam Integer priority,
                                 RedirectAttributes redirectAttributes) {
        try {
            boardCategoryService.updateBoardCategory(new BoardCategoryUpdateServiceRequest(id, name, priority));
            redirectAttributes.addFlashAttribute("message", "카테고리가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "카테고리 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boardCategoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("message", "카테고리가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "카테고리 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/boards")
    public String boardList(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) Long categoryId,
                            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BoardAdminResponse> boards;

        if (categoryId != null) {
            boards = boardService.getBoardsWithCategoryByCategoryPaged(categoryId, pageable);
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            boards = boardService.getAllBoardsWithCategoryPaged(pageable);
        }

        model.addAttribute("boards", boards);
        model.addAttribute("categories", boardCategoryService.findAll());
        model.addAttribute("active", "boards");
        return "admin/boards/list";
    }

    @GetMapping("/boards/new")
    public String boardCreateForm(Model model) {
        model.addAttribute("categories", boardCategoryService.findAll());
        model.addAttribute("active", "boards");
        return "admin/boards/form";
    }

    @PostMapping("/boards")
    public String createBoard(@RequestParam String name,
                              @RequestParam String description,
                              @RequestParam Long categoryId,
                              @RequestParam Integer priority,
                              RedirectAttributes redirectAttributes) {
        try {
            boardService.createBoard(BoardCreateServiceRequest.builder()
                    .name(name)
                    .boardCategoryId(categoryId)
                    .description(description)
                    .priority(priority)
                    .build());
            redirectAttributes.addFlashAttribute("message", "게시판이 성공적으로 생성되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "게시판 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/boards";
    }

    @GetMapping("/boards/{id}/edit")
    public String boardEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.findBoardAdminInfoBy(id));
        model.addAttribute("categories", boardCategoryService.findAll());
        model.addAttribute("active", "boards");
        return "admin/boards/edit";
    }

    @PostMapping("/boards/{id}")
    public String updateBoard(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam String description,
                              @RequestParam Long categoryId,
                              @RequestParam Integer priority,
                              RedirectAttributes redirectAttributes) {
        try {
            boardService.updateBoard(BoardUpdateServiceRequest.builder()
                    .boardId(id)
                    .name(name)
                    .description(description)
                    .boardCategoryId(categoryId)
                    .priority(priority)
                    .build());
            redirectAttributes.addFlashAttribute("message", "게시판이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "게시판 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/boards";
    }

    @PostMapping("/boards/{id}/delete")
    public String deleteBoard(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boardService.deleteBoard(id);
            redirectAttributes.addFlashAttribute("message", "게시판이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "게시판 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/boards";
    }

    @GetMapping("/members")
    public String memberList(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) MemberGrade grade,
                             Model model) {

        model.addAttribute("members", getMembers(keyword, grade, PageRequest.of(page, size)));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedGrade", grade);
        model.addAttribute("grades", MemberGrade.values());
        model.addAttribute("active", "members");
        return "admin/members/list";
    }

    @PostMapping("/members/{id}/grade")
    public String updateMemberGrade(@PathVariable Long id,
                                    @RequestParam MemberGrade grade,
                                    RedirectAttributes redirectAttributes) {
        try {
            memberService.editMemberGrade(id, grade);
            redirectAttributes.addFlashAttribute("message", "회원 등급이 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "회원 등급 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/members";
    }

    private Page<MemberResponse> getMembers(String keyword, MemberGrade grade, Pageable pageable) {
        if ((keyword != null && !keyword.trim().isEmpty()) || grade != null) {
            return memberService.searchMembers(keyword, grade, pageable);
        }
        return memberService.getAllMembersPaged(pageable);
    }
}