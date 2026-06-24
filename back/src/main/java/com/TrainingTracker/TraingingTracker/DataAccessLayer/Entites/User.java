package com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Types.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User  implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "codeforces_handle", unique = true)
    private String codeforcesHandle;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "rate")
    private Long rate;

    @Column(name="max_rate")
    private Long maxRate;

    @Column(name = "rank")
    private String rank;

    @Column(name = "max_rank")
    private String maxRank;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_team_id")
    private Team traineeTeam;

    @OneToMany(mappedBy = "coach")
    @Builder.Default
    private List<Team> teams = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "sender")
    @Builder.Default
    private List<Announcment> sentAnnouncments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<AnnouncmentUser> receivedAnnouncments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Submission> submissions = new ArrayList<>();
}
