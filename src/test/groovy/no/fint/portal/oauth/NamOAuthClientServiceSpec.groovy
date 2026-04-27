package no.fint.portal.oauth

import com.fasterxml.jackson.databind.ObjectMapper
import no.fint.portal.exceptions.EntityFoundException
import org.springframework.http.HttpEntity
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.common.exceptions.InvalidClientException
import spock.lang.Specification

class NamOAuthClientServiceSpec extends Specification {

    private restTemplate
    private namOAuthClientService
    private mapper

    void setup() {
        restTemplate = Mock(OAuth2RestTemplate)
        mapper = new ObjectMapper()
        namOAuthClientService = new NamOAuthClientService(restTemplate: restTemplate, mapper: mapper)
    }

    def "Add OAuth Client"() {

        when:
        def client = namOAuthClientService.addOAuthClient("name")

        then:
        1 * restTemplate.postForObject(_ as String, _ as HttpEntity, _ as Class, _) >> "{\"developerDn\":\"dev\",\"grant_types\":[\"password\"],\"application_type\":\"web\",\"Version\":\"4.1\",\"client_secret_expires_at\":1506509030813,\"registration_client_uri\":\"https://idp/nidp/oauth/nam/clients//9f30fa40-0178-4cbe-8cf5-e27c18a3ecbd\",\"redirect_uris\":[\"https://dummy.com\"],\"client_secret\":\"thesecret\",\"client_id_issued_at\":1506422630813,\"client_name\":\"80c66be1-a24a-4b55-84ab-8faeb775a85b\",\"client_id\":\"theid\",\"response_types\":[\"token\"]}"
        client != null
        !client.getClientId().isEmpty()
        !client.getClientSecret().isEmpty()
    }

    def "Add OAuth Client throws EntityFoundException when NAM reports invalid_client/already exists"() {
        given:
        def cause = new InvalidClientException("The client already exists. Please contact administrator")

        when:
        namOAuthClientService.addOAuthClient("c_duplicate")

        then:
        1 * restTemplate.postForObject(_ as String, _ as HttpEntity, _ as Class, _) >> { throw cause }
        def thrown = thrown(EntityFoundException)
        thrown.message == "OAuth client already exists in NAM: c_duplicate"
        thrown.cause.is(cause)
    }

    def "Add OAuth Client wraps unrelated OAuth2Exception in RuntimeException"() {
        given:
        def cause = new InvalidClientException("Bad credentials")

        when:
        namOAuthClientService.addOAuthClient("c_other")

        then:
        1 * restTemplate.postForObject(_ as String, _ as HttpEntity, _ as Class, _) >> { throw cause }
        def thrown = thrown(RuntimeException)
        !(thrown instanceof EntityFoundException)
        thrown.cause.is(cause)
    }
}
