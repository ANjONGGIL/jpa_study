package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId,Long itemId,int count){
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(),count);

        Order order = Order.createOrder(member,delivery,orderItem);
        orderRepository.save(order);
        return order.getId();
    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId){
        //주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        //주문 취소
        order.cancel();
        //중요! JPA의 강점
        //SQl Mapper의 경우 Item, Order의 상태값 변경 뒤에 SQL문을 날려주어야 한다.
        //하지만 jpa의 경우 더티체킹(변경 감지)기능에 의해 entity 변경된 내역들을 자동으로 업데이트를 친다.
    }

    public List<Order> findOrder(OrderSearch orderSearch){
        return orderRepository.findAll(orderSearch);
    }
}
