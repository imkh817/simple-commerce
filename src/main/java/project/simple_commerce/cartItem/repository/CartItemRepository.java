package project.simple_commerce.cartItem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.simple_commerce.cart.entity.Cart;
import project.simple_commerce.cartItem.entity.CartItem;
import project.simple_commerce.item.entity.Item;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCartAndItem(Cart cart, Item item);
}
