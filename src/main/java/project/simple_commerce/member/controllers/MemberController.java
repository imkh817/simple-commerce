package project.simple_commerce.member.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import project.simple_commerce.member.dto.MemberResponseDto;
import project.simple_commerce.member.dto.create.CreateMemberRequest;
import project.simple_commerce.member.dto.create.CreateMemberResponse;
import project.simple_commerce.member.service.MemberService;

import java.util.List;

@RequestMapping("/user")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public List<MemberResponseDto> findAll(){
        return memberService.findAll();
    }

    @GetMapping("/{id}")
    public MemberResponseDto findMember(@PathVariable Long id){
        return memberService.findMemberById(id);
    }

    @PostMapping
    public CreateMemberResponse createMember(@Valid @RequestBody CreateMemberRequest createMemberRequest){
        return memberService.createMember(createMemberRequest);
    }

}
