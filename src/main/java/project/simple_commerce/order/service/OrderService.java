package project.simple_commerce.order.service;

import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
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
import project.simple_commerce.order.repository.OrderRepository;
import project.simple_commerce.orderItem.OrderItem;

@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class OrderService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    //@Transactional
    public synchronized CreateOrderResponse createOrder(CreateOrderRequest request) {
        // 1. 회원 조회
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NotFoundMemberException("회원을 찾을 수 없습니다. ID: " + request.memberId()));

        // 2. 주문 항목 생성
        OrderItem[] orderItems = request.items().stream()
                .map(itemRequest -> {
                    // 상품 조회
                    Item item = itemRepository.findById(itemRequest.itemId())
                            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + itemRequest.itemId()));

                    // OrderItem 생성
                    return OrderItem.createOrderItem(item, itemRequest.count());
                })
                .toArray(OrderItem[]::new);

        // 3. 주문 생성
        Order order = Order.createOrder(member, orderItems);

        // 4. 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 5. 응답 DTO 변환 및 반환
        return CreateOrderResponse.from(savedOrder);
    }
}