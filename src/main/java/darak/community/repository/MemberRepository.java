package darak.community.repository;

import darak.community.domain.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        if (member.getId() == null) {
            em.persist(member);
        } else {
            em.merge(member);
        }
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class).setParameter("name", name).getResultList();
    }

    public List<Member> findByBirthAndPhone(LocalDate birthDay, String phoneNumber) {
        return em.createQuery(
                        "SELECT m FROM Member m WHERE m.birth = :birth AND m.phone = :phone", Member.class)
                .setParameter("birth", birthDay)
                .setParameter("phone", phoneNumber)
                .getResultList();
    }

    public void withdraw(Member member) {
        em.remove(member);
    }
}
