package ru.openblocks.users.client.dto.keycloak.updateuser.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakUpdateUserPasswordRequest {

    private List<KeycloakUpdateUserCredential> credentials;
}
