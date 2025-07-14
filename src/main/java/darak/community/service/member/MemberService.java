package darak.community.service.member;

import darak.community.domain.member.MemberGrade;
import darak.community.service.member.request.MemberJoinServiceRequest;
import darak.community.service.member.request.PasswordChangeServiceRequest;
import darak.community.service.member.response.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {
    MemberResponse join(MemberJoinServiceRequest request);

    void validateDuplicateMember(String loginId);

    void changePassword(PasswordChangeServiceRequest request);

    void editMemberGrade(Long memberId, MemberGrade grade);

    long getTotalMemberCount();

    Page<MemberResponse> getAllMembersPaged(Pageable pageable);

    Page<MemberResponse> searchMembers(String keyword, MemberGrade grade, Pageable pageable);

}
