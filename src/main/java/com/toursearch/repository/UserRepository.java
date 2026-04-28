package com.toursearch.repository;

import com.toursearch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTelegramId(Long telegramId);
    boolean existsByTelegramId(Long telegramId);
}
