package darak.community.controller;

import darak.community.domain.Attachment;
import darak.community.domain.Board;
import darak.community.domain.BoardCategory;
import darak.community.domain.Post;
import darak.community.domain.member.Member;
import darak.community.service.AttachmentService;
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
    private final CommentService commentService;
    private final PostHeartService postHeartService;
    private final AttachmentService attachmentService;

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

        List<BoardCategory> boardCategories = boardCategoryService.findAll();
        model.addAttribute("allBoardCategories", boardCategories);

        Map<String, Object> recentPostsData = getRecentPostMap();
        model.addAttribute("recentPostsData", recentPostsData);

        // 디버깅 정보 출력
        log.info("=== 갤러리 디버깅 정보 ===");
        log.info("갤러리 게시판 수: {}", postService.countGalleryBoards());
        log.info("전체 첨부파일 수: {}", postService.countAttachments());
        log.info("갤러리 이미지 첨부파일 수: {}", postService.countGalleryAttachments());
        
        // 갤러리 이미지 조회
        List<Attachment> galleryImages = postService.findRecentGalleryImages(8);
        log.info("조회된 갤러리 이미지 수: {} 개", galleryImages.size());
        
        if (!galleryImages.isEmpty()) {
            log.info("첫 번째 갤러리 이미지 URL: {}", galleryImages.get(0).getUrl());
            log.info("첫 번째 갤러리 이미지 파일타입: {}", galleryImages.get(0).getFileType());
        }
        
        model.addAttribute("galleryImages", galleryImages);

        return "community/communityHome";
    }

    private Map<String, Object> getRecentPostMap() {
        Map<String, Object> recentPostsData = new HashMap<>();
        List<BoardCategory> categories = boardCategoryService.findAll();

        for (BoardCategory category : categories) {
            // 각 카테고리별로 우선순위 가장 높은 게시판 찾기
            Board topPriorityBoard = boardService.findTopPriorityBoardByCategory(category.getId());

            if (topPriorityBoard != null) {
                // 해당 게시판의 최근 게시글 4개 조회
                List<Post> recentPosts = postService.findRecentPostsByBoardId(topPriorityBoard.getId(), 4);

                // 카테고리 정보와 게시판 정보, 게시글 목록을 함께 저장
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
