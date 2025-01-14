package com.github.devrafaht.userapi.web.dto;

import com.github.devrafaht.userapi.domain.enums.Role;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponseDto {

    private UUID id;
    private String fullName;
    private String username;
    private String role;
    private String status;

}
