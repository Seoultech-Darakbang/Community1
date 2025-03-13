package darak.community.controller;

import darak.community.domain.Attachment;
import darak.community.domain.BoardCategory;
import darak.community.domain.Post;
import darak.community.domain.member.Member;
import darak.community.service.BoardCategoryService;
import darak.community.service.BoardFavoriteService;
import darak.community.service.BoardService;
import darak.community.service.PostService;
import darak.community.web.argumentresolver.Login;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommunityController {

    private final BoardService boardService;
    private final BoardCategoryService boardCategoryService;
    private final BoardFavoriteService boardFavoriteService;
    private final PostService postService;

    @ModelAttribute
    public void addAttributes(Model model) {
        addBoardInformation(model);
    }

    @GetMapping("/community")
    public String communityHome(@Login Member member, Model model) {
        if (member == null) {
            return "login/loginForm";
        }
        model.addAttribute("member", member);

        model.addAttribute("boardFavorites", boardFavoriteService.findByMemberId(member.getId()));

        // 최근 게시글 가져오기
        Map<String, List<Post>> recentPostsByCategory = getRecentPostMap();
        model.addAttribute("recentPostsByCategory", recentPostsByCategory);

        // 갤러리 이미지 가져오기
        List<Attachment> galleryImages = postService.findRecentGalleryImages(4);
        model.addAttribute("galleryImages", galleryImages);

        return "community/communityHome";
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
