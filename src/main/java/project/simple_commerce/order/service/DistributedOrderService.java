package project.simple_commerce.order.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
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
import project.simple_commerce.orderItem.entity.OrderItem;
import project.simple_commerce.orderItem.dto.CreateOrderItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DistributedOrderService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final RedissonClient redissonClient;

    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest request) {

        List<OrderItem> orderItems = new ArrayList<>();
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new NotFoundMemberException("회원을 찾을 수 없습니다. ID: " + request.memberId()));


        for(CreateOrderItemRequest itemRequest : request.items()){
            Item item = itemRepository.findById(itemRequest.itemId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + itemRequest.itemId()));

            String lockKey = "item:stock:" + item.getId();
            RLock lock = redissonClient.getLock(lockKey);

            try{
                boolean isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
                if(!isLocked){
                    System.out.println("현재 요청이 너무 많습니다.");
                }

                OrderItem orderItem = OrderItem.createOrderItem(item, itemRequest.count());
                orderItems.add(orderItem);
            }catch (InterruptedException e){
                throw new RuntimeException("락 대기 중 문제 발생");
            }finally {
                if(lock.isHeldByCurrentThread()){
                    lock.unlock();
                }
            }
        }

        Order order = Order.createOrder(member, orderItems);

        Order savedOrder = orderRepository.save(order);

        return CreateOrderResponse.from(savedOrder);
    }

}
