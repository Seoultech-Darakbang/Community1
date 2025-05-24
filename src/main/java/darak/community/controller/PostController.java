package darak.community.controller;

import darak.community.domain.Board;
import darak.community.domain.BoardCategory;
import darak.community.domain.Comment;
import darak.community.domain.Post;
import darak.community.domain.member.Member;
import darak.community.dto.PostCURequestForm;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                                @PathVariable Long boardId, @ModelAttribute PostCURequestForm form,
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

    @PostMapping("/community/boards/{boardId}/posts")
    public String writePost(@Login Member member, @PathVariable Long boardId,
                            @ModelAttribute @Validated PostCURequestForm form, BindingResult bindingResult,
                            Model model) {
        if (member == null) {
            return "login/loginForm";
        }

        Board board = boardService.findBoardAndCategoryWithBoardId(boardId);
        if (board == null || board.getBoardCategory() == null) {
            return "redirect:/error/404";
        }
        addSideMenuInformation(model, board.getBoardCategory(), board, board.getBoardCategory().getBoards());

        if (bindingResult.hasErrors()) {
            return "community/post/createPostForm";
        }

        Post post = Post.builder()
                .anonymous(form.getAnonymous())
                .postType(form.getPostType())
                .title(form.getTitle())
                .content(form.getContent())
                .member(member)
                .board(board)
                .build();
        postService.save(post);

        return "redirect:/community/boards/" + boardId + "/posts/" + post.getId();
    }

    @DeleteMapping("/community/boards/{boardId}/posts/{postId}")
    public String deletePost(@Login Member member,
                             @PathVariable Long boardId,
                             @PathVariable Long postId,
                             RedirectAttributes attributes,
                             Model model) {
        if (member == null) {
            return "login/loginForm";
        }

        Board board = boardService.findBoardAndCategoryWithBoardId(boardId);
        if (board == null || board.getBoardCategory() == null) {
            return "redirect:/error/404";
        }
        addSideMenuInformation(model, board.getBoardCategory(), board, board.getBoardCategory().getBoards());

        postService.deleteById(postId, member);
        attributes.addAttribute("deleteStatus", true);
        return "redirect:/community/boards/" + boardId;
    }

    private void addBoardInformation(Model model) {
        model.addAttribute("boardCategories", boardCategoryService.findAll());
    }

    @GetMapping("/community/boards/{boardId}/posts/{postId}/edit")
    public String editPostForm(@Login Member member,
                               @PathVariable Long boardId,
                               @PathVariable Long postId,
                               @ModelAttribute PostCURequestForm form,
                               Model model) {
        if (member == null) {
            return "login/loginForm";
        }

        Board board = boardService.findBoardAndCategoryWithBoardId(boardId);
        Post post = postService.findById(postId);

        if (board == null || board.getBoardCategory() == null) {
            return "redirect:/error/404";
        }

        addSideMenuInformation(model, board.getBoardCategory(), board, board.getBoardCategory().getBoards());
        model.addAttribute("form", new PostCURequestForm(post));
        return "community/post/editPostForm";
    }

    @PatchMapping("/community/boards/{boardId}/posts/{postId}/edit")
    public String editPost(@Login Member member, @PathVariable Long boardId, @PathVariable Long postId,
                           @ModelAttribute @Validated PostCURequestForm form, BindingResult bindingResult,
                           RedirectAttributes attributes, Model model) {
        if (member == null) {
            return "login/loginForm";
        }

        Board board = boardService.findBoardAndCategoryWithBoardId(boardId);
        if (board == null || board.getBoardCategory() == null) {
            return "redirect:/error/404";
        }
        addSideMenuInformation(model, board.getBoardCategory(), board, board.getBoardCategory().getBoards());

        if (bindingResult.hasErrors()) {
            return "community/post/editPostForm";
        }

        postService.editPost(member, postId, form.getTitle(), form.getPostType(), form.getContent(),
                form.getAnonymous());

        attributes.addAttribute("editStatus", true);
        return "redirect:/community/boards/" + boardId + "/posts/" + postId;
    }

}
