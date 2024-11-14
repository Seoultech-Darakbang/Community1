package darak.community.domain.member;

import darak.community.domain.BaseEntity;
import darak.community.dto.MemberUpdateDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Embedded
    private MemberPassword password;

    @NotEmpty
    private String name;

    @Enumerated(EnumType.STRING)
    private MemberGrade memberGrade;

    private String phone;

    private LocalDate birth;

    // @Email
    private String email;

    // Builder 패턴의 단점 : 필수값을 놓칠 수 있음
    // -> NonNull을 붙여준다.
    @Builder
    public Member(String name, String password, String phone, LocalDate birth, String email) {
        this.name = name;
        this.password = new MemberPassword(password);
        this.phone = phone;
        this.birth = birth;
        this.email = email;
    }

    // 회원 정보 수정 메서드
    public void updateMember(MemberUpdateDTO memberUpdateDTO) {
        this.name = memberUpdateDTO.getName();
        this.password = new MemberPassword(memberUpdateDTO.getPassword());
        this.email = memberUpdateDTO.getEmail();
        this.phone = memberUpdateDTO.getPhone();
        this.birth = memberUpdateDTO.getBirth();
    }

    public boolean isMatchedPassword(final String rawPassword) {
        return password.isMatched(rawPassword);
    }

    public void changePassword(final String newPassword, final String oldPassword) {
        password.changePassword(newPassword, oldPassword);
    }

    public boolean isMatchedPhone(String phone) {
        return this.phone.equals(phone);
    }

}
