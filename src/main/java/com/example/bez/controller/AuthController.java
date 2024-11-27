package com.example.bez.controller;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import com.example.bez.UserDetailsResponse;
import com.example.bez.jwt.JwtResponse;
import com.example.bez.jwt.JwtUtils;
import com.example.bez.storage.StorageFileNotFoundException;
import com.example.bez.storage.StorageServiceInterface;
import com.example.bez.userinfo.MyLoginRequest;
import com.example.bez.userinfo.MyUser;
import com.example.bez.userinfo.UserService;
import io.jsonwebtoken.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    private final StorageServiceInterface storageService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
            StorageServiceInterface storageService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.storageService = storageService;
    }

    Comparator<File> comparator = Comparator.comparing(file -> {
        try {
            return Files.readAttributes(Paths.get(file.toURI()), BasicFileAttributes.class)
                    .creationTime();
        } catch (java.io.IOException e) {
            log.error("Error reading file attributes", e);
            return null;
        }
    });

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody MyLoginRequest loginRequest) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication.getName());

        // Find user and determine role
        MyUser user = userService.findUserByUsername(loginRequest.getUsername());
        String role = userService.determineRole(user.getDn().toString());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        return ResponseEntity.ok(new JwtResponse(jwt, role));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserDetails(@PathVariable String username) {
        MyUser user = userService.findUserByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        String role = userService.determineRole(user.getDn().toString());
        UserDetailsResponse userDetails =
                new UserDetailsResponse(user.getUsername(), user.getCommonName(), user.getSurname(),
                        user.getGivenName(), user.getDisplayName(), role);

        return ResponseEntity.ok(userDetails);
    }

    @PostMapping("/getFilesList")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        // Извлекаем имя пользователя из токена
        String username = jwtUtils.getUsernameFromJwtToken(token.replace("Bearer ", ""));

        if (username == null) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
        return ResponseEntity.ok(storageService.loadAllByUsername(username)
                .map(path -> MvcUriComponentsBuilder.fromMethodName(AuthController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));
    }

    @PostMapping("/uploadfiles")
    public ResponseEntity<String> uploadFile(@RequestParam("files") MultipartFile[] files,
            @RequestHeader("Authorization") String token) {
        // Извлекаем имя пользователя из токена
        String username = jwtUtils.getUsernameFromJwtToken(token.replace("Bearer ", ""));

        if (username == null) {
            return ResponseEntity.badRequest().body("Invalid token");
        }

        try {
            for (MultipartFile file : files) {
                storageService.store(file, username);
            }
            return ResponseEntity.ok("Файлы успешно загружены");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Ошибка при загрузке файлов: " + e.getMessage());
        }
    }

 @DeleteMapping("/deleteFile/{filename}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        // Декодируем URL-кодированное имя файла
        String decodedFilename = URLDecoder.decode(filename, StandardCharsets.UTF_8);

        // Кодируем имя файла для заголовка Content-Disposition
        String encodedFilename = "UTF-8''" + URLEncoder.encode(decodedFilename, StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename*=\"" + encodedFilename + "\"");

        // Логика удаления файла
        // ...

        return ResponseEntity.ok()
                .headers(headers)
                .body("File deleted successfully");
    }

    @GetMapping("/upload-dir/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);

        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf").body(file);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}