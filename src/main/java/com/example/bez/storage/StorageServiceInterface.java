package com.example.bez.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageServiceInterface {

    void init();

    void store(MultipartFile file);
    void store(MultipartFile file, String username);

    Stream<Path> loadAll();
    Stream<Path> loadAllByUsername(String username);

    Path load(String filename);

    Resource loadAsResource(String filename);
    Resource saveUserFileFromBytesArray(byte[] fileBytes, String filename, String username);
    void removeAsResource(String filename, String username);

    void deleteAll();
}