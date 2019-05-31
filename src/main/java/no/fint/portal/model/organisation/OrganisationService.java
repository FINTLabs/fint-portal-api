package no.fint.portal.model.organisation;

import lombok.extern.slf4j.Slf4j;
import no.fint.portal.ldap.Container;
import no.fint.portal.ldap.LdapService;
import no.fint.portal.model.adapter.Adapter;
import no.fint.portal.model.adapter.AdapterService;
import no.fint.portal.model.asset.Asset;
import no.fint.portal.model.asset.AssetService;
import no.fint.portal.model.client.Client;
import no.fint.portal.model.client.ClientService;
import no.fint.portal.model.component.Component;
import no.fint.portal.model.component.ComponentService;
import no.fint.portal.model.contact.Contact;
import no.fint.portal.model.contact.ContactService;
import no.fint.portal.utilities.LdapConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
@Slf4j
@Service
public class OrganisationService {

    @Autowired
    private OrganisationObjectService organisationObjectService;

    @Autowired
    private LdapService ldapService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private AdapterService adapterService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private AssetService assetService;

    @Value("${fint.ldap.organisation-base}")
    private String organisationBase;

    public boolean createOrganisation(Organisation organisation) {
        log.info("Creating organisation: {}", organisation);

        if (organisation.getDn() == null) {
            organisationObjectService.setupOrganisation(organisation);
        }

        boolean createdOrganisation = ldapService.createEntry(organisation);

        createClientContainer(organisation.getDn());
        createAdapterContainer(organisation.getDn());
        createAssetContainer(organisation.getDn());
        createPrimaryAsset(organisation);

        return createdOrganisation;
    }


    public boolean updateOrganisation(Organisation organisation) {
        return ldapService.updateEntry(organisation);
    }

    public List<Organisation> getOrganisations() {

        List<Organisation> organisations = ldapService.getAll(organisationBase, Organisation.class);

        organisations.forEach(organisation -> {
            organisation.setPrimaryAssetId(assetService.getPrimaryAsset(organisation).getAssetId());
        });
        return organisations;
    }

    public Optional<Organisation> getOrganisation(String name) {

        Optional<Organisation> oranisation = Optional.ofNullable(ldapService.getEntry(
                LdapNameBuilder.newInstance(organisationBase)
                        .add(LdapConstants.OU, name)
                        .build()
                        .toString(),
                Organisation.class
                )
        );

        if (oranisation.isPresent()) {
            oranisation.get().setPrimaryAssetId(assetService.getPrimaryAsset(oranisation.get()).getAssetId());
        }

        return oranisation;

    }

    public Optional<Organisation> getOrganisationByDn(String dn) {
        return Optional.ofNullable(ldapService.getEntry(dn, Organisation.class));
    }

    public void deleteOrganisation(Organisation organisation) {

        // Remove all adapters
        List<Adapter> adapters = adapterService.getAdapters(organisation.getName());

        if (adapters != null) {
            adapters.forEach(adapter -> adapterService.deleteAdapter(adapter));
        }
        // Remove all clients
        List<Client> clients = clientService.getClients(organisation.getName());

        if (clients != null) {
            clients.forEach(client -> clientService.deleteClient(client));
        }
        // Remove all assets
        List<Asset> assets = assetService.getAssets(organisation);

        if (assets != null) {
            assets.forEach(assetService::removeAsset);
        }

        // Remove adapter container
        Container adapterContainer = new Container();

        adapterContainer.setOu(LdapConstants.ADAPTER_CONTAINER_NAME);
        adapterContainer.setDn(LdapNameBuilder.newInstance(organisation.getDn())
                .add(LdapConstants.OU, LdapConstants.ADAPTER_CONTAINER_NAME)
                .build());
        ldapService.deleteEntry(adapterContainer);

        // remove client container
        Container clientContainer = new Container();

        clientContainer.setOu(LdapConstants.CLIENT_CONTAINER_NAME);
        clientContainer.setDn(LdapNameBuilder.newInstance(organisation.getDn())
                .add(LdapConstants.OU, LdapConstants.CLIENT_CONTAINER_NAME)
                .build());


        ldapService.deleteEntry(clientContainer);

        // remove asset container
        Container assetContainer = new Container();

        assetContainer.setOu(LdapConstants.ASSET_CONTAINER_NAME);
        assetContainer.setDn(LdapNameBuilder.newInstance(organisation.getDn())
                .add(LdapConstants.OU, LdapConstants.ASSET_CONTAINER_NAME)
                .build());


        ldapService.deleteEntry(assetContainer);
        ldapService.deleteEntry(organisation);
    }

