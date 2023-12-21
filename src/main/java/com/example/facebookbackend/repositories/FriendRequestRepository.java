package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.FriendRequest;
import com.example.facebookbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {
    List<FriendRequest> findFriendRequestsBySender(User sender);
    List<FriendRequest> findFriendRequestsByReceiver(User receiver);
}
