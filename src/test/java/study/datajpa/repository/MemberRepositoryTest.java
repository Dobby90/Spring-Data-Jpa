package study.datajpa.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@SpringBootTest
@Transactional
// @Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository mr;
    @Autowired TeamRepository tr;
    @PersistenceContext EntityManager em;

    @Test
    public void testMember() {

        Member member = new Member("조홍연");
        // member.setUsername("조홍연");

        Member saveMember = mr.save(member);
        Member findMember = mr.findById(saveMember.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        mr.save(member1);
        mr.save(member2);

        // 단건 조회 검증
        Member findMember1 = mr.findById(member1.getId()).get();
        Member findMember2 = mr.findById(member2.getId()).get();

        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = mr.findAll();

        Assertions.assertThat(all.size()).isEqualTo(2);

        // 카운트 검증

        long count = mr.count();

        Assertions.assertThat(count).isEqualTo(2);

        // 삭제 검증
        mr.delete(member1);
        mr.delete(member2);

        long deletedCount = mr.count();

        Assertions.assertThat(deletedCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        mr.save(m1);
        mr.save(m2);
        List<Member> result = mr.findByUsernameAndAgeGreaterThan("AAA", 15);

        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        mr.save(m1);
        mr.save(m2);

        List<Member> result = mr.findUser("AAA", 10);

        Assertions.assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        mr.save(m1);
        mr.save(m2);

        List<String> result = mr.findUsernameList();
        for (String s : result) {
            System.out.println("s = " + s);
        }        
    }

    @Test
    public void findMemberDtoList() {
        Member m1 = new Member("AAA", 10);
        mr.save(m1);

        Team team = new Team("teamA");
        tr.save(team);

        m1.setTeam(team);

        List<MemberDto> result = mr.findMemberDto();
        for (MemberDto md : result) {
            System.out.println("result = " + md);
            System.out.println("id = " + md.getId());
            System.out.println("username = " + md.getUsername());
            System.out.println("teamName = " + md.getTeamName());
        }
    }

    // 페이징 조건과 정렬 조건 설정
    @Test
    public void page() throws Exception {

        // given
        mr.save(new Member("member1", 10));
        mr.save(new Member("member2", 10));
        mr.save(new Member("member3", 10));
        mr.save(new Member("member4", 10));
        mr.save(new Member("member5", 10));

        // when
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = mr.findByAge(10, pageRequest);

        // then
        List<Member> content = page.getContent(); // 조회된 데이터

        Assertions.assertThat(content.size()).isEqualTo(3); // 조회된 데이터 수
        Assertions.assertThat(page.getTotalElements()).isEqualTo(5); // 전체 데이터 수
        Assertions.assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
        Assertions.assertThat(page.getTotalPages()).isEqualTo(2); // 전체 페이지 번호
        Assertions.assertThat(page.isFirst()).isTrue(); // 첫번째 항목인가?
        Assertions.assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있는가?
    }

    @Test
    public void bulkUpdate() throws Exception {

        // given
        mr.save(new Member("member1", 10));
        mr.save(new Member("member2", 19));
        mr.save(new Member("member3", 20));
        mr.save(new Member("member4", 21));
        mr.save(new Member("member5", 40));

        // when
        int resultCount = mr.bulkAgePlus(20);

        // then
        Assertions.assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() throws Exception {
        // member1 -> teamA

        // member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        tr.save(teamA);
        tr.save(teamB);
        mr.save(new Member("member1", 10, teamA));
        mr.save(new Member("member2", 20, teamB));
        em.flush();
        em.clear();

        // when
        List<Member> members = mr.findAll();

        // then
        for (Member member : members) {
            member.getTeam().getName();
        }
    }

    @Test
    public void queryHint() throws Exception {
        // given
        mr.save(new Member("member1", 10));
        em.flush();
        em.clear();
        
        // when
        Member member = mr.findReadOnlyByUsername("member1");
        member.setUsername("member2");
        em.flush(); // Update Query 실행X
    }

    @Test
    public void callCustom() {
        List<Member> result = mr.findMemberCustom();
    }
}
