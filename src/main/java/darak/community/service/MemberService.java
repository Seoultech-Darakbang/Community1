package darak.community.service;

import darak.community.domain.member.Member;
import darak.community.dto.MemberUpdateDTO;
import java.time.LocalDate;
import java.util.List;

public interface MemberService {
    Long join(Member member);

    Member findById(Long id);

    List<Member> findByName(String name);

    void validateDuplicateMember(String loginId);
    
    void update(MemberUpdateDTO memberUpdateDTO);

    void remove(Long id);

    List<String> findMemberNames(LocalDate birthDay, String phoneNumber);

}
