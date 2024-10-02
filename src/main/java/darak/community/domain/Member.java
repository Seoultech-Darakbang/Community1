package darak.community.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    private MemberGrade memberGrade;

    private String phone;

    private LocalDate birth;

    private String email;

    @Builder
    public Member(String name, String password, String phone, LocalDate birth, String email) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.birth = birth;
        this.email = email;
    }

    // 회원 정보 수정 메서드
    public void updateMember(Member modifiedMember) {
        if (modifiedMember.getName() != null) this.name = modifiedMember.name;
        if (modifiedMember.getPhone() != null) this.phone = modifiedMember.phone;
        if (modifiedMember.getBirth() != null) this.birth = modifiedMember.birth;
        if (modifiedMember.getEmail() != null) this.email = modifiedMember.email;
        if (modifiedMember.getPassword() != null) this.password = modifiedMember.password;
    }

}
