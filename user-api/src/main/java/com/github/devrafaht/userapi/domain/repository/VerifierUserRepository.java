package com.github.devrafaht.userapi.domain.repository;

import com.github.devrafaht.userapi.domain.entity.User;
import com.github.devrafaht.userapi.domain.entity.VerifierUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerifierUserRepository extends JpaRepository<VerifierUser, Long> {

    Optional<VerifierUser> findByUuid(UUID uuid);
    Optional<VerifierUser> findByUserId(UUID id);

}
