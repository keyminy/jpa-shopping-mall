package com.shop.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;
    
    public Long addCart(CartItemDto cartItemDto, String email){
    	//장바구니에 담을 상품 엔티티 조회
    	Item item = itemRepository.findById(cartItemDto.getItemId())
    			  .orElseThrow(EntityNotFoundException::new);
    	//현재 로그인한 회원 엔티티 조회
    	Member member = memberRepository.findByEmail(email);
    	//현재 로그인한 회원의 장바구니 엔티티 조회
    	Cart cart = cartRepository.findByMemberId(member.getId());
    	//상품을 처음으로 담음(cart가 null일때, "해당" 회원의 장바구니 엔티티 생성)
    	if(cart == null) {
    		//cart.setMember(member); return cart
    		cart = Cart.createCart(member);
    		cartRepository.save(cart);
    	}

    	//현재 상품이 장바구니에 이미 들어가 있는지 조회
    	CartItem savedCartItem =
    			cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

    	if(savedCartItem != null){
    		 System.out.println("이미존재");
    		//장바구니에 이미 존재하는 상품일 경우, 기존 수량에 현재 장바구니에 담을 수량만큼 더함
             savedCartItem.addCount(cartItemDto.getCount());
             return savedCartItem.getId();
        } else {
        	 System.out.println("없음");
             CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
             cartItemRepository.save(cartItem);
             return cartItem.getId();
        }
    }
    
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();
        Member member = memberRepository.findByEmail(email);
        //현재 로그인한 회원의 장바구니 엔티티 조회
        Cart cart = cartRepository.findByMemberId(member.getId());
        //장바구니에 상품을 한번도 담지 않은 회원일 경우 빈 리스트 반환
        if(cart == null){
            return cartDetailDtoList;
        }
        //장바구니에 담겨있는 상품 정보 조회(cart의 Id로)
        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }
    
    /*장바구니 수량 수정*/
    public void updateCartItemCount(Long cartItemId, int count){
    	//장바구니의 상품 수량을 update합니다.
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }
    
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
    	//현재 로그인한 회원 조회
        Member curMember = memberRepository.findByEmail(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        //장바구니 상품을 저장한 회원 조회.. 연관관계여러번!!..
        Member savedMember = cartItem.getCart().getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }

        return true;
    }
}
