package com.rshinna.taskboardapi.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rshinna.taskboardapi.dto.task.CreateTaskRequest;
import com.rshinna.taskboardapi.dto.task.UpdateTaskRequest;
import com.rshinna.taskboardapi.entity.TaskStatus;
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
  void shouldCreateUserSuccessfully() throws Exception {
    createUser("Rodrigo", "rodrigo@test.com");
  }

  @Test
  void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {

    createUser("Rodrigo", "rodrigo@test.com");

    String duplicatedUser =
        """
        {
          "name": "Rodrigo",
          "email": "rodrigo@test.com",
          "password": "123456",
          "role": "USER"
        }
        """;

    mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(duplicatedUser))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldLoginSuccessfully() throws Exception {

    createUser("Rodrigo", "rodrigo@test.com");

    loginAndGetToken("rodrigo@test.com");
  }

  @Test
  void shouldCreateTaskSuccessfully() throws Exception {

    createUser("Rodrigo", "rodrigo@test.com");

    String token = loginAndGetToken("rodrigo@test.com");

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

    createUser("Rodrigo", "rodrigo@test.com");

    String token = loginAndGetToken("rodrigo@test.com");

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

    createUser("Rodrigo", "rodrigo@test.com");

    String token = loginAndGetToken("rodrigo@test.com");
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

    createUser("Rodrigo", "rodrigo@test.com");

    String token = loginAndGetToken("rodrigo@test.com");

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

    createUser("Rodrigo", "rodrigo@test.com");

    String token = loginAndGetToken("rodrigo@test.com");

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

  @Test
  void shouldNotAllowUserToAccessTaskFromAnotherUser() throws Exception {

    createUser("Rodrigo", "rodrigoA@test.com");
    createUser("Maria", "maria@test.com");

    String tokenA = loginAndGetToken("rodrigoA@test.com");
    String tokenB = loginAndGetToken("maria@test.com");

    CreateTaskRequest taskRequest = new CreateTaskRequest("Task privada", "Somente dono acessa");

    String createdTaskResponse =
        mockMvc
            .perform(
                post("/tasks")
                    .header("Authorization", "Bearer " + tokenA)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(taskRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String taskId = objectMapper.readTree(createdTaskResponse).get("id").asText();

    mockMvc
        .perform(get("/tasks/" + taskId).header("Authorization", "Bearer " + tokenB))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldNotAllowUserToUpdateAnotherUserTask() throws Exception {

    createUser("Rodrigo", "rodrigo@test.com");
    createUser("Maria", "maria@test.com");

    String tokenA = loginAndGetToken("rodrigo@test.com");
    String tokenB = loginAndGetToken("maria@test.com");

    CreateTaskRequest createTaskRequest =
        new CreateTaskRequest("Task privada", "Somente Rodrigo pode editar");

    String taskResponse =
        mockMvc
            .perform(
                post("/tasks")
                    .header("Authorization", "Bearer " + tokenA)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTaskRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String taskId = objectMapper.readTree(taskResponse).get("id").asText();

    UpdateTaskRequest updateTaskRequest =
        new UpdateTaskRequest("hackeada", "Maria tentou editar", TaskStatus.COMPLETED);

    mockMvc
        .perform(
            put("/tasks/" + taskId)
                .header("Authorization", "Bearer " + tokenB)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTaskRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldNotAllowUserToDeleteAnotherUserTask() throws Exception {

    createUser("Rodrigo", "rodrigo@test.com");
    createUser("Maria", "maria@test.com");

    String tokenA = loginAndGetToken("rodrigo@test.com");
    String tokenB = loginAndGetToken("maria@test.com");

    CreateTaskRequest taskRequest = new CreateTaskRequest("Task privada", "Somente dono deleta");

    String taskResponse =
        mockMvc
            .perform(
                post("/tasks")
                    .header("Authorization", "Bearer " + tokenA)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(taskRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    String taskId = objectMapper.readTree(taskResponse).get("id").asText();

    mockMvc
        .perform(delete("/tasks/" + taskId).header("Authorization", "Bearer " + tokenB))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnUnauthorizedWhenLoginIsInvalid() throws Exception {

    String loginRequest =
        """
        {
          "email": "invalido@test.com",
          "password": "123456"
        }
        """;

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void shouldReturnUnauthorizedWithInvalidToken() throws Exception {

    mockMvc
        .perform(get("/tasks").header("Authorization", "Bearer token-falso"))
        .andExpect(status().isUnauthorized());
  }

  private void createUser(String name, String email) throws Exception {

    String userRequest =
        """
        {
            "name": "%s",
            "email": "%s",
            "password": "123456",
            "role": "USER"
        }
        """
            .formatted(name, email);

    mockMvc
        .perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userRequest))
        .andExpect(status().isCreated());
  }

  private String loginAndGetToken(String email) throws Exception {

    String loginRequest =
        """
        {
            "email": "%s",
            "password": "123456"
        }
        """
            .formatted(email);

    String response =
        mockMvc
            .perform(
                post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginRequest))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return objectMapper.readTree(response).get("token").asText();
  }
}
