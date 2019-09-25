package no.fint.portal.ldap;

import javax.naming.Name;
import java.io.Serializable;

public interface BasicLdapEntry extends Serializable {
    String getDn();

    void setDn(String dn);

    void setDn(Name dn);
}
