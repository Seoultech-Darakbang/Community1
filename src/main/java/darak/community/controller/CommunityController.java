package darak.community.controller;

import darak.community.domain.member.Member;
import darak.community.service.BoardCategoryService;
import darak.community.service.BoardService;
import darak.community.web.argumentresolver.Login;
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
    private final BoardCategoryService boardCategoryService;

    @GetMapping("/community")
    public String communityHome(@Login Member member, Model model) {
        if (member == null) {
            return "login/loginForm";
        }
        model.addAttribute("member", member);

        addBoardInformation(model);

        return "community/communityHome";
    }

    private void addBoardInformation(Model model) {
        model.addAttribute("boardCategories", boardCategoryService.findAll());
    }
}
