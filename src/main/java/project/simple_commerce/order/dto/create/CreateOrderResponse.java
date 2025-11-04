package project.simple_commerce.order.dto.create;

import lombok.Builder;
import project.simple_commerce.order.entity.Order;
import project.simple_commerce.orderItem.dto.CreateOrderItemResponse;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CreateOrderResponse(
        Long orderId,
        Long memberId,
        LocalDateTime orderDate,
        List<CreateOrderItemResponse> items,
        int totalPrice
) {
    public static CreateOrderResponse from(Order order) {
        return CreateOrderResponse.builder()
                .orderId(order.getId())
                .memberId(order.getMember().getId())
                .orderDate(order.getOrderDate())
                .items(order.getOrderItems().stream()
                        .map(CreateOrderItemResponse::from)
                        .toList())
                .totalPrice(order.getTotalPrice())
                .build();
    }
}
