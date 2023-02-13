package ru.openblocks.users.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.openblocks.users.api.dto.users.create.request.UserCreateRequest;
import ru.openblocks.users.client.KeycloakClient;
import ru.openblocks.users.client.dto.keycloak.admintoken.response.AdminTokenResponse;
import ru.openblocks.users.client.dto.keycloak.createuser.request.KeycloakCreateUserRequest;
import ru.openblocks.users.service.mapper.KeycloakMapper;

/**
 * Сервис предназначен для взаимодействия с Keycloak.
 */
@Slf4j
@Service
public class KeycloakService {

    private final KeycloakClient keycloakClient;

    private final KeycloakMapper keycloakMapper;

    @Autowired
    public KeycloakService(KeycloakClient keycloakClient,
                           KeycloakMapper keycloakMapper) {
        this.keycloakClient = keycloakClient;
        this.keycloakMapper = keycloakMapper;
    }

    /**
     * Создаёт нового пользователя в Keycloak.
     *
     * @param request запрос на создание нового пользователя
     */
    public void createUser(UserCreateRequest request) {
        final AdminTokenResponse adminTokenResponse = keycloakClient.getAdminToken();
        final String accessToken = adminTokenResponse.getAccessToken();
        final KeycloakCreateUserRequest keycloakRequest = keycloakMapper.toCreateUserRequest(request);
        keycloakClient.createUser(keycloakRequest, accessToken);
    }
}
