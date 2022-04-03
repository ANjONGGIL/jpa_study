package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createForm(Model model){
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findAll();

        model.addAttribute("members",members);
        model.addAttribute("items",items);
        return "order/orderForm";
    }

    @PostMapping("/order")
    public String order(
            @RequestParam(value = "memberId")Long memberId,
            @RequestParam(value = "itemId")Long itemId,
            @RequestParam(value = "count")int count){
        //최대한 엔티티는 서비스에서 찾자;;
        orderService.order(memberId,itemId,count);
        return "redirect:/orders";
    }

    @GetMapping("/orders")
    public String orderList(
            @ModelAttribute("orderSearch")OrderSearch orderSearch,
            Model model){
        List<Order> orders = orderService.findOrder(orderSearch);
        model.addAttribute("orders",orders);

        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId){
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
