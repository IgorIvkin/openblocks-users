package ru.openblocks.users.api.dto.users.update.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatePasswordRequest {

    @NotBlank
    private String userName;

    @NotBlank
    @Size(min = 5)
    private String password;
}
