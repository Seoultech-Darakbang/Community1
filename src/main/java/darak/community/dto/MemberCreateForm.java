package darak.community.dto;

import darak.community.domain.member.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class MemberCreateForm {

    @NotEmpty
    @Length(min = 4, max = 20)
    private String loginId;

    @NotEmpty
    @Length(min = 6, max = 40)
    private String password;

    @NotEmpty
    @Length(max = 16)
    private String name;

    private String phone;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;

    @Email
    @NotEmpty
    private String email;

    public Member toEntity() {
        return Member.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .phone(phone)
                .birth(birth)
                .email(email)
                .build();
    }
}
