package ru.openblocks.users.client.dto.keycloak.createuser.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakCreateUserCredential {

    private Boolean temporary;

    private String type = "password";

    private String value;
}
