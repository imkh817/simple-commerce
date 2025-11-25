package project.simple_commerce.cart.dto;

import project.simple_commerce.cart.entity.Cart;
import project.simple_commerce.item.entity.Item;

public record CreateCartResponseDto(Long cartId, Long itemId, int quantity, boolean isNewItem, int totalItemCount)
{
    public static CreateCartResponseDto of(Cart cart, Item item, int quantity, boolean isNewItem){
        int totalCount = cart.getCartItems().size();
        return new CreateCartResponseDto(cart.getId(), item.getId(), quantity, isNewItem, totalCount);
    }
}
