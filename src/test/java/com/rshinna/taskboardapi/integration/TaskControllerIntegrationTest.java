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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

        createTask(token, "Estudar Spring", "Aprender testes de integração");

        mockMvc
                .perform(get("/tasks").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Estudar Spring"))
                .andExpect(jsonPath("$.content[0].description").value("Aprender testes de integração"))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void shouldListTasksFilteredByStatus()throws Exception{

        createUser("Rodrigo", "rodrigo@test.com");

        String token = loginAndGetToken("rodrigo@test.com");

        String task1 = createTask(token, "Estudar Spring", "Aprender testes de integração");
        String task2 = createTask(token, "Entender testes", "Apĺicar testes ");

        String updateRequest =
                """
                        {
                            "title": "Entender testes atualizada",
                            "description": "Descrição atualizada",
                            "status": "IN_PROGRESS"
                        }
                        """;

        mockMvc.perform(put("/tasks/" + task2)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk());

        mockMvc
                .perform(get("/tasks?status=PENDING").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(task1))
                .andExpect(jsonPath("$.content[0].title").value("Estudar Spring"))
                .andExpect(jsonPath("$.content[0].description").value("Aprender testes de integração"))
                .andExpect(jsonPath("$.content[0].status").value("PENDING"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        mockMvc
                .perform(get("/tasks?status=IN_PROGRESS").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(task2))
                .andExpect(jsonPath("$.content[0].title").value("Entender testes atualizada"))
                .andExpect(jsonPath("$.content[0].description").value("Descrição atualizada"))
                .andExpect(jsonPath("$.content[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void shouldGetTaskByIdSuccessfully() throws Exception {

        createUser("Rodrigo", "rodrigo@test.com");

        String token = loginAndGetToken("rodrigo@test.com");

        String taskId = createTask(token, "Estudar Spring", "Aprender integração");
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

        String taskId = createTask(token, "Task antiga", "Descrição antiga");

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

        String taskId = createTask(token, "Task para deletar", "Teste de delete");

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

        String taskId = createTask(tokenA,"Task privada", "Somente dono acessa");

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

        String taskId = createTask(tokenA,"Task privada", "Somente Rodrigo pode editar");

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

        String taskId = createTask(tokenA, "Task privada", "Somente dono deleta");

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

    private String createTask(String token, String title, String description) throws Exception {
        CreateTaskRequest request = new CreateTaskRequest(title, description);

        String response =
                mockMvc.perform(
                                post("/tasks")
                                        .header("Authorization", "Bearer " + token)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }
}
