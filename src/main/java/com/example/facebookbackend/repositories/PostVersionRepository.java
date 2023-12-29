package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.Post;
import com.example.facebookbackend.entities.PostVersion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostVersionRepository extends JpaRepository<PostVersion, Integer> {
    List<PostVersion> findAllByPost(Post post);
        @Query( value = "SELECT v.* FROM post_version AS v JOIN ( " +
                "  SELECT  post_version.post_id, MAX(modified_time) as lastModifiedTime " +
                "  FROM post_version " +
                "  GROUP BY post_id " +
                " ) AS v2 on v.post_id = v2.post_id and v.modified_time = v2.lastModifiedTime " +
                " WHERE v.content like :keyword ",
        nativeQuery = true)
    List<PostVersion> findByContentContains(@Param("keyword") String keyword);
}
