package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.LikePost;
import com.example.facebookbackend.entities.Post;
import com.example.facebookbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikePostRepository extends JpaRepository<LikePost, Integer> {
    Optional<LikePost> findByPostAndUser(Post post, User user);
}
