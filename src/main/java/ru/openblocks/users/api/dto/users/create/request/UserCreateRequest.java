package ru.openblocks.users.api.dto.users.create.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    @NotBlank
    private String userName;

    @NotBlank
    private String password;

    @NotBlank
    private String lastName;

    @NotBlank
    private String firstName;

    private String patronymicName;

    private LocalDate birthDate;

    @Override
    public String toString() {
        return "UserCreateRequest{" +
                "userName='" + userName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", patronymicName='" + patronymicName + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}
