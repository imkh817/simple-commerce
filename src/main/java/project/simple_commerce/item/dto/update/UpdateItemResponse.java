package project.simple_commerce.item.dto.update;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import project.simple_commerce.item.entity.Item;

import static lombok.AccessLevel.PROTECTED;

@Getter
@AllArgsConstructor(access = PROTECTED)
public class UpdateItemResponse {
    private String itemName;
    private int price;

    public static UpdateItemResponse from(Item item){
        return new UpdateItemResponse(
                item.getItemName(),
                item.getPrice()
        );
    }
}
