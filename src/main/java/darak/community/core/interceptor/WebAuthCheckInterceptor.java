package darak.community.core.interceptor;

import darak.community.core.auth.ServiceAuth;
import darak.community.core.session.constant.SessionConst;
import darak.community.core.session.dto.LoginMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebAuthCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        ServiceAuth serviceAuth = getAuthAnnotation(handlerMethod);

        if (serviceAuth == null) {
            log.info("AuthCheck 시도: ServiceAuth가 없습니다. 인증 없이 진행합니다.");
            return true;
        }

        HttpSession session = request.getSession();
        if (session == null) {
            log.info("AuthCheck 시도: session is Empty");
            response.sendRedirect("/login?redirectURL=" + request.getRequestURI());
            return false;
        }

        LoginMember loginMember = (LoginMember) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if (loginMember == null) {
            log.info("AuthCheck 시도: session is Empty");
            response.sendRedirect("/login?redirectURL=" + request.getRequestURI());
            return false;
        }

        if (memberHasPermission(loginMember, serviceAuth)) {
            return true;
        }

        response.sendError(HttpStatus.FORBIDDEN.value(), serviceAuth.message());
        return false;
    }

    private boolean memberHasPermission(LoginMember loginMember, ServiceAuth serviceAuth) {
        return loginMember.getMemberGrade().isAtLeastThan(serviceAuth.value());
    }

    private ServiceAuth getAuthAnnotation(HandlerMethod handlerMethod) {
        ServiceAuth methodServiceAuth = handlerMethod.getMethodAnnotation(ServiceAuth.class);
        if (methodServiceAuth != null) {
            return methodServiceAuth;
        }

        return handlerMethod.getBeanType().getAnnotation(ServiceAuth.class);
    }
}
