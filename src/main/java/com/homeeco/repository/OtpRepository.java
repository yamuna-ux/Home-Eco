package com.homeeco.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.homeeco.entity.OtpData;

public interface OtpRepository extends JpaRepository<OtpData, Long> {

    Optional<OtpData> findByEmail(String email);
}
