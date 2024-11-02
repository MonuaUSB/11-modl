package ru.itmentor.spring.boot_security.demo.controller;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.itmentor.spring.boot_security.demo.models.User;
import java.util.Arrays;
import java.util.List;

public class RestClient {

    private static final String URL = "http://94.198.50.185:7081/api/users";
    private final RestTemplate restTemplate = new RestTemplate();
    private String sessionId;

    public static void main(String[] args) {
        RestClient client = new RestClient();
        client.run();
    }

    public void run() {
        List<User> users = getUsers();

        String result = addUser(new User(3L, "James", "Brown", (byte) 30));

        result += updateUser(new User(3L, "Thomas", "Shelby", (byte) 30));

        result += deleteUser(3L);

        System.out.println("Итоговый код: " + result);
    }

    private List<User> getUsers() {
        ResponseEntity<User[]> response = restTemplate.exchange(
                URL,
                HttpMethod.GET,
                null,
                User[].class
        );

        // Получение session id из заголовка Set-Cookie
        HttpHeaders headers = response.getHeaders();
        sessionId = headers.getFirst(HttpHeaders.SET_COOKIE);

        return Arrays.asList(response.getBody());
    }

    private String addUser(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.COOKIE, sessionId);

        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                URL,
                HttpMethod.POST,
                request,
                String.class
        );
        return response.getBody();
    }

    private String updateUser(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.COOKIE, sessionId);

        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                URL,
                HttpMethod.PUT,
                request,
                String.class
        );
        return response.getBody();
    }

    private String deleteUser(Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, sessionId);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                URL + "/" + id,
                HttpMethod.DELETE,
                request,
                String.class
        );
        return response.getBody();
    }
}


