package com.joaquin.backend.storage;

import org.springframework.core.io.Resource;

import java.io.InputStream;

public interface StorageService {
    String put(String key, InputStream in, long size, String contentType);
    Resource get(String key);
    void delete(String key);
}
