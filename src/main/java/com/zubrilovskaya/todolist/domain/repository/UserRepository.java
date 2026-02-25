package com.zubrilovskaya.todolist.domain.repository;

import com.zubrilovskaya.todolist.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);
    Optional<User> findByToken(String token);
}
