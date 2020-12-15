package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import study.datajpa.entity.Member;

@SpringBootTest
@Transactional
// @Rollback(false)
class MemberJpaRepositoryTest {
    
    @Autowired MemberJpaRepository mjr;

    @Test
    public void testMember() {

        Member member = new Member("조홍연");
        // member.setUsername("조홍연");

        Member saveMember = mjr.save(member);
        Member findMember = mjr.find(saveMember.getId());

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);
    }

    @Test
    // @Rollback(false)
    public void bulkUpdate() throws Exception {

        // given
        mjr.save(new Member("member1", 10));
        mjr.save(new Member("member2", 19));
        mjr.save(new Member("member3", 20));
        mjr.save(new Member("member4", 21));
        mjr.save(new Member("member5", 40));
        // when

        int resultCount = mjr.bulkAgePlus(20);
        // then
        Assertions.assertThat(resultCount).isEqualTo(3);
    }
}
