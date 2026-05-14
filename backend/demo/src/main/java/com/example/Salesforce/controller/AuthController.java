package com.example.Salesforce.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    @Value("${salesforce.clientId}")
    private String clientId;

    @Value("${salesforce.clientSecret}")
    private String clientSecret;

    @Value("${salesforce.redirectUri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    // 🔐 LOGIN
    @GetMapping("/login")
    public ResponseEntity<Void> login() {

        String url = "https://login.salesforce.com/services/oauth2/authorize"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri;

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(java.net.URI.create(url));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // 🔁 CALLBACK (UPDATED - REAL USERNAME)
    @GetMapping("/callback")
    public ResponseEntity<Void> callback(
            @RequestParam String code,
            HttpSession session
    ) {

        String tokenUrl = "https://login.salesforce.com/services/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "grant_type=authorization_code"
                + "&code=" + code
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + redirectUri;

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(tokenUrl, request, Map.class);

        Map<String, Object> data = response.getBody();

        String accessToken = (String) data.get("access_token");
        String instanceUrl = (String) data.get("instance_url");
        String idUrl = (String) data.get("id");

        // 🔥 GET USER INFO (IMPORTANT)
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);

        HttpEntity<String> userEntity = new HttpEntity<>(userHeaders);

        ResponseEntity<Map> userResponse =
                restTemplate.exchange(idUrl, HttpMethod.GET, userEntity, Map.class);

        Map<String, Object> userInfo = userResponse.getBody();

        String username = (String) userInfo.get("email"); // or "username"

        // ✅ STORE SESSION
        session.setAttribute("accessToken", accessToken);
        session.setAttribute("instanceUrl", instanceUrl);
        session.setAttribute("username", username);

        // 🔁 redirect to frontend
        HttpHeaders redirect = new HttpHeaders();
        redirect.setLocation(java.net.URI.create("http://localhost:5173"));

        return new ResponseEntity<>(redirect, HttpStatus.FOUND);
    }

    // 👤 GET USER
    @GetMapping("/api/salesforce/me")
    public ResponseEntity<?> getUser(HttpSession session) {

        String username = (String) session.getAttribute("username");

        if (username == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        return ResponseEntity.ok(Map.of("username", username));
    }

    // 🚪 LOGOUT
    @GetMapping("/api/salesforce/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out");
    }
}