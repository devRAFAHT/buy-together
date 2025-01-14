package com.github.devrafaht.userapi.domain.entity;

import com.github.devrafaht.userapi.domain.enums.CodeType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_verifier_user")
@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class VerifierUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private UUID uuid;
    @Column(nullable = false)
    private Instant expirationDate;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private User user;
    @Enumerated(EnumType.STRING)
    private CodeType codeType;

}
