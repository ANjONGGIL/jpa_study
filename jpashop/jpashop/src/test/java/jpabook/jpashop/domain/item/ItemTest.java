package jpabook.jpashop.domain.item;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional // DB에 직접 insert 되지 않음. 반복적인 작업을 하기 때문에
public class ItemTest {

    @Test
    public void 엔티티변경테스트(){
        Item testItem = new Book();

        testItem.setName("안종길책");
        testItem.setPrice(100);
        testItem.setStockQuantity(10);

        assertEquals(testItem.getName(),"안종길책");
        assertEquals(testItem.getPrice(),100);
        assertEquals(testItem.getStockQuantity(),10);

        testItem.change("안종길책1",10000,20);

        assertEquals(testItem.getName(),"안종길책1");
        assertEquals(testItem.getPrice(),10000);
        assertEquals(testItem.getStockQuantity(),20);
    }
}