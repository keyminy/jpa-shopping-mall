package com.shop.config;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<String>{

	@Override
	public Optional<String> getCurrentAuditor() {
		//감시동작은 인증 받은 후에 수행합니다.
		Authentication authentication =
				SecurityContextHolder.getContext().getAuthentication();
		//authentication객체 : 인증관련 정보를 담은 객체
		String userId = "";
		if(authentication != null) {
			userId = authentication.getName();
		}
		
		return Optional.of(userId);
	}

}
