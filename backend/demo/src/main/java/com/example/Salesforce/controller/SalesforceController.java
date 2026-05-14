package com.example.Salesforce.controller;

import com.example.Salesforce.service.SalesforceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/salesforce")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class SalesforceController {

    @Autowired
    private SalesforceService service;

    @GetMapping("/validation-rules")
    public ResponseEntity<?> getRules(HttpSession session) {
        return service.getValidationRules(session);
    }

    @PatchMapping("/toggle/{id}")
    public ResponseEntity<?> toggle(
            @PathVariable String id,
            @RequestParam boolean active,
            HttpSession session
    ) {
        return service.toggleValidationRule(id, active, session);
    }

    @PatchMapping("/toggle-all")
    public ResponseEntity<?> toggleAll(
            @RequestParam boolean active,
            HttpSession session
    ) {
        return service.toggleAllValidationRules(active, session);
    }
}