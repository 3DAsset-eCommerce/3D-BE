package com.phoenix.assetbe.model.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerifiedCodeRepository extends JpaRepository<VerifiedCode, Long> {
    Optional<VerifiedCode> findByEmail(String email);
}
