package darak.community.core.auth;

import darak.community.domain.member.MemberGrade;

public @interface WebAuth {

    MemberGrade value();

    String message() default "접근 불가능한 권한입니다";
}
