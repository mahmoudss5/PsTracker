package com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_name")
    private String teamName;

    @Column(name = "team_code", unique = true)
    private String teamCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id")
    private User coach;

    @OneToMany(mappedBy = "traineeTeam")
    @Builder.Default
    private List<User> trainees = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    @Builder.Default
    private List<TeamMessage> messages = new ArrayList<>();

    @OneToMany(mappedBy = "team")
    @Builder.Default
    private List<AnnouncmentTeam> announcements = new ArrayList<>();
}
