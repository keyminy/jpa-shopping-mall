package com.shop.config;


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

import lombok.extern.log4j.Log4j2;

@Configuration
@EnableWebSecurity
@Log4j2
public class SecurityConfig{
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		log.info("---------configure-------------");
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
