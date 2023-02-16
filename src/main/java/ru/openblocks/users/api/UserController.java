package ru.openblocks.users.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.openblocks.users.api.dto.users.create.request.UserCreateRequest;
import ru.openblocks.users.api.dto.users.create.response.UserCreateResponse;
import ru.openblocks.users.api.dto.users.get.UserGetResponse;
import ru.openblocks.users.api.dto.users.update.request.UserUpdatePasswordRequest;
import ru.openblocks.users.service.UserDataService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserDataService userDataService;

    @Autowired
    public UserController(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @PostMapping
    public UserCreateResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userDataService.createUser(request);
    }

    @PutMapping("/password")
    public void updateUserPassword(@Valid @RequestBody UserUpdatePasswordRequest request) {
        userDataService.updatePassword(request);
    }

    @GetMapping("/{id}")
    public UserGetResponse getById(@PathVariable Long id) {
        return userDataService.getById(id);
    }

    @GetMapping("/user-name/{userName}")
    public UserGetResponse getByUserName(@PathVariable String userName) {
        return userDataService.getByUserName(userName);
    }

    @GetMapping("/search")
    public List<UserGetResponse> getBySearch(@RequestParam String searchText) {
        return userDataService.getBySearch(searchText);
    }
}
