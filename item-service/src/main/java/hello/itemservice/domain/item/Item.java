package hello.itemservice.domain.item;

import java.util.List;

import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.ScriptAssert;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
//@ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000", message = "총합이 10000원 넘게 입력하지 마세요") 걍 자바코드로 사용하는것(컨트롤러에서)을 권장(기능이 약함)
public class Item {
	
	//@NotNull(groups = UpdateCheck.class)
	private Long id;
	
	//@NotBlank(groups = {SaveCheck.class, UpdateCheck.class})
	private String itemName;
	
	//@NotNull(groups = {SaveCheck.class, UpdateCheck.class})
	//@Range(min = 1000, max = 1000000, groups = {SaveCheck.class, UpdateCheck.class})
	private Integer price=0;
	
	//@NotNull(groups = {SaveCheck.class, UpdateCheck.class})
	//@Max(value = 9999, groups = {SaveCheck.class})
	private Integer quantity;
	
	private Boolean open;
	private List<String> regions;
	private ItemType itemType;
	private String deliveryCode;
	
	public Item() {
		
	}

	public Item(String itemName, Integer price, Integer quantity) {
		this.itemName = itemName;
		this.price = price;
		this.quantity = quantity;
	}
	
	
}
