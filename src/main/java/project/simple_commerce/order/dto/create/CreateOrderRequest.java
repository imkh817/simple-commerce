package project.simple_commerce.order.dto.create;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import project.simple_commerce.orderItem.dto.CreateOrderItemRequest;

import java.util.List;

public record CreateOrderRequest(
        @NotNull(message = "회원 ID는 필수입니다")
        Long memberId,

        @NotEmpty(message = "주문 항목은 최소 1개 이상이어야 합니다")
        @Valid
        List<CreateOrderItemRequest> items
) {}
