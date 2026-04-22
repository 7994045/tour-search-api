package com.toursearch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/max")
public class MaxCallbackController {

    private static final Logger log = LoggerFactory.getLogger(MaxCallbackController.class);

    @Value("${max.verify.token:}")
    private String verifyToken;

    @Value("${max.secret.key:}")
    private String secretKey;

    @PostMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestHeader(value = "X-Verify-Token", required = false) String headerToken,
            @RequestBody String body) {
        log.info("Max callback received: {}", body.length() > 200 ? body.substring(0, 200) + "..." : body);

        // Verify token
        if (verifyToken != null && !verifyToken.isEmpty()) {
            if (!verifyToken.equals(headerToken)) {
                log.warn("Max callback: verify token mismatch");
                return ResponseEntity.status(403).body("forbidden");
            }
        }

        // Max verification request
        if (body.contains("\"type\":\"verify\"")) {
            log.info("Max verification request");
            return ResponseEntity.ok(body);
        }

        // Process message events
        if (body.contains("\"type\":\"message\"")) {
            log.info("Max message event received");
            // TODO: implement message processing
            return ResponseEntity.ok("ok");
        }

        log.info("Max callback: unhandled event type");
        return ResponseEntity.ok("ok");
    }
}
