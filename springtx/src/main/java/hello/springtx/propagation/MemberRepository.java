package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

//스프링 데이터 jpa쓰면 좋겠지만 아직 익숙하지 않을거라서 직접 만들어본다.
@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    //jpa의 모든 데이터 변경은 트랜잭션 안에서 해야한다.
    @Transactional
    public void save(Member member){
        log.info("member 저장");
        em.persist(member);
    }

    public Optional<Member> find(String username){
        return em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", username)
                .getResultList().stream().findAny();

    }
}
