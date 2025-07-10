package darak.community.service.login;

import darak.community.service.login.request.LoginServiceRequest;
import darak.community.service.login.response.MemberLoginResponse;

public interface LoginService {

    MemberLoginResponse login(LoginServiceRequest request);
}
