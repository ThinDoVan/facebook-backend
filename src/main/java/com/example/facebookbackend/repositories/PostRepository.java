package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.Audience;
import com.example.facebookbackend.entities.Post;
import com.example.facebookbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAllByCreatedUser(User authorUser);

    List<Post> findAllByCreatedUserAndAudience(User authorUser, Audience eAudience);

    List<Post> findAllByCountReportedGreaterThanEqual(int number);
}
