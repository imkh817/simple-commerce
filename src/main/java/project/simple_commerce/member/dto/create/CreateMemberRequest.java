package project.simple_commerce.member.dto.create;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class CreateMemberRequest {
    @NotEmpty(message = "이름은 필수 값입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수 값입니다.")
    private String userpassword;
}
