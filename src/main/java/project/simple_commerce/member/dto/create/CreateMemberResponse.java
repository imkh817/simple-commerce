package project.simple_commerce.member.dto.create;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import project.simple_commerce.member.entity.Member;

import static lombok.AccessLevel.*;

@Getter
@AllArgsConstructor(access = PROTECTED)
public class CreateMemberResponse {
    private Long id;
    private String username;

    public static CreateMemberResponse from(Member member){
        return new CreateMemberResponse(
                member.getId(),
                member.getUsername()
        );
    }
}
