package com.github.devrafaht.userapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EditPasswordDto {

    @NotNull(message = "The code is required.")
    private UUID code;
    @NotBlank(message = "The new password is required.")
    private String newPassword;
    @NotBlank(message = "Password confirmation is required.")
    private String confirmPassword;

}
