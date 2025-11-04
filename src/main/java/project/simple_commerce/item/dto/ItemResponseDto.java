package project.simple_commerce.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import project.simple_commerce.item.entity.Item;

import static lombok.AccessLevel.PROTECTED;

@Getter
@AllArgsConstructor(access = PROTECTED)
public class ItemResponseDto {
    private Long id;
    private String itemName;
    private int price;

    public static ItemResponseDto from(Item item){
        return new ItemResponseDto(
                item.getId(),
                item.getItemName(),
                item.getPrice()
        );
    }
}
