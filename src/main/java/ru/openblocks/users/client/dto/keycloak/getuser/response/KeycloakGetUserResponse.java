package ru.openblocks.users.client.dto.keycloak.getuser.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakGetUserResponse {

    private UUID id;

    private Instant timestamp;

    @JsonProperty("username")
    private String userName;
}
