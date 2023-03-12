package com.shop.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import com.shop.utils.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name="item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{
	  	@Id
	    @Column(name="item_id")
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private Long id;       //상품 코드

	    @Column(nullable = false, length = 50)
	    private String itemNm; //상품명

	    @Column(name="price", nullable = false)
	    private int price; //가격

	    @Column(nullable = false)
	    private int stockNumber; //재고수량

	    @Lob
	    @Column(nullable = false)
	    private String itemDetail; //상품 상세 설명

	    @Enumerated(EnumType.STRING)
	    private ItemSellStatus itemSellStatus; //상품 판매 상태
	    
	    //private LocalDateTime regTime;
	    
	    //private LocalDateTime updateTime;
	    
	    public void updateItem(ItemFormDto itemFormDto){
	        this.itemNm = itemFormDto.getItemNm();
	        this.price = itemFormDto.getPrice();
	        this.stockNumber = itemFormDto.getStockNumber();
	        this.itemDetail = itemFormDto.getItemDetail();
	        this.itemSellStatus = itemFormDto.getItemSellStatus();
	    }
	    
	    public void removeStock(int stockNumber) {
	    	//현재 상품의 재고 수량에서 주문으로 들어오는 수량을 뺀 주문 후 남은 재고 수량 구함
	    	int restStock = this.stockNumber - stockNumber;
	    	if(restStock < 0) {
	    		throw new OutOfStockException("상품의 재고가 부족합니다. (현재 재고 수량 : " + this.stockNumber + ")");
	    	}
	    	//상품엔티티의 재고수량을 주문 후 남은 수량으로 할당(set)
	    	this.stockNumber = restStock;
	    }
}
