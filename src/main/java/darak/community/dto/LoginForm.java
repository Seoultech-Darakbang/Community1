package darak.community.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginForm {

    @NotEmpty
    @Max(20)
    private String loginId;

    @NotEmpty
    @Max(40)
    private String password;
}
