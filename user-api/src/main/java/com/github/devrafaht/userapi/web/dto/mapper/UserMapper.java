package com.github.devrafaht.userapi.web.dto.mapper;

import com.github.devrafaht.userapi.domain.entity.User;
import com.github.devrafaht.userapi.web.dto.UserCreateDto;
import com.github.devrafaht.userapi.web.dto.UserResponseDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static User toUser(UserCreateDto createDto) {
        return new ModelMapper().map(createDto, User.class);
    }

    public static UserResponseDto toDto(User user) {
        String role = user.getRole().name().substring("ROLE_".length());

        String status = user.getStatus().name();

        PropertyMap<User, UserResponseDto> props = new PropertyMap<User, UserResponseDto>() {
            @Override
            protected void configure() {
                map().setRole(role);
                map().setStatus(status);
            }
        };

        ModelMapper mapper = new ModelMapper();
        mapper.addMappings(props);
        return mapper.map(user, UserResponseDto.class);
    }


    public static List<UserResponseDto> toListDto(List<User> users) {
        return users.stream().map(user -> toDto(user)).collect(Collectors.toList());
    }
}