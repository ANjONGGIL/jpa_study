package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    //쿼리가 복잡해서 query dsl을 사용하자... 즉 query dsl좀 배우자
    public List<Order> findAll(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch  o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    public List<Order> findAllWithItem() {
        return em.createQuery("select distinct o from Order o" +
                        " join fetch  o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
                .getResultList();

        /*
         * distinct를 사용한 이유는 1대다 조인이 잇으므로 데이터베이스 row가 증가한다. 그 결과 같은 order엔티티의 조회 수도 증가하게 된다. jpa의 distnict는 sql에
         * distinct를 추가하고, 더해서 같은 엔티티가 조회되면 애플리케이션에서 중복을 걸러준다. 이 예에서 order가 컬렉션 페치 조인 때문에 중복조회 되는 것을 막아준다.
         *
         * -> 단점 : 페이징이 안된다.
         *
         * 페이징을 처리할 경우 데이터베이스에서 데이터를 전체를 가져온뒤 메모리에서 페이징 처리가됨
         *
         * */
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch  o.member m" +
                                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        /*
         * 위 문제를 해결하기위한 방법
         *
         * toOne 관계는 모두 페치조인 한다. (이번 예제에서는 member와 delivery는 페치 조인 하여도 상관없음)
         * 나머지 toMany와 연관된 엔티티는 지연로딩으로 한다.
         * @BatchSize를 사용한다. or yml파일에 설정 추가 (default_batch_fetch_size)
         *
         * 위 옵션의 사이즈 만큼 in 쿼리로 조회하게 된다.
         *
         * 결론 ToOne 관계는 페치 조인해도 페이징에 영향을 주지 않는다. 따라서 ToOne 관계는 페치조인으로 쿼리 수를
         * 줄이고 해결하고, 나머지는 hibernate.default_batch_fetch_size로 최적화 하자.
        *
        * */
    }
}
