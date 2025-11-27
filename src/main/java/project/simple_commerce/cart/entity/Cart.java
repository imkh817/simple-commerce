package project.simple_commerce.cart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import project.simple_commerce.cartItem.entity.CartItem;
import project.simple_commerce.member.entity.Member;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Cart {

    @Id @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.REMOVE)
    private List<CartItem> cartItems = new ArrayList<>();

    private int totalPrice;

    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.member = member;
        return cart;
    }

    public void addCartItem(CartItem cartItem){
        cartItems.add(cartItem);
    }

}
