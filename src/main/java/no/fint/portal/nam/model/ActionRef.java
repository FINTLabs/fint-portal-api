package no.fint.portal.nam.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Generated;

@Data
@Builder
public class ActionRef{

	@JsonProperty("elementRefType")
	private String elementRefType;

	@JsonProperty("externalElementRef")
	private String externalElementRef;

	@JsonProperty("externalDocRef")
	private String externalDocRef;
}