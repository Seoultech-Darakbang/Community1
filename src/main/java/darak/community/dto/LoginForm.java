package darak.community.dto;

import darak.community.service.login.request.LoginServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginForm {

    @NotEmpty
    private String loginId;

    @NotEmpty
    private String password;

    public LoginServiceRequest toServiceRequest() {
        return new LoginServiceRequest(loginId, password);
    }
}
