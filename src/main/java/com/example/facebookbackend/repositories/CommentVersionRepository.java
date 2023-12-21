package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.Comment;
import com.example.facebookbackend.entities.CommentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentVersionRepository extends JpaRepository<CommentVersion, Integer> {
    List<CommentVersion> findByComment(Comment comment);
}
