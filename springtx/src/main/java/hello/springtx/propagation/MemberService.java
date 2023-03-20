package hello.springtx.propagation;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LogRepository logRepository;

    @Transactional
    public void JoinV1(String username){
        Member member = new Member(username);
        Log logMessage = new Log(username);

        //각각의 save에 트랜잭션이 적용되있다.
        log.info("== memberRepository 호출 시작 ==");
        memberRepository.save(member);
        log.info("== memberRepository 호출 종료 ==");

        log.info("== logRepository 호출 시작 ==");
        logRepository.save(logMessage);
        log.info("== logRepository 호출 종료 ==");
    }

    //JoinV1과 같지만 로그저장에 실패해서 회원가입이 롤백되는 경우를 막음
    @Transactional
    public void JoinV2(String username){
        Member member = new Member(username);
        Log logMessage = new Log(username);

        //각각의 save에 트랜잭션이 적용되있다.
        log.info("== memberRepository 호출 시작 ==");
        memberRepository.save(member);
        log.info("== memberRepository 호출 종료 ==");

        //v1에서는 log.save해서 runtime오류 터지면 memberservice 밖으로던진다
        log.info("== logRepository 호출 시작 ==");
        try {
            logRepository.save(logMessage);
        }catch (RuntimeException e){
            log.info("log 저장에 실패했습니다. logMessage={}",logMessage.getMessage());
            log.info("정상 흐름 반환");
        }
        log.info("== logRepository 호출 종료 ==");
    }
}
