package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.Comment;
import com.example.facebookbackend.entities.LikeComment;
import com.example.facebookbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeCommentRepository extends JpaRepository<LikeComment, Integer> {
    Optional<LikeComment> findByCommentAndUser(Comment comment, User user);
    List<LikeComment> findByComment(Comment comment);
}
