package com.shop.utils.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@EntityListeners(value = {AuditingEntityListener.class}) //Auditing을 적용한다
@MappedSuperclass //이 클래스를 상속받는 자식클래스에 매핑 정보를 제공한다.(공통 매핑정보제공)
@Getter
@Setter
public abstract class BaseTimeEntity {
	
	//엔티티가 생성되어 저장될 때, 시간 자동 저장
	@CreatedDate //작성일
	@Column(updatable = false) //등록일은 한번 등록되고 고정이니까
	private LocalDateTime regTime;
	
	@LastModifiedDate //가장 먼저 수정된 날
	//엔티티 값 변경 시, 시간을 자동으로 저장
	private LocalDateTime updateTime;
}
