package darak.community.service.member;

import darak.community.core.exception.PasswordFailedExceededException;
import darak.community.service.member.request.MemberJoinServiceRequest;
import darak.community.service.member.response.MemberResponse;

public interface MemberService {
    MemberResponse join(MemberJoinServiceRequest request);

    void validateDuplicateMember(String loginId);

    void changePassword(Long id, String oldPassword, String newPassword) throws PasswordFailedExceededException;
}
