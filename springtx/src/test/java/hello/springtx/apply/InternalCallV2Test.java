package hello.springtx.apply;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV2Test {

    @Autowired
    CallService callService; //메서드에 트랜잭션이 있기떄문에 프록시가 주입된다.

    @Test //프록시가 들어오는지 확인
    void printProxy() {
        log.info("callService class={}", callService.getClass());
    }

    @Test //내부호출로 트랜잭션이 걸리는지 보기 이게문제!!
    void externalCallV2(){
        callService.external();
    }

    @TestConfiguration
    static class InternalV1TestConfig{

        @Bean
        CallService callService(){
            return new CallService(internalService());
        }

        @Bean
        InternalService internalService() {
            return new InternalService();
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    static class CallService{

        private final InternalService internalService;

        public void external(){
            log.info("call external");
            printTxInfo();
            internalService.internal();
        }

        public void printTxInfo(){
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }

    static class InternalService{
        @Transactional
        public void internal(){
            log.info("call internal");
            printTxInfo();
        }

        public void printTxInfo(){
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("tx active={}", txActive);
        }
    }
}
