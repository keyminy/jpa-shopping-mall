package com.shop.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.shop.constant.OrderStatus;
import com.shop.utils.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="orders")
@Getter
@Setter
public class Order extends BaseEntity{
	
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY) //한 명의 회원은 여러 주문 가능
    @JoinColumn(name = "member_id")
    private Member member;
    
    private LocalDateTime orderDate; //주문일
    
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문 상태
    
    //@OneToMany는 기본 LAZY지만,헷갈리니까 걍 붙인다.
    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL
    			,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    /* 주문은 언제 주문했고, 언제 주문 변경이 일어 났는지가 중요하다*/
    //private LocalDateTime regTime;
    
   //private LocalDateTime updateTime;
    
    /* 생성한 주문 상품 객체를 이용해서 주문 객체를 만드는 메서드 */
    public void addOrderItem(OrderItem orderItem) {
    	orderItems.add(orderItem);
    	//Order와 OrderItem엔티티는 양방향 참조관계이므로..
    	//OrderItem엔티티에도 order정보를 셋팅해줘야함
    	orderItem.setOrder(this);
    }
    /*상품 페이지에서는 1개의 상품을 주문하지만
     * 장바구니 페이지에서는 여러개의 상품을 주문 합니다.
     * 그러므로 여러개의 주문 상품을 담을 수 있도록 List형태로 파라미터로 받고
     * Order엔티티에 List의 요소(orderItem)를 하나씩 add해줌 */
    public static Order createOrder(Member member,List<OrderItem> orderItemList) {
    	Order order = new Order();
    	order.setMember(member);
    	for(OrderItem orderItem : orderItemList) {
    		order.addOrderItem(orderItem);
    	}
    	//주문상태로 설정
    	order.setOrderStatus(OrderStatus.ORDER);
    	order.setOrderDate(LocalDateTime.now());
    	return order;
    }
    //총 주문 금액
    public int getTotalPrice() {
    	int totalPrice = 0;
    	for(OrderItem orderItem : orderItems) {
    		//OrderItem의 주문 가격 * 주문 수량한 가격
    		totalPrice += orderItem.getTotalPrice();
    	}
    	return totalPrice;
    }
    
    public void cancelOrder() {
    	this.orderStatus = OrderStatus.CANCLE;
    	for(OrderItem orderItem : orderItems) {
    		orderItem.cancle();
    	}
    }
}
