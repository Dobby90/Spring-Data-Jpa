package study.datajpa.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import study.datajpa.entity.Member;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class).getSingleResult();
    }
    
    public Member find(Long id) {
        return em.find(Member.class, id);
    }

    public int bulkAgePlus(int age) {

        int resultCount = em.createQuery("update Member m set m.age = m.age + 1 where m.age >= :age")
                            .setParameter("age", age)
                            .executeUpdate();
        return resultCount;
    }

}
