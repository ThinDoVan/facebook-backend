package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.LikeComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeCommentRepository extends JpaRepository<LikeComment, Integer> {
}
