package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
@RequiredArgsConstructor
public class JpaItemRepositoryV2 implements ItemRepository {

    private final SpringDataJpaItemRepository repository;

    @Override
    public Item save(Item item) {
        //SpringDataJpaItemRepository를 따라가다 보면 save가 이미있다
        return repository.save(item);
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = repository.findById(itemId).orElseThrow();
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
        //jpa는 이렇게만해두고 따로 저장을 안해도 실제 db에 update쿼리가 나간다.
    }

    @Override
    public Optional<Item> findById(Long id) {
        //SpringDataJpaItemRepository를 따라가다 보면 findById가 이미있다
       return repository.findById(id);
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        //실무에서는 간단한 경우가 아니면 동적쿼리를 작성하지 이렇게 하지는 않음
        if(StringUtils.hasText(itemName) && maxPrice != null){
        //    return  repository.findByItemNameLikeAndPriceLessThanEqual("%" + itemName + "%", maxPrice);
            return repository.findItems("%" + itemName + "%", maxPrice);
        }else if (StringUtils.hasText(itemName)){
            return repository.findByItemNameLike("%" + itemName + "%");
        }else if (maxPrice != null){
            return repository.findByPriceLessThanEqual(maxPrice);
        }else {
            return repository.findAll();
        }
    }
}
