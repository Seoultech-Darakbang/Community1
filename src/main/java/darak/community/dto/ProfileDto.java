package darak.community.dto;

import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ProfileDto {
    private Long id;
    private String loginId;
    private String name;
    private String email;
    private String phone;
    private LocalDate birth;
    private MemberGrade memberGrade;
    private LocalDateTime createdDate;
    
    // 활동 통계
    private long postCount;
    private long commentCount;
    private long receivedLikeCount;

    public ProfileDto(Member member, long postCount, long commentCount, long receivedLikeCount) {
        this.id = member.getId();
        this.loginId = member.getLoginId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.phone = member.getPhone();
        this.birth = member.getBirth();
        this.memberGrade = member.getMemberGrade();
        this.createdDate = member.getCreatedDate();
        this.postCount = postCount;
        this.commentCount = commentCount;
        this.receivedLikeCount = receivedLikeCount;
    }
} 