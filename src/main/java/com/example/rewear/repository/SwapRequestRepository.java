package com.example.rewear.repository;

import com.example.rewear.domain.Item;
import com.example.rewear.domain.Swap;
import com.example.rewear.domain.User;
import com.example.rewear.domain.enums.SwapStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SwapRequestRepository extends JpaRepository<Swap, Long> {
    List<Swap> findAllByUser(User user);
    List<Swap> findAllByItem(Item item);
    List<Swap> findAllBySwapStatus(SwapStatus swapStatus);
}
