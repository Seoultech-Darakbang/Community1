package darak.community.service.member.request;

import darak.community.domain.member.Member;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberJoinServiceRequest {

    private final String loginId;
    private final String name;
    private final String password;
    private final String phone;
    private final LocalDate birth;
    private final String email;

    @Builder
    private MemberJoinServiceRequest(String loginId, String name, String password,
                                     String phone, LocalDate birth, String email) {
        this.loginId = loginId;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.birth = birth;
        this.email = email;
    }

    public Member toEntity() {
        return Member.guestMember(loginId, name, password, phone, birth, email);
    }
} 