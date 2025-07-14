package darak.community.core.auth;

import darak.community.domain.member.MemberGrade;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceAuth {

    MemberGrade value();

    String message() default "접근 불가능한 권한입니다";

}
