package no.fint.portal.ldap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.support.LdapNameBuilder;

import javax.naming.Name;

@Schema
@Data
@Entry(objectClasses = {"organizationalUnit", "top"})
public class Container implements BasicLdapEntry {

    @Id
    private Name dn;

    @Schema(defaultValue = "This should be the name of the container.")
    @Attribute(name = "ou")
    private String ou;

    @Override
    public String getDn() {
        if (dn != null) {
            return dn.toString();
        } else {
            return null;
        }
    }

    @Override
    public void setDn(Name dn) {
        this.dn = dn;
    }

    @Override
    public void setDn(String dn) {
        this.dn = LdapNameBuilder.newInstance(dn).build();
    }
}

