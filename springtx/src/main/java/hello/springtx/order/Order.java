package hello.springtx.order;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "orders") //이클래스는 orders라는 db테이블과 매핑해서 사용한다는 뜻
//db에 order by 문법으로 order를 쓸수없는경우가 많아서 orders로 많이쓴다고함
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    private String username; //정상, 예외, 잔고부족
    private String payStatus; //대기, 완료
}
