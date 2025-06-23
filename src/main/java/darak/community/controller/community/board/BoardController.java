package darak.community.controller.community.board;

import darak.community.domain.Board;
import darak.community.domain.BoardCategory;
import darak.community.domain.Post;
import darak.community.domain.member.Member;
import darak.community.service.BoardCategoryService;
import darak.community.service.BoardService;
import darak.community.service.PostService;
import darak.community.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final BoardCategoryService boardCategoryService;
    private final PostService postService;

    @ModelAttribute
    public void addAttributes(@Login Member member, Model model) {
        addBoardInformation(model);
        model.addAttribute("member", member);
    }

    @GetMapping("/community/categories/{categoryId}")
    public String redirectFirstBoard(@Login Member member, @PathVariable Long categoryId, Model model) {
        BoardCategory category = boardCategoryService.findById(categoryId);
        if (category == null) {
            return "redirect:/error/404";
        }
        return "redirect:/community/boards/" + category.getFirstBoardId();
    }

    @GetMapping("/community/boards/{boardId}")
    public String board(@Login Member member,
                        @PathVariable Long boardId,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {

        Board board = boardService.findBoardAndCategoryWithBoardId(boardId);

        if (board == null || board.getBoardCategory() == null) {
            return "redirect:/error/404";
        }

        model.addAttribute("category", board.getBoardCategory());
        model.addAttribute("activeBoard", board);
        model.addAttribute("boards", board.getBoardCategory().getBoards());

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        Page<Post> postPage = postService.findByBoardIdPaged(board.getId(), pageable);

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("startPage", Math.max(1, page - 2));
        model.addAttribute("endPage", Math.min(postPage.getTotalPages(), page + 2));

        return "community/board/board";
    }


    private void addBoardInformation(Model model) {
        model.addAttribute("boardCategories", boardCategoryService.findAll());
    }
}
