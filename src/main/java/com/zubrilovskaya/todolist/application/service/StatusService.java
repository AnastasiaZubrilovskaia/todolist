package com.zubrilovskaya.todolist.application.service;

import com.zubrilovskaya.todolist.application.exception.NotFoundException;
import com.zubrilovskaya.todolist.domain.model.Status;
import com.zubrilovskaya.todolist.domain.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class StatusService {
    private final StatusRepository statusRepository;

    public Status getStatusById(Integer id) {
        return statusRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Status not found"));
    }

    public Status getDefaultStatus() {
        return statusRepository.findByName("создана")
                .orElseThrow(() -> new NotFoundException("Default status not found"));
    }
}
