package com.phoenix.assetbe.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmailAndStatus(String email, Status status);

    boolean existsByIdAndStatus(Long id, Status status);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.status = :status")
    Optional<User> findByUserWithEmailAndStatus(@Param("email") String email, @Param("status") Status status);

    @Query("SELECT u FROM User u WHERE u.id = :id AND u.status = :status")
    Optional<User> findByUserWithIdAndStatus(@Param("id") Long id, @Param("status") Status status);
}
