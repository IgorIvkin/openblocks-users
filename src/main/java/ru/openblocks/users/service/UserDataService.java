package ru.openblocks.users.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.openblocks.users.api.dto.users.create.request.UserCreateRequest;
import ru.openblocks.users.api.dto.users.create.response.UserCreateResponse;
import ru.openblocks.users.persistence.entity.UserDataEntity;
import ru.openblocks.users.persistence.repository.UserDataRepository;
import ru.openblocks.users.service.mapper.UserDataMapper;

import java.time.Clock;
import java.time.Instant;

/**
 * Сервис предназначен для взаимодействия с пользователями системы.
 */
@Slf4j
@Service
public class UserDataService {

    private final KeycloakService keycloakService;

    private final UserDataRepository userDataRepository;

    private final UserDataMapper userDataMapper;

    private final Clock clock = Clock.systemDefaultZone();

    @Autowired
    public UserDataService(KeycloakService keycloakService,
                           UserDataRepository userDataRepository,
                           UserDataMapper userDataMapper) {
        this.keycloakService = keycloakService;
        this.userDataRepository = userDataRepository;
        this.userDataMapper = userDataMapper;
    }

    /**
     * Создает нового пользователя в БД общего сервиса пользователей, а также регистрирует его
     * в Keycloak. Если пользователя не получилось завести в Keycloak, добавляться в базу данных он
     * не будет.
     *
     * @param request запрос на создание пользователя
     * @return идентификатор нового пользователя
     */
    public UserCreateResponse createUser(UserCreateRequest request) {

        log.info("Create new user {}", request);

        // Создаём нового пользователя в Keycloak
        log.info("Create user in Keycloak");
        keycloakService.createUser(request);

        // Создаём нового пользователя в БД сервиса
        log.info("Create user in database of Users common service");
        UserDataEntity userToCreate = userDataMapper.toUserData(request);
        userToCreate.setCreatedAt(Instant.now(clock));
        UserDataEntity createdUser = userDataRepository.save(userToCreate);

        // Возвращаем идентификатор добавленного пользователя
        Long userId = createdUser.getId();
        log.info("User was successfully created, ID {}", userId);
        return UserCreateResponse.builder()
                .id(userId)
                .build();
    }
}
