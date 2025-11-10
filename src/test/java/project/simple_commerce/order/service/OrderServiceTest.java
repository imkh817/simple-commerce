package project.simple_commerce.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import project.simple_commerce.item.entity.Item;
import project.simple_commerce.item.repository.ItemRepository;
import project.simple_commerce.member.entity.Member;
import project.simple_commerce.member.repository.MemberRepository;
import project.simple_commerce.orderItem.dto.CreateOrderItemRequest;
import project.simple_commerce.order.dto.create.CreateOrderRequest;
import project.simple_commerce.order.dto.create.CreateOrderResponse;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OptimisticOrderFacade optimisticOrderFacade;

    @Autowired
    PessimisticOrderService pessimisticOrderService;

    private Member member;
    private Item item;

    @BeforeEach
    void setUp() {
        // 테스트용 회원 생성
        member = Member.builder()
                .username("testUser")
                .password("password123")
                .build();
        memberRepository.save(member);

        // 테스트용 상품 생성 (재고 100개)
        item = Item.builder()
                .itemName("테스트 상품")
                .price(10000)
                .stockQuantity(100)
                .build();
        itemRepository.save(item);
    }

    @Test
    @DisplayName("동시성 테스트 [비관적 락] - 100개 재고에 100명이 동시에 1개씩 주문")
    void concurrent_order_success() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // when
        for(int i = 0; i < threadCount; i++){
            executorService.submit(()->{
                try{
                    CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                            member.getId(),
                            List.of(new CreateOrderItemRequest(item.getId(), 1)));

                    pessimisticOrderService.createOrder(createOrderRequest);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.out.println("주문 실패: " + e.getMessage());
                    System.out.println(e.getMessage());
                }finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        // then
        Item updatedItem = itemRepository.findById(item.getId()).orElse(null);

        System.out.println("=== 동시성 테스트 결과 ===");
        System.out.println("성공한 주문: " + successCount.get());
        System.out.println("실패한 주문: " + failureCount.get());
        System.out.println("남은 재고: " + updatedItem.getStockQuantity());

        assertThat(successCount.get()).isEqualTo(100);
        assertThat(failureCount.get()).isEqualTo(0);
        assertThat(updatedItem.getStockQuantity()).isEqualTo(0);


    }

    @Test
    @DisplayName("동시성 테스트 [낙관적 락] - 100개 재고에 100명이 동시에 1개씩 주문")
    void optimistic_lock_success() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // when
        for(int i = 0; i < threadCount; i++){
            executorService.submit(()->{
                try{
                    CreateOrderRequest createOrderRequest = new CreateOrderRequest(
                            member.getId(),
                            List.of(new CreateOrderItemRequest(item.getId(), 1)));

                    orderService.createOrderWithRetry(createOrderRequest);
                    successCount.incrementAndGet();
                }catch(Exception e){
                    failureCount.incrementAndGet();
                    System.out.println("주문 실패: " + e.getMessage());
                    assertThat(e).isInstanceOf(ObjectOptimisticLockingFailureException.class);
                }finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        // then
        Item updatedItem = itemRepository.findById(item.getId()).orElse(null);

        System.out.println("=== 동시성 테스트 결과 ===");
        System.out.println("성공한 주문: " + successCount.get());
        System.out.println("실패한 주문: " + failureCount.get());
        System.out.println("남은 재고: " + updatedItem.getStockQuantity());
    }



    @Test
    @DisplayName("동시성 테스트 - 100개 재고에 150명이 동시에 1개씩 주문 (50명 실패해야 함)")
    void concurrent_order_with_stock_shortage() throws InterruptedException {
        // given
        int threadCount = 150;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    CreateOrderRequest request = new CreateOrderRequest(
                            member.getId(),
                            List.of(new CreateOrderItemRequest(item.getId(), 1))
                    );
                    orderService.createOrder(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();

        System.out.println("=== 재고 부족 동시성 테스트 결과 ===");
        System.out.println("성공한 주문: " + successCount.get());
        System.out.println("실패한 주문: " + failCount.get());
        System.out.println("남은 재고: " + updatedItem.getStockQuantity());

        // 100개만 성공하고, 50개는 실패해야 함
        assertThat(successCount.get()).isEqualTo(100);
        assertThat(failCount.get()).isEqualTo(50);
        assertThat(updatedItem.getStockQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("단일 주문 성공 테스트")
    void create_order_success() {
        // given
        CreateOrderRequest request = new CreateOrderRequest(
                member.getId(),
                List.of(new CreateOrderItemRequest(item.getId(), 5))
        );

        // when
        CreateOrderResponse response = orderService.createOrder(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.orderId()).isNotNull();
        assertThat(response.memberId()).isEqualTo(member.getId());
        assertThat(response.totalPrice()).isEqualTo(50000); // 10000 * 5
        assertThat(response.items()).hasSize(1);

        // 재고 확인
        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(updatedItem.getStockQuantity()).isEqualTo(95); // 100 - 5
    }

    @Test
    @DisplayName("재고 부족 시 주문 실패 테스트")
    void create_order_fail_due_to_insufficient_stock() {
        // given - 재고 100개인데 101개 주문
        CreateOrderRequest request = new CreateOrderRequest(
                member.getId(),
                List.of(new CreateOrderItemRequest(item.getId(), 101))
        );

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            orderService.createOrder(request);
        });

        // 재고는 그대로 100개여야 함 (트랜잭션 롤백)
        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertThat(updatedItem.getStockQuantity()).isEqualTo(100);
    }
}
