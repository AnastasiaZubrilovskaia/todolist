package com.zubrilovskaya.todolist.application.service;

import com.zubrilovskaya.todolist.application.exception.NotFoundException;
import com.zubrilovskaya.todolist.domain.model.Status;
import com.zubrilovskaya.todolist.domain.repository.StatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatusServiceTest {

    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private StatusService statusService;

    @Test
    void getStatusById() {
        Status status = new Status();

        when(statusRepository.findById(1)).thenReturn(Optional.of(status));
        Status result = statusService.getStatusById(1);
        assertEquals(status, result);
    }

    @Test
    void getStatusById_throwIfNotFound() {
        when(statusRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> statusService.getStatusById(1));
    }

    @Test
    void getDefaultStatus() {
        Status status = new Status();

        when(statusRepository.findByName("создана")).thenReturn(Optional.of(status));
        Status result = statusService.getDefaultStatus();

        assertEquals(status, result);
    }

    @Test
    void getDefaultStatus_throwIfNotFound() {
        when(statusRepository.findByName("создана")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> statusService.getDefaultStatus());
    }
}