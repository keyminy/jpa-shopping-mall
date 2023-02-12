package com.shop.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import com.shop.dto.MemberFormDto;
import com.shop.repository.CartRepository;
import com.shop.repository.MemberRepository;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
public class CartTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;
    
    public Member createMember(){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }
    
    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest() {
    	Member member = createMember();
    	//member save
    	memberRepository.save(member);
    	
    	Cart cart = new Cart();
    	cart.setMember(member);
    	//cart save
    	cartRepository.save(cart);
    	
    	//영속성 컨텍스트에 저장된 것들 -> DB에 반영
    	em.flush();
    	//Cart select테스트(member도 같이 조회?)를 위해 영속성 컨텍스트 비워주기
    	em.clear();
    	
    	Cart savedCart = cartRepository.findById(cart.getId())
    								.orElseThrow(EntityNotFoundException::new);
    	//처음에 저장한 member엔티티의 id와 savedCart에 매핑된 member엔티티의 id비교
    	assertEquals(savedCart.getMember().getId(), member.getId());
    }
}
