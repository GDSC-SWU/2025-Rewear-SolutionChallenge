package com.example.rewear.repository;

import com.example.rewear.domain.Notification;
import com.example.rewear.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);
}
