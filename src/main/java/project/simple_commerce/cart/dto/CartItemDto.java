package project.simple_commerce.cart.dto;

import project.simple_commerce.cartItem.entity.CartItem;

public record CartItemDto(
        Long itemId,
        String itemName,
        int itemPrice,
        int totalPrice,
        int quantity
) {

    public static CartItemDto from(CartItem cartItem){
        return new CartItemDto(
                cartItem.getItem().getId(),
                cartItem.getItem().getItemName(),
                cartItem.getItem().getPrice(),
                cartItem.getTotalPrice(),
                cartItem.getQuantity());
    }
}
