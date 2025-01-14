package com.github.devrafaht.userapi.web.controller;

import com.github.devrafaht.userapi.domain.entity.User;
import com.github.devrafaht.userapi.domain.service.UserService;
import com.github.devrafaht.userapi.web.dto.CheckUserDto;
import com.github.devrafaht.userapi.web.dto.EditPasswordDto;
import com.github.devrafaht.userapi.web.dto.UserCreateDto;
import com.github.devrafaht.userapi.web.dto.UserResponseDto;
import com.github.devrafaht.userapi.web.dto.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("buy-together/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public String status(){
        return "Ok.";
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@Valid @RequestBody UserCreateDto dto) {
        User createdUser = userService.create(UserMapper.toUser(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(createdUser));
    }

    @PostMapping(value = "/check-registration")
    public ResponseEntity checkRegistration(@Valid @RequestBody CheckUserDto dto){
        userService.checkRegistration(dto.getCode());

        return ResponseEntity.ok("Verified user.");
    }

    @GetMapping(params = "id")
    public ResponseEntity<UserResponseDto> findById(@RequestParam("id") UUID id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @GetMapping(params = "username")
    public ResponseEntity<UserResponseDto> findByUsername(@RequestParam("username") String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @GetMapping(params = "email")
    public ResponseEntity<UserResponseDto> findByEmail(@RequestParam("email") String email) {
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(UserMapper.toDto(user));
    }

    @PostMapping(value = "/send-code-for-user", params = "id")
    public ResponseEntity sendCodeToEditPassword(@RequestParam("id") UUID id){
        userService.sendCodeToEditPassword(id);

        return ResponseEntity.ok("Code sent to your email.");
    }

    @PostMapping(value = "/resend-code-for-user", params = "id")
    public ResponseEntity resendCode(@RequestParam("id") UUID id){
        userService.resendCode(id);

        return ResponseEntity.ok("Code resent to your email.");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updatePassword(@PathVariable UUID id, @Valid @RequestBody EditPasswordDto dto) {
        userService.editPassword(id, dto.getCode(), dto.getNewPassword(), dto.getConfirmPassword());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
