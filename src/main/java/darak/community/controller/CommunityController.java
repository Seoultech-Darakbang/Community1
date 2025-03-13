package darak.community.controller;

import darak.community.domain.member.Member;
import darak.community.service.BoardCategoryService;
import darak.community.service.BoardFavoriteService;
import darak.community.service.BoardService;
import darak.community.web.argumentresolver.Login;
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
        
        return "community/communityHome";
    }

    private void addBoardInformation(Model model) {
        model.addAttribute("boardCategories", boardCategoryService.findAll());
    }
}
