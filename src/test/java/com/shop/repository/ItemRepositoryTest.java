package com.shop.repository;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;

import com.querydsl.core.BooleanBuilder;
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
    
    /*QuerydslPredicateExecutor로 테스트*/
    public void createItemList2(){
        for(int i=1;i<=5;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }


        for(int i=6;i<=10;i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }
    
    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2(){
    	this.createItemList2();
    	String itemDetail = "테스트";
    	int price = 10003;
    	String itemSellState = "SELL";
    	
    	QItem item = QItem.item;
    	
    	BooleanBuilder builder = new BooleanBuilder();
    	builder.and(item.itemDetail.like("%" + itemDetail + "%"));
    	builder.and(item.price.gt(price)); // where price > 10003
    	
    	if(StringUtils.equals(itemSellState, ItemSellStatus.SELL)) {
    		builder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
    	}
    	/*가져올 갯수 제한(페이징)*/
    	Pageable pageable = PageRequest.of(0, 5); //0페이지로 시작,사이즈:5
    	Page<Item> itemPagingResult =
    			itemRepository.findAll(builder, pageable);
    	System.out.println("전체 갯수 : " + itemPagingResult.getTotalElements());
    	List<Item> resultItemList = itemPagingResult.getContent();
    	for(Item resultItem : resultItemList) {
    		System.out.println(resultItem.toString());
    	}
    }
    
    
}
