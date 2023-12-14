package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.PostVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVersionRepository extends JpaRepository<PostVersion, Integer> {
}
