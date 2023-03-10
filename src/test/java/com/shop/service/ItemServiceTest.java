package com.shop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
public class ItemServiceTest {
	
    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemImgRepository itemImgRepository;
    
    /* MockMultipartFile 클래스를 이용해 가짜 MultipartFile 리스트를 만들어 반환 */
    List<MultipartFile> createMultipartFiles() throws Exception{
    	List<MultipartFile> multipartFileList = new ArrayList<>();
    	
    	for(int i=0;i<5;i++) {
    		String path = "D:/dev/shop/item/";
    		String imageName = "image" + i + ".jpg";
    		MockMultipartFile multipartFile =
    				new MockMultipartFile(path, imageName
    						,"image/jpg",new byte[] {1,2,3,4});
    		multipartFileList.add(multipartFile);
    	}
    	return multipartFileList;
    }
    
    @Test
    @DisplayName("상품 등록 테스트")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveItem() throws Exception {
    	//상품 등록화면에서 입력 받는 상품 데이터 세팅
    	ItemFormDto itemFormDto = new ItemFormDto();
    	 itemFormDto.setItemNm("테스트상품");
         itemFormDto.setItemSellStatus(ItemSellStatus.SELL);
         itemFormDto.setItemDetail("테스트 상품 입니다.");
         itemFormDto.setPrice(1000);
         itemFormDto.setStockNumber(100);
         
         List<MultipartFile> multipartFileList = createMultipartFiles();
         //상품정보(itemFormDto)와 이미지정보(multipartFileList)넘겨서 저장된 상품 아이디를 얻음
         Long itemId = itemService.saveItem(itemFormDto, multipartFileList);
         
         List<ItemImg> itemImgList = 
        		 itemImgRepository.findByItemIdOrderByIdAsc(itemId);
         Item item = itemRepository.findById(itemId)
        		 			.orElseThrow(EntityNotFoundException::new);
         
         assertEquals(itemFormDto.getItemNm(), item.getItemNm());
         assertEquals(itemFormDto.getItemSellStatus(), item.getItemSellStatus());
         assertEquals(itemFormDto.getItemDetail(), item.getItemDetail());
         assertEquals(itemFormDto.getPrice(), item.getPrice());
         assertEquals(itemFormDto.getStockNumber(), item.getStockNumber());
         assertEquals(multipartFileList.get(0).getOriginalFilename(), itemImgList.get(0).getOriImgName());
    }
}
