package ru.openblocks.users.client.dto.keycloak.admintoken.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;
}
