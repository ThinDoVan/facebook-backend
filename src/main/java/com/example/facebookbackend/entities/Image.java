package com.example.facebookbackend.entities;

import com.example.facebookbackend.enums.EImageType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "image")
@Data
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    private LocalDateTime createdTime;
    private String fileName;
    private String contentType;
    private String url;
    private long size;
    @Enumerated(EnumType.STRING)
    private EImageType imageType;
}
