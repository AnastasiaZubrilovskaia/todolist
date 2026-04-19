package com.zubrilovskaya.todolist.api.controller;

import com.zubrilovskaya.todolist.api.dto.request.LoginRequest;
import com.zubrilovskaya.todolist.api.dto.request.RegisterRequest;
import com.zubrilovskaya.todolist.api.dto.response.UserResponse;
import com.zubrilovskaya.todolist.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest request) {
        String token = authService.register(request);
        UserResponse response = new UserResponse();
        response.setToken(token);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody @Valid LoginRequest request) {
        String token = authService.login(request);
        UserResponse response = new UserResponse();
        response.setToken(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

}
