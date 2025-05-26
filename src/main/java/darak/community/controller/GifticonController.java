package darak.community.controller;

import darak.community.domain.GifticonClaim;
import darak.community.domain.member.Member;
import darak.community.dto.GifticonDto;
import darak.community.service.GifticonService;
import darak.community.web.argumentresolver.Login;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/community/gifticons")
@RequiredArgsConstructor
public class GifticonController {

    private final GifticonService gifticonService;

    @GetMapping
    public String gifticonList(@Login Member member, Model model, HttpSession session) {
        List<GifticonDto.Response> activeGifticons =
                GifticonDto.Response.from(gifticonService.getActiveGifticons());

        model.addAttribute("gifticons", activeGifticons);
        model.addAttribute("member", member);
        List<GifticonDto.ClaimResponse> myClaims =
                GifticonDto.ClaimResponse.from(gifticonService.getMemberClaims(member));
        model.addAttribute("myClaims", myClaims);

        return "gifticon/list";
    }

    @GetMapping("/{id}")
    public String gifticonDetail(@Login Member member, @PathVariable Long id, Model model, HttpSession session) {
        GifticonDto.Response gifticon =
                GifticonDto.Response.from(gifticonService.getGifticon(id));
        model.addAttribute("gifticon", gifticon);
        model.addAttribute("member", member);

        return "gifticon/detail";
    }

    @PostMapping("/{id}/claim")
    @ResponseBody
    public ResponseEntity<?> claimGifticon(@Login Member member, @PathVariable Long id) {

        try {
            GifticonClaim claim = gifticonService.claimGifticon(id, member);
            return ResponseEntity.ok(GifticonDto.ClaimResponse.from(claim));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("기프티콘 수령 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/my")
    public String myGifticons(@Login Member member, Model model) {
        
        List<GifticonDto.ClaimResponse> myClaims =
                GifticonDto.ClaimResponse.from(gifticonService.getMemberClaims(member));

        model.addAttribute("myClaims", myClaims);
        model.addAttribute("member", member);
        return "gifticon/my";
    }

    @PostMapping("/use")
    @ResponseBody
    public ResponseEntity<?> useGifticon(@Login Member member, @RequestParam String gifticonCode) {
        if (member == null) {
            return ResponseEntity.badRequest().body("로그인이 필요합니다.");
        }

        try {
            gifticonService.useGifticon(gifticonCode, member);
            return ResponseEntity.ok("기프티콘이 사용되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("기프티콘 사용 중 오류가 발생했습니다.");
        }
    }
} 