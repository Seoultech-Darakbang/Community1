package darak.community.service.login.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginServiceRequest {

    private String loginId;
    private String rawPassword;

}
