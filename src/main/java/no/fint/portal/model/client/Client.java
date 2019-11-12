package no.fint.portal.model.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.ToString;
import no.fint.portal.ldap.BasicLdapEntry;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.support.LdapNameBuilder;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.List;

@ApiModel
@ToString(exclude = {"password"})
@Entry(objectClasses = {"fintClient", "inetOrgPerson", "organizationalPerson", "person", "top"})
public final class Client implements BasicLdapEntry {

    @ApiModelProperty(value = "DN of the client. This is automatically set.")
    @Id
    private Name dn;

    @ApiModelProperty(value = "Username for the client.")
    @Attribute(name = "cn")
    private String name;

    @ApiModelProperty(value = "Short description of the client")
    @Attribute(name = "sn")
    private String shortDescription;

    @ApiModelProperty(value = "OrgId of the organisation the client is connected to. This is automatically set.")
    @Attribute(name = "fintClientAssetId")
    private String assetId;

    @ApiModelProperty(value = "DN of the organisation the client is connected to. This is automatically set.")
    @Attribute(name = "fintClientAsset")
    private String asset;

    @ApiModelProperty(value = "A note of the client.")
    @Attribute(name = "description")
    private String note;

    @Attribute(name = "userPassword")
    private String password;

    @ApiModelProperty(value = "OAuth client id")
    @Attribute(name = "fintOAuthClientId")
    private String clientId;

    @Attribute(name = "fintClientComponents")
    private List<String> components;

    @Attribute(name = "fintClientAuthorizationPolicyId")
    private String fintClientAuthorizationPolicyId;

    public String getFintClientAuthorizationPolicyId() {
        return fintClientAuthorizationPolicyId;
    }

    public void setFintClientAuthorizationPolicyId(String fintClientAuthorizationPolicyId) {
        this.fintClientAuthorizationPolicyId = fintClientAuthorizationPolicyId;
    }

    public Client() {
        components = new ArrayList<>();
    }

    public void addComponent(String componentDn) {
        if (!components.stream().anyMatch(componentDn::equalsIgnoreCase)) {
            components.add(componentDn);
        }
    }

    public void removeComponent(String componentDn) {
        components.removeIf(component -> component.equalsIgnoreCase(componentDn));
    }

    public List<String> getComponents() {
        return components;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String orgId) {
        this.assetId = orgId;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    public void setSecret(String secret) {
        this.password = secret;
    }

    public String getDn() {
        if (dn != null) {
            return dn.toString();
        } else {
            return null;
        }
    }

    @Override
    public void setDn(String dn) {
        this.dn = LdapNameBuilder.newInstance(dn).build();
    }

    @Override
    public void setDn(Name dn) {
        this.dn = dn;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
