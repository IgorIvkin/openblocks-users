package ru.openblocks.users.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.openblocks.users.api.dto.users.create.request.UserCreateRequest;
import ru.openblocks.users.api.dto.users.create.response.UserCreateResponse;
import ru.openblocks.users.api.dto.users.get.UserGetResponse;
import ru.openblocks.users.api.dto.users.update.request.UserUpdatePasswordRequest;
import ru.openblocks.users.exception.UserNotFoundException;
import ru.openblocks.users.persistence.entity.UserDataEntity;
import ru.openblocks.users.persistence.repository.UserDataRepository;
import ru.openblocks.users.persistence.specification.UserSpecification;
import ru.openblocks.users.service.mapper.UserDataMapper;

import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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

    /**
     * Обновляет пароль пользователя в Keycloak.
     *
     * @param request запрос на изменение пароля пользователя в Keycloak
     */
    public void updatePassword(UserUpdatePasswordRequest request) {

        final String userName = request.getUserName();

        log.info("Update password for user: {}", userName);
        keycloakService.updateUserPassword(userName, request);
    }

    /**
     * Возвращает данные пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return данные пользователя по его идентификатору
     */
    public UserGetResponse getById(Long userId) {

        log.info("Get user by id {}", userId);

        UserDataEntity user = userDataRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.ofUserId(userId));

        return userDataMapper.toUserGetResponse(user);
    }

    /**
     * Возвращает данные пользователя по его логину.
     *
     * @param userName логин пользователя
     * @return данные пользователя по его логину
     */
    public UserGetResponse getByUserName(String userName) {

        log.info("Get user by username {}", userName);

        UserDataEntity user = userDataRepository.findByUserName(userName)
                .orElseThrow(() -> UserNotFoundException.ofUserName(userName));

        return userDataMapper.toUserGetResponse(user);
    }

    /**
     * Возвращает пользователей по поисковому запросу. Если в поисковом запросе указано
     * одно слово, оно считается фамилией пользователя. Если в поисковом запросе указано
     * несколько слов, то фамилией всегда считается первое из указанных слов.
     *
     * @param searchText поисковый запрос на поиск пользователей
     * @return список пользователей
     */
    public List<UserGetResponse> getBySearch(String searchText) {

        log.info("Search users by query: {}", searchText);

        if (Objects.isNull(searchText) || searchText.length() < 3) {
            return List.of();
        } else {

            // Разбиваем запрос на отдельные слова, пропускаем пробельные символы
            List<String> queryParts =
                    Arrays.stream(searchText.split(" "))
                            .map(String::trim)
                            .filter(StringUtils::hasText)
                            .toList();

            if (queryParts.isEmpty()) {
                return List.of();
            } else {

                // Первое из заданных слов будет считаться фамилией, остальные слова могут быть расположены
                // в любой части данных ФИО: имени, фамилии или отчестве, но если слов много, то все они должны
                // быть представлены в результате
                String firstQueryPart = queryParts.get(0);
                Specification<UserDataEntity> specification =
                        UserSpecification.byLastName(firstQueryPart)
                                .and(UserSpecification.byQueryWords(queryParts));

                List<UserDataEntity> users = userDataRepository.findAll(specification);
                return users.stream().map(userDataMapper::toUserGetResponse).toList();
            }
        }
    }

}
