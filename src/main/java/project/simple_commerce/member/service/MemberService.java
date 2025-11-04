package project.simple_commerce.member.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.simple_commerce.member.dto.MemberResponseDto;
import project.simple_commerce.member.dto.create.CreateMemberRequest;
import project.simple_commerce.member.dto.create.CreateMemberResponse;
import project.simple_commerce.member.entity.Member;
import project.simple_commerce.member.exception.NotFoundMemberException;
import project.simple_commerce.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;


    public List<MemberResponseDto> findAll() {
        return memberRepository.findAll().stream()
                .map(MemberResponseDto::from)
                .toList();

    }

    public MemberResponseDto findMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundMemberException("User not found with id: " + id));

        return MemberResponseDto.from(member);
    }

    @Transactional
    public CreateMemberResponse createMember(CreateMemberRequest createMemberRequest) {
        Member member = Member.builder()
                .username(createMemberRequest.getUsername())
                .password(createMemberRequest.getUserpassword())
                .build();

        Member savedMember = memberRepository.save(member);
        return CreateMemberResponse.from(savedMember);
    }
}

