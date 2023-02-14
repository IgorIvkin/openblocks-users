package ru.openblocks.users.service.mapper;

import org.mapstruct.Mapper;
import ru.openblocks.users.api.dto.users.create.request.UserCreateRequest;
import ru.openblocks.users.api.dto.users.get.UserGetResponse;
import ru.openblocks.users.persistence.entity.UserDataEntity;

@Mapper(componentModel = "spring")
public interface UserDataMapper {

    UserDataEntity toUserData(UserCreateRequest request);

    UserGetResponse toUserGetResponse(UserDataEntity user);
}
