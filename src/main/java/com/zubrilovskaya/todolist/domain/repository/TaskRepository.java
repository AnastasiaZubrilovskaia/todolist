package com.zubrilovskaya.todolist.domain.repository;

import com.zubrilovskaya.todolist.domain.model.Status;
import com.zubrilovskaya.todolist.domain.model.Task;
import com.zubrilovskaya.todolist.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findTasksByUser(User user);
    List<Task> findTasksByStatus(Status status);
}
