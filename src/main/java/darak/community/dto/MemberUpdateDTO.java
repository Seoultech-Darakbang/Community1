package darak.community.dto;


import darak.community.domain.member.Member;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import lombok.Data;

@Data
public class MemberUpdateDTO { // Required for transporting Controller to Service Layer
    @NotEmpty
    private Long id;
    private String password;

    @NotEmpty
    private String name;

    private String phone;

    private LocalDate birth;

    private String email;

    public Member toEntity() {
        return Member.builder()
                .name(name)
                .password(password)
                .phone(phone)
                .birth(birth)
                .email(email)
                .build();
    }
}
