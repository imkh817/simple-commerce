package project.simple_commerce.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import project.simple_commerce.order.dto.create.CreateOrderRequest;
import project.simple_commerce.order.dto.create.CreateOrderResponse;
import project.simple_commerce.order.exception.ConcurrentOrderException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OptimisticOrderFacade {
    private final OrderService orderService;

    public CreateOrderResponse createOrderWithRetry(CreateOrderRequest request) throws InterruptedException {
        int maxRetries = 5;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                CreateOrderResponse response = orderService.createOrder(request);

                if (attempt > 0) {
                    log.info("주문 성공 ({}회 재시도 후): orderId={}", attempt, response.orderId());
                } else {
                    log.info("주문 성공 (1회 시도): orderId={}", response.orderId());
                }

                return response;

            } catch (ObjectOptimisticLockingFailureException e) {
                log.warn("낙관적 락 충돌 발생 ({}회차): memberId={}", attempt + 1, request.memberId());

                long delay = 100L * (long) Math.pow(2, attempt);
                Thread.sleep(Math.min(delay, 3000L));
            }
        }

        throw new ConcurrentOrderException("현재 많은 주문이 처리되고 있습니다. 잠시 후 다시 시도해주세요.");
    }
}
