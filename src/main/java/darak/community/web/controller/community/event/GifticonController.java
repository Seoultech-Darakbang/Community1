package darak.community.web.controller.community.event;

import darak.community.core.argumentresolver.Login;
import darak.community.core.session.dto.LoginMember;
import darak.community.service.event.gifticon.GifticonService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/community/gifticons")
@RequiredArgsConstructor
public class GifticonController {

    private final GifticonService gifticonService;

    @GetMapping
    public String gifticonList(@Login LoginMember loginMember, Model model) {
        model.addAttribute("gifticons", gifticonService.findActiveGifticonsAll());
        model.addAttribute("myClaims", gifticonService.getMemberClaims(loginMember.getId()));
        return "gifticon/list";
    }

    @GetMapping("/{id}")
    public String gifticonDetail(@Login LoginMember loginMember, @PathVariable("id") Long gifticonId, Model model,
                                 HttpSession session) {
        model.addAttribute("gifticon", gifticonService.getGifticon(gifticonId));
        return "gifticon/detail";
    }

    @GetMapping("/my")
    public String myGifticons(@Login LoginMember loginMember, Model model) {
        model.addAttribute("myClaims", gifticonService.getMemberClaims(loginMember.getId()));
        return "gifticon/my";
    }

} 