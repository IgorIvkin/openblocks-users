package ru.openblocks.users.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.keycloak-service")
public class KeycloakServiceConfig {

    public static final String ADMIN_TOKEN_URL = "admin-token";

    public static final String CREATE_USER_URL = "create-user";

    @NotNull
    private String clientId;

    @NotNull
    private String clientSecret;

    @NotNull
    private String host;

    @NotNull
    private Map<String, String> urls;
}
