package darak.community.web.api.event;

import darak.community.core.argumentresolver.Login;
import darak.community.core.session.dto.LoginMember;
import darak.community.dto.ApiResponse;
import darak.community.service.event.gifticon.GifticonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/gifticons")
public class GifticonApiController {

    private final GifticonService gifticonService;

    @PostMapping("/{id}/claim")
    public ApiResponse<?> claimGifticon(@Login LoginMember loginMember, @PathVariable("id") Long gifticonId) {
        return ApiResponse.success(gifticonService.claimGifticon(gifticonId, loginMember.getId()));
    }

    @PostMapping("/use")
    public ApiResponse<?> useGifticon(@Login LoginMember loginMember, @RequestParam String gifticonCode) {
        gifticonService.useGifticon(gifticonCode, loginMember.getId());
        return ApiResponse.successWithNoData("기프티콘 사용이 완료되었습니다.");
    }
}
