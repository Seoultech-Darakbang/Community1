package darak.community.controller;

import darak.community.domain.Board;
import darak.community.domain.BoardCategory;
import darak.community.domain.Comment;
import darak.community.domain.Post;
import darak.community.domain.member.Member;
import darak.community.service.BoardCategoryService;
import darak.community.service.BoardFavoriteService;
import darak.community.service.BoardService;
import darak.community.service.CommentService;
import darak.community.service.PostHeartService;
import darak.community.service.PostService;
import darak.community.web.argumentresolver.Login;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final BoardService boardService;
    private final BoardCategoryService boardCategoryService;
    private final BoardFavoriteService boardFavoriteService;
    private final PostService postService;
    private final CommentService commentService;
    private final PostHeartService postHeartService;

    @ModelAttribute
    public void addAttributes(@Login Member member, Model model) {
        addBoardInformation(model);
        model.addAttribute("member", member);
    }

    @GetMapping("/community/boards/{boardId}/posts/{postId}")
    public String viewPost(@Login Member member,
                           @PathVariable Long boardId,
                           @PathVariable Long postId,
                           Model model) {
        if (member == null) {
            return "login/loginForm";
        }

        Board board = boardService.findBoardAndCategoryWithBoardId(boardId);
        Post post = postService.findById(postId);

        if (board == null || board.getBoardCategory() == null || post == null) {
            return "redirect:/error/404";
        }

        // 조회수 증가
        postService.increaseReadCount(postId);

        model.addAttribute("post", post);
        addSideMenuInformation(model, board.getBoardCategory(), board, board.getBoardCategory().getBoards());

        List<Comment> comments = commentService.findByPostId(postId);
        model.addAttribute("comments", comments);

        boolean isLiked = postHeartService.isLiked(postId, member.getId());
        model.addAttribute("isLiked", isLiked);

        return "community/post/viewPost";
    }

    private void addSideMenuInformation(Model model, BoardCategory category, Board board, List<Board> boards) {
        model.addAttribute("category", category);
        model.addAttribute("activeBoard", board);
        model.addAttribute("boards", boards);
    }

    @GetMapping("/community/boards/{boardId}/posts")
    public String writePostForm(@Login Member member,
                                @PathVariable Long boardId,
                                Model model) {
        if (member == null) {
            return "login/loginForm";
        }

        Board board = boardService.findBoardAndCategoryWithBoardId(boardId);

        if (board == null || board.getBoardCategory() == null) {
            return "redirect:/error/404";
        }

        addSideMenuInformation(model, board.getBoardCategory(), board, board.getBoardCategory().getBoards());
        return "community/post/createPostForm";
    }

    private void addBoardInformation(Model model) {
        model.addAttribute("boardCategories", boardCategoryService.findAll());
    }
}
