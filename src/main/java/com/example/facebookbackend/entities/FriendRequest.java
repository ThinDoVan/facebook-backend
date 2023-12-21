package com.example.facebookbackend.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendRequest")
@Data
@NoArgsConstructor
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int friendRequestId;

    @ManyToOne
    @JoinColumn(name = "sentUserId")
    @JsonIgnoreProperties
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receivedUserId")
    private User receiver;

    private String message;

    private LocalDateTime createdTime;

    public FriendRequest(User sender, User receiver, String message, LocalDateTime createdTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "friendRequestId=" + friendRequestId +
                ", sentUser=" + sender +
                ", receivedUser=" + receiver +
                ", message='" + message + '\'' +
                ", createdTime=" + createdTime +
                '}';
    }
}
