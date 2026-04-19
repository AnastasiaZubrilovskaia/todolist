package com.zubrilovskaya.todolist.api.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String login;

    private String password;
}