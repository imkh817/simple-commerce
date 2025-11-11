package project.simple_commerce.order.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.simple_commerce.order.dto.create.CreateOrderRequest;
import project.simple_commerce.order.dto.create.CreateOrderResponse;
import project.simple_commerce.orderItem.dto.CreateOrderItemRequest;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DistributedOrderFacade {

    private final RedissonClient redissonClient;
    private final OrderService orderService;

    public CreateOrderResponse createOrderWithDistributedLock(CreateOrderRequest request) {

        List<RLock> locks = request.items().stream()
                .map(item -> redissonClient.getLock("item:stock:" + item.itemId()))
                .toList();

        RLock multiLock = redissonClient.getMultiLock(locks.toArray(new RLock[0]));
        try {
            boolean isLocked = multiLock.tryLock(10, 5, TimeUnit.SECONDS);

            if (!isLocked) {
                throw new RuntimeException("현재 요청이 너무 많습니다. (락 획득 실패)");
            }

            return orderService.createOrder(request);

        } catch (InterruptedException e) {
            throw new RuntimeException("락 대기 중 문제 발생");
        } finally {
            if (multiLock.isHeldByCurrentThread()) {
                multiLock.unlock();
            }
        }
    }
}

