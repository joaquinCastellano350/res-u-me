package com.joaquin.backend.resume.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "resumes", indexes = {
        @Index(name = "idx_resumes_user", columnList = "user_id")
})
public class Resume {
    @Id
    private UUID id;

    @Column(name = "user_id" , nullable = false)
    private UUID userId;

    @Column(name = "s3_key" , nullable = false)
    private String storageKey;

    @Column(name = "file_name", nullable = false)
    private String originalFilename;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    @Column(name = "published_at", nullable = true)
    private Instant publishedAt;

    @Column(name = "visibility", nullable = false)
    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PRIVATE;

    @Column(name = "share_token_hash")
    private String shareTokenHash;

    @Column(name = "share_created_at")
    private Instant shareCreatedAt;

    @Column(name = "share_expirates_at")
    private Instant shareExpiratesAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "resume_tags",
            joinColumns = @JoinColumn(name = "resume_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();


    @PrePersist
    public void prePersist() {
        if (uploadedAt == null) {
            uploadedAt = Instant.now();
        }
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public String getShareTokenHash() {
        return shareTokenHash;
    }

    public void setShareTokenHash(String shareTokenHash) {
        this.shareTokenHash = shareTokenHash;
    }

    public Instant getShareCreatedAt() {
        return shareCreatedAt;
    }

    public void setShareCreatedAt(Instant shareCreatedAt) {
        this.shareCreatedAt = shareCreatedAt;
    }

    public Instant getShareExpiratesAt() {
        return shareExpiratesAt;
    }

    public void setShareExpiratesAt(Instant shareExpiratesAt) {
        this.shareExpiratesAt = shareExpiratesAt;
    }
}
