package com.zubrilovskaya.todolist.controller;

import com.zubrilovskaya.todolist.api.controller.TaskController;
import com.zubrilovskaya.todolist.api.dto.request.TaskRequest;
import com.zubrilovskaya.todolist.api.dto.response.TaskResponse;
import com.zubrilovskaya.todolist.application.service.AuthService;
import com.zubrilovskaya.todolist.application.service.TaskService;
import com.zubrilovskaya.todolist.domain.model.Status;
import com.zubrilovskaya.todolist.domain.model.Task;
import com.zubrilovskaya.todolist.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @Test
    void createTask() {
        TaskRequest request = new TaskRequest();

        User user = new User();
        user.setId(1);

        Task task = new Task();
        task.setTitle("task");
        task.setDeadline(LocalDate.now().plusDays(1));

        Status status = new Status();
        status.setName("создана");

        task.setStatus(status);

        when(authService.getByToken("token")).thenReturn(user);
        when(taskService.createTask(eq(1), any())).thenReturn(task);

        ResponseEntity<TaskResponse> response =
                taskController.createTask("Bearer token", request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("task", response.getBody().getTitle());
    }

    @Test
    void getTasks() {
        User user = new User();
        user.setId(1);

        Task task = new Task();
        task.setTitle("t");
        task.setDeadline(LocalDate.now().plusDays(1));

        Status status = new Status();
        status.setName("создана");

        task.setStatus(status);

        when(authService.getByToken("token")).thenReturn(user);
        when(taskService.getTasksByUserId(1)).thenReturn(List.of(task));

        ResponseEntity<List<TaskResponse>> response =
                taskController.getTasks("Bearer token");

        assertEquals(1, response.getBody().size());
    }

    @Test
    void deleteTask() {
        User user = new User();
        user.setId(1);

        when(authService.getByToken("token")).thenReturn(user);

        ResponseEntity<Void> response =
                taskController.deleteTask("Bearer token", 1);

        verify(taskService).deleteTask(1);
        assertEquals(204, response.getStatusCode().value());
    }
}
