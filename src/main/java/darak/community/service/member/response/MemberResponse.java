package darak.community.service.member.response;

import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponse {

    private Long id;

    private String loginId;

    private String name;

    private MemberGrade memberGrade;

    private String phone;

    private LocalDate birth;

    private String email;

    private LocalDateTime createdDate;

    @Builder
    private MemberResponse(Long id, String loginId, String name, MemberGrade memberGrade, String phone,
                           LocalDate birth, String email, LocalDateTime createdDate) {
        this.id = id;
        this.loginId = loginId;
        this.name = name;
        this.memberGrade = memberGrade;
        this.phone = phone;
        this.birth = birth;
        this.email = email;
        this.createdDate = createdDate;
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .name(member.getName())
                .memberGrade(member.getMemberGrade())
                .phone(member.getPhone())
                .birth(member.getBirth())
                .email(member.getEmail())
                .createdDate(member.getCreatedDate())
                .build();
    }
}
