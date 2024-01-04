package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.Post;
import com.example.facebookbackend.entities.ReportRequest;
import com.example.facebookbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRequestRepository extends JpaRepository<ReportRequest, Integer> {
    List<ReportRequest> findByPost(Post post);
    Optional<ReportRequest> findByPostAndCreatedUser(Post post, User createdUser);
    Optional<ReportRequest> findByUserAndCreatedUser(User user, User createdUser);
}
