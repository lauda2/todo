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

    @Test
    public void should_update_when_put() throws Exception {
        Todo todo = new Todo("123", "Buy bread", false);
        todoRepository.save(todo);
        Todo updatedTodo = new Todo(null, "Buy snacks", true);
        MockHttpServletRequestBuilder request = put("/todos/123").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(updatedTodo));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.text").value("Buy snacks"))
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    public void should_ignore_id_when_update() throws Exception {
        Todo todo = new Todo("123", "Buy bread", false);
        todoRepository.save(todo);
        Todo updatedTodo = new Todo("456", "Buy snacks", true);
        MockHttpServletRequestBuilder request = put("/todos/123").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(updatedTodo));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.text").value("Buy snacks"))
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    public void should_reject_update_when_id_not_exist() throws Exception {
        Todo updatedTodo = new Todo(null, "Buy snacks", true);
        MockHttpServletRequestBuilder request = put("/todos/999").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(updatedTodo));

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_reject_when_update_empty() throws Exception {
        MockHttpServletRequestBuilder request = put("/todos/123").contentType(MediaType.APPLICATION_JSON).content("{}");

        mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void should_respond_204_when_delete() throws Exception {
        Todo todo = new Todo("123", "Buy bread", false);
        todoRepository.save(todo);
        MockHttpServletRequestBuilder request = delete("/todos/123").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    public void should_respond_404_when_delete_non_exist_todo() throws Exception {
        MockHttpServletRequestBuilder request = delete("/todos/123").contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_allow_cors() throws Exception {
        MockHttpServletRequestBuilder request = options("/todos")
                .header("Access-Control-Request-Method", "*")
                .header("Origin", "http://localhost:3000");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

}
