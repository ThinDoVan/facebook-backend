package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.Comment;
import com.example.facebookbackend.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPost(Post post);
    List<Comment> findByParentComment(Comment comment);
}
