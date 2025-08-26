package com.joaquin.backend.resume.repository;

import com.joaquin.backend.resume.domain.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {
    Page<Resume> findAllByUserIdOrderByUploadedAtDesc(UUID userId, Pageable pageable);
    Optional<Resume> findByUserIdAndId(UUID userId, UUID id);
    boolean existsByUserIdAndId(UUID userId, UUID id);


}
