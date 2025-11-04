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
@Transactional(readOnly = true)
public class OrderService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public synchronized CreateOrderResponse createOrder(CreateOrderRequest request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NotFoundMemberException("회원을 찾을 수 없습니다. ID: " + request.memberId()));

        OrderItem[] orderItems = request.items().stream()
                .map(itemRequest -> {
                    Item item = itemRepository.findByIdWithPessimisticLock(itemRequest.itemId())
                            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + itemRequest.itemId()));
                    return OrderItem.createOrderItem(item, itemRequest.count());
                })
                .toArray(OrderItem[]::new);

        Order order = Order.createOrder(member, orderItems);

        Order savedOrder = orderRepository.save(order);

        return CreateOrderResponse.from(savedOrder);
    }
}