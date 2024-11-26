package com.example.bez;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.bez.storage.FileSystemStorageService;
import com.example.bez.storage.StorageProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemStorageServiceTest {

    @TempDir
    Path tempDir;

    private FileSystemStorageService storageService;

    @BeforeEach
    void setUp() {
        StorageProperties properties = new StorageProperties();
        properties.setLocation(tempDir.toString());
        storageService = new FileSystemStorageService(properties);
        storageService.init();
    }

    @Test
    void store() throws IOException {
        MultipartFile file = new MockMultipartFile("test.txt", "Hello, World!".getBytes());
        storageService.store(file);

        Path storedFile = tempDir.resolve("test.txt");
        assertTrue(Files.exists(storedFile));
        assertEquals("Hello, World!", Files.readString(storedFile));
    }

    @Test
    void storeWithUsername() throws IOException {
        MultipartFile file = new MockMultipartFile("test.txt", "Hello, World!".getBytes());
        storageService.store(file, "user1");

        Path userDirectory = tempDir.resolve("user1");
        assertTrue(Files.exists(userDirectory));

        Path storedFile = userDirectory.resolve("test.txt");
        assertTrue(Files.exists(storedFile));
        assertEquals("Hello, World!", Files.readString(storedFile));
    }

    @Test
    void loadAll() {
        MultipartFile file1 = new MockMultipartFile("test1.txt", "Hello, World 1!".getBytes());
        MultipartFile file2 = new MockMultipartFile("test2.txt", "Hello, World 2!".getBytes());
        storageService.store(file1);
        storageService.store(file2);

        try (Stream<Path> paths = storageService.loadAll()) {
            assertEquals(2, paths.count());
        }
    }

    @Test
    void loadAllByUsername() {
        MultipartFile file1 = new MockMultipartFile("test1.txt", "Hello, World 1!".getBytes());
        MultipartFile file2 = new MockMultipartFile("test2.txt", "Hello, World 2!".getBytes());
        storageService.store(file1, "user1");
        storageService.store(file2, "user2");

        try (Stream<Path> paths = storageService.loadAllByUsername("user1")) {
            assertEquals(1, paths.count());
        }
    }

    @Test
    void load() {
        MultipartFile file = new MockMultipartFile("test.txt", "Hello, World!".getBytes());
        storageService.store(file);

        Path loadedFile = storageService.load("test.txt");
        assertTrue(Files.exists(loadedFile));
        assertEquals("test.txt", loadedFile.getFileName().toString());
    }

    @Test
    void loadAsResource() throws IOException {
        MultipartFile file = new MockMultipartFile("test.txt", "Hello, World!".getBytes());
        storageService.store(file);

        Resource resource = storageService.loadAsResource("test.txt");
        assertTrue(resource.exists());
        assertEquals("Hello, World!", new String(resource.getInputStream().readAllBytes()));
    }

    @Test
    void deleteAll() {
        MultipartFile file = new MockMultipartFile("test.txt", "Hello, World!".getBytes());
        storageService.store(file);

        storageService.deleteAll();
        assertFalse(Files.exists(tempDir.resolve("test.txt")));
    }

    @Test
    void init() {
        assertTrue(Files.exists(tempDir));
    }
}