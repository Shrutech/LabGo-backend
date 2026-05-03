package com.labgo.backend.controller;

import com.labgo.backend.dto.AuthRequestDto;
import com.labgo.backend.dto.AuthResponseDto;
import com.labgo.backend.dto.ForgotPasswordRequest;
import com.labgo.backend.dto.LoginResponseDto;
import com.labgo.backend.entity.User;
import com.labgo.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:4173",
    "https://your-app-name.netlify.app"
})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody AuthRequestDto request) {
        String msg = authService.register(request);
        return ResponseEntity.ok(new AuthResponseDto(msg));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody AuthRequestDto request) {
        User user = authService.login(request);
        return ResponseEntity.ok(new LoginResponseDto("Login successful", user.getId(), user.getEmail()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponseDto> resetPassword(@RequestBody ForgotPasswordRequest request) {
        String msg = authService.resetPassword(request);
        return ResponseEntity.ok(new AuthResponseDto(msg));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }
}