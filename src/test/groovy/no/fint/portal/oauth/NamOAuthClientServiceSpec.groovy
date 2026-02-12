package no.fint.portal.oauth

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class NamOAuthClientServiceSpec extends Specification {

    private restTemplate
    private tokenRestTemplate
    private namOAuthClientService
    private mapper

    void setup() {
        restTemplate = Mock(RestTemplate)
        tokenRestTemplate = Mock(RestTemplate)
        mapper = new ObjectMapper()
        namOAuthClientService = new NamOAuthClientService(
                restTemplate: restTemplate,
                tokenRestTemplate: tokenRestTemplate,
                mapper: mapper,
                username: "user",
                password: "pass",
                idpHostname: "idp",
                clientId: "client",
                clientSecret: "secret"
        )
    }

    def "Add OAuth Client"() {

        when:
        def client = namOAuthClientService.addOAuthClient("name")

        then:
        1 * tokenRestTemplate.postForObject(_ as String, _ as HttpEntity, Map.class) >> [access_token: "token", expires_in: 3600]
        1 * restTemplate.postForObject(_ as String, _ as HttpEntity, _ as Class, _) >> "{\"developerDn\":\"dev\",\"grant_types\":[\"password\"],\"application_type\":\"web\",\"Version\":\"4.1\",\"client_secret_expires_at\":1506509030813,\"registration_client_uri\":\"https://idp/nidp/oauth/nam/clients//9f30fa40-0178-4cbe-8cf5-e27c18a3ecbd\",\"redirect_uris\":[\"https://dummy.com\"],\"client_secret\":\"thesecret\",\"client_id_issued_at\":1506422630813,\"client_name\":\"80c66be1-a24a-4b55-84ab-8faeb775a85b\",\"client_id\":\"theid\",\"response_types\":[\"token\"]}"
        client != null
        !client.getClientId().isEmpty()
        !client.getClientSecret().isEmpty()
    }
}
