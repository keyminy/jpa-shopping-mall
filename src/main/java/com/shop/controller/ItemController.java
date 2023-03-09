package com.shop.controller;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.service.ItemService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
    	model.addAttribute("itemFormDto",new ItemFormDto());
        return "/item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){
    	if(bindingResult.hasErrors()) {
    		//상품 등록 시, 필수값이 없으면 다시 상품 등록 페이지로 전환
    		return "item/itemForm";
    	}
    	/*상품 등록 시, 첫 번째 이미지가 없다면 에러 메시지와 함께 상품 등록 페이지로 전환
    	 * 상품의 첫 번째 이미지는 메인 페이지에서 보여줄 상품 이미지로 사용하기 위해 필수값으로 지정함*/
    	if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
    		model.addAttribute("errorMessage","첫번째 상품 이미지는 필수 입력 값 입니다.");
    		return "item/itemForm";
    	}
    	
    	try {
			itemService.saveItem(itemFormDto, itemImgFileList);
		} catch (Exception e) {
			model.addAttribute("errorMessage","상품 등록 중 에러가 발생했습니다.");
			return "item/itemForm";
		}
    	return "redirect:/";
    }
    
    //상품 수정
    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId,Model model) {
    	try {
    		ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
			model.addAttribute("itemFormDto",itemFormDto);
		} catch (EntityNotFoundException e) {
			model.addAttribute("errorMessage","존재하지 않는 상품 입니다.");
			model.addAttribute("itemFormDto",new ItemFormDto());
			return "item/itemForm";
		}
    	return "item/itemForm";
    }
    
    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model){
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    } 
    
    //관리자 상품관리화면 페이지 이동
    //URL에 {page}번호가 있는 경우와 없는 경우로 나뉜다
    @GetMapping(value = {"/admin/items","/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto
			 ,@PathVariable("page") Optional<Integer> page,Model model){
    	//PageRequest.of 메소드 : Pageable객체를 생성한다.
    	//첫번째 파라미터 : 조회할 페이지 번호, 두번째 파라미터 : 한 번에 가지고 올 데이터 수(size)
    	Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
    	Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
    	model.addAttribute("items",items);
    	model.addAttribute("itemSearchDto",itemSearchDto);
    	model.addAttribute("maxPage",5); //상품 관리 메뉴 하단의 페이지 번호의 최대 갯수
    	return "item/itemMng";
    }
}