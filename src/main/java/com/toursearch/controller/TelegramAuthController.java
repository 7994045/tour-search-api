package com.toursearch.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class TelegramAuthController {

    @Value("${telegram.bot.token:}")
    private String botToken;

    @PostMapping("/telegram")
    public ResponseEntity<?> authTelegram(@RequestBody Map<String, Object> body) {
        String initData = (String) body.get("init_data");

        if (initData == null || initData.isEmpty()) {
            // Fallback: accept user data without verification (for testing)
            Object userId = body.get("id");
            if (userId != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", userId);
                result.put("first_name", body.getOrDefault("first_name", ""));
                result.put("last_name", body.getOrDefault("last_name", ""));
                result.put("username", body.getOrDefault("username", ""));
                result.put("authenticated", false);
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.badRequest().body(Map.of("error", "init_data is required"));
        }

        // Verify initData hash
        if (!verifyInitData(initData)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid hash"));
        }

        // Parse user data from initData
        Map<String, String> params = parseInitData(initData);
        String userJson = params.get("user");

        Map<String, Object> result = new HashMap<>();
        result.put("authenticated", true);

        if (userJson != null) {
            // Parse user JSON manually (simple approach)
            userJson = userJson.replace("{", "").replace("}", "");
            String[] pairs = userJson.split(",");
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    String key = kv[0].replace("\"", "").trim();
                    String value = kv[1].replace("\"", "").trim();
                    result.put(key, value);
                }
            }
        }

        return ResponseEntity.ok(result);
    }

    private boolean verifyInitData(String initData) {
        try {
            Map<String, String> params = parseInitData(initData);
            String hash = params.remove("hash");
            if (hash == null) return false;

            // Sort remaining params
            List<String> keys = new ArrayList<>(params.keySet());
            Collections.sort(keys);
            StringBuilder dataCheckString = new StringBuilder();
            for (String key : keys) {
                dataCheckString.append(key).append("=").append(params.get(key));
                dataCheckString.append("\n");
            }
            dataCheckString.deleteCharAt(dataCheckString.length() - 1);

            // Calculate secret key: SHA256("WebAppData", botToken)
            byte[] secretKey = hmacSha256("WebAppData", botToken);

            // Calculate hash: HMAC-SHA256(secretKey, dataCheckString)
            byte[] computedHash = hmacSha256Bytes(secretKey, dataCheckString.toString());

            // Convert to hex
            StringBuilder hexHash = new StringBuilder();
            for (byte b : computedHash) {
                hexHash.append(String.format("%02x", b));
            }

            return hexHash.toString().equals(hash);
        } catch (Exception e) {
            return false;
        }
    }

    private Map<String, String> parseInitData(String initData) {
        Map<String, String> params = new LinkedHashMap<>();
        String[] pairs = initData.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(kv[0], URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            }
        }
        return params;
    }

    private byte[] hmacSha256(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] hmacSha256Bytes(byte[] key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
