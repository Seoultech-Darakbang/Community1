package darak.community.web.controller.admin;

import darak.community.core.argumentresolver.Login;
import darak.community.domain.board.Board;
import darak.community.domain.board.BoardCategory;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.service.admin.AdminService;
import darak.community.service.board.BoardCategoryService;
import darak.community.service.board.BoardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;
    private final BoardCategoryService boardCategoryService;
    private final BoardService boardService;

    @ModelAttribute
    public void checkAdminAccess(@Login Member member, Model model) {
        if (member.getMemberGrade() != MemberGrade.ADMIN
                && member.getMemberGrade() != MemberGrade.MASTER) {
            throw new IllegalAccessError("관리자만 접근 가능합니다.");
        }
        model.addAttribute("member", member);
    }

    @GetMapping
    public String adminHome(Model model) {
        long totalMembers = adminService.getTotalMemberCount();
        long totalPosts = adminService.getTotalPostCount();
        long totalComments = adminService.getTotalCommentCount();
        long totalCategories = adminService.getTotalCategoryCount();
        long totalBoards = adminService.getTotalBoardCount();

        model.addAttribute("totalMembers", totalMembers);
        model.addAttribute("totalPosts", totalPosts);
        model.addAttribute("totalComments", totalComments);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("totalBoards", totalBoards);
        model.addAttribute("active", "dashboard");

        return "admin/dashboard";
    }

    @GetMapping("/categories")
    public String categoryList(Model model) {
        List<BoardCategory> categories = boardCategoryService.findAll();
        model.addAttribute("categories", categories);
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
            adminService.createCategory(name, priority);
            redirectAttributes.addFlashAttribute("message", "카테고리가 성공적으로 생성되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "카테고리 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/{id}/edit")
    public String categoryEditForm(@PathVariable Long id, Model model) {
        BoardCategory category = boardCategoryService.findById(id);
        model.addAttribute("category", category);
        model.addAttribute("active", "categories");
        return "admin/categories/edit";
    }

    @PostMapping("/categories/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @RequestParam String name,
                                 @RequestParam Integer priority,
                                 RedirectAttributes redirectAttributes) {
        try {
            adminService.updateCategory(id, name, priority);
            redirectAttributes.addFlashAttribute("message", "카테고리가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "카테고리 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("message", "카테고리가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "카테고리 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // ===== 게시판 관리 =====
    @GetMapping("/boards")
    public String boardList(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(required = false) Long categoryId,
                            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boards;

        if (categoryId != null) {
            boards = adminService.getBoardsByCategoryPaged(categoryId, pageable);
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            boards = adminService.getAllBoardsPaged(pageable);
        }

        List<BoardCategory> categories = boardCategoryService.findAll();

        model.addAttribute("boards", boards);
        model.addAttribute("categories", categories);
        model.addAttribute("active", "boards");
        return "admin/boards/list";
    }

    @GetMapping("/boards/new")
    public String boardCreateForm(Model model) {
        List<BoardCategory> categories = boardCategoryService.findAll();
        model.addAttribute("categories", categories);
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
            adminService.createBoard(name, description, categoryId, priority);
            redirectAttributes.addFlashAttribute("message", "게시판이 성공적으로 생성되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "게시판 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/boards";
    }

    @GetMapping("/boards/{id}/edit")
    public String boardEditForm(@PathVariable Long id, Model model) {
        Board board = boardService.findById(id);
        List<BoardCategory> categories = boardCategoryService.findAll();
        model.addAttribute("board", board);
        model.addAttribute("categories", categories);
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
            adminService.updateBoard(id, name, description, categoryId, priority);
            redirectAttributes.addFlashAttribute("message", "게시판이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "게시판 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/boards";
    }

    @PostMapping("/boards/{id}/delete")
    public String deleteBoard(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteBoard(id);
            redirectAttributes.addFlashAttribute("message", "게시판이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "게시판 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/boards";
    }

    // ===== 회원 관리 =====
    @GetMapping("/members")
    public String memberList(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) MemberGrade grade,
                             Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Member> members = getMembers(keyword, grade, pageable);

        model.addAttribute("members", members);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedGrade", grade);
        model.addAttribute("grades", MemberGrade.values());
        model.addAttribute("active", "members");
        return "admin/members/list";
    }

    private Page<Member> getMembers(String keyword, MemberGrade grade, Pageable pageable) {
        if ((keyword != null && !keyword.trim().isEmpty()) || grade != null) {
            return adminService.searchMembers(keyword, grade, pageable);
        }
        return adminService.getAllMembersPaged(pageable);
    }

    @PostMapping("/members/{id}/grade")
    public String updateMemberGrade(@PathVariable Long id,
                                    @RequestParam MemberGrade grade,
                                    RedirectAttributes redirectAttributes) {
        try {
            adminService.updateMemberGrade(id, grade);
            redirectAttributes.addFlashAttribute("message", "회원 등급이 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "회원 등급 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/members";
    }
} 