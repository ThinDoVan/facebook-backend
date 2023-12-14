package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {
}
