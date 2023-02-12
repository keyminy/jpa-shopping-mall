package com.shop.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.shop.service.MemberService;

import lombok.extern.log4j.Log4j2;

@Configuration
@EnableWebSecurity
@Log4j2
public class SecurityConfig{
	
	@Autowired
	MemberService memberService;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		log.info("---------configure-------------");
        http.formLogin()
	        .loginPage("/members/login")
	        .defaultSuccessUrl("/")
	        .usernameParameter("email")
	        .failureUrl("/members/login/error")
	        .and()
	        .logout()
	        .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) //logout URL지정
	        .logoutSuccessUrl("/");
		
        http.authorizeRequests()
	        .mvcMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()
	        .mvcMatchers("/admin/**").hasRole("ADMIN")
	        .anyRequest().authenticated();

		http.exceptionHandling()
			//인증되지 않은 사용자가 리소스에 접근하였을 때 수행되는 핸들러 설정
		        .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
		
		/*CsrfToken을 생성해주는 옵션을 추가
		 * https://cafe.naver.com/codefirst/348 */
		http.csrf()
			.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		return http.build();
	}
	
	/*정적으로 동작하는 파일들에 시큐리티를 적용할 필요가 없다*/
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		log.info("---------web configure-------------");
		return (web) -> web.ignoring()
							.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
