package jpabook.jpashop.api.controller;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
* API 개발 고급 정리
*
* 여러 테이블을 가져올때 성능이 안나옴 -> fetch 조인을 사용
* 컬렉션 fetch 조인을 사용 시 페이징이 안됨
* -> ToOne 관계는 페치 조인을 하고
* 나머지 컬렉션은 은치조인 대신 지연로딩을 유지하고, hiubernate.default_batch_fetch_size, @BatchSize로 최적화
*
* 권장 순서
*
* 1.엔티티 조회방식으로 우선 접근
*   1-1. 페치 조인으로 쿼리 수를 최적화
*   1-2. 컬렉션 최적화
*       1-2-1. 페이징 필요 O hiubernate.default_batch_fetch_size, @BatchSize로 최적화
*       1-2-2. 페이징 필요 X -> 페치 조인 사용
* 2. 엔티티 조회 방식으로 해결이 안되면 DTO 조회 방식 사용
* 3. DTO 조회 방식으로 해결이 안되면 NativeSQL or 스프링 JdbcTemplate
*
* 참고 : 엔티티 조회 방식은 페치 조인이나 hiubernate.default_batch_fetch_size, @BatchSize 같이 코드를 거이
* 수정하지 않고, 옵션만 약간 변경해서, 다양한 성능 최적화를 시도할 수 있다. 반명네 DTO를 직접 조회하는 방식은 성능을 퇴적화 방식을 변경할 때
* 많은 코드를 변경해야 한다.
*
* 엔티티는 직접 캐싱을 하면 안된다!!!
*
* 참고: 개발자는 성능 최적화와 코드 복잡도 사이에서 줄타기를 해야한다. 항상 그런 것은 아니지만, 보통 성능최적화는 단순한 코드를 복잡한 코드를 몰고 간다.
* 엔티티 조회 방식은 Jpa가 많은 부분을 최적화 해주기 때문에, 단순한 코드를 유지하면서, 성능을 최적화 할 수 있다.
* 반면에 DTO 조회 방식은 SQL을 직접 다루는 것과 유사하기 때문에, 둘 사이에 줄타기를 해야한다.
*
* DTO 조회 방식은 선택지
* DTO로 조회하는 방법도 장단이 있다. 단순하게 쿼리가 한번 나가는것이 항상 좋은것은 아니다.
* */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    @GetMapping("/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAll(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.forEach(o -> o.getItem().getName());
        }

        return all;
    }

    @GetMapping("/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAll(new OrderSearch());

        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/v3/orders")
    public List<OrderDto> orderV2(){
        List<Order> orders = orderRepository.findAllWithItem();

        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/v3.1/orders")
    public List<OrderDto> orderV3_page(
            @RequestParam(value = "offset",defaultValue = "0")int offset,
            @RequestParam(value = "limit",defaultValue = "100")int limit
    ){
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset,limit);

        return orders.stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/v4/orders")
    public List<OrderQueryDto> orderV4(){
        return orderQueryRepository.findOrderQueryDto();
    }

    @GetMapping("/v5/orders")
    public List<OrderQueryDto> orderV5(){
        return orderQueryRepository.findOrderQueryDtoOptimization();
    }

    @GetMapping("/v6/orders")
    public List<OrderFlatDto> orderV6(){
        return orderQueryRepository.findAllByDtoFlat();
    }

    @Data
    static class OrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getMember().getAddress();
            this.orderItems = order.getOrderItems().stream().map(OrderItemDto::new).collect(Collectors.toList());
        }
    }

    @Data
    static class OrderItemDto{

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getTotalPrice();
        }
    }
}
