package no.fint.portal.model.client

import spock.lang.Specification

import java.time.LocalDateTime

class ClientSpec extends Specification {

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
}
