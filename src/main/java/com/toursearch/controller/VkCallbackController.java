package com.toursearch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vk")
public class VkCallbackController {

    private static final Logger log = LoggerFactory.getLogger(VkCallbackController.class);

    @Value("${vk.confirmation.code:}")
    private String confirmationCode;

    @Value("${vk.group.id:0}")
    private long groupId;

    @Value("${vk.secret:}")
    private String secret;

    @PostMapping("/callback")
    public ResponseEntity<String> callback(@RequestBody String body) {
        log.info("VK callback received: {}", body.length() > 200 ? body.substring(0, 200) + "..." : body);

        // VK confirmation
        if (body.contains("\"type\":\"confirmation\"")) {
            if (confirmationCode != null && !confirmationCode.isEmpty()) {
                log.info("VK confirmation request");
                return ResponseEntity.ok(confirmationCode);
            }
            return ResponseEntity.ok("ok");
        }

        // Verify secret if set
        if (secret != null && !secret.isEmpty() && !body.contains(secret)) {
            log.warn("VK callback: secret mismatch");
            return ResponseEntity.ok("ok");
        }

        // Process message_new events
        if (body.contains("\"type\":\"message_new\"")) {
            log.info("VK message_new event received");
            // TODO: implement message processing
            return ResponseEntity.ok("ok");
        }

        log.info("VK callback: unhandled event type");
        return ResponseEntity.ok("ok");
    }
}
