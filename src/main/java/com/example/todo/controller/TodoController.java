package com.example.todo.controller;

import com.example.todo.dto.TodoRequest;
import com.example.todo.entity.Todo;
import com.example.todo.mapper.TodoMapper;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;
    private final TodoMapper todoMapper;

    public TodoController(TodoService todoService,  TodoMapper todoMapper) {
        this.todoService = todoService;
        this.todoMapper = todoMapper;
    }

    @GetMapping
    public List<Todo> getTodoList() {
        return todoService.getTodoList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Todo createTodo(@Valid @RequestBody TodoRequest todoRequest) {
        return todoService.createTodo(todoMapper.toEntity(todoRequest));
    }

    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable String id, @Valid @RequestBody TodoRequest todoRequest) {
        return todoService.updateTodo(id, todoMapper.toEntity(todoRequest));
    }

}
