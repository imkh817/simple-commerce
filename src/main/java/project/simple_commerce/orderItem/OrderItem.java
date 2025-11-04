package project.simple_commerce.orderItem;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.simple_commerce.item.entity.Item;
import project.simple_commerce.order.entity.Order;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int orderPrice;  // 주문 당시 가격
    private int count;       // 주문 수량

    // === 생성 메서드 === //
    public static OrderItem createOrderItem(Item item, int count) {
        // 재고 차감
        item.removeStock(count);

        OrderItem orderItem = new OrderItem();
        orderItem.item = item;
        orderItem.orderPrice = item.getPrice();
        orderItem.count = count;
        return orderItem;
    }

    // === 연관관계 편의 메서드 === //
    public void setOrder(Order order) {
        this.order = order;
    }

    // === 비즈니스 로직 === //

    /**
     * 주문 취소 - 재고 복구
     */
    public void cancel() {
        item.addStock(count);
    }

    /**
     * 주문 항목 총 가격 조회
     */
    public int getTotalPrice() {
        return orderPrice * count;
    }
}
