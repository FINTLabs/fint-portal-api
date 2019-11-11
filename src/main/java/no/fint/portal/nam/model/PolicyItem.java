package no.fint.portal.nam.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PolicyItem {

    @JsonProperty("policyEnforcementPointRef")
    private PolicyEnforcementPointRef policyEnforcementPointRef;

    @JsonProperty("policyId")
    private String policyId;

    @JsonProperty("policyName")
    private String policyName;

    @JsonProperty("enable")
    private boolean enable;

    @JsonProperty("rule")
    private List<Object> rule;

    @JsonProperty("uri")
    private String uri;
}