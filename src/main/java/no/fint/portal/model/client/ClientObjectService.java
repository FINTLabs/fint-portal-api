package no.fint.portal.model.client;

import no.fint.portal.model.organisation.Organisation;
import no.fint.portal.utilities.LdapConstants;
import no.fint.portal.utilities.PasswordUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import javax.naming.Name;

@Service
public class ClientObjectService {

    @Value("${fint.ldap.organisation-base}")
    private String organisationBase;

    public void setupClient(Client client, Organisation organisation) {
        client.setName(String.format("%s@client.%s", client.getName(), organisation.getPrimaryAssetId()));
        client.setDn(
                LdapNameBuilder.newInstance(getClientBase(organisation.getName()))
                        .add(LdapConstants.CN, client.getName())
                        .build()
        );
        client.setSecret(PasswordUtility.generateSecret());
    }

    public Name getClientBase(String orgUuid) {
        return LdapNameBuilder.newInstance(organisationBase)
                .add(LdapConstants.OU, orgUuid)
                .add(LdapConstants.OU, LdapConstants.CLIENT_CONTAINER_NAME)
                .build();
    }

    public String getClientDn(String clientUuid, String orgUuid) {
        return LdapNameBuilder.newInstance(getClientBase(orgUuid))
                .add(LdapConstants.CN, clientUuid)
                .build()
                .toString();
    }
}
