package com.shop.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shop.dto.OrderDto;
import com.shop.entity.Item;
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
}
