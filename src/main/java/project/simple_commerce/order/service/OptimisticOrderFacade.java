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
                    log.info("주문 생성 성공 (재시도 {}회): orderId={}",
                            attempt, response.orderId());
                }

                return response;

            } catch (ObjectOptimisticLockingFailureException e) {
                if (attempt == maxRetries - 1) {
                    log.error("주문 생성 최종 실패 (재시도 {}회): memberId={}",
                            attempt, request.memberId());

                    request.items().forEach(item ->
                            log.error("  - 상품ID: {}, 수량: {}",
                                    item.itemId(), item.count())
                    );

                    throw new ConcurrentOrderException(
                            "현재 많은 주문이 처리되고 있습니다. 잠시 후 다시 시도해주세요."
                    );
                }

                log.warn("낙관적 락 충돌 발생 ({}회 재시도): memberId={}",
                        attempt + 1, request.memberId());

                long delay = 50L * (long) Math.pow(2, attempt);
                Thread.sleep(Math.min(delay, 500L));
            }
        }

        throw new IllegalStateException("예상치 못한 오류 발생");
    }
}
