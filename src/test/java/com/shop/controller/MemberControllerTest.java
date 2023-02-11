package com.shop.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

import javax.transaction.Transactional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc //MockMvc테스트를 위한 어노테이션 선언
@TestPropertySource(locations="classpath:application-test.properties")
public class MemberControllerTest {
	
	@Autowired
	private MemberService memberService;
	
	/*MockMvc : 실제 객체와 비슷하지만 테스트에 필요한 기능만을 가지는 가짜 객체
	 * MockMvc 객체를 이용하면 웹 브라우저에서 요청을 하는 것처럼 테스트 가능 */
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public Member createMember(String email,String password) {
		MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail(email);
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword(password);
        Member member = Member.createMember(memberFormDto, passwordEncoder);
        return memberService.saveMember(member);
	}
	
    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginSuccessTest() throws Exception{
        String email = "test@email.com";
        String password = "1234";
        this.createMember(email, password);
        //formLogin()은 SecurityMockMvcRequestBuilders클래스에 있어요
        mockMvc.perform(formLogin().userParameter("email")
                .loginProcessingUrl("/members/login")
                .user(email).password(password))
        		//가입된 회원 정보로 로그인되??
                .andExpect(SecurityMockMvcResultMatchers.authenticated());
    }
    
    @Test
    @DisplayName("로그인 실패 테스트")
    public void loginFailTest() throws Exception{
        String email = "test@email.com";
        String password = "1234";
        this.createMember(email, password);
        mockMvc.perform(formLogin().userParameter("email")
                .loginProcessingUrl("/members/login")
                .user(email).password("12345"))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }
}
