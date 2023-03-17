package hello.springtx.propagation;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

//db에 남기는 로그를 위한 클래스
@Entity
@Getter @Setter
public class Log {

    @Id @GeneratedValue
    private Long id;
    private String message;

    //jpa스펙상 필요한 기본생성자
    public Log() {
    }

    public Log(String message){
        this.message = message;
    }
}
