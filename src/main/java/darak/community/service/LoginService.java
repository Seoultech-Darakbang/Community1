package darak.community.service;

import darak.community.domain.member.Member;
import darak.community.exception.PasswordFailedExceededException;

public interface LoginService {

    Member login(String loginId, String password)
            throws PasswordFailedExceededException; // Form 객체는 Controller 에서만 사용하자
}
