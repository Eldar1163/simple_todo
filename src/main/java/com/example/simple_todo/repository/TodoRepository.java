package com.example.simple_todo.repository;

import com.example.simple_todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUserIdAndParentIsNull(Long id);

    Optional<Todo> findByIdAndUserId(Long id, Long userId);
}
