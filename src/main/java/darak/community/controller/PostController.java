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
import org.springframework.web.bind.annotation.PostMapping;

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

    @GetMapping("/community/{categoryName}/{boardName}/{postId}")
    public String viewPost(@Login Member member,
                           @PathVariable String categoryName,
                           @PathVariable String boardName,
                           @PathVariable Long postId,
                           Model model) {
        if (member == null) {
            return "login/loginForm";
        }

        BoardCategory category = boardCategoryService.findByName(categoryName);
        Board board = boardService.findByName(boardName);
        Post post = postService.findById(postId);

        if (category == null || board == null || post == null) {
            return "redirect:/error/404";
        }

        // 조회수 증가
        postService.increaseReadCount(postId);

        model.addAttribute("category", category);
        model.addAttribute("board", board);
        model.addAttribute("post", post);
        model.addAttribute("member", member);
        model.addAttribute("boardType", categoryName);
        model.addAttribute("activeBoard", boardName);

        model.addAttribute("boards", boardService.findBoardsByCategory(categoryName));

        List<Comment> comments = commentService.findByPostId(postId);
        model.addAttribute("comments", comments);

        boolean isLiked = postHeartService.isLiked(postId, member.getId());
        model.addAttribute("isLiked", isLiked);

        return "community/post/viewPost";
    }

    @GetMapping("/community/{categoryName}/{boardName}/posts")
    public String writePostForm(@Login Member member,
                                @PathVariable String categoryName,
                                @PathVariable String boardName,
                                Model model) {
        if (member == null) {
            return "login/loginForm";
        }

        BoardCategory category = boardCategoryService.findByName(categoryName);
        Board board = boardService.findByName(boardName);

        if (category == null || board == null) {
            return "redirect:/error/404";
        }

        model.addAttribute("category", category);
        model.addAttribute("board", board);
        model.addAttribute("boardType", categoryName);
        model.addAttribute("activeBoard", boardName);

        // 추가: 사이드바에 표시할 게시판 목록 전달
        model.addAttribute("boards", boardService.findBoardsByCategory(categoryName));

        return "community/post/createPostForm";
    }

    @PostMapping("/community/{categoryName}/{boardName}/posts")
    public String writePost(@Login Member member,
                            @PathVariable String categoryName,
                            @PathVariable String boardName,
                            Model model) {
        if (member == null) {
            return "login/loginForm";
        }

        BoardCategory category = boardCategoryService.findByName(categoryName);
        Board board = boardService.findByName(boardName);

        if (category == null || board == null) {
            return "redirect:/error/404";
        }

        model.addAttribute("category", category);
        model.addAttribute("board", board);
        model.addAttribute("boardType", categoryName);
        model.addAttribute("activeBoard", boardName);

        // 추가: 사이드바에 표시할 게시판 목록 전달
        model.addAttribute("boards", boardService.findBoardsByCategory(categoryName));

        return "community/post/createPostForm";
    }

    private void addBoardInformation(Model model) {
        model.addAttribute("boardCategories", boardCategoryService.findAll());
    }
}
