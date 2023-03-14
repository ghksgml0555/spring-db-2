package hello.itemservice.repository.v2;

import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 스프링이 제공하는 JpaRepository상속
 */
public interface ItemRepositoryV2 extends JpaRepository<Item, Long> {
}
