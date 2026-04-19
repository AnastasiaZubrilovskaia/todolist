package com.zubrilovskaya.todolist.api.controller;

import com.zubrilovskaya.todolist.api.dto.request.TaskRequest;
import com.zubrilovskaya.todolist.api.dto.response.TaskResponse;
import com.zubrilovskaya.todolist.application.exception.ForbiddenException;
import com.zubrilovskaya.todolist.application.service.AuthService;
import com.zubrilovskaya.todolist.application.service.TaskService;
import com.zubrilovskaya.todolist.domain.model.Task;
import com.zubrilovskaya.todolist.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final AuthService authService;

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestHeader("Authorization") String authHeader,
                                                   @RequestBody TaskRequest request) {

        User user = getUserFromAuthHeader(authHeader);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(taskService.createTask(user.getId(), request)));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromAuthHeader(authHeader);

        List<TaskResponse> tasks = taskService.getTasksByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(@RequestHeader("Authorization") String authHeader,
                                                               @PathVariable Integer id) {
        getUserFromAuthHeader(authHeader);

        List<TaskResponse> tasks = taskService.getTaskByStatusId(id).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@RequestHeader("Authorization") String authHeader,
                                                   @RequestBody TaskRequest request, @PathVariable Integer id) {

        getUserFromAuthHeader(authHeader);
        return ResponseEntity.ok(toResponse(taskService.updateTask(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@RequestHeader("Authorization") String authHeader,
                                           @PathVariable Integer id) {
        getUserFromAuthHeader(authHeader);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    private TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus().getName());
        response.setCreatedAt(task.getCreatedAt());
        response.setDeadline(task.getDeadline());
        return response;
    }

    private User getUserFromAuthHeader(String authHeader) {
        if (!authHeader.startsWith("Bearer ")) {
            throw new ForbiddenException("Invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return authService.getByToken(token);
    }

}
