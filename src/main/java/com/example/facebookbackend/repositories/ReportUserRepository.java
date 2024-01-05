package com.example.facebookbackend.repositories;

import com.example.facebookbackend.entities.ReportUser;
import com.example.facebookbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface ReportUserRepository extends JpaRepository<ReportUser, Integer> {
    Optional<ReportUser> findByUserAndCreatedUser(User user, User createdUser);
}
