package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Transactional //JPA의 모든 변경은 트랜잭션 안에서 이루어진다.
public class JpaItemRepository implements ItemRepository {

    //JPA에서는 EntityManager를 의존관계 주입받아야 한다.
    private final EntityManager em;

    public JpaItemRepository(EntityManager em){
        this.em = em;
    }

    @Override
    public Item save(Item item) {
        //persist()로 저장
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
        //이렇게만해두고 따로 저장을 안해도 실제 db에 update쿼리가 나간다.
    }

    @Override
    public Optional<Item> findById(Long id) {
        //조회는 find
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item); //혹시 null일수도있으니 ofNullable

    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        //하나를 조회할때는 find 여러 데이터를 여러 조건으로 조회할때는 jpql
        String jpql = "select i from Item i"; //Item 엔티티 객체

        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();

        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql += " where";
        }
        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            jpql += " i.itemName like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
        }
        log.info("jpql={}", jpql);

        TypedQuery<Item> query = em.createQuery(jpql, Item.class);
        if (StringUtils.hasText(itemName)) {
            query.setParameter("itemName", itemName);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        return query.getResultList();
    }
}
