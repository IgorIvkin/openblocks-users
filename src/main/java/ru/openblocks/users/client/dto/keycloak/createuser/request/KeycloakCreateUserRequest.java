package ru.openblocks.users.client.dto.keycloak.createuser.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakCreateUserRequest {

    private Boolean enabled = true;

    @JsonProperty("username")
    private String username;

    private String lastName;

    private String firstName;

    private String email;

    private List<KeycloakCreateUserCredential> credentials;

    @Override
    public String toString() {
        return "KeycloakCreateUserRequest{" +
                "enabled=" + enabled +
                ", username='" + username + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
