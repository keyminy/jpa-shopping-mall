package com.shop.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;


@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
public class ItemRepositoryTest {
    
	@Autowired
	EntityManager em;
	
	@Autowired
    ItemRepository itemRepository;
	  
	@Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }
	
    public void createItemList(){
        for(int i=1;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100); item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }
	
    @Test
    @DisplayName("가격 내림차순 조회 테스트")
    public void findByPriceLessThanOrderByPriceDesc(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }
    
    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for(Item item : itemList){
            System.out.println(item.toString());
        }
    }
    
    @Test
    @DisplayName("Querydsl 테스트")
    public void querydslTest() {
    	this.createItemList();
    	JPAQueryFactory queryFactory = new JPAQueryFactory(em);
    	//queryFactory를 만들었으므로, 이제 쿼리 가능
    	QItem qItem = new QItem("i"); //QItem의 명칭 정의
    	List<Item> list = queryFactory.select(qItem)
				    				.from(qItem)
				    				.where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
				    				.where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명"  +"%"))
				    				.orderBy(qItem.price.desc())
				    				.fetch();
    	for(Item item : list) {
    		System.out.println(item);
    	}
    }
    
}
