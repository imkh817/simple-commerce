package project.simple_commerce.orderItem.dto;

import lombok.Builder;
import project.simple_commerce.orderItem.OrderItem;

@Builder
public record CreateOrderItemResponse(
        Long orderItemId,
        String itemName,
        int orderPrice,
        int count,
        int totalPrice
) {
    public static CreateOrderItemResponse from(OrderItem orderItem) {
        return CreateOrderItemResponse.builder()
                .orderItemId(orderItem.getId())
                .itemName(orderItem.getItem().getItemName())
                .orderPrice(orderItem.getOrderPrice())
                .count(orderItem.getCount())
                .totalPrice(orderItem.getTotalPrice())
                .build();
    }
}
