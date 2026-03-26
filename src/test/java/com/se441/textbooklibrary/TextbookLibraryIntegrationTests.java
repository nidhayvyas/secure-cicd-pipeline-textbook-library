package com.se441.textbooklibrary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.se441.textbooklibrary.dto.TextbookRequest;
import com.se441.textbooklibrary.model.Textbook;
import com.se441.textbooklibrary.repository.TextbookRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TextbookLibraryIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TextbookRepository textbookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        textbookRepository.deleteAll();

        textbookRepository.save(new Textbook(
                "Introduction to Software Engineering",
                0,
                "A comprehensive guide to software engineering principles",
                "https://images.unsplash.com/photo-1516116216624-53e697fedbea?w=800",
                "Ian Sommerville",
                "10th Edition"
        ));

        textbookRepository.save(new Textbook(
                "Clean Code",
                1,
                "A handbook of agile software craftsmanship",
                "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=800",
                "Robert C. Martin",
                "1st Edition"
        ));

        textbookRepository.save(new Textbook(
                "Design Patterns",
                2,
                "Elements of reusable object-oriented software",
                "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800",
                "Gang of Four",
                "1st Edition"
        ));
    }

    @Test
    @Order(1)
    @DisplayName("GET / - should return index page")
    void testIndexPage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    @DisplayName("GET /os - should return OS info")
    void testOsEndpoint() throws Exception {
        mockMvc.perform(get("/os"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.os").exists())
                .andExpect(jsonPath("$.env").exists());
    }

    @Test
    @Order(3)
    @DisplayName("GET /live - should return live status")
    void testLivenessEndpoint() throws Exception {
        mockMvc.perform(get("/live"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("live"));
    }

    @Test
    @Order(4)
    @DisplayName("GET /ready - should return ready status")
    void testReadinessEndpoint() throws Exception {
        mockMvc.perform(get("/ready"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ready"));
    }

    @Test
    @Order(5)
    @DisplayName("POST /textbook - should return textbook with id 0")
    void testGetTextbookId0() throws Exception {
        TextbookRequest request = new TextbookRequest(0);

        mockMvc.perform(post("/textbook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.name").value("Introduction to Software Engineering"))
                .andExpect(jsonPath("$.author").value("Ian Sommerville"));
    }

    @Test
    @Order(6)
    @DisplayName("POST /textbook - should return textbook with id 1")
    void testGetTextbookId1() throws Exception {
        TextbookRequest request = new TextbookRequest(1);

        mockMvc.perform(post("/textbook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Clean Code"))
                .andExpect(jsonPath("$.author").value("Robert C. Martin"));
    }

    @Test
    @Order(7)
    @DisplayName("POST /textbook - should return textbook with id 2")
    void testGetTextbookId2() throws Exception {
        TextbookRequest request = new TextbookRequest(2);

        mockMvc.perform(post("/textbook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Design Patterns"))
                .andExpect(jsonPath("$.author").value("Gang of Four"));
    }

    @Test
    @Order(8)
    @DisplayName("POST /textbook - should return 404 for non-existent textbook")
    void testGetTextbookNotFound() throws Exception {
        TextbookRequest request = new TextbookRequest(999);

        mockMvc.perform(post("/textbook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @DisplayName("Database should contain seeded textbooks")
    void testDatabaseContainsTextbooks() {
        long count = textbookRepository.count();
        Assertions.assertEquals(3, count, "Database should contain 3 textbooks");
    }

    @Test
    @Order(10)
    @DisplayName("Repository should find textbook by custom id field")
    void testRepositoryFindById() {
        var textbook = textbookRepository.findById(1);
        Assertions.assertTrue(textbook.isPresent(), "Textbook with id 1 should exist");
        Assertions.assertEquals("Clean Code", textbook.get().getName());
    }
}