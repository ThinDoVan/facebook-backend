package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.Friendship;
import com.example.facebookbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
    Friendship findByUser1AndUser2(User user1, User user2);
    List<Friendship> findByUser1(User user1);
    List<Friendship> findByUser2(User user2);

}
