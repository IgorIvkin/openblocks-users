package ru.openblocks.users.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.openblocks.users.api.dto.users.create.request.UserCreateRequest;
import ru.openblocks.users.client.dto.keycloak.admintoken.response.AdminTokenResponse;
import ru.openblocks.users.client.dto.keycloak.createuser.request.KeycloakCreateUserRequest;
import ru.openblocks.users.config.KeycloakServiceConfig;
import ru.openblocks.users.exception.KeycloakClientException;
import ru.openblocks.users.service.mapper.KeycloakMapper;

/**
 * Этот клиент предназначен для взаимодействия с REST API Keycloak.
 */
@Slf4j
@Component
public class KeycloakClient {

    private final KeycloakMapper keycloakMapper;

    private final RestTemplate restTemplate;

    private final KeycloakServiceConfig keycloakServiceConfig;

    private KeycloakClient keycloakClient;

    @Autowired
    public KeycloakClient(@Qualifier("keycloak-rest-template") RestTemplate restTemplate,
                          KeycloakMapper keycloakMapper,
                          KeycloakServiceConfig keycloakServiceConfig) {
        this.restTemplate = restTemplate;
        this.keycloakMapper = keycloakMapper;
        this.keycloakServiceConfig = keycloakServiceConfig;
    }

    @Lazy
    @Autowired
    public void setKeycloakClient(KeycloakClient keycloakClient) {
        this.keycloakClient = keycloakClient;
    }

    /**
     * Создаёт нового пользователя в Keycloak.
     *
     * @param request запрос на создание нового пользователя
     */
    public void createUser(UserCreateRequest request) {
        final AdminTokenResponse adminTokenResponse = keycloakClient.getAdminToken();
        final String accessToken = adminTokenResponse.getAccessToken();

        final String url = keycloakServiceConfig.getHost()
                + keycloakServiceConfig.getUrls().get(KeycloakServiceConfig.CREATE_USER_URL);
        final KeycloakCreateUserRequest keycloakRequest = keycloakMapper.toCreateUserRequest(request);
        log.info("Request to create user in Keycloak, url: {}, request: {}", url, keycloakRequest);

        try {
            restTemplate.postForObject(url, getCreateUserRequest(accessToken, keycloakRequest), Void.class);
        } catch (Exception ex) {
            log.error("Cannot create new user in Keycloak, reason: {}", ex.getMessage());
            throw new KeycloakClientException("Cannot create new user in Keycloak, reason: " + ex.getMessage());
        }
    }

    /**
     * Получает администраторский токен из Keycloak для совершения административных действий.
     *
     * @return администраторский токен из Keycloak
     */
    public AdminTokenResponse getAdminToken() {
        final String url = keycloakServiceConfig.getHost()
                + keycloakServiceConfig.getUrls().get(KeycloakServiceConfig.ADMIN_TOKEN_URL);
        log.info("Request to retrieve Keycloak admin token, url: {}", url);

        try {
            return restTemplate.postForObject(url, getAdminTokenRequest(), AdminTokenResponse.class);
        } catch (Exception ex) {
            log.error("Cannot receive admin token from Keycloak, reason: {}", ex.getMessage());
            throw new KeycloakClientException("Cannot receive admin token from Keycloak, reason: " + ex.getMessage());
        }
    }

    private HttpEntity<KeycloakCreateUserRequest> getCreateUserRequest(String adminToken,
                                                                       KeycloakCreateUserRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken);
        return new HttpEntity<>(request, headers);
    }

    private HttpEntity<MultiValueMap<String, String>> getAdminTokenRequest() {
        MultiValueMap<String, String> requestData = new LinkedMultiValueMap<>();
        requestData.add("client_id", keycloakServiceConfig.getClientId());
        requestData.add("client_secret", keycloakServiceConfig.getClientSecret());
        requestData.add("grant_type", "client_credentials");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<>(requestData, headers);
    }
}
