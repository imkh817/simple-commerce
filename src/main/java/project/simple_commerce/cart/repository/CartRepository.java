package project.simple_commerce.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.simple_commerce.cart.entity.Cart;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByMemberId(Long memberId);
}
