package darak.community.service.member;

import darak.community.core.exception.PasswordFailedExceededException;
import darak.community.domain.member.Member;
import darak.community.infra.repository.MemberRepository;
import darak.community.service.member.request.MemberJoinServiceRequest;
import darak.community.service.member.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public MemberResponse join(MemberJoinServiceRequest request) {
        validateDuplicateMember(request.getLoginId());
        Member joinMember = memberRepository.save(request.toEntity());
        return MemberResponse.of(joinMember);
    }

    @Override
    public void validateDuplicateMember(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

    @Transactional
    @Override
    public void changePassword(Long id, String newPassword, String oldPassword) throws PasswordFailedExceededException {
        Member findMember = findMemberBy(id);
        findMember.changePassword(newPassword, oldPassword);
    }

    private Member findMemberBy(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }
}
