package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init(){
        initService.dbInti1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{
        private final EntityManager em;

        public void dbInti1(){
            Member member = getMember("userA", "서울", "1", "1111");
            em.persist(member);

            Book book = getBook("JPA1 Book", 10000, 100);
            em.persist(book);

            Book book1 = getBook("JPA2 Book", 20000, 100);
            em.persist(book1);

            OrderItem orderItem1 = OrderItem.createOrderItem(book, 10000,1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book1, 20000,2);

            Delivery delivery = getDelivery(member);
            Order order = Order.createOrder(member,delivery,orderItem1,orderItem2);

            em.persist(order);
        }

        public void dbInit2() {
            Member member = getMember("userB", "진주", "2", "2222");
            em.persist(member);

            Book book = getBook("Spring1 Book", 20000, 200);
            em.persist(book);

            Book book1 = getBook("Spring2 Book", 40000, 300);
            em.persist(book1);

            OrderItem orderItem1 = OrderItem.createOrderItem(book, 20000,3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book1, 40000,4);

            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            Order order = Order.createOrder(member,delivery,orderItem1,orderItem2);

            em.persist(order);
        }

        private Delivery getDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }

        private Member getMember(String userName, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(userName);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }

        private Book getBook(String JPA1_Book, int price, int stockQuantity) {
            Book book = new Book();
            book.setName(JPA1_Book);
            book.setPrice(price);
            book.setStockQuantity(stockQuantity);
            return book;
        }
    }
}
