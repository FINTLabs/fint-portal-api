package no.fint.portal.nam.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConditionSetItem{

	@JsonProperty("setOrder")
	private int setOrder;

	@JsonProperty("condition")
	private List<ConditionItem> condition;

	@JsonProperty("not")
	private boolean not;

	@JsonProperty("enable")
	private boolean enable;

	@JsonProperty("userInterfaceID")
	private String userInterfaceID;

	@JsonProperty("order")
	private int order;
}