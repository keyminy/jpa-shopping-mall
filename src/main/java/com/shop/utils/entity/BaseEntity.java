package com.shop.utils.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@EntityListeners(value = {AuditingEntityListener.class}) //Auditing을 적용한다
@MappedSuperclass //이 클래스를 상속받는 자식클래스에 매핑 정보를 제공한다.(공통 매핑정보제공)
@Getter
@Setter
public abstract class BaseEntity extends BaseTimeEntity{
	
	@CreatedBy
	@Column(updatable = false)
	private String createdBy;
	
	@LastModifiedBy
	private String modifiedBy;
}
