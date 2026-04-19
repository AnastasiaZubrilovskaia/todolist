package com.zubrilovskaya.todolist.application.service;

import com.zubrilovskaya.todolist.api.dto.request.LoginRequest;
import com.zubrilovskaya.todolist.api.dto.request.RegisterRequest;
import com.zubrilovskaya.todolist.application.exception.ConflictException;
import com.zubrilovskaya.todolist.domain.model.User;
import com.zubrilovskaya.todolist.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_createUser(){
        RegisterRequest request = new RegisterRequest();
        request.setLogin("test");
        request.setPassword("123");
        request.setName("name");

        when(userRepository.existsByLogin("test")).thenReturn(false);

        String token = authService.register(request);

        assertNotNull(token);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throwIfUserExists(){
        RegisterRequest request = new RegisterRequest();
        request.setLogin("test");

        when(userRepository.existsByLogin("test")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService.register(request));
    }

    @Test
    void login_returnToken(){
        LoginRequest request = new LoginRequest();
        request.setLogin("test");
        request.setPassword("123");

        User user = new User();
        user.setLogin("test");
        user.setPassword(new BCryptPasswordEncoder().encode("123"));

        when(userRepository.findByLogin("test")).thenReturn(Optional.of(user));
        String token = authService.login(request);
        assertNotNull(token);
    }

    @Test
    void login_wrongPassword() {
        LoginRequest request = new LoginRequest();
        request.setLogin("test");
        request.setPassword("wrong");

        User user = new User();
        user.setLogin("test");
        user.setPassword(new BCryptPasswordEncoder().encode("123"));

        when(userRepository.findByLogin("test")).thenReturn(Optional.of(user));
        assertThrows(Exception.class, () -> authService.login(request));
    }

    @Test
    void getByToken() {
        User user = new User();
        user.setToken("abc");
        when(userRepository.findByToken("abc")).thenReturn(Optional.of(user));

        User result = authService.getByToken("abc");
        assertEquals(user, result);
    }

    @Test
    void logout_shouldClearToken() {
        User user = new User();
        user.setToken("abc");

        when(userRepository.findByToken("abc")).thenReturn(Optional.of(user));
        authService.logout("abc");

        assertNull(user.getToken());
        verify(userRepository).save(user);
    }
}
