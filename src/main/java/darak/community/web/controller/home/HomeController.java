package darak.community.web.controller.home;

import darak.community.core.argumentresolver.Login;
import darak.community.domain.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@Login Member member, Model model) {
        model.addAttribute("member", member);
        return "home";
    }
}
