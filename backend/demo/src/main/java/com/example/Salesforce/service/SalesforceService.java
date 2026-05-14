package com.example.Salesforce.service;

import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class SalesforceService {

    private final RestTemplate restTemplate;

    public SalesforceService() {
        this.restTemplate = new RestTemplate(
                new HttpComponentsClientHttpRequestFactory()
        );
    }

    private boolean notLoggedIn(String token, String url) {
        return token == null || url == null;
    }

    // 📥 GET RULES
    public ResponseEntity<Map> getValidationRules(HttpSession session) {

        String accessToken = (String) session.getAttribute("accessToken");
        String instanceUrl = (String) session.getAttribute("instanceUrl");

        if (accessToken == null || instanceUrl == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "❌ Please login first"));
        }

        try {
            // ✅ DO NOT ENCODE
            String query = "SELECT Id, ValidationName, Active FROM ValidationRule";

            String url = instanceUrl + "/services/data/v59.0/tooling/query?q=" + query;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            System.out.println("✅ Salesforce Response: " + response.getBody());

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "❌ Salesforce error: " + e.getMessage()));
        }
    }

    // 🔄 TOGGLE ONE
    public ResponseEntity<String> toggleValidationRule(String id, boolean active, HttpSession session) {

        String token = (String) session.getAttribute("accessToken");
        String urlBase = (String) session.getAttribute("instanceUrl");

        if (notLoggedIn(token, urlBase)) {
            return ResponseEntity.badRequest().body("Login required");
        }

        try {
            String url = urlBase + "/services/data/v59.0/tooling/sobjects/ValidationRule/" + id;

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);

            // GET metadata
            ResponseEntity<Map> res = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

            Map<String, Object> metadata =
                    (Map<String, Object>) res.getBody().get("Metadata");

            metadata.put("active", active);

            // PATCH
            HttpHeaders patchHeaders = new HttpHeaders();
            patchHeaders.setBearerAuth(token);
            patchHeaders.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.exchange(
                    url,
                    HttpMethod.PATCH,
                    new HttpEntity<>(Map.of("Metadata", metadata), patchHeaders),
                    String.class
            );

            return ResponseEntity.ok("Updated");

        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    // 🔄 TOGGLE ALL
    public ResponseEntity<String> toggleAllValidationRules(boolean active, HttpSession session) {

        ResponseEntity<Map> response = getValidationRules(session);

        // 🔥 STEP 1: check body
        if (response.getBody() == null) {
            return ResponseEntity.status(500)
                    .body("❌ Salesforce response is NULL");
        }

        Object recordsObj = response.getBody().get("records");

        // 🔥 STEP 2: check records exist
        if (recordsObj == null) {
            return ResponseEntity.status(500)
                    .body("❌ No validation rules found (Salesforce issue)");
        }

        List<Map<String, Object>> rules;

        try {
            rules = (List<Map<String, Object>>) recordsObj;
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("❌ Invalid records format");
        }

        // 🔥 STEP 3: check empty
        if (rules.isEmpty()) {
            return ResponseEntity.ok("⚠️ No rules to update");
        }

        // 🔁 STEP 4: toggle each rule
        for (Map<String, Object> rule : rules) {
            String id = (String) rule.get("Id");

            if (id != null) {
                toggleValidationRule(id, active, session);
            }
        }

        return ResponseEntity.ok("✅ All rules updated successfully");
    }
}