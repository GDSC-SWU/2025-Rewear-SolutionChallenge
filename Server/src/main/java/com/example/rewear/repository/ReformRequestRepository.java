package com.example.rewear.repository;

import com.example.rewear.domain.Reform;
import com.example.rewear.domain.User;
import com.example.rewear.domain.enums.ReformStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReformRequestRepository extends JpaRepository<Reform,Long> {
    List<Reform> findAllByUser(User user);
    List<Reform> findAllByReformStatus(ReformStatus reformStatus);

    Optional<Reform> findTopByItemIdAndReformStatusOrderByIdDesc(Long itemId, ReformStatus reformStatus);
}
