package com.shop.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.entity.Member;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;
    
    public Long order(OrderDto orderDto, String email){
    	//주문상품ID로 조회한 item객체 생성
    	Item item = itemRepository.findById(orderDto.getItemId())
    			.orElseThrow(EntityNotFoundException::new);
    	//현재 로그인한 회원의 이메일 정보 조회
    	Member member = memberRepository.findByEmail(email);
    	
    	List<OrderItem> orderItemList = new ArrayList<>();
    	//주문할 item엔티티와 주문 수량을 이용해 주문 상품 엔티티 생성
    	OrderItem orderItem = OrderItem.createOrderItem(item,orderDto.getCount());
    	orderItemList.add(orderItem);
    	
    	Order order = Order.createOrder(member, orderItemList);
    	orderRepository.save(order);
    	return order.getId();
    }
    
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
        	//주문 리스트(List<Order>를 순회 구매 이력 페이지에 전달할 OrderHistDto생성
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn
                        (orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto =
                        new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }

        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }
    
    /*주문을 취소하는 로직 구현*/
    //현재 로그인한 사용자와 주문 데이터를 생성한 사용자가 같은지 검사
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }

        return true;
    }

    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        //this.orderStatus = OrderStatus.CANCLE;로
        //주문엔티티의 상태가 취소로 변경되면, 변경 감지 기능에 의해,트랜잭션이 끝날 때 update쿼리가 실행됨
        order.cancelOrder();
    }
    
    /*장바구니에서 주문할 상품 데이터를 전달받아, 주문을 생성하는 로직*/
    public Long orders(List<OrderDto> orderDtoList, String email){

        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
        	//주문할 상품(Item)리스트를 만들어준다
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }
        //현재 로그인한 회원과 주문 상품 목록(List<OrderItem>으로 Order엔티티 만듬)
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }
}
