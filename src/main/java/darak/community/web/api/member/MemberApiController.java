package darak.community.web.api.member;

import darak.community.service.member.MemberService;
import darak.community.web.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members/new/confirmLoginId")
    @ResponseBody
    public ApiResponse<?> checkDuplicatedLoginId(@RequestParam String loginId) {

        log.info("아이디 중복 확인 요청: {}", loginId);
        
        if (loginId.isEmpty()) {
            return ApiResponse.error("아이디를 입력해주세요");
        }

        if (loginId.length() < 4 || loginId.length() > 20) {
            return ApiResponse.error("아이디는 4자 이상, 20자 이하입니다");
        }

        memberService.validateDuplicateMember(loginId);
        return ApiResponse.successWithNoData("사용 가능한 회원 ID 입니다.");
    }
}
