package com.itm.space.backendresources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.space.backendresources.api.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        token = getAccessToken();
    }

    private String getAccessToken() throws Exception {
        String url = "http://backend-gateway-client:8080/auth/realms/ITM/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Добавляем scope=openid для получения правильного токена
        String requestBody = String.format("client_id=%s&client_secret=%s&username=%s&password=%s&grant_type=%s&scope=%s",
                URLEncoder.encode("backend-gateway-client", StandardCharsets.UTF_8),
                URLEncoder.encode("RTJHvYwavDdYQbMS1N3WH3SEdOk7OFDo", StandardCharsets.UTF_8),
                URLEncoder.encode("user", StandardCharsets.UTF_8),
                URLEncoder.encode("user", StandardCharsets.UTF_8),
                URLEncoder.encode("password", StandardCharsets.UTF_8),
                URLEncoder.encode("openid", StandardCharsets.UTF_8));

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, POST, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("access_token").asText();
        }
        throw new RuntimeException("Failed to get token: " + response.getStatusCode());
    }

    @Test
    void createUser_ShouldReturnOkStatus() throws Exception {
        // Arrange
        String userRequestJson = """
            {
                "username": "john_doe",
                "firstName": "John",
                "lastName": "Doe",
                "email": "john.doe@example.com",
                "password": "password123"
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<String> request = new HttpEntity<>(userRequestJson, headers);

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/users",
                POST,
                request,
                Void.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        // Arrange
        UUID userId = UUID.fromString("fb8da62e-8d10-475b-8b03-f4d66d9f1e35");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users/" + userId,
                GET,
                request,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        String responseBody = response.getBody();
        assertThat(responseBody).isNotNull();

        // Parse response manually
        UserResponse userResponse = objectMapper.readValue(responseBody, UserResponse.class);
        assertThat(userResponse.getFirstName()).isEqualTo("Jane");
        assertThat(userResponse.getLastName()).isEqualTo("Doe");
        assertThat(userResponse.getEmail()).isEqualTo("jane.doe@baeldung.com");

    }

    @Test
    void hello_ShouldReturnUsername() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/users/hello",
                GET,
                request,
                String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
    }
}
