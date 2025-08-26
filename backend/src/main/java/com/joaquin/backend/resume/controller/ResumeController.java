package com.joaquin.backend.resume.controller;

import com.joaquin.backend.resume.dto.ShareResponse;
import com.joaquin.backend.resume.service.ResumeService;
import com.joaquin.backend.resume.dto.PageResponse;
import com.joaquin.backend.resume.dto.ResumeResponse;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {
    private final ResumeService resumeService;
    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeResponse> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "filename", required = false) String filename
    ) {
        ResumeResponse saved = resumeService.upload(file, filename);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public PageResponse<ResumeResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "uploadedAt,DESC") String sort
    ){
        return resumeService.list(page, size, sort);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResumeResponse> get(
            @PathVariable UUID id
    ){
        ResumeResponse res = resumeService.get(id);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> getFile(@PathVariable UUID id,
                                            @RequestParam(required = false) String shareToken){

        Resource file = resumeService.getFile(id , shareToken);
        ResumeResponse md = null;
        try {
            md = resumeService.get(id);
        }catch(Exception ignored) {
            md = new ResumeResponse(
                    id,
                    "PROTECTED",
                    "application/pdf",
                    0,
                    "PROTECTED",
                    Instant.now()
            );
        }
        if (!file.exists()){
            throw new NoSuchElementException("File Missing");
        }
        String encoded = URLEncoder.encode(md.originalFilename(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(md.mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encoded)
                .body(file);


    }

    @PostMapping("/{id}/share")
    public ResponseEntity<ShareResponse> share(@PathVariable UUID id){
        ShareResponse res = resumeService.generateOrRotateShare(id);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResumeResponse> delete(
            @PathVariable UUID id){
        resumeService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
