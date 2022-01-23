package com.example.simple_todo.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "parent_id")
    private Todo parent;

    private String title;

    private Boolean done;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "parent")
    @JsonManagedReference
    private List<Todo> subtasks;

    public Todo(User user, Todo parent, String title, Boolean done, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = user;
        this.parent = parent;
        this.title = title;
        this.done = done;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}