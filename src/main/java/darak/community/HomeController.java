package darak.community;

import darak.community.domain.member.Member;
import darak.community.web.argumentresolver.Login;
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
