package darak.community.web.controller.community;

import darak.community.core.argumentresolver.Login;
import darak.community.core.session.dto.LoginMember;
import darak.community.service.board.BoardFavoriteService;
import darak.community.service.board.BoardService;
import darak.community.service.event.gifticon.GifticonService;
import darak.community.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommunityController {

    private final BoardService boardService;
    private final BoardFavoriteService boardFavoriteService;
    private final PostService postService;
    private final GifticonService gifticonService;


    @GetMapping("/community")
    public String communityHome(@Login LoginMember loginMember, Model model) {

        model.addAttribute("favoriteBoards", boardFavoriteService.findFavoriteBoardsBy(loginMember.getId()));
        model.addAttribute("recentPostsData", boardService.findRecentPostsGroupedByBoardLimit(
                2));
        model.addAttribute("recentGifticons", gifticonService.findActiveGifticonsLimit(3));
        model.addAttribute("galleryImages", postService.findRecentGalleryImages(8));

        return "community/communityHome";
    }

}
