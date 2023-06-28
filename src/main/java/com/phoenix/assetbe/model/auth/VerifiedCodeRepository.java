package com.phoenix.assetbe.model.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VerifiedCodeRepository extends JpaRepository<VerifiedCode, Long> {
}
