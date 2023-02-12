package ru.openblocks.users.service.mapper;

import org.springframework.stereotype.Component;
import ru.openblocks.users.api.dto.users.create.request.UserCreateRequest;
import ru.openblocks.users.client.dto.keycloak.createuser.request.KeycloakCreateUserCredential;
import ru.openblocks.users.client.dto.keycloak.createuser.request.KeycloakCreateUserRequest;

import java.util.List;

@Component
public class KeycloakMapper {

    public KeycloakCreateUserRequest toCreateUserRequest(UserCreateRequest request) {
        KeycloakCreateUserRequest keycloakRequest = new KeycloakCreateUserRequest();
        keycloakRequest.setEmail(request.getUserName());
        keycloakRequest.setUsername(request.getUserName());
        keycloakRequest.setLastName(request.getLastName());
        keycloakRequest.setFirstName(request.getFirstName());

        KeycloakCreateUserCredential keycloakCredential = new KeycloakCreateUserCredential();
        keycloakCredential.setTemporary(false);
        keycloakCredential.setType("password");
        keycloakCredential.setValue(request.getPassword());

        keycloakRequest.setCredentials(List.of(keycloakCredential));

        return keycloakRequest;
    }
}
