package project.simple_commerce.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.simple_commerce.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
