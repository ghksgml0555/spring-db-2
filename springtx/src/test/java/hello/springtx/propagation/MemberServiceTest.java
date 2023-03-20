package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    /**
     * memberService     @Transaction:OFF
     * memberRepository  @Transaction:ON
     * logRepository     @Transaction:ON
     */
    @Test
    void outerTx_success(){
        //given
        String username = "outerTxOff_success";

        //when
        memberService.JoinV1(username);

        //then : 모든 데이터가 정상 저장된다.
        //이번엔 juint의 Assertions를 골라서 assertTrue를 사용한다
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService     @Transaction:OFF
     * memberRepository  @Transaction:ON
     * logRepository     @Transaction:ON Exception
     */
    @Test
    void outerTxOff_fail(){
        //given
        String username = "로그예외 outerTxOff_fail";

        //when
        org.assertj.core.api.Assertions.assertThatThrownBy(()->memberService.JoinV1(username))
                        .isInstanceOf(RuntimeException.class);

        //then : 모든 데이터가 정상 저장된다.
        //이번엔 juint의 Assertions를 골라서 assertTrue를 사용한다
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService     @Transaction:ON
     * memberRepository  @Transaction:OFF
     * logRepository     @Transaction:OFF
     */
    @Test
    void singleTx(){
        //JoinV1에 @Transactional을 달고
        // memberRepository, logRepository의 save의 @Transactional을 주석처리

        //given
        String username = "outerTxOff_success";

        //when
        memberService.JoinV1(username);

        //then : 모든 데이터가 정상 저장된다.
        //이번엔 juint의 Assertions를 골라서 assertTrue를 사용한다
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService     @Transaction:ON
     * memberRepository  @Transaction:ON
     * logRepository     @Transaction:ON
     */
    @Test
    void outerTxOn_success(){
        //JoinV1에 @Transactional을 달고
        // memberRepository, logRepository의 save의 @Transactional을 주석처리

        //given
        String username = "outerTxOn_success";

        //when
        memberService.JoinV1(username);

        //then : 모든 데이터가 정상 저장된다.
        //이번엔 juint의 Assertions를 골라서 assertTrue를 사용한다
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService     @Transaction:ON
     * memberRepository  @Transaction:ON
     * logRepository     @Transaction:ON Exception
     */
    @Test
    void outerTxOn_fail(){
        //given
        String username = "로그예외_outerTxOn_fail";

        //when
        org.assertj.core.api.Assertions.assertThatThrownBy(()->memberService.JoinV1(username))
                .isInstanceOf(RuntimeException.class);

        //then : 모든 데이터가 롤백된다.
        //이번엔 juint의 Assertions를 골라서 assertTrue를 사용한다
        Assertions.assertTrue(memberRepository.find(username).isEmpty());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService     @Transaction:ON
     * memberRepository  @Transaction:ON
     * logRepository     @Transaction:ON Exception
     */
    @Test
    void recoverException_fail(){
        //given
        String username = "로그예외_recoverException_fail";

        //when
        org.assertj.core.api.Assertions.assertThatThrownBy(()->memberService.JoinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        //then : 모든 데이터가 롤백된다.
        //이번엔 juint의 Assertions를 골라서 assertTrue를 사용한다
        Assertions.assertTrue(memberRepository.find(username).isEmpty());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService     @Transaction:ON
     * memberRepository  @Transaction:ON
     * logRepository     @Transaction:ON(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_success(){
        //given
        String username = "로그예외_recoverException_success";

        //when
        memberService.JoinV2(username);

        //then : member 저장, log 롤백
        Assertions.assertTrue(memberRepository.find(username).isPresent());
        Assertions.assertTrue(logRepository.find(username).isEmpty());
    }
}