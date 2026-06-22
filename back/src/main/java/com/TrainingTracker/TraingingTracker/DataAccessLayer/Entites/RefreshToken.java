package com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    @Column(name = "hased_refresh_token", nullable = false, unique = true)
    private String hased_refresh_token;

    @Column(name = "revoked", nullable = false)
    private Boolean revoked;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "expired_at")
    private Instant expiryDate;



}
