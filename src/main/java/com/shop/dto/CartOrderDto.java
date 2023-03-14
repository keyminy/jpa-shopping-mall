package com.shop.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartOrderDto {
    private Long cartItemId;
    //장바구니에서 여러 상품을 주문하므로, CartOrderDto클래스에서 자기 자신을 List로 가지고 있도록
    private List<CartOrderDto> cartOrderDtoList;
}
