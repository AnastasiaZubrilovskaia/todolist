package com.zubrilovskaya.todolist.controller;

import com.zubrilovskaya.todolist.api.controller.AuthController;
import com.zubrilovskaya.todolist.api.dto.request.RegisterRequest;
import com.zubrilovskaya.todolist.api.dto.response.UserResponse;
import com.zubrilovskaya.todolist.application.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_shouldReturn201() {
        RegisterRequest request = new RegisterRequest();
        request.setLogin("test");
        request.setPassword("123");
        request.setName("Test");

        when(authService.register(any())).thenReturn("token");

        ResponseEntity<UserResponse> response = authController.register(request);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("token", response.getBody().getToken());
    }

    @Test
    void register_controller_shouldReturnToken() {
        RegisterRequest request = new RegisterRequest();
        request.setLogin("test");
        request.setPassword("123");
        request.setName("name");

        when(authService.register(any())).thenReturn("token");

        ResponseEntity<UserResponse> response =
                authController.register(request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("token", response.getBody().getToken());
    }
}