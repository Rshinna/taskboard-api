package com.rshinna.taskboardapi.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rshinna.taskboardapi.dto.task.CreateTaskRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class TaskControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldReturnUnauthorizedWhenCreatingTaskWithoutToken() throws Exception {
    CreateTaskRequest request = new CreateTaskRequest("Estudar testes", "Aprender integração");

    mockMvc
        .perform(
            post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void createUser() throws Exception {

    String requestBody =
        """
        {
        "name": "Rodrigo",
        "email": "rodrigo@testemail.com",
        "password": "123456",
        "role": "USER"}
        """;

    mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {

    String requestBody =
        """
        {
        "name": "Rodrigo",
        "email": "rodrigo@testemail.com",
        "password": "123456",
        "role": "USER"}
        """;

    mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated());

    mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldLoginSuccessfully() throws Exception {

    String requestBody =
        """
        {
        "name": "Rodrigo",
        "email": "rodrigo@testemail.com",
        "password": "123456",
        "role": "USER"}
        """;

    mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated());

    String loginRequest =
        """
        {
        "email": "rodrigo@testemail.com",
        "password": "123456"
        }

        """;

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists());
  }

  @Test
  void shouldCreateTaskSuccessfully() throws Exception {

    String requestBody =
        """
        {
        "name": "Rodrigo",
        "email": "rodrigo@testemail.com",
        "password": "123456",
        "role": "USER"}
        """;

    mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated());

    String loginRequest =
        """
        {
        "email": "rodrigo@testemail.com",
        "password": "123456"
        }

        """;

    String loginResponse =
        mockMvc
            .perform(
                post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String token = objectMapper.readTree(loginResponse).get("token").asText();

    CreateTaskRequest taskRequest =
        new CreateTaskRequest("Estudar Spring", "Aprender testes de integração");

    mockMvc
        .perform(
            post("/tasks")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").value("Estudar Spring"))
        .andExpect(jsonPath("$.description").value("Aprender testes de integração"))
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  @Test
  void shouldListTasksSuccessfully() throws Exception {

    String requestBody =
        """
        {
        "name": "Rodrigo",
        "email": "rodrigo@testemail.com",
        "password": "123456",
        "role": "USER"}
        """;

    mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated());

    String loginRequest =
        """
        {
        "email": "rodrigo@testemail.com",
        "password": "123456"
        }

        """;

    String loginResponse =
        mockMvc
            .perform(
                post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String token = objectMapper.readTree(loginResponse).get("token").asText();

    CreateTaskRequest taskRequest =
        new CreateTaskRequest("Estudar Spring", "Aprender testes de integração");

    mockMvc
        .perform(
            post("/tasks")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(get("/tasks").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].title").value("Estudar Spring"))
        .andExpect(jsonPath("$[0].description").value("Aprender testes de integração"))
        .andExpect(jsonPath("$[0].status").value("PENDING"));
  }

  @Test
  void shouldGetTaskByIdSuccessfully() throws Exception {

    String userRequest =
        """
        {
            "name": "Rodrigo",
            "email": "rodrigo@test.com",
            "password": "123456",
            "role": "USER"
        }
        """;

    mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userRequest))
        .andExpect(status().isCreated());

    String loginRequest =
        """
        {
            "email": "rodrigo@test.com",
            "password": "123456"
        }
        """;

    String loginResponse =
        mockMvc
            .perform(
                post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String token = objectMapper.readTree(loginResponse).get("token").asText();

    CreateTaskRequest taskRequest = new CreateTaskRequest("Estudar Spring", "Aprender integração");

    String taskResponse =
        mockMvc
            .perform(
                post("/tasks")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(taskRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String taskId = objectMapper.readTree(taskResponse).get("id").asText();
    mockMvc
        .perform(get("/tasks/" + taskId).header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Estudar Spring"))
        .andExpect(jsonPath("$.description").value("Aprender integração"))
        .andExpect(jsonPath("$.status").value("PENDING"));
  }

  @Test
  void shouldUpdateTaskSuccessfully() throws Exception {

    String userRequest =
        """
        {
            "name": "Rodrigo",
            "email": "rodrigo@test.com",
            "password": "123456",
            "role": "USER"
        }
        """;

    mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userRequest))
        .andExpect(status().isCreated());

    String loginRequest =
        """
        {
            "email": "rodrigo@test.com",
            "password": "123456"
        }
        """;

    String loginResponse =
        mockMvc
            .perform(
                post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String token = objectMapper.readTree(loginResponse).get("token").asText();

    CreateTaskRequest createTaskRequest = new CreateTaskRequest("Task antiga", "Descrição antiga");

    String taskResponse =
        mockMvc
            .perform(
                post("/tasks")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTaskRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String taskId = objectMapper.readTree(taskResponse).get("id").asText();

    String updateRequest =
        """
        {
                    "title": "Task atualizada",
                    "description": "Descrição atualizada",
                    "status": "IN_PROGRESS"
                }
        """;

    mockMvc
        .perform(
            put("/tasks/" + taskId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Task atualizada"))
        .andExpect(jsonPath("$.description").value("Descrição atualizada"))
        .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
  }

  @Test
  void shouldDeleteTaskSuccessfully() throws Exception {

    String userRequest =
        """
        {
            "name": "Rodrigo",
            "email": "rodrigo@test.com",
            "password": "123456",
            "role": "USER"
        }
        """;

    mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userRequest))
        .andExpect(status().isCreated());

    String loginRequest =
        """
        {
            "email": "rodrigo@test.com",
            "password": "123456"
        }
        """;

    String loginResponse =
        mockMvc
            .perform(
                post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String token = objectMapper.readTree(loginResponse).get("token").asText();

    CreateTaskRequest createTaskRequest =
        new CreateTaskRequest("Task para deletar", "Teste de delete");

    String taskResponse =
        mockMvc
            .perform(
                post("/tasks")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTaskRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String taskId = objectMapper.readTree(taskResponse).get("id").asText();

    mockMvc
        .perform(delete("/tasks/" + taskId).header("Authorization", "Bearer " + token))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(get("/tasks/" + taskId).header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound());
  }
}
