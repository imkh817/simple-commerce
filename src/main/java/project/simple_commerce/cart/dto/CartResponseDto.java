package project.simple_commerce.cart.dto;

import project.simple_commerce.cart.entity.Cart;
import project.simple_commerce.cartItem.entity.CartItem;

import java.util.List;

public record CartResponseDto(
        Long memberId,
        List<CartItemDto> cartItems
) {

    public static CartResponseDto from(Cart cart){
        List<CartItemDto> cartItemDtos = cart.getCartItems().stream().map(CartItemDto::from).toList();
        return new CartResponseDto(cart.getMember().getId(), cartItemDtos);
    }

    public static CartResponseDto empty(Long memberId){
        return new CartResponseDto(memberId, List.of());
    }
}
