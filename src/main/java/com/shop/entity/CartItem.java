package com.shop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.shop.utils.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="cart_item")
@Getter
@Setter
public class CartItem extends BaseEntity{
	
	@Id
	@GeneratedValue
	@Column(name="cart_item_id")
	private Long id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int count;
    
    public static CartItem createCartItem(Cart cart, Item item, int count) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }
    /*장바구니에 기존에 담겨있는 상품일 때,
     * 해당 상품을 추가로 장바구니에 담을 때 
     * 기존 수량에 현재 담을 수량을 더해줄 때 사용하는 메서드*/
    public void addCount(int count){
        this.count += count;
    }

}
