package ru.openblocks.users.api.dto.users.get;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGetResponse {

    private Long id;

    private String userName;

    private String lastName;

    private String firstName;

    private String patronymicName;

    private LocalDate birthDate;
}
