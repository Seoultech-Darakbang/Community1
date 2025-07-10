package darak.community.service.login;

import darak.community.domain.member.Member;
import darak.community.infra.repository.MemberRepository;
import darak.community.service.login.request.LoginServiceRequest;
import darak.community.service.login.response.MemberLoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public MemberLoginResponse login(LoginServiceRequest request) {
        Member member = findMemberBy(request.getLoginId());
        member.validatePassword(request.getRawPassword());
        // TODO: 로그인 기록 로깅 or 저장 로직 추가

        return MemberLoginResponse.from(member);
    }

    private Member findMemberBy(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }
}
