package no.fint.portal.model.component;

import no.fint.portal.ldap.LdapService;
import no.fint.portal.model.adapter.Adapter;
import no.fint.portal.model.client.Client;
import org.springframework.stereotype.Service;

@Service
public class ComponentLinkService {

    private final LdapService ldapService;

    public ComponentLinkService(LdapService ldapService) {
        this.ldapService = ldapService;
    }

    public void linkClient(Component component, Client client) {
        component.addClient(client.getDn());
        client.addComponent(component.getDn());
        ldapService.updateEntry(client);
        ldapService.updateEntry(component);
    }

    public void unLinkClient(Component component, Client client) {
        component.removeClient(client.getDn());
        client.removeComponent(component.getDn());

        ldapService.updateEntry(client);
        ldapService.updateEntry(component);
    }

    public void linkAdapter(Component component, Adapter adapter) {
        component.addAdapter(adapter.getDn());
        adapter.addComponent(component.getDn());

        ldapService.updateEntry(adapter);
        ldapService.updateEntry(component);
    }

    public void unLinkAdapter(Component component, Adapter adapter) {
        component.removeAdapter(adapter.getDn());
        adapter.removeComponent(component.getDn());

        ldapService.updateEntry(adapter);
        ldapService.updateEntry(component);
    }
}