    public void linkLegalContact(Organisation organisation, Contact contact) {
        String previousLegalContactDn = organisation.getLegalContact();
        if (!StringUtils.isEmpty(previousLegalContactDn)) {
            contactService.getContactByDn(previousLegalContactDn).ifPresent(previousLegalContact -> {
                previousLegalContact.removeOrganisationLegalContact(organisation.getDn());
                contactService.updateContact(previousLegalContact);
                log.info("Removed {} from {}", organisation.getDn(), previousLegalContactDn);
            });
        }

        organisation.setLegalContact(contact.getDn());
        contact.addOrganisationLegalContact(organisation.getDn());

        contactService.updateContact(contact);
        updateOrganisation(organisation);
    }

    public void unLinkLegalContact(Organisation organisation, Contact contact) {
        organisation.setLegalContact(null);
        contact.removeOrganisationLegalContact(organisation.getDn());

        ldapService.updateEntry(contact);
        ldapService.updateEntry(organisation);
    }

    public void linkTechnicalContact(Organisation organisation, Contact contact) {
        organisation.addTechnicalContact(contact.getDn());
        contact.addOrganisationTechnicalContact(organisation.getDn());

        ldapService.updateEntry(contact);
        ldapService.updateEntry(organisation);
    }

    public void unLinkTechnicalContact(Organisation organisation, Contact contact) {
        organisation.removeTechicalContact(contact.getDn());
        contact.removeOrganisationTechnicalContact(organisation.getDn());

        ldapService.updateEntry(contact);
        ldapService.updateEntry(organisation);
    }

    public void linkComponent(Organisation organisation, Component component) {
        organisation.addComponent(component.getDn());
        component.addOrganisation(organisation.getDn());

        ldapService.updateEntry(organisation);
        ldapService.updateEntry(component);
    }

    public void unLinkComponent(Organisation organisation, Component component) {
        // TODO: Add tests for this.
        List<Client> clients = clientService.getClients(organisation.getName());
        List<Adapter> adapters = adapterService.getAdapters(organisation.getName());

        clients.forEach(client -> componentService.unLinkClient(component, client));
        adapters.forEach(adapter -> componentService.unLinkAdapter(component, adapter));

        organisation.removeComponent(component.getDn());
        component.removeOrganisation(organisation.getDn());


        ldapService.updateEntry(organisation);
        ldapService.updateEntry(component);
    }

    public List<Contact> getTechnicalContacts(Organisation organisation) {
        List<Contact> contacts = contactService.getContacts();


        return contacts.stream().filter(contact ->
                organisation.getTechicalContacts().contains(contact.getDn())
        ).collect(Collectors.toList());

    }

    public Contact getLegalContact(Organisation organisation) {
        if (organisation.getLegalContact() == null) return null;

        return contactService.getContacts()
                .stream()
                .filter(
                        contact -> contact.getDn().equals(organisation.getLegalContact())
                ).findAny()
                .orElse(null);
    }

    private void createAssetContainer(String organisationDn) {
        Container assetContainer = new Container();

        assetContainer.setOu(LdapConstants.ASSET_CONTAINER_NAME);
        assetContainer.setDn(LdapNameBuilder.newInstance(organisationDn)
                .add(LdapConstants.OU, LdapConstants.ASSET_CONTAINER_NAME)
                .build());


        ldapService.createEntry(assetContainer);
    }

    private void createPrimaryAsset(Organisation organisation) {
        Asset asset = new Asset();
        asset.setAssetId(organisation.getName().replace("_", "."));
        asset.setDescription("Primær ressurs");
        assetService.addPrimaryAsset(asset, organisation);
    }

    private void createClientContainer(String organisationDn) {
        Container clientContainer = new Container();

        clientContainer.setOu(LdapConstants.CLIENT_CONTAINER_NAME);
        clientContainer.setDn(LdapNameBuilder.newInstance(organisationDn)
                .add(LdapConstants.OU, LdapConstants.CLIENT_CONTAINER_NAME)
                .build());


        ldapService.createEntry(clientContainer);

    }

    private void createAdapterContainer(String organisationDn) {
        Container adapterContainer = new Container();

        adapterContainer.setOu(LdapConstants.ADAPTER_CONTAINER_NAME);
        adapterContainer.setDn(LdapNameBuilder.newInstance(organisationDn)
                .add(LdapConstants.OU, LdapConstants.ADAPTER_CONTAINER_NAME)
                .build());
        ldapService.createEntry(adapterContainer);
    }

}
