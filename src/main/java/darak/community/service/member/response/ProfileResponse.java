package darak.community.service.member.response;

import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProfileResponse {

    private Long id;
    private String loginId;
    private String name;
    private String email;
    private String phone;
    private LocalDate birth;
    private MemberGrade memberGrade;
    private LocalDateTime createdDate;

    @Builder
    private ProfileResponse(Long id, String loginId, String name, String email, String phone, LocalDate birth,
                            MemberGrade memberGrade, LocalDateTime createdDate) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birth = birth;
        this.memberGrade = memberGrade;
        this.createdDate = createdDate;
    }

    public static ProfileResponse of(Member member) {
        return ProfileResponse.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .birth(member.getBirth())
                .memberGrade(member.getMemberGrade())
                .createdDate(member.getCreatedDate())
                .build();
    }
}
