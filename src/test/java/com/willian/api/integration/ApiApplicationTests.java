package com.willian.api.integration;

import com.willian.api.integration.config.KafkaTestcontainersConfig;
import com.willian.api.integration.config.PostgresTestcontainersConfig;
import com.willian.api.model.TaskModelRequest;
import com.willian.api.model.TaskModelResponse;
import com.willian.api.model.enums.TaskStatus;
import com.willian.api.service.TaskService;

import java.time.Duration;
import java.time.LocalDateTime;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({PostgresTestcontainersConfig.class, KafkaTestcontainersConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiApplicationTests {

    @Autowired
    private TaskService taskService;

    @Autowired
    private KafkaTemplate<String, TaskModelRequest> kafkaTemplate;

    @Autowired
    private Environment environment;

    @Autowired
    private MockMvc mockMvc;

    private static String authToken;

    private final PageRequest pageable = PageRequest.of(0, 10, Sort.by("id").descending());

    @BeforeAll
    static void authenticate(
        @Autowired
        MockMvc mockMvc
    ) throws Exception {
        mockMvc.perform(post("/tasks/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "test",
                            "password": "test"
                        }
                    """))
            .andExpect(status().isOk());

        String authResult = mockMvc.perform(post("/tasks/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "username": "test",
                            "password": "test"
                        }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty())
            .andReturn()
            .getResponse()
            .getContentAsString();

        authToken = new JSONObject(authResult).getString("token");
    }

    @Test
    @Order(1)
    void shouldReturn403WhenNotAuthenticated() throws Exception {
        // Given, When, Then
        mockMvc.perform(get("/tasks/getAll")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }

    @Test
    @Order(2)
    void shouldReturn403WhenTokenIsNotValid() throws Exception {
        // Given, When, Then
        mockMvc.perform(get("/tasks/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer SoMeToKeNhErE"))
            .andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    void shouldConsumeKafkaMessageAndSaveTask() throws Exception {
        // Given
        final String title = "Task 1";
        final String note = "Note 1";
        TaskModelRequest task = TaskModelRequest.builder()
            .title(title)
            .note(note)
            .build();

        // When
        sendMessage(LocalDateTime.now().toString(), task);

        // Then
        Awaitility.await().pollDelay(Duration.ofSeconds(1)).until(() -> true);

        mockMvc.perform(get("/tasks/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content.[0].id").value(1))
            .andExpect(jsonPath("$.content[0].title").value(title))
            .andExpect(jsonPath("$.content[0].note").value(note))
            .andExpect(jsonPath("$.content[0].status").value(TaskStatus.TO_DO.name()))
            .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty())
            .andExpect(jsonPath("$.content[0].updatedAt").isEmpty());

        Page<TaskModelResponse> allTasks = taskService.getAll(pageable);

        assertNotNull(allTasks);
        assertEquals(1, allTasks.getTotalElements());

        assertThat(allTasks.getContent().getFirst().title()).isEqualTo(title);
        assertThat(allTasks.getContent().getFirst().note()).isEqualTo(note);
        assertThat(allTasks.getContent().getFirst().status()).isEqualTo(TaskStatus.TO_DO);
        assertThat(allTasks.getContent().getFirst().createdAt()).isNotNull();
        assertThat(allTasks.getContent().getFirst().updatedAt()).isNull();
    }

    @Test
    @Order(4)
    void shouldConsumeKafkaMessageAndSaveTaskAndUpdateTask() throws Exception {
        // Given
        final String title = "Task 2";
        final String note = "Note 2";
        TaskModelRequest task = TaskModelRequest.builder()
            .title(title)
            .note(note)
            .status(TaskStatus.ON_HOLD)
            .build();
        final String requestUpdate = """
                {
                "status": "COMPLETED"
                }
            """;

        // When
        sendMessage(LocalDateTime.now().toString(), task);

        // Then
        Awaitility.await().pollDelay(Duration.ofSeconds(1)).until(() -> true);

        mockMvc.perform(get("/tasks/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2));

        Page<TaskModelResponse> allTasks = taskService.getAll(pageable);

        assertNotNull(allTasks);
        assertEquals(2, allTasks.getTotalElements());

        assertThat(allTasks.getContent().getFirst().title()).isEqualTo(title);
        assertThat(allTasks.getContent().getFirst().note()).isEqualTo(note);
        assertThat(allTasks.getContent().getFirst().status()).isEqualTo(TaskStatus.ON_HOLD);
        assertThat(allTasks.getContent().getFirst().createdAt()).isNotNull();
        assertThat(allTasks.getContent().getFirst().updatedAt()).isNull();

        mockMvc.perform(put("/tasks/update/2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken)
                .content(requestUpdate))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.title").value(title))
            .andExpect(jsonPath("$.note").value(note))
            .andExpect(jsonPath("$.status").value(TaskStatus.COMPLETED.name()))
            .andExpect(jsonPath("$.createdAt").isNotEmpty())
            .andExpect(jsonPath("$.updatedAt").isNotEmpty());

        TaskModelResponse task2 = taskService.getById(2L);
        assertThat(task2.title()).isEqualTo(title);
        assertThat(task2.note()).isEqualTo(note);
        assertThat(task2.status()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(task2.createdAt()).isNotNull();
        assertThat(task2.updatedAt()).isNotNull();
    }

    @Test
    @Order(5)
    void shouldDeleteTask() throws Exception {
        // Given, When
        mockMvc.perform(get("/tasks/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2));

        mockMvc.perform(get("/tasks/get/2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk());

        Page<TaskModelResponse> allTasks = taskService.getAll(pageable);

        assertNotNull(allTasks);
        assertEquals(2, allTasks.getTotalElements());

        mockMvc.perform(delete("/tasks/delete/2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/tasks/getAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(1));

        allTasks = taskService.getAll(pageable);

        assertNotNull(allTasks);
        assertEquals(1, allTasks.getTotalElements());
    }

    @Test
    @Order(6)
    void shouldReturn404WhenTaskNotFound() throws Exception {
        // Given, When, Then
        mockMvc.perform(get("/tasks/get/2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + authToken))
            .andExpect(status().isNotFound());

        Assertions.assertThrows(HttpClientErrorException.class, () -> taskService.getById(2L));
    }

    private void sendMessage(String key, TaskModelRequest message) {
        final String topic = environment.getProperty("spring.kafka.topic");
        assert topic != null;
        kafkaTemplate.send(topic, key, message);
    }
}
