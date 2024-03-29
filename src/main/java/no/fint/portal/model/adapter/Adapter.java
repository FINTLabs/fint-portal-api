package no.fint.portal.model.adapter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.ToString;
import no.fint.portal.ldap.BasicLdapEntry;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.support.LdapNameBuilder;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.List;

@Schema
@ToString(exclude = {"password"})
@Entry(objectClasses = {"fintAdapter", "inetOrgPerson", "organizationalPerson", "person", "top"})
public final class Adapter implements BasicLdapEntry {

    @Schema(defaultValue = "DN of the adapter. This is automatically set.")
    @Id
    private Name dn;

    @Schema(defaultValue = "Username for the adapter. This is automatically set.")
    @Attribute(name = "cn")
    private String name;

    @Schema(defaultValue = "Short description of the adapter")
    @Attribute(name = "sn")
    private String shortDescription;

    @Schema(defaultValue = "A note of the adapter.")
    @Attribute(name = "description")
    private String note;

    @Attribute(name = "userPassword")
    private String password;

    @Schema(defaultValue = "OAuth client id")
    @Attribute(name = "fintOAuthClientId")
    private String clientId;

    @Attribute(name = "fintAdapterComponents")
    private List<String> components;

    @Attribute(name = "fintAdapterAssets")
    private List<String> assets;

    @Attribute(name = "fintAdapterAssetIds")
    private List<String> assetIds;

    @Attribute(name = "fintAdapterManaged")
    private boolean managed;

    public Adapter() {
        components = new ArrayList<>();
        assets = new ArrayList<>();
        assetIds = new ArrayList<>();
    }

    public List<String> getAssetIds() {
        return assetIds;
    }

    public void addAssetId(String assetId) {
        if (!assetIds.stream().anyMatch(assetId::equalsIgnoreCase)) {
            assetIds.add(assetId);
        }
    }

    public void removeAssetId(String assetId) {
        assetIds.removeIf(asset -> asset.equalsIgnoreCase(assetId));
    }


    public void addComponent(String componentDn) {
        if (!components.stream().anyMatch(componentDn::equalsIgnoreCase)) {
            components.add(componentDn);
        }
    }

    public void removeComponent(String componentDn) {
        components.removeIf(component -> component.equalsIgnoreCase(componentDn));
    }

    public void addAsset(String assetId) {
        if (!assets.stream().anyMatch(assetId::equalsIgnoreCase)) {
            assets.add(assetId);
        }
    }

    public void removeAsset(String assetId) {
        assets.removeIf(asset -> asset.equalsIgnoreCase(assetId));
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

    public List<String> getAssets() {
        return assets;
    }

    public boolean isManaged() {
        return managed;
    }

    public void setManaged(boolean managed) {
        this.managed = managed;
    }
}
