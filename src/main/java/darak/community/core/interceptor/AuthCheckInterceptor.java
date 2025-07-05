package darak.community.core.interceptor;

import darak.community.core.auth.Auth;
import darak.community.core.session.constant.SessionConst;
import darak.community.service.member.MemberService;
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
public class AuthCheckInterceptor implements HandlerInterceptor {

    private final MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession();
        if (session == null) {
            log.info("AuthCheck 시도: session is Empty");
            response.sendRedirect("/login?redirectURL=" + request.getRequestURI());
            return false;
        }

        Long memberId = (Long) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
        if (memberId == null) {
            log.info("AuthCheck 시도: memberID is Empty");
            response.sendRedirect("/login?redirectURL=" + request.getRequestURI());
            return false;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Auth auth = getAuthAnnotation(handlerMethod);

        if (auth == null || memberHasPermission(memberId, auth)) {
            return true;
        }

        response.sendError(HttpStatus.FORBIDDEN.value(), auth.message());
        return false;
    }

    private boolean memberHasPermission(Long memberId, Auth auth) {
        return memberService.isMemberGradeOrHigher(memberId, auth.value());
    }

    private Auth getAuthAnnotation(HandlerMethod handlerMethod) {
        Auth methodAuth = handlerMethod.getMethodAnnotation(Auth.class);
        if (methodAuth != null) {
            return methodAuth;
        }

        return handlerMethod.getBeanType().getAnnotation(Auth.class);
    }
}
