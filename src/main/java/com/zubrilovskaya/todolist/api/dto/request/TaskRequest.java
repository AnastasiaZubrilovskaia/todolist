package com.zubrilovskaya.todolist.api.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequest {
    private String title;

    private String description;

    private Integer status;

    private LocalDate createdAt;

    private LocalDate deadline;
}
