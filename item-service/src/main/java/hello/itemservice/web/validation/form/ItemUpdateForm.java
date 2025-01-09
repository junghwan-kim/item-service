package hello.itemservice.web.validation.form;

import java.util.List;

import org.hibernate.validator.constraints.Range;

import hello.itemservice.domain.item.ItemType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemUpdateForm {
	
	@NotNull
	private Long id;
		
	@NotBlank
	private String itemName;
	
	@NotNull
	@Range(min = 1000, max = 1000000)
	private Integer price=0;
	
	//수정에서 수량은 자유롭게 변경 가능.
	private Integer quantity;
	
	private Boolean open;
	private List<String> regions;
	private ItemType itemType;
	private String deliveryCode;
	
}
