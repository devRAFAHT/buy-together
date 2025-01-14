package com.github.devrafaht.userapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CheckUserDto {

    @NotNull(message = "The code is required")
    private UUID code;

}
