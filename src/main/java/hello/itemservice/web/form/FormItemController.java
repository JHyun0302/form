package hello.itemservice.web.form;

import hello.itemservice.domain.item.DeliveryCode;
import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.ItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/form/items")
@RequiredArgsConstructor
public class FormItemController {
    private final ItemRepository itemRepository;

    /**
     * 상품 add, edit, item에 모두  Map<String, String> regions = new LinkedHashMap<>(); 해야함! (코드 중복!)
     *
     * @Controller 시작할 때 자동으로 model에 addAttribute됨
     */
    @ModelAttribute("regions") //자바 코드
    public Map<String, String> regions() {
        Map<String, String> regions = new LinkedHashMap<>(); //hashMap은 순서 보장이 안됨!
        regions.put("SEOUL", "서울"); //"SEOUL": 시스템상 key, "서울": 사용자에게 보이는 값
        regions.put("BUSAN", "부산");
        regions.put("JEJU", "제주");

        return regions; //model.addattribute("regions", regions) 자동으로 모델에 담김
    }

    @ModelAttribute("itemTypes") //enum 사용
    public ItemType[] itemTypes() {
        return ItemType.values(); //enum의 모든 정보를 배열로 반환: [BOOK, FOOD, ETC]
    }

    @ModelAttribute("deliveryCodes") //자바 객체 사용
    public List<DeliveryCode> deliveryCodes() {
        List<DeliveryCode> deliveryCodes = new ArrayList<>();
        deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
        deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
        deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));
        return deliveryCodes;
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "form/items";
    }

    //상품 상세
    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "form/item";
    }

    //상품 등록
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "form/addForm";
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        log.info("item.open={}", item.getOpen()); //체크박스 log 찍기(true, false)
        log.info("item.regions={}", item.getRegions()); //멀티 체크박스 log (list 형식)
        log.info("item.itemType={}", item.getItemType()); //라디오 박스 log (enum 형식 - 단일)

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/form/items/{itemId}";
    }

    //상품 수정
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "form/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/form/items/{itemId}";
    }

}

