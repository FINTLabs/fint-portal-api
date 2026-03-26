package no.fint.portal.model.client

import no.fint.portal.model.organisation.Organisation
import spock.lang.Specification

class ClientObjectServiceSpec extends Specification {
    def clientObjectService

    void setup() {
        clientObjectService = new ClientObjectService(organisationBase: "ou=org,o=fint")
    }

    def "Setup Client"() {
        given:
        def client = new Client(name: "TestClient")

        when:
        clientObjectService.setupClient(client, new Organisation(name: "orgName"))

        then:
        client.password != null
        client.dn.contains("orgName")
        client.name != null
    }

    def "Get Client Base"() {
        when:
        def dn = clientObjectService.getClientBase("orgUuid")

        then:
        dn != null
        dn.toString().contains("orgUuid")
    }

    def "Get Client Dn"() {
        when:
        def dn = clientObjectService.getClientDn("clientUuid", "orgUuid")

        then:
        dn != null
        dn.contains("clientUuid")
        dn.toString().contains("orgUuid")
    }

    def "ModelVersion is null by default"() {
        when:
        def client = new Client()

        then:
        client.getModelVersion() == null
    }

    def "Set and get ModelVersion V3"() {
        given:
        def client = new Client()

        when:
        client.setModelVersion(ModelVersion.V3)

        then:
        client.getModelVersion() == ModelVersion.V3
    }

    def "Set and get ModelVersion V4"() {
        given:
        def client = new Client()

        when:
        client.setModelVersion(ModelVersion.V4)

        then:
        client.getModelVersion() == ModelVersion.V4
    }

    def "Setting ModelVersion to null returns null"() {
        given:
        def client = new Client()
        client.setModelVersion(ModelVersion.V3)

        when:
        client.setModelVersion(null)

        then:
        client.getModelVersion() == null
    }
}
