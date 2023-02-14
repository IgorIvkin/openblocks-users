package ru.openblocks.users.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.openblocks.users.api.dto.users.create.request.UserCreateRequest;
import ru.openblocks.users.api.dto.users.update.request.UserUpdatePasswordRequest;
import ru.openblocks.users.client.KeycloakClient;
import ru.openblocks.users.client.dto.keycloak.createuser.request.KeycloakCreateUserRequest;
import ru.openblocks.users.client.dto.keycloak.getuser.response.KeycloakGetUserResponse;
import ru.openblocks.users.client.dto.keycloak.updateuser.request.KeycloakUpdateUserPasswordRequest;
import ru.openblocks.users.exception.UserNotFoundException;
import ru.openblocks.users.service.mapper.KeycloakMapper;

import java.util.Objects;
import java.util.UUID;

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
        final KeycloakCreateUserRequest keycloakRequest = keycloakMapper.toCreateUserRequest(request);
        keycloakClient.createUser(keycloakRequest);
    }

    /**
     * Изменяет пользователю пароль в Keycloak.
     *
     * @param userName username пользователя в Keycloak
     * @param request  запрос на изменение пароля
     */
    public void updateUserPassword(String userName, UserUpdatePasswordRequest request) {

        // Определяем идентификатор (UUID) пользователя в Keycloak
        final UUID userId = getUserIdByUserName(userName);
        final KeycloakUpdateUserPasswordRequest keycloakRequest = keycloakMapper.toUpdateUserPasswordRequest(request);

        keycloakClient.updateUserPassword(userId, keycloakRequest);
    }

    private UUID getUserIdByUserName(String userName) {
        final KeycloakGetUserResponse user = keycloakClient.getUserByUserName(userName)
                .orElseThrow(() -> UserNotFoundException.ofUserName(userName));
        final UUID userId = user.getId();

        if (Objects.isNull(userId)) {
            throw new IllegalStateException("User ID is null in Keycloak for username: " + userName);
        }

        return userId;
    }
}
