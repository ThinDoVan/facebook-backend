package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.Post;
import com.example.facebookbackend.entities.ReportPost;
import com.example.facebookbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportPostRepository extends JpaRepository<ReportPost, Integer> {
    Optional<ReportPost> findByPostAndCreatedUser(Post post, User createdUser);
    List<ReportPost> findByPost(Post post);
;}
