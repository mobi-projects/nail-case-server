package com.nailcase.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nailcase.model.entity.Member;
import com.nailcase.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	
	private final MemberRepository memberRepository;

	public List<Member> getMembers() {
		return memberRepository.findAll();
	}
}
