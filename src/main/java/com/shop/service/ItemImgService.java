package com.shop.service;

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
}
