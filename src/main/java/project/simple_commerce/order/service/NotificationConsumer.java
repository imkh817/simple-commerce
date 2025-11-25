package project.simple_commerce.order.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import project.simple_commerce.order.dto.OrderEvent;

//@Service

@Slf4j
@Component
public class NotificationConsumer {
    @KafkaListener(
            topics = "order-topic",
            groupId = "notification-group"
    )
    public void handleOrderEvent(OrderEvent orderEvent) {
        log.info("Received order event: {}", orderEvent);
        log.info("사용자에게 메일 발송... 사용자 id: {}", orderEvent.userId());
    }
}
