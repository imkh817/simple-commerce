package project.simple_commerce.delivery.entity;

import jakarta.persistence.*;
import project.simple_commerce.delivery.enums.DeliveryStatus;
import project.simple_commerce.member.vo.Address;
import project.simple_commerce.order.dto.OrderEvent;
import project.simple_commerce.order.entity.Order;

@Entity
public class Delivery {
    @Id @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    // == 생성 메서드 == //
    public static Delivery createDelivery(Order order){
        return null;
    }

}
