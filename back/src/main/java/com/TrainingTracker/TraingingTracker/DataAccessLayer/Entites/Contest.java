package com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "contests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contest implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contest_id")
    private Long contestId;

    @Column(name = "contest_name")
    private String contestName;

    @Column(name = "rank")
    private Integer rank;

    @Column(name = "old_rating")
    private Integer oldRating;

    @Column(name = "new_rating")
    private Integer newRating;

    @Column(name = "rating_update_time")
    private LocalDateTime ratingUpdateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
