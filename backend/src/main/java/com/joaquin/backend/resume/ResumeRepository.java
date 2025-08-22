package com.joaquin.backend.resume;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {
    Page<Resume> findAllByUserIdOrderByUploadedAtDesc(UUID userId, Pageable pageable);
    Optional<Resume> findByUserIdAndId(UUID userId, UUID id);
    boolean existsByUserIdAndId(UUID userId, UUID id);
}
