package com.zubrilovskaya.todolist.application.service;

import com.zubrilovskaya.todolist.api.dto.request.TaskRequest;
import com.zubrilovskaya.todolist.application.exception.NotFoundException;
import com.zubrilovskaya.todolist.domain.model.Status;
import com.zubrilovskaya.todolist.domain.model.Task;
import com.zubrilovskaya.todolist.domain.model.User;
import com.zubrilovskaya.todolist.domain.repository.TaskRepository;
import com.zubrilovskaya.todolist.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StatusService statusService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask(){
        TaskRequest request = new TaskRequest();
        request.setTitle("task");
        request.setDeadline(LocalDate.now().plusDays(1));

        User user = new User();
        user.setId(1);

        Status status = new Status();
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(statusService.getDefaultStatus()).thenReturn(status);
        when(taskRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Task task = taskService.createTask(1, request);
        assertEquals("task", task.getTitle());
        assertEquals(user, task.getUser());
        assertEquals(status, task.getStatus());
    }

    @Test
    void updateTask_shouldUpdateFields(){
        Task task = new Task();
        task.setTitle("old");
        task.setDeadline(LocalDate.now().plusDays(1));

        TaskRequest request = new TaskRequest();
        request.setTitle("new");

        when(taskRepository.findById(1)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Task newTask =  taskService.updateTask(1, request);
        assertEquals("new", newTask.getTitle());
    }

    @Test
    void updateTask_notFoundTast(){
        when(taskRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> taskService.updateTask(1, new TaskRequest()));
    }

    @Test
    void deleteTask(){
        when(taskRepository.existsById(1)).thenReturn(true);

        taskService.deleteTask(1);
        verify(taskRepository).deleteById(1);
    }

    @Test
    void deleteTask_notFoundTask(){
        when(taskRepository.existsById(1)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> taskService.deleteTask(1));
    }

    @Test
    void getTasksByUserId(){
        User user = new User();
        user.setId(1);

        List<Task> tasks = List.of(new Task());

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(taskRepository.findTasksByUser(user)).thenReturn(tasks);
        List<Task> res = taskService.getTasksByUserId(1);

        assertEquals(1, res.size());
    }

    @Test
    void getTasksByStatusId(){
        Status status = new Status();

        List<Task> tasks = List.of(new Task());

        when(statusService.getStatusById(1)).thenReturn(status);
        when(taskRepository.findTasksByStatus(status)).thenReturn(tasks);
        List<Task> res = taskService.getTaskByStatusId(1);

        assertEquals(1, res.size());
    }

    @Test
    void getTasksByUserId_userNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> taskService.getTasksByUserId(1));
    }

    @Test
    void getTasksByStatusId_statusNotFound() {
        when(statusService.getStatusById(1))
                .thenThrow(new NotFoundException("Status not found"));

        assertThrows(NotFoundException.class,
                () -> taskService.getTaskByStatusId(1));
    }

}
