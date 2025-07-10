package darak.community.core.session;

import darak.community.core.session.constant.SessionConst;
import darak.community.core.session.dto.LoginMember;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {

    public void login(HttpSession session, LoginMember loginMember) {
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
}
