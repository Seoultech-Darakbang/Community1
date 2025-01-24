package darak.community.dto;

import darak.community.domain.member.MemberPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MemberCreateForm {

    @NotEmpty
    @Length(min = 4, max = 20)
    private String loginId;

    @NotEmpty
    @Length(min = 6, max = 40)
    private MemberPassword password;

    @NotEmpty
    @Length(max = 16)
    private String name;

    private String phone;

    private LocalDate birth;

    @Email
    @NotEmpty
    private String email;
}
