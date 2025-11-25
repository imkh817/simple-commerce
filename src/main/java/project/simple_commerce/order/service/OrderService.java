package project.simple_commerce.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.simple_commerce.item.entity.Item;
import project.simple_commerce.item.repository.ItemRepository;
import project.simple_commerce.member.entity.Member;
import project.simple_commerce.member.exception.NotFoundMemberException;
import project.simple_commerce.member.repository.MemberRepository;
import project.simple_commerce.order.dto.OrderEvent;
import project.simple_commerce.order.dto.create.CreateOrderRequest;
import project.simple_commerce.order.dto.create.CreateOrderResponse;
import project.simple_commerce.order.entity.Order;
import project.simple_commerce.order.repository.OrderRepository;
import project.simple_commerce.orderItem.entity.OrderItem;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NotFoundMemberException("회원을 찾을 수 없습니다. ID: " + request.memberId()));

        List<OrderItem> orderItems = request.items().stream()
                .map(itemRequest -> {
                    Item item = itemRepository.findById(itemRequest.itemId())
                            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + itemRequest.itemId()));
                    return OrderItem.createOrderItem(item, itemRequest.count());
                })
                .toList();

        Order order = Order.createOrder(member, orderItems);
        Order savedOrder = orderRepository.save(order);

        OrderEvent event = new OrderEvent(order.getId(), order.getMember().getId());
        kafkaTemplate.send("order-topic", event);

        return CreateOrderResponse.from(savedOrder);
    }

}