package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getTodoList() {
        return todoRepository.findAll();
    }

    public Todo createTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    public Todo updateTodo(String id, Todo todo) {
        todo.setId(id);
        return todoRepository.save(todo);
    }
}
