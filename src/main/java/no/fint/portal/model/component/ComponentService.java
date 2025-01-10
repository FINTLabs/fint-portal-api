package no.fint.portal.model.component;

import lombok.extern.slf4j.Slf4j;
import no.fint.portal.ldap.LdapService;
import no.fint.portal.model.adapter.Adapter;
import no.fint.portal.model.asset.Asset;
import no.fint.portal.model.asset.AssetService;
import no.fint.portal.model.client.Client;
import no.fint.portal.model.organisation.OrganisationService;
import no.fint.portal.utilities.LdapConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ComponentService {

    @Autowired
    private LdapService ldapService;

    @Autowired
    private ComponentObjectService componentObjectService;

    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private ComponentLinkService componentLinkService;

    @Value("${fint.ldap.component-base}")
    private String componentBase;

    public boolean createComponent(Component component) {
        log.info("Creating component: {}", component);

        if (component.getDn() == null) {
            componentObjectService.setupComponent(component);
        }
        return ldapService.createEntry(component);
    }

    public boolean updateComponent(Component component) {
        log.info("Updating component: {}", component);

        return ldapService.updateEntry(component);
    }

    public List<Component> getComponents() {
        return ldapService.getAll(componentBase, Component.class);
    }

    public Optional<Component> getComponentByName(String name) {
        return getComponetByDn(getComponentDnByName(name));
    }

    public Optional<Component> getComponetByDn(String dn) {
        return Optional.ofNullable(ldapService.getEntry(dn, Component.class));
    }

    public void deleteComponent(Component component) {
        ldapService.deleteEntry(component);
    }

    public String getComponentDnByName(String name) {
        if (name != null) {
            return LdapNameBuilder.newInstance(componentBase)
                    .add(LdapConstants.OU, name)
                    .build().toString();
        }
        return null;
    }

    public List<Asset> getActiveAssetsForComponent(Component component) {
        return component
                .getOrganisations()
                .stream()
                .map(organisationService::getOrganisationByDn)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(assetService::getAssets)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * @deprecated
     * <p>use {@link ComponentLinkService} to call this method instead</p>
     */
    @Deprecated
    public void linkClient(Component component, Client client) {
        componentLinkService.linkClient(component, client);
    }

    /**
     * @deprecated
     * <p>use {@link ComponentLinkService} to call this method instead</p>
     */
    @Deprecated
    public void unLinkClient(Component component, Client client) {
        componentLinkService.unLinkClient(component, client);
    }

    /**
     * @deprecated
     * <p>use {@link ComponentLinkService} to call this method instead</p>
     */
    @Deprecated
    public void linkAdapter(Component component, Adapter adapter) {
        componentLinkService.linkAdapter(component, adapter);
    }

    /**
     * @deprecated
     * <p>use {@link ComponentLinkService} to call this method instead</p>
     */
    @Deprecated
    public void unLinkAdapter(Component component, Adapter adapter) {
        componentLinkService.unLinkAdapter(component, adapter);
    }
}
