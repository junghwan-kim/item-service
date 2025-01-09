package hello.itemservice.web.validation;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import hello.itemservice.domain.item.Item;

@Component
public class ItemValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {		
		return Item.class.isAssignableFrom(clazz); //isAssignableFrom 자식클래스까지 커버
	}

	@Override
	public void validate(Object target, Errors errors) {
		Item item = (Item) target;
		
		//ValidationUtils.rejectIfEmptyOrWhitespace(errors, "itemName","required");
		
		if (!StringUtils.hasText(item.getItemName())) {
			errors.rejectValue("itemName", "required");
        }
		
		 if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {
			 errors.rejectValue("price", "range", new Object[]{1000, 1000000},null);
		 }
		 if (item.getQuantity() == null || item.getQuantity() > 10000) {
			 errors.rejectValue("quantity", "max", new Object[]{9999}, null);
		 }
		
	}

}
