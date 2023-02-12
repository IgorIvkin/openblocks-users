package ru.openblocks.users.persistence.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.openblocks.users.persistence.entity.UserDataEntity;

import java.util.Optional;

@Repository
public interface UserDataRepository extends JpaRepository<UserDataEntity, Long> {

    Optional<UserDataEntity> findByUserName(@NotNull String userName);
}
