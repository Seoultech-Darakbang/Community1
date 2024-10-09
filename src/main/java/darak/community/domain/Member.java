package darak.community.domain;

import darak.community.dto.MemberUpdateDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String password;

    @NotEmpty
    private String name;

    @Enumerated(EnumType.STRING)
    private MemberGrade memberGrade;

    private String phone;

    private LocalDate birth;

    private String email;

    // Builder 패턴의 단점 : 필수값을 놓칠 수 있음
    // -> NonNull을 붙여준다.
    @Builder
    public Member(String name, String password, String phone, LocalDate birth, String email) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.birth = birth;
        this.email = email;
    }

    // 회원 정보 수정 메서드
    public void updateMember(MemberUpdateDTO memberUpdateDTO) {
        this.name = memberUpdateDTO.getName();
        this.password = memberUpdateDTO.getPassword();
        this.email = memberUpdateDTO.getEmail();
        this.phone = memberUpdateDTO.getPhone();
        this.birth = memberUpdateDTO.getBirth();
    }

}
