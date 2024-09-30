package darak.community.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private MemberGrade memberGrade;

    private String phone;

    private LocalDate birth;

    private String email;

    @Embedded
    private TimeStamp timeStamp;

}
