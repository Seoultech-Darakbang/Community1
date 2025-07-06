package darak.community.core.aop;

import darak.community.core.auth.ServiceAuth;
import darak.community.core.auth.ServiceAuthChecker;
import darak.community.core.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceAuthAspect {

    private final ServiceAuthChecker serviceAuthChecker;

    @Before("execution(* darak.community.service..*.*(..)) && (@annotation(darak.community.core.auth.ServiceAuth) || @within(darak.community.core.auth.ServiceAuth))")
    public void authCheck(JoinPoint joinPoint) {
        log.info("{} 에서 권한 체크 진행", joinPoint.getSignature());

        Long currentMemberId = UserContext.getCurrentMemberId();

        if (currentMemberId == null) {
            throw new IllegalStateException("로그인되지 않은 사용자입니다.");
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        ServiceAuth serviceAuth = getServiceAuth(joinPoint, signature);

        if (serviceAuth == null) {
            throw new IllegalStateException("@ServiceAuth 어노테이션을 찾을 수 없습니다.");
        }

        if (!serviceAuthChecker.isMemberGradeOrHigher(currentMemberId, serviceAuth.value())) {
            throw new IllegalStateException("접근할 수 없는 권한의 메서드를 호출하였습니다: " + serviceAuth.message());
        }
    }

    private ServiceAuth getServiceAuth(JoinPoint joinPoint, MethodSignature signature) {
        ServiceAuth methodAuth = signature.getMethod().getAnnotation(ServiceAuth.class);
        if (methodAuth != null) {
            return methodAuth;
        }

        Class<?> targetClass = joinPoint.getTarget().getClass();
        return targetClass.getAnnotation(ServiceAuth.class);
    }
}
