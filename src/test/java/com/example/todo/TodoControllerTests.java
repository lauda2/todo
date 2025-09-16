package com.example.todo;

import com.example.todo.entity.Todo;
import com.example.todo.repository.TodoRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.not;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerTests {

    private final Gson gson = new Gson();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @BeforeEach
    public void setup() {
        todoRepository.deleteAll();
    }

    @Test
    public void should_response_empty_list_when_index_with_no_any_todo() throws Exception {
        MockHttpServletRequestBuilder request = get("/todos").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void should_response_one_todo_when_index_with_one_todo() throws Exception {
        Todo todo = new Todo(null, "Buy milk", false);
        todoRepository.save(todo);
        MockHttpServletRequestBuilder request = get("/todos").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].text").value("Buy milk"))
                .andExpect(jsonPath("$[0].done").value(false));
    }

    @Test
    public void should_create_todo_when_post_todo() throws Exception {
        Todo todo = new Todo(null, "Buy milk", false);
        MockHttpServletRequestBuilder request = post("/todos").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(todo));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.text").value("Buy milk"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    public void should_reject_when_empty_text() throws Exception {
        Todo todo = new Todo(null, "", false);
        MockHttpServletRequestBuilder request = post("/todos").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(todo));

        mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void should_reject_when_miss_text() throws Exception {
        Todo todo = new Todo(null, null, false);
        MockHttpServletRequestBuilder request = post("/todos").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(todo));

        mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void should_ignore_id_when_post_id() throws Exception {
        Todo todo = new Todo("client-sent", "Buy bread", false);
        MockHttpServletRequestBuilder request = post("/todos").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(todo));

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(not("client-sent")))
                .andExpect(jsonPath("$.text").value("Buy bread"))
                .andExpect(jsonPath("$.done").value(false));
    }

}
