package com.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.entity.CartItem;

public interface CartItemRepository  extends JpaRepository<CartItem, Long> {
	//CartId와 ItemId를 이용해서 상품이 장바구니에 들어있는지 조회
	 CartItem findByCartIdAndItemId(Long cartId, Long itemId);
}
