package com.shop.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.shop.ItemSellStatus;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.OrderRepository;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
public class OrderTest {

	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	OrderItemRepository orderItemRepository;
	
	@Autowired
	ItemRepository itemRepository;
	
	@Autowired
	MemberRepository memberRepository;
	
	@PersistenceContext
	EntityManager em;
	
	/*dummy데이터 생성!!*/
    public Item createItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }
    
    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest() {
        Order order = new Order();

        for(int i=0;i<3;i++){
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);//FK추가(item_id)
            orderItem.setCount(10); //주문 수량
            orderItem.setOrderPrice(1000); //주문 가격
            orderItem.setOrder(order);//FK추가(order_id)
            //부모 엔티티(Order)에, OrderItem넣음 1개의 주문에 OrderItem 3번 Insert 수행됨
            order.getOrderItems().add(orderItem);
        }
        //영속성 컨텍스트에 save + DB쿼리 수행(flush)
        //order save()명령이 일어났는데 -> OrderItem save()명령이 일어난 것에 주목!!
        orderRepository.saveAndFlush(order);
        em.clear();//영속성 컨텍스트 초기화

        Order savedOrder = orderRepository.findById(order.getId())
        		//값을 찾으면 해당값 반환하고, 못찾으면 예외를 던짐
                .orElseThrow(EntityNotFoundException::new);
        //예상되는 값 : 3,실제값
        assertEquals(3, savedOrder.getOrderItems().size());
    }
    
    /* 고아 객체 테스트(p.211) */
    public Order createOrder(){
        Order order = new Order();
        for(int i=0;i<3;i++){
            Item item = createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }
        Member member = new Member();
        memberRepository.save(member);
        order.setMember(member);
        orderRepository.save(order);
        return order;
    }
    
    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest() {
    	Order order = this.createOrder();
    	//OrderItem제거되면 부모 엔티티와 연관 관계가 끊어지므로 고아 객체 제거 쿼리문 수행
    	order.getOrderItems().forEach(System.out::println);
    	order.getOrderItems().remove(0);
    	em.flush();
    	System.out.println("완료==================");
    	order.getOrderItems().forEach(System.out::println);
    	/*
    	 *     delete 
			    from
			        order_item 
			    where
			        order_item_id=? */
    }
    
    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest() {
    	Order order = this.createOrder();
    	Long orderItemId = order.getOrderItems().get(0).getId();
    	em.flush();
    	em.clear();
    	
    	OrderItem orderItem = orderItemRepository.findById(orderItemId)
    			.orElseThrow(EntityNotFoundException::new);
    	System.out.println("Order class : " 
    						+ orderItem.getOrder().getClass());
    	System.out.println("============================");
    	//Order엔티티의 주문일을 조회할 때, 비로소 from orders로 시작하는 order테이블 SELECT시작
    	orderItem.getOrder().getOrderDate();
    	System.out.println("============================");
    }
    
}
