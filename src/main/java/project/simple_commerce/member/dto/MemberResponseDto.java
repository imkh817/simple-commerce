package project.simple_commerce.member.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import project.simple_commerce.member.entity.Member;

import static lombok.AccessLevel.PROTECTED;

@Getter
@AllArgsConstructor(access = PROTECTED)
public class MemberResponseDto {
    private Long id;
    private String username;

    public static MemberResponseDto from(Member member){
        return new MemberResponseDto(
                member.getId(),
                member.getUsername()
        );
    }
}
