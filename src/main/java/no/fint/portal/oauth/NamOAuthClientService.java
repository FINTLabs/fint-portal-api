package no.fint.portal.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.Map;

@Service
@Slf4j
public class NamOAuthClientService {

    @Autowired
    private ObjectMapper mapper;
    @Value("${fint.nam.oauth.username}")
    private String username;
    @Value("${fint.nam.oauth.password}")
    private String password;
    @Value("${fint.nam.oauth.idp-hostname}")
    private String idpHostname;
    @Value("${fint.nam.oauth.clientId}")
    private String clientId;
    @Value("${fint.nam.oauth.clientSecret}")
    private String clientSecret;

    private RestTemplate restTemplate;
    private RestTemplate tokenRestTemplate;
    private String accessToken;
    private Instant accessTokenExpiresAt;

    @PostConstruct
    private void init() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        if (tokenRestTemplate == null) {
            tokenRestTemplate = new RestTemplate();
        }
    }

    public OAuthClient addOAuthClient(String name) {
        log.info("Adding client {}...", name);
        OAuthClient oAuthClient = new OAuthClient(name);
        String jsonOAuthClient = null;

        try {
            jsonOAuthClient = mapper.writeValueAsString(oAuthClient);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAccessToken());

        HttpEntity<String> request = new HttpEntity<>(jsonOAuthClient, headers);

        try {
            String response = restTemplate.postForObject(NamOAuthConstants.CLIENT_REGISTRATION_URL_TEMPLATE, request, String.class, idpHostname);
            OAuthClient client = mapper.readValue(response, OAuthClient.class);
            log.info("Client ID {} created.", client.getClientId());
            return client;
        } catch (Exception e) {
            log.error("Unable to create client {}", name, e);
            throw new RuntimeException(e);
        }
    }

    public void removeOAuthClient(String clientId) {
        log.info("Deleting client {}...", clientId);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(getAccessToken());
            HttpEntity<Void> request = new HttpEntity<>(headers);
            restTemplate.exchange(NamOAuthConstants.CLIENT_URL_TEMPLATE, HttpMethod.DELETE, request, Void.class, idpHostname, clientId);
        } catch (Exception e) {
            log.error("Unable to delete client {}", clientId, e);
            throw e;
        }
    }

    public OAuthClient getOAuthClient(String clientId) {
        log.info("Fetching client {}...", clientId);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(getAccessToken());
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<OAuthClient> response = restTemplate.exchange(
                    NamOAuthConstants.CLIENT_URL_TEMPLATE,
                    HttpMethod.GET,
                    request,
                    OAuthClient.class,
                    idpHostname,
                    clientId
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Unable to get client {}", clientId, e);
            throw e;
        }
    }

    private synchronized String getAccessToken() {
        Instant now = Instant.now();
        if (accessToken != null && accessTokenExpiresAt != null && accessTokenExpiresAt.isAfter(now.plusSeconds(60))) {
            return accessToken;
        }

        String tokenUrl = String.format(NamOAuthConstants.ACCESS_TOKEN_URL_TEMPLATE, idpHostname);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", NamOAuthConstants.PASSWORD_GRANT_TYPE);
        form.add("username", username);
        form.add("password", password);
        form.add("scope", NamOAuthConstants.SCOPE);
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);
        Map<String, Object> tokenResponse = tokenRestTemplate.postForObject(tokenUrl, request, Map.class);

        if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
            throw new IllegalStateException("Token response missing access_token");
        }

        accessToken = tokenResponse.get("access_token").toString();
        Object expiresIn = tokenResponse.get("expires_in");
        long ttlSeconds = expiresIn instanceof Number ? ((Number) expiresIn).longValue() : 300L;
        accessTokenExpiresAt = now.plusSeconds(ttlSeconds);

        return accessToken;
    }
}
