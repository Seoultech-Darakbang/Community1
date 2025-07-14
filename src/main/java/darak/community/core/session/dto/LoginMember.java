package darak.community.core.session.dto;

import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class LoginMember {

    private final Long id;
    private final MemberGrade memberGrade;

    private LoginMember(Long id, MemberGrade memberGrade) {
        if (id == null) {
            throw new IllegalArgumentException("회원 ID는 null일 수 없습니다.");
        }

        if (memberGrade == null) {
            throw new IllegalArgumentException("회원 ID는 null일 수 없습니다.");
        }
        this.id = id;
        this.memberGrade = memberGrade;
    }

    public static LoginMember of(Long id, MemberGrade memberGrade) {
        return new LoginMember(id, memberGrade);
    }

    public static LoginMember from(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member는 null일 수 없습니다.");
        }
        return new LoginMember(member.getId(), member.getMemberGrade());
    }

    @Override
    public String toString() {
        return "LoginMember{" +
                "id=" + id +
                ", memberGrade=" + memberGrade +
                '}';
    }
}