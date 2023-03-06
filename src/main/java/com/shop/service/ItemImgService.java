package com.shop.service;

import java.io.File;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {
	/* ItemImgService : 상품 이미지를 업로드하고, 상품 이미지 정보 저장 */
    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;
    
    public void saveItemImg(ItemImg itemImg,MultipartFile itemImgFile) 
    throws Exception{
    	String oriImgName = itemImgFile.getOriginalFilename();
    	String imgName = "";
    	String imgUrl = "";
    	
    	//파일 업로드
    	if(!StringUtils.isEmpty(oriImgName)) {
    		//imgName : 업로드된 파일의 이름(uuid + .jpg확장자)
    		imgName = fileService.uploadFile(itemImgLocation, oriImgName
    				, itemImgFile.getBytes());
    		System.out.println("imgName : " + imgName);
    		//uploadFile()로, 저장한 상품 이미지를 불러올 경로 설정
    		/*외부 리소스를 불러오는 urlPatterns으로 WebMvcConfig클래스에서 
    		 * /images/** 경로를 설정해주었습니다. */
    		imgUrl = "/images/item/" + imgName;
    	}
    	//save로 상품 이미지 정보 저장
    	itemImg.updateItemImg(oriImgName, imgName, imgUrl);
    	itemImgRepository.save(itemImg);
    }
    
    /* 상품 이미지 수정 - 변경감지 연산 이용 */
    public void updateItemImg(Long itemImgId,MultipartFile itemImgFile)
    	throws Exception {
    	if(!itemImgFile.isEmpty()) {
    		//상품 이미지id를 이용해, 상품이미지 엔티티를 조회했다
    		ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
    				.orElseThrow(EntityNotFoundException::new);
    		
    		//기존 이미지 파일 삭제
    		if(!StringUtils.isEmpty(savedItemImg.getImgName())) {
    			fileService.deleteFile(itemImgLocation + File.separator + savedItemImg.getImgName());
    		}
    		//이미지 수정작업
    		String oriImgName = itemImgFile.getOriginalFilename();
    		String imgName = fileService.uploadFile(itemImgLocation, oriImgName,itemImgFile.getBytes());
    		String imgUrl = "/images/item/" + imgName;
    		savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
    		//변경된 상품 이미지 정보를 셋팅해주었따
    		//여기서 중요한 점은 상품 등록 때처럼 save()메서드를 호출하지 않았다.
    		//savedItemImg엔티티는 현재 영속 상태(영속성 컨텍스트 메모리에 올라옴!!) 이므로 데이터를 변경하는 것 만으로 변경감지 기능이 작동하여
    		//트랜잭션 종료 후, update쿼리가 실행된다, 중요한 것은 엔티티가 영속 상태여야 한다는 것이다.
    	}
    }
}
