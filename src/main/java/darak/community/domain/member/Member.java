package darak.community.domain.member;

import darak.community.core.exception.PasswordFailedExceededException;
import darak.community.core.exception.PasswordMismatchException;
import darak.community.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Profile VO 분리 작업 필요
// TODO: MemberGrade Enum 분리 작업 필요
// TODO: 일급 컬렉션으로 Phone, Email, Birth 등 분리 작업 필요
//
@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {
    private static final Logger log = LoggerFactory.getLogger(Member.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private String email;

    @Builder(access = AccessLevel.PRIVATE)
    private Member(String loginId, String name, String password, String phone, LocalDate birth, String email,
                   MemberGrade grade) {
        this.loginId = loginId;
        this.name = name;
        this.password = new MemberPassword(password);
        this.phone = phone;
        this.birth = birth;
        this.email = email;
        this.memberGrade = grade;
    }

    public static Member guestMember(String loginId, String name, String password, String phone, LocalDate birth,
                                     String email) {
        return new Member(loginId, name, password, phone, birth, email, MemberGrade.GUEST);
    }

    // 회원 정보 수정 메서드
    public void updateMember(String name, String phone, LocalDate birth, String email) {
        updateName(name);
        updatePhone(phone);
        updateBirth(birth);
        updateEmail(email);
    }

    private void updateBirth(LocalDate birth) {
        if (birth != null) {
            this.birth = birth;
        }
    }

    private void updateName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
    }

    private void updateEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            this.email = email;
        }
    }

    private void updatePhone(String phone) {
        if (phone != null && !phone.trim().isEmpty()) {
            this.phone = phone;
        }
    }

    public void validatePassword(final String rawPassword) throws PasswordFailedExceededException {
        if (!password.isMatched(rawPassword)) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }
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

    public boolean isAtLeastThan(MemberGrade target) {
        return this.memberGrade.isAtLeastThan(target);
    }

}
