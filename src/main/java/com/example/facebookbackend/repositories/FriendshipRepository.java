package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.Friendship;
import com.example.facebookbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
    Friendship findAllByUser1AndUser2(User user1, User user2);
}
