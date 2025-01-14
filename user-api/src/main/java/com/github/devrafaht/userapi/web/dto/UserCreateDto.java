package com.github.devrafaht.userapi.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCreateDto {

    @NotBlank(message = "The full name field is required.")
    @Size(max = 50, message = "The full name cannot be longer than 50 characters.")
    private String fullName;
    @Email(message = "Please provide a valid email address.")
    @NotBlank(message = "The email field is mandatory.")
    @Size(max = 320, message = "The email cannot be longer than 320 characters.")
    private String email;
    @NotBlank(message = "The username field is required.")
    @Size(max = 30, message = "The username cannot be longer than 30 characters.")
    private String username;
    @NotBlank(message = "The password field is required.")
    @Size(min = 8, message = "The password must be at least 8 characters long.")
    private String password;

}
