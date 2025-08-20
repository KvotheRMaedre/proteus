package tech.kvothe.proteus.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.kvothe.proteus.dto.UserDto;
import tech.kvothe.proteus.dto.RecoveryJwtTokenDto;
import tech.kvothe.proteus.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDto> authenticateUser(@RequestBody @Valid UserDto userDto) {
        RecoveryJwtTokenDto token = userService.authenticateUser(userDto);
        return ResponseEntity.ok(token);
    }

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody @Valid UserDto userDto) {
        userService.createUser(userDto);
        return ResponseEntity.ok().build();
    }
}