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
}
