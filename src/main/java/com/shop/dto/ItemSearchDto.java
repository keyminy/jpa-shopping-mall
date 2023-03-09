package com.shop.dto;

import com.shop.ItemSellStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
//상품 조회 조건DTO
public class ItemSearchDto {
	//현재 시간과 상품 등록일을 비교해서 상품 데이터 조회
    /* <조회시간 기준>
     * all : 상품 등록일 전체
     *  1d : 최근 하루 동안 등록된 상품..
     *  ~ 1m : 최근 한달동안 등록된 상품 */
	private String searchDateType;
    //상품 판매상태
    private ItemSellStatus searchSellStatus;
    //상품 조회 시, 어떤 유형으로 조회할지
    /*1.itemNm : 상품명, 2.createdBy : 상품 등록자 아이디*/
    private String searchBy;
    //조회할 검색어를 저장할 변수
    private String searchQuery = "";
}