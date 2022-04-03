package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model){
        model.addAttribute("form",new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form){
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }

    @GetMapping("/items")
    public String list(Model model){
        List<Item> items = itemService.findAll();
        model.addAttribute("items",items);
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId,Model model){
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setIsbn(item.getIsbn());
        form.setAuthor(item.getAuthor());

        model.addAttribute("form",form);
        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@ModelAttribute("form") BookForm bookForm, @PathVariable long itemId){
        /*
        *준영속 엔티티.
        *영속성 컨텍스트가 더는 관리하지 않는 엔티티를 말한다. DB에 저장되어서 식별자가 존재하는 상태.
        *임의로 만들어낸 엔티티도 기존 식별자를 가지고 있다면 준영속 엔티티로 볼수가 있다
        *
        * JPA가 관리하지 않음
        * 값을 바꿔도 DB에 업데이트가 되지 않음
        *
        * 준영속 엔티티를 수정하는 방법
        *
        * 더티체킹
        * 병합
        *  */

//        Book book = new Book();
//        book.setId(book.getId());
//        book.setName(bookForm.getName());
//        book.setPrice(bookForm.getPrice());
//        book.setStockQuantity(bookForm.getStockQuantity());
//        book.setIsbn(bookForm.getIsbn());
//        book.setAuthor(book.getAuthor());

        /*
        * 컨트롤러에서 어설프게 만들지 말자
        *
        * */

        itemService.updateItem(itemId, bookForm.getName(), bookForm.getPrice(), bookForm.getStockQuantity());
        return "redirect:items";
    }

}
