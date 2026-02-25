package com.zubrilovskaya.todolist.api.dto.response;


import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskResponse {
    private String title;
    private String description;
    private String status;
    private LocalDate createdAt;
    private LocalDate deadline;
}
