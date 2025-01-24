package darak.community.domain.member;

import darak.community.domain.BaseEntity;
import darak.community.exception.PasswordFailedExceededException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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

    private String loginId;

    @Embedded
    private MemberPassword password;

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
    public Member(String loginId, String name, String password, String phone, LocalDate birth, String email) {
        this.loginId = loginId;
        this.name = name;
        this.password = new MemberPassword(password);
        this.phone = phone;
        this.birth = birth;
        this.email = email;
    }

    // 회원 정보 수정 메서드
    public void updateMember(Member editInfoMember) {
        updatePassword(editInfoMember);
        updateName(editInfoMember);
        updatePhone(editInfoMember);
        updateBirth(editInfoMember);
        updateEmail(editInfoMember);
    }

    private void updateEmail(Member editInfoMember) {
        if (editInfoMember.email != null) {
            this.email = editInfoMember.email;
        }
    }

    private void updateBirth(Member editInfoMember) {
        if (editInfoMember.birth != null) {
            this.birth = editInfoMember.birth;
        }
    }

    private void updatePhone(Member editInfoMember) {
        if (editInfoMember.phone != null) {
            this.phone = editInfoMember.phone;
        }
    }

    private void updateName(Member editInfoMember) {
        if (editInfoMember.name != null) {
            this.name = editInfoMember.name;
        }
    }

    private void updatePassword(Member editInfoMember) {
        if (editInfoMember.password != null) {
            this.password = editInfoMember.password;
        }
    }

    public boolean isMatchedPassword(final String rawPassword) throws PasswordFailedExceededException {
        return password.isMatched(rawPassword);
    }

    public void changePassword(final String newPassword, final String oldPassword)
            throws PasswordFailedExceededException {
        password.changePassword(newPassword, oldPassword);
    }

    public boolean isMatchedPhone(String phone) {
        return this.phone.equals(phone);
    }

    public boolean isPasswordExpired() {
        return password.isPasswordExpired();
    }

}
