package com.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfig implements WebMvcConfigurer {

	@Value("${uploadPath}")
	String uploadPath;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//url이 /images로 시작하는 경우,uploadPath에 설정한 폴더 기준으로 파일 읽도록 설정
		registry.addResourceHandler("/images/**") 
			.addResourceLocations(uploadPath);
		//addResourceLocations : 로컬 컴퓨터에 저장된 파일을 읽어올 root경로 설정
	}
}
