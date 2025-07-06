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

    @Before("execution(* darak.community.service..*.*(..)) && @annotation(darak.community.core.auth.ServiceAuth)")
    public void authCheck(JoinPoint joinPoint) {
        log.info("{} 에서 권한 체크 진행", joinPoint.getSignature());

        // ThreadLocal에서 현재 로그인한 사용자 정보 조회
        Long currentMemberId = UserContext.getCurrentMemberId();

        if (currentMemberId == null) {
            throw new IllegalStateException("로그인되지 않은 사용자입니다.");
        }

        // 메서드에서 @Auth 어노테이션 정보 추출
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        ServiceAuth serviceAuth = signature.getMethod().getAnnotation(ServiceAuth.class);

        if (serviceAuth == null) {
            throw new IllegalStateException("@Auth 어노테이션을 찾을 수 없습니다.");
        }

        if (!serviceAuthChecker.isMemberGradeOrHigher(currentMemberId, serviceAuth.value())) {
            throw new IllegalStateException("접근할 수 없는 권한의 메서드를 호출하였습니다");
        }

    }
}
