package darak.community.core.auth;

import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.infra.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceAuthChecker {

    private final MemberRepository memberRepository;

    public boolean isMemberGradeOrHigher(Long memberId, MemberGrade target) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return member.isAtLeastThan(target);
    }
}
