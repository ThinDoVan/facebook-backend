package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.LikePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikePostRepository extends JpaRepository<LikePost, Integer> {
}
