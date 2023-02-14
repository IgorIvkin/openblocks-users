package ru.openblocks.users.client.dto.keycloak.updateuser.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakUpdateUserCredential {

    private Boolean temporary;

    private String type = "password";

    private String value;
}
