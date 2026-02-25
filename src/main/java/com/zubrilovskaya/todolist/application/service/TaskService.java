package com.zubrilovskaya.todolist.application.service;

import com.zubrilovskaya.todolist.api.dto.request.TaskRequest;
import com.zubrilovskaya.todolist.application.exception.NotFoundException;
import com.zubrilovskaya.todolist.domain.model.Status;
import com.zubrilovskaya.todolist.domain.model.Task;
import com.zubrilovskaya.todolist.domain.model.User;
import com.zubrilovskaya.todolist.domain.repository.TaskRepository;
import com.zubrilovskaya.todolist.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final StatusService statusService;

    @Transactional
    public Task createTask(Integer userId, TaskRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Status status;
        if (request.getStatus() == null) {
            status = statusService.getDefaultStatus();
        } else {
            status = statusService.getStatusById(request.getStatus());
        }

        Task task = new Task();
        task.setUser(user);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(status);
        task.setDeadline(request.getDeadline());

        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Integer id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getDeadline() != null) task.setDeadline(request.getDeadline());
        if (request.getStatus() != null) {
            Status status = statusService.getStatusById(request.getStatus());
            task.setStatus(status);
        }
        return taskRepository.save(task);
    }

    public void deleteTask(Integer id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Task not found");
        }
        taskRepository.deleteById(id);
    }

    public List<Task> getTasksByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return taskRepository.findTasksByUser(user);
    }

    public List<Task> getTaskByStatusId(Integer statusId) {
        Status status = statusService.getStatusById(statusId);

        return taskRepository.findTasksByStatus(status);
    }

}
