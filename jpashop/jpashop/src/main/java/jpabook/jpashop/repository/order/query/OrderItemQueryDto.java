package jpabook.jpashop.repository.order.query;

import lombok.Data;

@Data
public class OrderItemQueryDto {

    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int orderCount;

    public OrderItemQueryDto(Long id, String itemName, int orderPrice, int orderCount) {
        this.orderId = id;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.orderCount = orderCount;
    }
}
