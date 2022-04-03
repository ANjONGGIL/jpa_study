package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item){
        if (item.getId() == null) {
            em.persist(item);
        }
//        }else{
//            em.merge(item);
//            /* merge 병합
//            *
//            * merge가 된 PARAMETER는 영속성 컨텍스트로 관리가 되지 않는다
//            * merge를 하고 반환된 객체가 영속성 컨텍스트로 관리가 된다.
//            *
//            * !!주의 : 변경 감지 기능을 사용하면 원하는 속성만 선택해서 변경할 수 있지만, 병합을 사용하면 모든 속성이 변경된다.
//            * 병합시 값이 없으면 null로 업데이트 할 위험도 있다.
//            *
//            * 가급적 merge사용 X
//            * */
//        }
    }

    public Item findOne(Long id){
        return em.find(Item.class,id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i ",Item.class)
                .getResultList();
    }
}
