package darak.community.core.session.dto;

import darak.community.domain.member.Member;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class LoginMember {
    
    private final Long id;

    private LoginMember(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("회원 ID는 null일 수 없습니다.");
        }
        this.id = id;
    }

    public static LoginMember of(Long id) {
        return new LoginMember(id);
    }

    public static LoginMember from(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member는 null일 수 없습니다.");
        }
        return new LoginMember(member.getId());
    }

    public boolean isPresent() {
        return id != null;
    }

    @Override
    public String toString() {
        return "LoginMember{id=" + id + "}";
    }
} 