package no.fint.portal.model.component

import no.fint.portal.ldap.LdapService
import no.fint.portal.testutils.ObjectFactory
import spock.lang.Specification

class ComponentLinkServiceSpec extends Specification {

    private componentLinkService
    private ldapService

    def setup() {
        ldapService = Mock(LdapService)
        componentLinkService = new ComponentLinkService(ldapService)
    }

    def "Remove Client from Component"() {
        given:
        def component = ObjectFactory.newComponent()
        def c1 = ObjectFactory.newClient()
        def c2 = ObjectFactory.newClient()

        c1.setDn("name=c1,o=fint")
        c2.setDn("name=c2,o=fint")
        component.addClient(c1.getDn())
        component.addClient(c2.getDn())

        when:
        componentLinkService.unLinkClient(component, c1)

        then:
        component.getClients().size() == 1
        component.getClients().get(0) == "name=c2,o=fint"
        1 * ldapService.updateEntry(_ as Component)
    }

    def "Add Client to Component"() {
        given:
        def client = ObjectFactory.newClient()
        def component = ObjectFactory.newComponent()

        client.setDn("name=c1")
        component.setDn("ou=comp1")

        when:
        componentLinkService.linkClient(component, client)

        then:
        component.getClients().size() == 1
        1 * ldapService.updateEntry(_ as Component)
    }

}
