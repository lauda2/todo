package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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
        Optional<Todo> optionalTodo = todoRepository.findById(id);
        if (optionalTodo.isPresent()) {
            todo.setId(id);
            return todoRepository.save(todo);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public void deleteTodo(String id) {
        todoRepository.deleteById(id);
    }
}
