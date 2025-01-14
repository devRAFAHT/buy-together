package com.github.devrafaht.userapi.domain.entity;

import com.github.devrafaht.userapi.domain.enums.Role;
import com.github.devrafaht.userapi.domain.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_user")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 50)
    private String fullName;
    @Column(unique = true, nullable = false, length = 320)
    private String email;
    @Column(unique = true, nullable = false, length = 30)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, length = 50)
    @Enumerated(value = EnumType.STRING)
    private Role role = Role.ROLE_CLIENT;
    @Enumerated(value = EnumType.STRING)
    private Status status = Status.PENDING;

}
