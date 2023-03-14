package com.shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shop.dto.CartDetailDto;
import com.shop.entity.CartItem;

public interface CartItemRepository  extends JpaRepository<CartItem, Long> {
	//CartId와 ItemId를 이용해서 상품이 장바구니에 들어있는지 조회
	 CartItem findByCartIdAndItemId(Long cartId, Long itemId);
	 
    @Query("select new com.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
            "from CartItem ci "
            + "join Item i "
            + "on ci.item = i "
            + "join ItemImg im "
            + "on im.item = i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " +
            "and im.repimgYn = 'Y' " +
            "order by ci.regTime desc"
            )
    List<CartDetailDto> findCartDetailDtoList(Long cartId);
    //and i.id = im.item.id는 조인조건 안해줘도되나?(자동으로 on이 붙나??)
}
