package com.se441.textbooklibrary;

import com.se441.textbooklibrary.controller.SystemController;
import com.se441.textbooklibrary.controller.TextbookController;
import com.se441.textbooklibrary.dto.TextbookRequest;
import com.se441.textbooklibrary.model.Textbook;
import com.se441.textbooklibrary.repository.TextbookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests using mocked MongoDB repository.
 * These tests are fast and don't require a real database.
 */
@WebMvcTest({TextbookController.class, SystemController.class})
class TextbookLibraryApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TextbookRepository textbookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Context loads")
    void contextLoads() {
    }

    @Test
    @DisplayName("POST /textbook - should return textbook (mocked)")
    void testGetTextbook_Success() throws Exception {
        Textbook textbook = new Textbook(
            "Software Engineering",
            1,
            "A comprehensive guide to software engineering",
            "http://example.com/image.jpg",
            "Ian Sommerville",
            "10th Edition"
        );

        when(textbookRepository.findById(1)).thenReturn(Optional.of(textbook));

        TextbookRequest request = new TextbookRequest(1);

        mockMvc.perform(post("/textbook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Software Engineering"))
                .andExpect(jsonPath("$.author").value("Ian Sommerville"));
    }

    @Test
    @DisplayName("POST /textbook - should return 404 when not found (mocked)")
    void testGetTextbook_NotFound() throws Exception {
        when(textbookRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        TextbookRequest request = new TextbookRequest(999);

        mockMvc.perform(post("/textbook")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /live - should return live status")
    void testLivenessEndpoint() throws Exception {
        mockMvc.perform(get("/live"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("live"));
    }

    @Test
    @DisplayName("GET /ready - should return ready status")
    void testReadinessEndpoint() throws Exception {
        mockMvc.perform(get("/ready"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ready"));
    }

    @Test
    @DisplayName("GET /os - should return OS info")
    void testOsEndpoint() throws Exception {
        mockMvc.perform(get("/os"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.os").exists())
                .andExpect(jsonPath("$.env").exists());
    }
}
