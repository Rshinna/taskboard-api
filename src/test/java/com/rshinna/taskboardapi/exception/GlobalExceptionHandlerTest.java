package com.rshinna.taskboardapi.exception;

import com.rshinna.taskboardapi.auth.security.*;
import com.rshinna.taskboardapi.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    @WithMockUser
    void shouldReturn404WhenTaskNotFound() throws Exception {
        when(taskService.getTaskById(any(UUID.class)))
                .thenThrow(new ResourceNotFoundException("Task not found"));

        mockMvc.perform(get("/tasks/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Task not found"))
                .andDo(print());

        verify(taskService).getTaskById(any(UUID.class));

    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenAccessIsDenied() throws Exception {
        when(taskService.getTaskById(any(UUID.class)))
                .thenThrow(new AccessDeniedException("Access denied"));

        mockMvc.perform(get("/tasks/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value(
                        "You do not have permission to access this resource"))
                .andDo(print());

        verify(taskService).getTaskById(any(UUID.class));
    }

    @Test
    @WithMockUser
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\",\"description\":\"test\"}"))
                        .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

}
