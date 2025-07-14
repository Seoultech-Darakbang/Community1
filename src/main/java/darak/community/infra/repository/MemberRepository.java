package darak.community.infra.repository;

import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public Member save(Member member) {
        if (member.getId() == null) {
            em.persist(member);
        } else {
            em.merge(member);
        }
        return member;
    }

    public boolean existsByLoginId(String loginId) {
        return em.createQuery("SELECT COUNT(m) FROM Member m WHERE m.loginId = :loginId", Long.class)
                .setParameter("loginId", loginId)
                .getSingleResult() > 0;
    }

    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(em.find(Member.class, id));
    }

    public Optional<Member> findByLoginId(String loginId) {
        return findAll().stream().filter(m -> m.getLoginId().equals(loginId)).findFirst();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class).setParameter("name", name)
                .getResultList();
    }

    public List<Member> findByBirthAndPhone(LocalDate birthDay, String phoneNumber) {
        return em.createQuery(
                        "SELECT m FROM Member m WHERE m.birth = :birth AND m.phone = :phone", Member.class)
                .setParameter("birth", birthDay)
                .setParameter("phone", phoneNumber)
                .getResultList();
    }

    public List<Member> findAll() {
        return em.createQuery("SELECT m from Member m", Member.class).getResultList();
    }

    public long count() {
        return em.createQuery("SELECT COUNT(m) FROM Member m", Long.class).getSingleResult();
    }

    public Page<Member> findAllPaged(Pageable pageable) {
        List<Member> members = em.createQuery("SELECT m FROM Member m", Member.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        long total = count();
        return new PageImpl<>(members, pageable, total);
    }

    public void withdraw(Member member) {
        em.remove(member);
    }

    public void saveAndFlush(Member member) {
        em.persist(member);
        em.flush();
    }

    public void flush() {
        em.flush();
    }

    public Page<Member> searchMembers(String keyword, MemberGrade grade, Pageable pageable) {
        StringBuilder jpql = new StringBuilder("SELECT m FROM Member m WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty()) {
            jpql.append(" AND (m.name LIKE :keyword OR m.loginId LIKE :keyword OR m.email LIKE :keyword)");
        }

        if (grade != null) {
            jpql.append(" AND m.memberGrade = :grade");
        }

        jpql.append(" ORDER BY m.createdDate DESC");

        var query = em.createQuery(jpql.toString(), Member.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }

        if (grade != null) {
            query.setParameter("grade", grade);
        }

        List<Member> members = query.getResultList();

        StringBuilder countJpql = new StringBuilder("SELECT COUNT(m) FROM Member m WHERE 1=1");

        if (keyword != null && !keyword.trim().isEmpty()) {
            countJpql.append(" AND (m.name LIKE :keyword OR m.loginId LIKE :keyword OR m.email LIKE :keyword)");
        }

        if (grade != null) {
            countJpql.append(" AND m.memberGrade = :grade");
        }

        var countQuery = em.createQuery(countJpql.toString(), Long.class);

        if (keyword != null && !keyword.trim().isEmpty()) {
            countQuery.setParameter("keyword", "%" + keyword + "%");
        }

        if (grade != null) {
            countQuery.setParameter("grade", grade);
        }

        Long count = countQuery.getSingleResult();

        return new PageImpl<>(members, pageable, count);
    }
}
