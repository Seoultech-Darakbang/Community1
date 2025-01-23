package darak.community.controller;

import darak.community.dto.LoginForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@ModelAttribute("loginForm") LoginForm form, Model model) {
        return "login/loginForm";
    }
}
