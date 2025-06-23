package darak.community.controller.community;

import darak.community.domain.Attachment;
import darak.community.domain.Board;
import darak.community.domain.BoardCategory;
import darak.community.domain.Post;
import darak.community.domain.member.Member;
import darak.community.dto.GifticonDto;
import darak.community.service.BoardCategoryService;
import darak.community.service.BoardFavoriteService;
import darak.community.service.BoardService;
import darak.community.service.GifticonService;
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
    private final GifticonService gifticonService;

    @ModelAttribute
    public void addAttributes(@Login Member member, Model model) {
        addBoardInformation(model);
        model.addAttribute("member", member);
    }

    @GetMapping("/community")
    public String communityHome(@Login Member member, Model model) {

        model.addAttribute("boardFavorites", boardFavoriteService.findByMemberId(member.getId()));

        List<BoardCategory> boardCategories = boardCategoryService.findAll();
        model.addAttribute("allBoardCategories", boardCategories);

        Map<String, Object> recentPostsData = getRecentPostMap();
        model.addAttribute("recentPostsData", recentPostsData);

        List<GifticonDto.Response> recentGifticons =
                GifticonDto.Response.from(gifticonService.getActiveGifticons().stream().limit(3).toList());
        model.addAttribute("recentGifticons", recentGifticons);

        List<Attachment> galleryImages = postService.findRecentGalleryImages(8);
        model.addAttribute("galleryImages", galleryImages);

        return "community/communityHome";
    }

    private Map<String, Object> getRecentPostMap() {
        Map<String, Object> recentPostsData = new HashMap<>();
        List<BoardCategory> categories = boardCategoryService.findAll();

        for (BoardCategory category : categories) {
            Board topPriorityBoard = boardService.findTopPriorityBoardByCategory(category.getId());

            if (topPriorityBoard != null) {
                List<Post> recentPosts = postService.findRecentPostsByBoardId(topPriorityBoard.getId(), 4);

                Map<String, Object> categoryData = new HashMap<>();
                categoryData.put("category", category);
                categoryData.put("board", topPriorityBoard);
                categoryData.put("posts", recentPosts);

                recentPostsData.put(category.getName().toLowerCase(), categoryData);
            }
        }

        return recentPostsData;
    }

    private void addBoardInformation(Model model) {
        model.addAttribute("boardCategories", boardCategoryService.findAll());
    }
}
