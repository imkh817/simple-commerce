package project.simple_commerce.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.simple_commerce.item.entity.Item;
import project.simple_commerce.item.repository.ItemRepository;
import project.simple_commerce.member.entity.Member;
import project.simple_commerce.member.exception.NotFoundMemberException;
import project.simple_commerce.member.repository.MemberRepository;
import project.simple_commerce.order.dto.create.CreateOrderRequest;
import project.simple_commerce.order.dto.create.CreateOrderResponse;
import project.simple_commerce.order.entity.Order;
import project.simple_commerce.order.exception.ConcurrentOrderException;
import project.simple_commerce.order.repository.OrderRepository;
import project.simple_commerce.orderItem.OrderItem;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    public CreateOrderResponse createOrderWithRetry(CreateOrderRequest request){

        int maxRetries = 5;

        for(int attempt = 0; attempt < maxRetries; attempt++){
            try{
                CreateOrderResponse response = createOrder(request);

                if(attempt > 0){
                    log.info("주문 생성 성공 (재시도 {}회): orderId={}",
                            attempt, response.orderId());
                }
                return response;
            }catch (Exception e){
                if(attempt == maxRetries - 1){
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

                try {
                    long delay = 50L * (long) Math.pow(2, attempt);
                    Thread.sleep(Math.min(delay, 500L));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("주문 처리 중 인터럽트 발생", ie);
                }
            }
        }

        throw new IllegalStateException("예상치 못한 오류 발생");
    }

    @Transactional
    public CreateOrderResponse createOrder_back(CreateOrderRequest request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NotFoundMemberException("회원을 찾을 수 없습니다. ID: " + request.memberId()));

        OrderItem[] orderItems = request.items().stream()
                .map(itemRequest -> {
                    Item item = itemRepository.findById(itemRequest.itemId())
                            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + itemRequest.itemId()));
                    return OrderItem.createOrderItem(item, itemRequest.count());
                })
                .toArray(OrderItem[]::new);

        Order order = Order.createOrder(member, orderItems);

        Order savedOrder = orderRepository.save(order);

        return CreateOrderResponse.from(savedOrder);
    }

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NotFoundMemberException("회원을 찾을 수 없습니다. ID: " + request.memberId()));

        OrderItem[] orderItems = request.items().stream()
                .map(itemRequest -> {
                    Item item = itemRepository.findById(itemRequest.itemId())
                            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + itemRequest.itemId()));
                    return OrderItem.createOrderItem(item, itemRequest.count());
                })
                .toArray(OrderItem[]::new);

        Order order = Order.createOrder(member, orderItems);

        Order savedOrder = orderRepository.save(order);

        return CreateOrderResponse.from(savedOrder);
    }

}