package com.shop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shop.dto.ItemFormDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
	
	private final ItemRepository itemRepository;
	private final ItemImgService itemImgService;
	private final ItemImgRepository itemImgRepository;
	
	public Long saveItem(ItemFormDto itemFormDto,
			List<MultipartFile> itemImgFileList) throws Exception {
		//상품 등록
		//itemFormDto -> Item엔티티 생성
		Item item = itemFormDto.createItem();
		itemRepository.save(item);
		
		//이미지 등록
		for(int i=0;i<itemImgFileList.size();i++) {
			ItemImg itemImg = new ItemImg();
			itemImg.setItem(item);//item_img테이블의 FK인 item_id에 item 정보 셋팅
			if(i ==0) {
				//첫번째 이미지일 경우, 대표 상품 이미지 여부 값을 "Y"로 셋팅
				//나머지는 대표 상품 이미지 여부를 "N"으로 셋팅
				itemImg.setRepimgYn("Y");
			}else {
				itemImg.setRepimgYn("N");
			}
			itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
		}
		return item.getId();
	}
}
