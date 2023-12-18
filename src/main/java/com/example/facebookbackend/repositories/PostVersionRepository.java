package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.Post;
import com.example.facebookbackend.entities.PostVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostVersionRepository extends JpaRepository<PostVersion, Integer> {
    List<PostVersion> findAllByPost(Post post);
}
