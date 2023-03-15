package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.Basic;

@Slf4j
@SpringBootTest //aop들이 동작하게 하기 위해서
public class TxBasicTest {

    //빈으로 등록했기때문에 주입받을수 있다.
    @Autowired BasicService basicService;

    @Test
    void proxyCheck(){
        //프록시가 적용되는지는 클래스만 봐도 다르게 나오기때문에 알 수 있다
        log.info("aop class={}", basicService.getClass());
        Assertions.assertThat(AopUtils.isAopProxy(basicService)).isTrue();
    }

    @Test
    void txTest(){
        basicService.tx();
        basicService.nonTx();
    }

    @TestConfiguration
    static class TxApplyBasicConfig{
        @Bean
        BasicService basicService() {
            return new BasicService();
        }
    }

    @Slf4j
    static class BasicService{

        @Transactional//트랜잭션 적용되는것
        public void tx(){
            log.info("call tx");
            //이걸 호출해보면 여기안에서 트랜잭션이 적용되는게 맞는지 응답해준다.
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx action={}", txActive);
        }

        //트랜잭션 적용 안되는것
        public void nonTx(){
            log.info("call nontx");
            //이걸 호출해보면 여기안에서 트랜잭션이 적용되는게 맞는지 응답해준다.
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx action={}", txActive);
        }
    }
}
