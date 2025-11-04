package project.simple_commerce.orderItem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderItemRequest(
        @NotNull(message = "상품 ID는 필수입니다")
        Long itemId,

        @Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다")
        int count
) {}
