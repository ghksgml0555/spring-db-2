package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity //JPA에서 관리하는 Entity라는 의미
//@Table(name = "item") 객체명과 같으면 생략해도된다.
public class Item {

    @Id /*pk*/ @GeneratedValue(strategy = GenerationType.IDENTITY) //pk의 값이 IDENTITY전략으로 생성(db에서 값을 넣어주는 전략)
    private Long id;

    //이 필드는 db의 item_name 컬럼과 매핑된다는 의미 컬럼명과 테이블명이 같으면 비워도 된다.
    @Column(name = "item_name", length=10)
    private String itemName;

    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
