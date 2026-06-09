package com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "problems")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "problem_index")
    private String problemIndex;

    @Column(name = "problem_name", unique = true)
    private String name;

    @Column(name = "contest_id")
    private Long contestId;

    @Column(name = "rating")
    private Integer rating;

    @ManyToMany
    @JoinTable(
            name = "problem_tags",
            joinColumns = @JoinColumn(name = "problem_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "problem")
    @Builder.Default
    private List<Submission> submissions = new ArrayList<>();
}
