package project.simple_commerce.item.dto.update;

import lombok.Getter;

@Getter
public class UpdateItemRequest {
    private String itemName;
    private int price;
}
