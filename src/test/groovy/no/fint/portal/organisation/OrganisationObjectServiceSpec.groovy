package no.fint.portal.organisation

import no.fint.portal.ldap.LdapService
import spock.lang.Specification

class OrganisationObjectServiceSpec extends Specification {
    def organisationObjectService
    def ldapServiceMock

    def setup() {
        ldapServiceMock = Mock(LdapService)
        organisationObjectService = new OrganisationObjectService(organisationBase: "ou=org,o=fint", ldapService: ldapServiceMock)
    }

    def "Setup Organisation"() {
        given:
        def organisation = new Organisation(orgId: "test.no", name: "TestOrganisation")

        when:
        organisationObjectService.setupOrganisation(organisation)

        then:
        organisation.dn != null
        organisation.name != null
        1 * ldapServiceMock.getEntryByUniqueName(_ as String, _ as String, _ as Class) >> null
    }
}
