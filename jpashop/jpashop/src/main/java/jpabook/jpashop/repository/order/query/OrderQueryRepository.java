package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDto(){
        List<OrderQueryDto> result = findOrders();

        result.forEach(order ->{
            List<OrderItemQueryDto> orderItems = this.findOrderItems(order.getOrderId());
            order.setOrderItem(orderItems);
        });

        return result;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id,i.name, oi.orderPrice, oi.count)" +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId",orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findOrders() {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id,m.name,o.orderDate,o.status,d.address) from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    public List<OrderQueryDto> findOrderQueryDtoOptimization() {
        List<OrderQueryDto> orders = this.findOrders();

        List<Long> orderIds = orders.stream()
                .map(OrderQueryDto::getOrderId)
                .collect(Collectors.toList());

        List<OrderItemQueryDto> orderItems = em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id,i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream().collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
        orders.forEach(o -> o.setOrderItem(orderItemMap.get(o.getOrderId())));

        return orders;
    }

    public List<OrderFlatDto> findAllByDtoFlat() {
        return  em.createQuery(
                "select distinct new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id,m.name,o.orderDate,o.status,d.address,i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i" , OrderFlatDto.class)
                .getResultList();
    }
}
