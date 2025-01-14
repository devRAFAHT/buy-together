package com.github.devrafaht.userapi.domain.service;

import com.github.devrafaht.userapi.domain.entity.User;
import com.github.devrafaht.userapi.domain.entity.VerifierUser;
import com.github.devrafaht.userapi.domain.enums.CodeType;
import com.github.devrafaht.userapi.domain.enums.Status;
import com.github.devrafaht.userapi.domain.exception.*;
import com.github.devrafaht.userapi.domain.repository.UserRepository;
import com.github.devrafaht.userapi.domain.repository.VerifierUserRepository;
import com.github.devrafaht.userapi.domain.service.mailsender.MailSenderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository repository;
    private final VerifierUserRepository verifierUserRepository;
    private final MailSenderManager mailSenderManager;

    @Transactional
    public User create(User user) {
        log.info("Creating a new user with username: {}", user.getUsername());
        try {
            User savedUser = repository.save(user);
            log.info("User with id: {} successfully created", savedUser.getId());

            log.info("Generating verifier for user with id: {}", savedUser.getId());
            VerifierUser verifierUser = new VerifierUser();
            verifierUser.setUser(savedUser);
            verifierUser.setUuid(UUID.randomUUID());
            verifierUser.setExpirationDate(Instant.now().plusMillis(9));
            verifierUser.setCodeType(CodeType.ACCOUNT_CREATION);
            log.info("Verifier generated with UUID: {} and expiration date: {}", verifierUser.getUuid(), verifierUser.getExpirationDate());

            verifierUserRepository.save(verifierUser);
            log.info("VerifierUser successfully saved for user with id: {}", savedUser.getId());

            String message = String.format(
                    "Hello %s,\n\n" +
                            "Welcome to Buy Together! To activate your account, please use the code below:\n\n" +
                            "Activation Code: %s\n\n" +
                            "This code expires in 15 minutes. If you did not request the activation, please ignore this email.\n\n" +
                            "This email was sent automatically, so please do not reply to it. If you need further assistance, contact our technical support team.\n\n" +
                            "Best regards,\n" +
                            "Buy Together Team\n" +
                            "support@buytogether.com",
                    user.getFullName(),
                    verifierUser.getUuid()
            );

            mailSenderManager.sendMail("JavaMailSender","BUY TOGETHER <noreplybuytogether@gmail.com>", user.getEmail(), "Activate Account.", message);

            return savedUser;
        } catch (DataIntegrityViolationException e) {
            log.error("Error creating user with username: {}", user.getUsername(), e);
            throw new UniqueViolationException("There is already a registered user with this name or email");
        }
    }

    public void checkRegistration(UUID uuid) {
        log.info("Starting registration check for UUID: {}", uuid);

        VerifierUser verifierUser = verifierUserRepository.findByUuid(uuid)
                .orElseThrow(() -> {
                    log.error("User with UUID {} not found", uuid);
                    return new ResourceNotFoundException("Code " + uuid + " is invalid.");
                });

        if (verifierUser.getExpirationDate().compareTo(Instant.now()) < 0) {
            log.warn("Verification for UUID {} has expired.", uuid);
            throw new VerificationTimeoutException("Verification time has expired.");
        }

        User user = repository.findById(verifierUser.getUser().getId()).get();
        user.setStatus(Status.ACTIVE);
        repository.save(user);

        log.info("User with ID {} successfully verified and set to ACTIVE", user.getId());
    }

    @Transactional(readOnly = true)
    public User findById(UUID id) {
        log.info("Fetching user with id: {}", id);
        User user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id: " + id + " not found."));
        log.info("User with id: {} successfully fetched", id);
        return user;
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        log.info("Fetching user with username: {}", username);
        User user = repository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User with username: " + username + " not found."));
        log.info("User with username: {} successfully fetched", username);
        return user;
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        User user = repository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email: " + email + " not found."));
        log.info("User with email: {} successfully fetched", email);
        return user;
    }

    @Transactional
    public void sendCodeToEditPassword(UUID id) {
        User user = findById(id);

        VerifierUser verifierUser = verifierUserRepository.findByUserId(id).get();

        verifierUser.setExpirationDate(Instant.now().plusMillis(900000));
        verifierUser.setUuid(UUID.randomUUID());
        verifierUser.setCodeType(CodeType.PASSWORD_RESET);

        verifierUserRepository.save(verifierUser);

        String message = String.format(
                "Hello %s,\n\n" +
                        "We received a request to reset the password for your Buy Together account. If this was you, please use the code below to reset your password:\n\n" +
                        "Reset Code: %s\n\n" +
                        "This code expires in 15 minutes. If you did not request a password reset, you can safely ignore this email.\n\n" +
                        "This email was sent automatically, so please do not reply to it. If you need further assistance, contact our technical support team.\n\n" +
                        "Best regards,\n" +
                        "Buy Together Team\n" +
                        "support@buytogether.com",
                user.getFullName(),
                verifierUser.getUuid()
        );

        mailSenderManager.sendMail("JavaMailSender","BUY TOGETHER <noreplybuytogether@gmail.com>", user.getEmail(), "Activate Account.", message);
    }

    @Transactional
    public void editPassword(UUID id, UUID code, String newPassword, String confirmPassword) {
        User user = findById(id);

        VerifierUser verifierUser = verifierUserRepository.findByUuid(code)
                .orElseThrow(() -> {
                    log.error("Verifier user with UUID {} not found", code);
                    return new ResourceNotFoundException("Code " + code + " is invalid.");
                });

        if (verifierUser.getExpirationDate().compareTo(Instant.now()) < 0) {
            log.warn("Verification for UUID {} has expired.", code);
            throw new VerificationTimeoutException("Verification time has expired.");
        }

        if (!verifierUser.getUser().equals(user)) {
            log.error("The verification code {} does not belong to user with id {}", code, id);
            throw new CodeNotAssociatedException("The provided verification code does not belong to the specified user.");
        }


        log.info("Editing password for user with id: {}", id);
        if (!newPassword.equals(confirmPassword)) {
            log.error("Password confirmation does not match for user with id: {}", id);
            throw new PasswordInvalidException("New password does not match password confirmation.");
        }


        user.setPassword(newPassword);
        log.info("Password successfully updated for user with id: {}", id);

        repository.save(user);
    }

    @Transactional
    public void resendCode(UUID id) {
        User user = findById(id);

        VerifierUser verifierUser = verifierUserRepository.findByUserId(id)
                .orElseThrow(() -> new IllegalStateException("No verification code found for user with ID: " + id + ". Please request a new code."));

        if (verifierUser.getExpirationDate().compareTo(Instant.now()) < 0) {
            verifierUser.setUuid(UUID.randomUUID());
            verifierUser.setExpirationDate(Instant.now().plusMillis(900000));
            verifierUserRepository.save(verifierUser);
        }

        String message;

        if(verifierUser.getCodeType().equals(CodeType.PASSWORD_RESET)){
            message = String.format(
                    "Hello %s,\n\n" +
                            "We received a request to reset the password for your Buy Together account. If this was you, please use the code below to reset your password:\n\n" +
                            "Reset Code: %s\n\n" +
                            "This code expires in 15 minutes. If you did not request a password reset, you can safely ignore this email.\n\n" +
                            "This email was sent automatically, so please do not reply to it. If you need further assistance, contact our technical support team.\n\n" +
                            "Best regards,\n" +
                            "Buy Together Team\n" +
                            "support@buytogether.com",
                    user.getFullName(),
                    verifierUser.getUuid());
        } else{
            message = String.format(
                    "Hello %s,\n\n" +
                            "Welcome to Buy Together! To activate your account, please use the code below:\n\n" +
                            "Activation Code: %s\n\n" +
                            "This code expires in 15 minutes. If you did not request the activation, please ignore this email.\n\n" +
                            "This email was sent automatically, so please do not reply to it. If you need further assistance, contact our technical support team.\n\n" +
                            "Best regards,\n" +
                            "Buy Together Team\n" +
                            "support@buytogether.com",
                    user.getFullName(),
                    verifierUser.getUuid());
        }

        mailSenderManager.sendMail("JavaMailSender","BUY TOGETHER <noreplybuytogether@gmail.com>", user.getEmail(), "Activate Account.", message);
    }


    @Transactional
    public void delete(UUID id) {
        log.info("Deleting user with id: {}", id);
        User user = findById(id);

        try {
            repository.delete(user);
            log.info("User with id {} successfully deleted", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Error when trying to delete user with id: {}", id, e);
            throw new DatabaseException("Error when trying to delete the educational program with id: " + id);
        }
    }
}