package com.shop.service;

import java.util.ArrayList;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.ItemSearchDto;
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
	
	/*상품 수정 페이지 이동 시, 등록된 상품 정보 조회*/
	@Transactional(readOnly = true) //읽기 전용이되어 JPA가 변경감지 연산을 하지 않는다
	public ItemFormDto getItemDtl(Long itemId) {
		//해당 상품id로 상품의 이미지리스트 조회(등록순으로 조회한다(asc키워드))
		List<ItemImg> itemImgList = 
			itemImgRepository.findByItemIdOrderByIdAsc(itemId);
		//Entity -> DTO 변환작업
		List<ItemImgDto> itemImgDtoList = new ArrayList<ItemImgDto>();
		for(ItemImg itemImg : itemImgList) {
			ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
			itemImgDtoList.add(itemImgDto);
		}
		Item item = itemRepository.findById(itemId)
				.orElseThrow(EntityNotFoundException::new);
		ItemFormDto itemFormDto = ItemFormDto.of(item);
		itemFormDto.setItemImgDtoList(itemImgDtoList);
		//ItemImgDto List배열이 담긴 itemFormDto반환
		return itemFormDto;
	}
	
	//Item(상품)업데이트
	 public Long updateItem(ItemFormDto itemFormDto,
			 List<MultipartFile> itemImgFileList) throws Exception{
        //상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),
                    itemImgFileList.get(i));
        }

        return item.getId();
    }

	 //관리자 상품관리화면 페이지 이동
	 @Transactional(readOnly = true) //읽기 전용
	 public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto,
			 Pageable pageable){
		 return itemRepository.getAdminItemPage(itemSearchDto, pageable);
	 }
}
