package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    //원래는 스프링이 트랜잭션 매니저도 자동으로 등록해주는데 빈을 직접등록하면 직접등록한게 사용된다.
    @Autowired
    PlatformTransactionManager txManager;

    @TestConfiguration
    static class Config{
        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource){
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit(){
        log.info("트랜잭션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 커밋 시작");
        txManager.commit(status);
        log.info("트랜잭션 커밋 완료");
    }

    @Test
    void rollback(){
        log.info("트랜잭션 시작");
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 롤백 시작");
        txManager.rollback(status);
        log.info("트랜잭션 롤백 완료");
    }

    @Test
    void double_commit(){
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋");
        txManager.commit(tx1);

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 커밋");
        txManager.commit(tx2);
    }

    @Test
    void double_commit_rollback(){
        log.info("트랜잭션1 시작");
        TransactionStatus tx1 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋");
        txManager.commit(tx1);

        log.info("트랜잭션2 시작");
        TransactionStatus tx2 = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 롤백");
        txManager.rollback(tx2);
    }

    @Test
    void inner_commit(){
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        //이게 처음 수행된 트랜잭션인지 > isNewTransaction()
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction());

        //외부에서 트랜잭션이 수행되고 있는데 그 안에서 트랜잭션 시작
        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
        log.info("내부 트랜잭션 커밋");
        txManager.commit(inner);

        log.info("외부 트랜잭션 커밋");
        txManager.commit(outer);

    }

    @Test
    void outer_rollback(){
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

        //외부에서 트랜잭션이 수행되고 있는데 그 안에서 트랜잭션 시작
        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("내부 트랜잭션 커밋");
        txManager.commit(inner);

        log.info("외부 트랜잭션 롤백");
        txManager.rollback(outer);

    }

    @Test
    void inner_rollback(){
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());

        //외부에서 트랜잭션이 수행되고 있는데 그 안에서 트랜잭션 시작
        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("내부 트랜잭션 롤백");
        txManager.rollback(inner);

        log.info("외부 트랜잭션 커밋");
        Assertions.assertThatThrownBy(()->txManager.commit(outer))
                .isInstanceOf(UnexpectedRollbackException.class);

    }

    @Test
    void inner_rollback_requires_new(){
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = txManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction()={}", outer.isNewTransaction()); //당연히 TRUE

        log.info("내부 트랜잭션 시작");
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
        //setPropagationBehavior의 기본옵션은 트랜잭션이 있으면 그 트랜잭션에 참여하는것(REQUIRED)
        //PROPAGATION_REQUIRES_NEW는 새로운 트랜잭션을 만들어버리는것
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus inner = txManager.getTransaction(definition);
        log.info("inner.isNewTransaction()={}", inner.isNewTransaction()); //이것도 TRUE

        log.info("내부 트랜잭션 롤백");
        txManager.rollback(inner);//롤백

        log.info("외부 트랜잭션 커밋");
        txManager.commit(outer);
    }
}
