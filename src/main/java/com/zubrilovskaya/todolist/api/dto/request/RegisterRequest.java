package com.zubrilovskaya.todolist.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;

    @NotBlank(message = "login is required")
    private String login;

    @NotBlank(message = "password is required")
    private String password;
}
