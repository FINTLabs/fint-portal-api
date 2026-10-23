package no.fint.portal.model.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import spock.lang.Specification

import java.time.LocalDateTime

class ClientSpec extends Specification {

    def objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())

    def "getLastLoginTime parses the LDAP Generalized Time value to a LocalDateTime"() {
        given:
        def client = new Client()
        client.@lastLoginTime = "20260429080336Z"

        expect:
        client.getLastLoginTime() == LocalDateTime.of(2026, 4, 29, 8, 3, 36)
    }

    def "getLastLoginTime returns null when the directory provided no value"() {
        given:
        def client = new Client()

        expect:
        client.getLastLoginTime() == null
    }

    def "lastLoginTime is ignored when deserializing a request body"() {
        when:
        def client = objectMapper.readValue('{"name":"test","lastLoginTime":"20260429080336Z"}', Client)

        then:
        client.getName() == "test"
        client.getLastLoginTime() == null
    }

    def "lastLoginTime is included when serializing a response body"() {
        given:
        def client = new Client()
        client.@lastLoginTime = "20260429080336Z"

        when:
        def json = objectMapper.readTree(objectMapper.writeValueAsString(client))

        then:
        json.has("lastLoginTime")
    }
}
