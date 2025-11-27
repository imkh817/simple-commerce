package project.simple_commerce.cartItem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import project.simple_commerce.cart.entity.Cart;
import project.simple_commerce.item.entity.Item;

@Getter
@Entity
public class CartItem {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // 상품

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    private int totalPrice;
    private int quantity; // 수량

    public static CartItem createCartItem(Cart cart, Item item, int quantity){
        CartItem cartItem = new CartItem();
        cartItem.cart = cart;
        cartItem.item = item;
        cartItem.quantity = quantity;
        cartItem.totalPrice = quantity * item.getPrice();
        return cartItem;
    }

    public void addQuantity(int quantity){
        this.quantity += quantity;
        addTotalPrice(quantity);
    }

    private void addTotalPrice(int quantity){
        int addPrice = quantity * this.item.getPrice();
        this.totalPrice += addPrice;
    }
}
