package darak.community.service.login.response;

import darak.community.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberLoginResponse {

    private Long memberId;
    private String loginId;
    private String nickname;

    @Builder
    private MemberLoginResponse(Long memberId, String loginId, String nickname) {
        this.memberId = memberId;
        this.loginId = loginId;
        this.nickname = nickname;
    }

    public static MemberLoginResponse from(Member member) {
        return MemberLoginResponse.builder()
                .memberId(member.getId())
                .loginId(member.getLoginId())
                .nickname(member.getName())
                .build();
    }
}
