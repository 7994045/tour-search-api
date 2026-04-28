package com.toursearch.controller;

import com.toursearch.model.User;
import com.toursearch.repository.UserRepository;
import com.toursearch.service.JwtService;
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

    @Value("${telegram.bot.token}")
    private String botToken;

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public TelegramAuthController(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/telegram")
    public ResponseEntity<?> authTelegram(@RequestBody Map<String, Object> body) {
        String initData = (String) body.get("init_data");

        if (initData == null || initData.isEmpty()) {
            Object userId = body.get("id");
            if (userId != null) {
                Long tgId = Long.valueOf(userId.toString());
                User user = userRepository.findByTelegramId(tgId)
                        .orElseGet(() -> {
                            User u = new User();
                            u.setTelegramId(tgId);
                            return u;
                        });

                user.setFirstName((String) body.getOrDefault("first_name", ""));
                user.setLastName((String) body.getOrDefault("last_name", ""));
                user.setUsername((String) body.getOrDefault("username", null));
                user.setLanguageCode((String) body.getOrDefault("language_code", null));
                userRepository.save(user);

                String token = jwtService.generateToken(tgId, user.getUsername());

                Map<String, Object> result = new HashMap<>();
                result.put("authenticated", false);
                result.put("token", token);
                result.put("user", Map.of(
                    "id", tgId,
                    "first_name", user.getFirstName() != null ? user.getFirstName() : "",
                    "last_name", user.getLastName() != null ? user.getLastName() : "",
                    "username", user.getUsername() != null ? user.getUsername() : ""
                ));
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.badRequest().body(Map.of("error", "init_data is required"));
        }

        if (!verifyInitData(initData)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid hash"));
        }

        Map<String, String> params = parseInitData(initData);
        String userJson = params.get("user");

        Map<String, Object> result = new HashMap<>();
        result.put("authenticated", true);

        if (userJson != null) {
            userJson = userJson.replace("{", "").replace("}", "");
            String[] pairs = userJson.split(",");
            Map<String, String> userFields = new HashMap<>();
            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    String key = kv[0].replace("\"", "").trim();
                    String value = kv[1].replace("\"", "").trim();
                    userFields.put(key, value);
                }
            }

            Long tgId = Long.valueOf(userFields.getOrDefault("id", "0"));

            User user = userRepository.findByTelegramId(tgId)
                    .orElseGet(() -> {
                        User u = new User();
                        u.setTelegramId(tgId);
                        return u;
                    });

            user.setFirstName(userFields.getOrDefault("first_name", ""));
            user.setLastName(userFields.getOrDefault("last_name", ""));
            user.setUsername(userFields.getOrDefault("username", null));
            user.setLanguageCode(userFields.getOrDefault("language_code", null));
            userRepository.save(user);

            String token = jwtService.generateToken(tgId, user.getUsername());

            result.put("token", token);
            result.put("user", Map.of(
                "id", tgId,
                "first_name", user.getFirstName() != null ? user.getFirstName() : "",
                "last_name", user.getLastName() != null ? user.getLastName() : "",
                "username", user.getUsername() != null ? user.getUsername() : ""
            ));
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }

        Long telegramId = jwtService.getTelegramIdFromToken(token);
        User user = userRepository.findByTelegramId(telegramId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        return ResponseEntity.ok(Map.of(
            "id", user.getTelegramId(),
            "first_name", user.getFirstName() != null ? user.getFirstName() : "",
            "last_name", user.getLastName() != null ? user.getLastName() : "",
            "username", user.getUsername() != null ? user.getUsername() : "",
            "phone", user.getPhone() != null ? user.getPhone() : "",
            "email", user.getEmail() != null ? user.getEmail() : "",
            "city", user.getCity() != null ? user.getCity() : ""
        ));
    }

    @PostMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String authHeader,
                                          @RequestBody Map<String, String> body) {
        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
        }

        Long telegramId = jwtService.getTelegramIdFromToken(token);
        User user = userRepository.findByTelegramId(telegramId).orElse(null);

        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        if (body.containsKey("phone")) user.setPhone(body.get("phone"));
        if (body.containsKey("email")) user.setEmail(body.get("email"));
        if (body.containsKey("city")) user.setCity(body.get("city"));
        if (body.containsKey("first_name")) user.setFirstName(body.get("first_name"));
        if (body.containsKey("last_name")) user.setLastName(body.get("last_name"));

        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
            "id", user.getTelegramId(),
            "first_name", user.getFirstName() != null ? user.getFirstName() : "",
            "last_name", user.getLastName() != null ? user.getLastName() : "",
            "username", user.getUsername() != null ? user.getUsername() : "",
            "phone", user.getPhone() != null ? user.getPhone() : "",
            "email", user.getEmail() != null ? user.getEmail() : "",
            "city", user.getCity() != null ? user.getCity() : ""
        ));
    }

    private boolean verifyInitData(String initData) {
        try {
            Map<String, String> params = parseInitData(initData);
            String hash = params.remove("hash");
            if (hash == null) return false;

            List<String> keys = new ArrayList<>(params.keySet());
            Collections.sort(keys);
            StringBuilder dataCheckString = new StringBuilder();
            for (String key : keys) {
                dataCheckString.append(key).append("=").append(params.get(key)).append("\n");
            }
            dataCheckString.deleteCharAt(dataCheckString.length() - 1);

            byte[] secretKey = hmacSha256("WebAppData", botToken);
            byte[] computedHash = hmacSha256Bytes(secretKey, dataCheckString.toString());

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