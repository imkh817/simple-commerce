package project.simple_commerce.cart.dto;

public record CreateCartRequestDto(
        Long memberId,
        Long itemId,
        int quantity
) {
}
