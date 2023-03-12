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

import com.shop.dto.OrderDto;
import com.shop.service.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;

	@PostMapping(value = "/order")
	public @ResponseBody ResponseEntity<?> order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult,
			Principal principal) {
		if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
		String email = principal.getName();
		System.out.println("email : " + email);
		Long orderId;
		try {
			orderId = orderService.order(orderDto, email);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		//주문이 완료된 생성된 주문 번호와 요청 성공 응답 상태코드 반환
		return new ResponseEntity<Long>(orderId, HttpStatus.OK);
	}
}
