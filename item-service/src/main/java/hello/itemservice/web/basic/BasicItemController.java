package hello.itemservice.web.basic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import hello.itemservice.domain.item.DeliveryCode;
import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.ItemType;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
import hello.itemservice.web.validation.ItemValidator;
import hello.itemservice.web.validation.form.ItemSaveForm;
import hello.itemservice.web.validation.form.ItemUpdateForm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ch.qos.logback.core.util.StringUtil;

@Slf4j
@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {
	
	private final ItemRepository itemRepository;
	//private final ItemValidator itemValidator;
	/*
	@Autowired
	public BasicItemController(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}
	*/
	
	/*
	@InitBinder
	private void init(WebDataBinder dataBinder) {
		dataBinder.addValidators(itemValidator);
	}*/
	
	
	@ModelAttribute("regions") //controll 호출 시 \자동으로 모델에 등록됨
	public Map<String, String> regions(){
		Map<String, String> regions = new LinkedHashMap<>(); //LinkedHashMap 은 순서가 보장됨
		regions.put("SEOUL", "서울");
		regions.put("BUSAM", "부산");
		regions.put("JEJU", "제주");	
		return regions;
	}
	
	@ModelAttribute("itemTypes")
	public ItemType[] itemTypes() {
		return ItemType.values();
	}
	
	@ModelAttribute("deliveryCodes")
	public List<DeliveryCode> deliveryCodes(){
		List<DeliveryCode> deliveryCodes = new ArrayList<>();
		deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
		deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
		deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));
		return deliveryCodes;
	}
	
	
	@GetMapping
	public String items(Model model) {
		List<Item> items = itemRepository.findAll();
		model.addAttribute("items",items);
		return "basic/items";
	}
	
	@GetMapping("/{itemId}")
	public String item(@PathVariable("itemId") long itemId, Model model) {
		Item item = itemRepository.findById(itemId);
		model.addAttribute("item",item);
		return "/basic/item";
	}
	
	@GetMapping("/add")
	public String addForm(Model model) {
		model.addAttribute("item", new Item());
		return "basic/addForm";
	}
	
	//@PostMapping("/add")
	public String addItemV1(
			@RequestParam("itemName") String itemName
			,@RequestParam("price") int price
			,@RequestParam("quantity") Integer quantity
			,Model model
			) {
		
		Item item = new Item();
		item.setItemName(itemName);
		item.setPrice(price);
		item.setQuantity(quantity);
		
		itemRepository.save(item);
		
		model.addAttribute("item",item);
		
		return "basic/item";
	}
	
	//@PostMapping("/add")
	public String addItemV2(@ModelAttribute("item") Item item,Model model) {
		itemRepository.save(item);		
		//model.addAttribute("item",item);	//@ModelAttribute로 지정한 객체 (item)를 model에 자동 추가해줌
		return "basic/item";
	}
	
	
	//@PostMapping("/add")
	public String addItemV3(@ModelAttribute Item item,Model model) {
		itemRepository.save(item);		
		//model.addAttribute("item",item);	//@ModelAttribute 이름을 지정하지 않으면 class 이름의 첫글자를 소문자로 바꿔 model에자동 추가함
		return "basic/item";
	}
	
	//@PostMapping("/add")
	public String addItemV4(Item item,Model model) {
		itemRepository.save(item);		
		//model.addAttribute("item",item);	//임의의 객체인 경우 @ModelAttribute 생략가능 
		return "redirect:/basic/items/"+item.getId();
	}
	
	//@PostMapping("/add")
	public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        log.info("objectName={}", bindingResult.getObjectName());
        log.info("target={}", bindingResult.getTarget());

        if (!StringUtils.hasText(item.getItemName())) {
            bindingResult.rejectValue("itemName", "required");
        }
        
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
            bindingResult.rejectValue("price", "range", new Object[]{1000, 10000000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999) {
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        //성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }
	
	//@PostMapping("/add")
	public String addItemV6(@ModelAttribute Item item, BindingResult bindingResult ,RedirectAttributes redirectAttributes) {
		/*
		log.info("item.open={}", item.getOpen());
		log.info("item.regions={}", item.getRegions());
		*/
		
		//itemValidator.validate(item, bindingResult);
		
		//검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "basic/addForm";
        }
		
        //성공로직
		Item savedItem = itemRepository.save(item);		
		redirectAttributes.addAttribute("itemId",savedItem.getId());
		redirectAttributes.addAttribute("status",true); //이건 쿼리파라미터 형식으로 나감. ?status=true
		return "redirect:/basic/items/{itemId}";
		
	}
	
	//@PostMapping("/add")
	public String addItemV7(@Validated(SaveCheck.class) @ModelAttribute Item item, BindingResult bindingResult ,RedirectAttributes redirectAttributes) {

		//itemValidator.validate(item, bindingResult); //webdatabinder를 추가하면 컨트롤러에서 검증기를 자동으로 적용할 수 있다. @Validated (검증기를 실행하라라는 애노테이션)
		
		 //특정 필드 예외가 아닌 전체 예외
		 if (item.getPrice() != null && item.getQuantity() != null) {
			 int resultPrice = item.getPrice() * item.getQuantity();
			 if (resultPrice < 10000) {
				 bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice},null);
			 }
		 }
		 
		//검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "basic/addForm";
        }
		
        //성공로직
		Item savedItem = itemRepository.save(item);		
		redirectAttributes.addAttribute("itemId",savedItem.getId());
		redirectAttributes.addAttribute("status",true); //이건 쿼리파라미터 형식으로 나감. ?status=true
		return "redirect:/basic/items/{itemId}";
		
	}
	
	@PostMapping("/add")
	public String addItemV8(@Validated @ModelAttribute("item") ItemSaveForm form, BindingResult bindingResult ,RedirectAttributes redirectAttributes) {


		 //특정 필드 예외가 아닌 전체 예외
		 if (form.getPrice() != null && form.getQuantity() != null) {
			 int resultPrice = form.getPrice() * form.getQuantity();
			 if (resultPrice < 10000) {
				 bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice},null);
			 }
		 }
		 
		//검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "basic/addForm";
        }
		
        //성공로직
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setPrice(form.getPrice());
        item.setQuantity(form.getQuantity());
        
		Item savedItem = itemRepository.save(item);		
		redirectAttributes.addAttribute("itemId",savedItem.getId());
		redirectAttributes.addAttribute("status",true); //이건 쿼리파라미터 형식으로 나감. ?status=true
		return "redirect:/basic/items/{itemId}";
		
	}
	
	@GetMapping("/{itemId}/edit")
	public String editForm(@PathVariable("itemId") Long itemId, Model model ) {
		Item item = itemRepository.findById(itemId);
		model.addAttribute("item",item);
		return "basic/editForm";
	}
	
	//@PostMapping("/{itemId}/edit")
	public String edit(@PathVariable("itemId") Long itemId, @Validated @ModelAttribute Item item , BindingResult bindingResult) {
		
		 //특정 필드 예외가 아닌 전체 예외
		 if (item.getPrice() != null && item.getQuantity() != null) {
			 int resultPrice = item.getPrice() * item.getQuantity();
			 if (resultPrice < 10000) {
				 bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice},null);
			 }
		 }
		 
		//검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "basic/editForm";
        }
        
		itemRepository.update(itemId, item);
		return "redirect:/basic/items/{itemId}";
	}
	
	//@PostMapping("/{itemId}/edit")
	public String edit2(@PathVariable("itemId") Long itemId, @Validated(UpdateCheck.class) @ModelAttribute Item item , BindingResult bindingResult) {
		
		 //특정 필드 예외가 아닌 전체 예외
		 if (item.getPrice() != null && item.getQuantity() != null) {
			 int resultPrice = item.getPrice() * item.getQuantity();
			 if (resultPrice < 10000) {
				 bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice},null);
			 }
		 }
		 
		//검증에 실패하면 다시 입력 폼으로
       if (bindingResult.hasErrors()) {
           log.info("errors={} ", bindingResult);
           return "basic/editForm";
       }
       
		itemRepository.update(itemId, item);
		return "redirect:/basic/items/{itemId}";
	}
	
	@PostMapping("/{itemId}/edit")
	public String edit3(@PathVariable("itemId") Long itemId, @Validated @ModelAttribute("item") ItemUpdateForm form , BindingResult bindingResult) {
		
		 //특정 필드 예외가 아닌 전체 예외
		 if (form.getPrice() != null && form.getQuantity() != null) {
			 int resultPrice = form.getPrice() * form.getQuantity();
			 if (resultPrice < 10000) {
				 bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice},null);
			 }
		 }
		 
		//검증에 실패하면 다시 입력 폼으로
      if (bindingResult.hasErrors()) {
          log.info("errors={} ", bindingResult);
          return "basic/editForm";
      }
      
      Item itemParam = new Item();
      itemParam.setItemName(form.getItemName());
      itemParam.setPrice(form.getPrice());
      itemParam.setQuantity(form.getQuantity());
      
		itemRepository.update(itemId, itemParam);
		return "redirect:/basic/items/{itemId}";
	}
	
	/*테스트용 데이터 추가*/
	@PostConstruct
	public void init() {
		itemRepository.save(new Item("itemA",10000,10));
		itemRepository.save(new Item("itemB",20000,20));
	}
	
}
