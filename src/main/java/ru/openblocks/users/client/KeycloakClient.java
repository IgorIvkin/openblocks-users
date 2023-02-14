package ru.openblocks.users.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.openblocks.users.client.dto.keycloak.admintoken.response.AdminTokenResponse;
import ru.openblocks.users.client.dto.keycloak.createuser.request.KeycloakCreateUserRequest;
import ru.openblocks.users.client.dto.keycloak.getuser.response.KeycloakGetUserResponse;
import ru.openblocks.users.client.dto.keycloak.updateuser.request.KeycloakUpdateUserPasswordRequest;
import ru.openblocks.users.config.KeycloakServiceConfig;
import ru.openblocks.users.exception.KeycloakClientException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Этот клиент предназначен для взаимодействия с REST API Keycloak.
 */
@Slf4j
@Component
public class KeycloakClient {

    private static final String BEARER = "Bearer ";

    private final RestTemplate restTemplate;

    private final KeycloakServiceConfig keycloakServiceConfig;

    private KeycloakClient keycloakClient;

    @Autowired
    public KeycloakClient(@Qualifier("keycloak-rest-template") RestTemplate restTemplate,
                          KeycloakServiceConfig keycloakServiceConfig) {
        this.restTemplate = restTemplate;
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
    public void createUser(KeycloakCreateUserRequest request) {

        final String accessToken = keycloakClient.getAdminToken().getAccessToken();
        final String url = keycloakServiceConfig.getHost()
                + keycloakServiceConfig.getUrls().get(KeycloakServiceConfig.CREATE_USER_URL);
        log.info("Request to create user in Keycloak, url: {}, request: {}", url, request);

        try {
            restTemplate.postForObject(url, getBasicTypedHttpRequest(accessToken, request), Void.class);
        } catch (Exception ex) {
            log.error("Cannot create new user in Keycloak, reason: {}", ex.getMessage());
            throw new KeycloakClientException("Cannot create new user in Keycloak, reason: " + ex.getMessage());
        }
    }

    /**
     * Изменяет пароль пользователя в Keycloak.
     *
     * @param userId UUID пользователя в Keycloak
     * @param request запрос на изменение пароля в Keycloak
     */
    public void updateUserPassword(UUID userId, KeycloakUpdateUserPasswordRequest request) {

        final String accessToken = keycloakClient.getAdminToken().getAccessToken();
        final String url = buildUpdateUserUrl(userId);
        log.info("Request to update user password of user {} in Keycloak, url: {}", userId, url);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, getBasicTypedHttpRequest(accessToken, request), Void.class);
        } catch (Exception ex) {
            log.error("Cannot update user password in Keycloak, reason: {}", ex.getMessage());
            throw new KeycloakClientException("Cannot update user password in Keycloak, reason: " + ex.getMessage());
        }
    }

    /**
     * Возвращает пользователя по заданному логину (username).
     *
     * @param userName username пользователя
     * @return данные по пользователю
     */
    public Optional<KeycloakGetUserResponse> getUserByUserName(String userName) {

        final String accessToken = keycloakClient.getAdminToken().getAccessToken();
        final String url = buildGetUserUrl(userName);
        log.info("Request to get user in Keycloak, url: {}", url);

        try {

            ParameterizedTypeReference<List<KeycloakGetUserResponse>> typeReference
                    = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<List<KeycloakGetUserResponse>> usersResponse =
                    restTemplate.exchange(url, HttpMethod.GET, getBasicHttpRequest(accessToken), typeReference);
            List<KeycloakGetUserResponse> users = usersResponse.getBody();

            // Определяем, есть ли такой пользователь по заданному "username", есть есть, возвращаем его
            return getUserFromResponse(users, userName);

        } catch (Exception ex) {
            log.error("Cannot get user in Keycloak by id, reason: {}", ex.getMessage());
            throw new KeycloakClientException("Cannot get user in Keycloak by id, reason " + ex.getMessage());
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

    private String buildGetUserUrl(String userName) {
        String url = keycloakServiceConfig.getHost()
                + keycloakServiceConfig.getUrls().get(KeycloakServiceConfig.GET_USER_BY_USERNAME);
        return url.replace("{username}", userName);
    }

    private String buildUpdateUserUrl(UUID userId) {
        String url = keycloakServiceConfig.getHost()
                + keycloakServiceConfig.getUrls().get(KeycloakServiceConfig.UPDATE_USER);
        return url.replace("{id}", userId.toString());
    }

    private Optional<KeycloakGetUserResponse> getUserFromResponse(List<KeycloakGetUserResponse> users, String userName) {
        if (users != null) {
            for (KeycloakGetUserResponse user : users) {
                if (Objects.equals(userName, user.getUserName())) {
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }

    private <T> HttpEntity<T> getBasicTypedHttpRequest(String adminToken,
                                                       T request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, BEARER + adminToken);
        return new HttpEntity<>(request, headers);
    }

    private HttpEntity<Void> getBasicHttpRequest(String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, BEARER + adminToken);
        return new HttpEntity<>(headers);
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
