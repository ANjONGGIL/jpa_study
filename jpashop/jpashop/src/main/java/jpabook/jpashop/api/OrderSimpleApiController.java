package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAll(new OrderSearch());
        return all;
    } // 이렇듯 엔티티를 그대로 전달하는것은 매우 별로임 -> DTO 변환이 필요함

    @GetMapping("/v2/simple-orders")
    public List<SimpleOrderDto> orderV2(){
        List<Order> orders = orderRepository.findAll(new OrderSearch());
        return orders.stream().map(SimpleOrderDto::new).collect(Collectors.toList());

        /*
        * 엔티티를 DTO로 변환하는 일반적인 방법
        * 쿼리가 총 1 + N + N번 실행 된다.
        * 최악의 경우  n + 1 문제가 발생한다.
        * */
    }

    @GetMapping("/v3/simple-orders")
    public List<SimpleOrderDto> orderV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        return orders.stream().map(SimpleOrderDto::new).collect(Collectors.toList());
    }

    @GetMapping("/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4(){
        return orderSimpleQueryRepository.findOrderDtos();
        //v3 v4의 성늘의 우열을 정할 수는 없으나 v4의 성능의 경우를 생각했을때는 뛰어나나 확장성을 고려했을때는 v3가 더 좋다.
    }

    /*
    * 쿼리 방식 석택 권장 순서
    * 1. 우선 엔티티를 DTO 로 변환하는 방법을 선택한다.
    * 2. 필요하면 페치 조인으로 성능을 최적화 한다. -> 대부분의 성능 이슈가 해결된다.
    * 3. 그래도 안되면 DTO를 직접 조회하는 방법을 사용한다.
    * 4. 최후의 방법은 JPA가 제공하는 네이티브 sql이나 스프링 jdbc template을 사용하여 sql을 직접 사용한다.
    * */

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order){
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
        }
    }
}
