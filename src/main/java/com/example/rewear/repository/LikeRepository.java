package com.example.rewear.repository;

import com.example.rewear.domain.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<UserLike, Long> {
}
