package com.example.Salesforce.controller;

import com.example.Salesforce.service.SalesforceService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salesforce")
@CrossOrigin(
        origins = {
                "http://localhost:5173",
                "https://YOUR-VERCEL-URL.vercel.app"
        },
        allowCredentials = "true"
)
public class SalesforceController {

    @Autowired
    private SalesforceService service;

    // =========================
    // GET VALIDATION RULES
    // =========================
    @GetMapping("/validation-rules")
    public ResponseEntity<?> getRules(
            HttpSession session
    ) {
        return service.getValidationRules(session);
    }

    // =========================
    // TOGGLE SINGLE RULE
    // =========================
    @PatchMapping("/toggle/{id}")
    public ResponseEntity<?> toggle(
            @PathVariable String id,
            @RequestParam boolean active,
            HttpSession session
    ) {

        return service.toggleValidationRule(
                id,
                active,
                session
        );
    }

    // =========================
    // TOGGLE ALL RULES
    // =========================
    @PatchMapping("/toggle-all")
    public ResponseEntity<?> toggleAll(
            @RequestParam boolean active,
            HttpSession session
    ) {

        return service.toggleAllValidationRules(
                active,
                session
        );
    }
}
