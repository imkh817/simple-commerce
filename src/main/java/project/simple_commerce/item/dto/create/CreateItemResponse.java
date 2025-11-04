package project.simple_commerce.item.dto.create;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.simple_commerce.item.entity.Item;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class CreateItemResponse {
    private Long id;
    private String itemName;
    private int price;

    public static CreateItemResponse from(Item item){
        return new CreateItemResponse(
                item.getId(),
                item.getItemName(),
                item.getPrice()
        );
    }

}
