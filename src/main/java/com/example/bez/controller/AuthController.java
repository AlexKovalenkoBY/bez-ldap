package com.example.bez.controller;

import java.io.File;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.bez.UserDetailsResponse;
import com.example.bez.jwt.JwtResponse;
import com.example.bez.jwt.JwtUtils;
import com.example.bez.storage.StorageFileNotFoundException;
import com.example.bez.storage.StorageServiceInterface;
import com.example.bez.userinfo.MyLoginRequest;
import com.example.bez.userinfo.MyUser;
import com.example.bez.userinfo.UserService;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
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
            return Files.readAttributes(Paths.get(file.toURI()), BasicFileAttributes.class).creationTime();
        } catch (java.io.IOException e) {
            log.error("Error reading file attributes", e);
            return null;
        }
    });

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody MyLoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

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
        UserDetailsResponse userDetails = new UserDetailsResponse(
                user.getUsername(),
                user.getCommonName(),
                user.getSurname(),
                user.getGivenName(),
                user.getDisplayName(),
                role);

        return ResponseEntity.ok(userDetails);
    }

    @PostMapping("/getFilesList")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        // Извлекаем имя пользователя из токена
        String username = jwtUtils.getUsernameFromJwtToken(token.replace("Bearer ", ""));

        if (username == null) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
        return ResponseEntity.ok(storageService.loadAllByUsername(username).map(
                path -> MvcUriComponentsBuilder.fromMethodName(AuthController.class,
                        "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));
    }

    @GetMapping("/upload-dir/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);

        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(file);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}