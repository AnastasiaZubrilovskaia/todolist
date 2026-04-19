package com.zubrilovskaya.todolist.application.service;

import com.zubrilovskaya.todolist.api.dto.request.LoginRequest;
import com.zubrilovskaya.todolist.api.dto.request.RegisterRequest;
import com.zubrilovskaya.todolist.application.exception.ConflictException;
import com.zubrilovskaya.todolist.application.exception.NotFoundException;
import com.zubrilovskaya.todolist.application.exception.UnauthorizedException;
import com.zubrilovskaya.todolist.domain.model.User;
import com.zubrilovskaya.todolist.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String register(RegisterRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new ConflictException("User already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setLogin(request.getLogin());

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);

        user.setToken(UUID.randomUUID().toString());

        userRepository.save(user);

        return user.getToken();
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Wrong password");
        }

        user.setToken(UUID.randomUUID().toString());
        userRepository.save(user);

        return user.getToken();
    }

    public User getByToken(String token) {
        return userRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid token"));
    }

    public void logout(String token) {
        User user = userRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Invalid token"));
        user.setToken(null);
        userRepository.save(user);
    }
}


