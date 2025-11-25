package project.simple_commerce.delivery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.simple_commerce.order.dto.OrderEvent;
import project.simple_commerce.order.entity.Order;
import project.simple_commerce.order.exception.NotFoundOrderException;
import project.simple_commerce.order.repository.OrderRepository;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(
            topics = "order-topic",
            groupId = "delivery-group"
    )
    @Transactional
    public void startDelivery(OrderEvent orderEvent){

        Order order = orderRepository.findById(orderEvent.orderId())
                .orElseThrow(() -> new NotFoundOrderException("주문을 찾을 수 없습니다. ID: " + orderEvent.orderId()));



        log.info("배송 시작...");
    }
}
