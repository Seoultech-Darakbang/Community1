package darak.community.controller;

import darak.community.domain.member.Member;
import darak.community.service.BoardService;
import darak.community.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommunityController {

    private final BoardService boardService;

    @GetMapping("/community")
    public String communityHome(@Login Member member) {
        if (member == null) {
            return "login/loginForm";
        }
        return "community/communityHome";
    }
}
