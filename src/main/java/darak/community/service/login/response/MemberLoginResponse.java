package darak.community.service.login.response;

import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberLoginResponse {

    private Long memberId;
    private String loginId;
    private String nickname;
    private MemberGrade memberGrade;

    @Builder
    private MemberLoginResponse(Long memberId, String loginId, String nickname, MemberGrade memberGrade) {
        this.memberId = memberId;
        this.loginId = loginId;
        this.nickname = nickname;
        this.memberGrade = memberGrade;
    }

    public static MemberLoginResponse from(Member member) {
        return MemberLoginResponse.builder()
                .memberId(member.getId())
                .loginId(member.getLoginId())
                .nickname(member.getName())
                .memberGrade(member.getMemberGrade())
                .build();
    }
}
