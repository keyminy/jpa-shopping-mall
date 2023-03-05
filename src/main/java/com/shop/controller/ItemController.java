package com.shop.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.shop.dto.ItemFormDto;
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
 
}