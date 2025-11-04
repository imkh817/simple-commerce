package project.simple_commerce.orderItem.dto;

import lombok.Getter;
import project.simple_commerce.item.dto.ItemRequestDto;

import java.util.List;

@Getter
public class OrderItemRequestDto {
    private ItemRequestDto item;
    private int price;
}
