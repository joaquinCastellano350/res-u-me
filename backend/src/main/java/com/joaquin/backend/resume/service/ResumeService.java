package com.joaquin.backend.resume.service;

import com.joaquin.backend.resume.domain.Resume;
import com.joaquin.backend.resume.domain.Visibility;
import com.joaquin.backend.resume.dto.PageResponse;
import com.joaquin.backend.resume.dto.ResumeResponse;
import com.joaquin.backend.resume.dto.ShareResponse;
import com.joaquin.backend.resume.repository.ResumeRepository;
import com.joaquin.backend.security.AuthUser;
import com.joaquin.backend.security.ShareTokens;
import com.joaquin.backend.storage.StorageService;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final StorageService storageService;
    private final Set<String> allowedMime;
    private final long maxBytes;
    private final long shareExpTime;

    public ResumeService(ResumeRepository resumeRepository, StorageService storageService,
                         @Value("${upload.allowed-mime}") String allowedMime, @Value("${upload.max-bytes}")long maxBytes, @Value("${upload.share.expiration-time}") long shareExpTime) {
        this.resumeRepository = resumeRepository;
        this.storageService = storageService;
        this.allowedMime = Arrays.stream(allowedMime.split(",")).map(String::trim).collect(Collectors.toSet());
        this.maxBytes = maxBytes;
        this.shareExpTime = shareExpTime;
    }

    @Transactional
    public ResumeResponse upload(MultipartFile file, String overrideFileName) {
        UUID userId = AuthUser.id();

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is null or empty");
        }
        String contentType = Optional.ofNullable(file.getContentType()).orElse("");
        if (!allowedMime.contains(contentType)) {
            throw new UnsupportedOperationException("Unsupported file type: " + contentType);
        }
        if (file.getSize() > maxBytes) {
            throw new IllegalStateException("File is too large");
        }
        String ext = contentType.split("/")[1];
        String originalFilename = (overrideFileName != null) ? overrideFileName : Optional.ofNullable(file.getOriginalFilename()).orElse("upload." + ext);
        UUID id = UUID.randomUUID();
        String storageKey = "files/%s/%s.%s".formatted(userId, id, ext);

        try {
            storageService.put(storageKey, file.getInputStream(), file.getSize() , contentType);

        } catch (IOException e) {
            throw new RuntimeException("Storage Write Failed", e);
        }

        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setOriginalFilename(originalFilename);
        resume.setMimeType(contentType);
        resume.setFileSize(file.getSize());
        resume.setStorageKey(storageKey);

        resume =  resumeRepository.save(resume);
        return toDto(resume);
    }

    public PageResponse<ResumeResponse> list(int page, int size, String sort) {
        UUID userId = AuthUser.id();
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<Resume> p = resumeRepository.findAllByUserIdOrderByUploadedAtDesc(userId , pageable);

        return new PageResponse<>(
                p.getContent().stream().map(this::toDto).toList(),
                p.getNumber(),
                p.getSize(),
                p.getTotalElements(),
                p.getTotalPages(),
                sort
        );
    }

    public ResumeResponse get(UUID id) {


        UUID userId = AuthUser.id();
        Resume e = resumeRepository.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new NoSuchElementException("File not found"));

        return toDto(e);
    }

    public Resource getFile(UUID id, @Nullable String shareKey){

        var r = resumeRepository.findById(id).orElseThrow(() -> new NoSuchElementException("File not found"));

        if (shareKey == null || shareKey.isBlank()) {
            UUID userId = AuthUser.id();
            Resume e = resumeRepository.findByUserIdAndId(userId, id).orElseThrow(() -> new NoSuchElementException("File not found"));
            return storageService.get(e.getStorageKey());
        }
        if (r.getVisibility() == Visibility.PUBLIC) {
            return storageService.get(r.getStorageKey());
        }
        if (r.getVisibility() == Visibility.UNLISTED && ShareTokens.slowEquals(r.getShareTokenHash(), shareKey)){
            return storageService.get(r.getStorageKey());
        }
        throw new NoSuchElementException("File not found");
    }


    @Transactional
    public ShareResponse generateOrRotateShare(UUID id) {
        UUID userId = AuthUser.id();
        var r = resumeRepository.findByUserIdAndId(userId, id)
                .orElseThrow(() -> new NoSuchElementException("File not found"));
        if (r.getVisibility() == Visibility.PRIVATE) {
            throw new UnsupportedOperationException("Private files can't be shared");
        }
        var token = ShareTokens.generateToken();
        r.setShareTokenHash(ShareTokens.sha256(token));
        r.setShareCreatedAt(Instant.now());
        r.setShareExpiratesAt(Instant.now().plusSeconds(shareExpTime));
        return new ShareResponse(token, Instant.now().plusSeconds(shareExpTime));
    }



    @Transactional
    public void delete(UUID id){
        UUID userId = AuthUser.id();
        Resume e = resumeRepository.findByUserIdAndId(userId, id).orElseThrow(() -> new NoSuchElementException("File not found"));
        storageService.delete(e.getStorageKey());
        resumeRepository.delete(e);

    }








    private ResumeResponse toDto(Resume resume) {
        return new ResumeResponse(
                resume.getId(),
                resume.getOriginalFilename(),
                resume.getMimeType(),
                resume.getFileSize(),
                resume.getStorageKey(),
                resume.getUploadedAt()
        );
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.DESC, "createdAt");
        String[] parts =  sort.split(",");
        String prop = parts[0].trim();
        Sort.Direction direction = (parts.length > 1 && "ASC".equalsIgnoreCase(parts[1].trim())) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, prop);
    }

}
