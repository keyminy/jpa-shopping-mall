package com.shop.service;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional //로직 처리 에러 시, 변경된 데이터를 로직을 수행하기 이전 상태로 콜백
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;


    public Member saveMember(Member member){
        validateDuplicateMember(member);//여기서 중복이면 IllegalStateException
        return memberRepository.save(member);
    }


    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }
}
