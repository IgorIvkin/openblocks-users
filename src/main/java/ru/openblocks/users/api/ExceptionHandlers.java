package ru.openblocks.users.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.openblocks.users.api.dto.error.ErrorMessage;
import ru.openblocks.users.exception.KeycloakClientException;
import ru.openblocks.users.exception.UserNotFoundException;

@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return handleDefaultException(ex);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorMessage keycloakClientException(KeycloakClientException ex) {
        return handleDefaultException(ex);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage userNotFoundException(UserNotFoundException ex) {
        return handleDefaultException(ex);
    }

    private ErrorMessage handleDefaultException(Throwable ex) {
        return ErrorMessage.builder()
                .message(ex.getMessage())
                .build();
    }
}
