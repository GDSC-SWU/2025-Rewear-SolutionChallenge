package com.example.rewear.repository;

import com.example.rewear.domain.Review;
import com.example.rewear.domain.Swap;
import com.example.rewear.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findAllByUser(User user);
    List<Review> findAllBySwap(Swap swap);

}
