package project.simple_commerce.item.dto.create;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class CreateItemRequest {
    @NotEmpty(message = "품목 명은 필수 값입니다.")
    private String itemName;
    @Min(value = 1, message = "가격은 최소 1원 이상이여야 합니다.")
    private int price;
}
