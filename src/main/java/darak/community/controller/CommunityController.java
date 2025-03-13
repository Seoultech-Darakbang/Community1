package darak.community.controller;

import darak.community.domain.Attachment;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommunityController {

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

    @GetMapping("/community")
    public String communityHome(@Login Member member, Model model) {
        if (member == null) {
            return "login/loginForm";
        }

        model.addAttribute("boardFavorites", boardFavoriteService.findByMemberId(member.getId()));

        // 최근 게시글 가져오기
        Map<String, List<Post>> recentPostsByCategory = getRecentPostMap();
        model.addAttribute("recentPostsByCategory", recentPostsByCategory);

        // 갤러리 이미지 가져오기
        List<Attachment> galleryImages = postService.findRecentGalleryImages(4);
        model.addAttribute("galleryImages", galleryImages);

        return "community/communityHome";
    }

    @GetMapping("/community/{categoryName}")
    public String redirectFirstBoard(@Login Member member, @PathVariable String categoryName, Model model) {
        if (member == null) {
            return "login/loginForm";
        }
        BoardCategory category = boardCategoryService.findByName(categoryName);
        if (category == null) {
            return "redirect:/error/404";
        }
        return "redirect:/community/" + categoryName + "/" + category.getFirstBoardName();
    }

    @GetMapping("/community/{categoryName}/{boardName}")
    public String board(@Login Member member,
                        @PathVariable String categoryName,
                        @PathVariable String boardName,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
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

        // 페이지네이션 처리
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdDate").descending());
        Page<Post> postPage = postService.findByBoardIdPaged(board.getId(), pageable);

        model.addAttribute("posts", postPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("startPage", Math.max(1, page - 2));
        model.addAttribute("endPage", Math.min(postPage.getTotalPages(), page + 2));

        return "community/board/board";
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

    @GetMapping("/community/{categoryName}/{boardName}/write")
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


    private Map<String, List<Post>> getRecentPostMap() {
        Map<String, List<Post>> recentPostsByCategory = new HashMap<>();
        List<BoardCategory> categories = boardCategoryService.findAll();
        for (BoardCategory category : categories) {
            String categoryName = category.getName().toLowerCase();
            List<Post> recentPosts = postService.findRecentPostsByCategory(category.getId(), 3);
            recentPostsByCategory.put(categoryName, recentPosts);
        }
        return recentPostsByCategory;
    }

    private void addBoardInformation(Model model) {
        model.addAttribute("boardCategories", boardCategoryService.findAll());
    }
}
