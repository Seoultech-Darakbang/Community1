package darak.community.service.login.request;

import lombok.Getter;

@Getter
public class LoginServiceRequest {

    private String loginId;
    private String rawPassword;
}
