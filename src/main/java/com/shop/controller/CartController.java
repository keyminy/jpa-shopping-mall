package com.shop.controller;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shop.dto.CartItemDto;
import com.shop.service.CartService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@PostMapping(value = "/cart")
	public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto
			, BindingResult bindingResult,	Principal principal) {

		if (bindingResult.hasErrors()) {
			StringBuilder sb = new StringBuilder();
			List<FieldError> fieldErrors = bindingResult.getFieldErrors();

			for (FieldError fieldError : fieldErrors) {
				sb.append(fieldError.getDefaultMessage());
			}

			return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
		}

		String email = principal.getName();
		Long cartItemId;

		try {
			//장바구니에 담을 상품 정보와, 현재 로그인한 회원의 이메일 정보를 이용해 장바구니에 상품을 담는 로직 호출
			cartItemId = cartService.addCart(cartItemDto, email);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		//결과값으로 생성된 장바구니 상품 아이디와 요청 성공 응답 상태코드 반환
		return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
	}

}
