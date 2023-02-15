package com.shop.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.shop.constant.OrderStatus;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name ="orders")
@Getter
@Setter
public class Order {
	
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY) //한 명의 회원은 여러 주문 가능
    @JoinColumn(name = "member_id")
    private Member member;
    
    private LocalDateTime orderDate; //주문일
    
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; //주문 상태
    
    //@OneToMany는 기본 LAZY지만,헷갈리니까 걍 붙인다.
    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL
    			,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    /* 주문은 언제 주문했고, 언제 주문 변경이 일어 났는지가 중요하다*/
    private LocalDateTime regTime;
    
    private LocalDateTime updateTime;
}
