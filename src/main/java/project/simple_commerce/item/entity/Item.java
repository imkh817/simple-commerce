package project.simple_commerce.item.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.internal.build.AllowSysOut;
import project.simple_commerce.item.dto.update.UpdateItemRequest;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Item {
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;
    @Column(nullable = false)
    private String itemName;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private int stockQuantity;  // 재고 수량
    //@Version
    public Long version;

    @Builder
    public Item(String itemName, int price, int stockQuantity){
        this.itemName = itemName;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public void updateInfo(UpdateItemRequest updateItemRequest) {
        this.itemName = updateItemRequest.getItemName();
        this.price = updateItemRequest.getPrice();
    }

    // === 비즈니스 로직 === //

    /**
     * 재고 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * 재고 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + this.stockQuantity + ", 요청 수량: " + quantity);
        }
        this.stockQuantity = restStock;
    }
}
