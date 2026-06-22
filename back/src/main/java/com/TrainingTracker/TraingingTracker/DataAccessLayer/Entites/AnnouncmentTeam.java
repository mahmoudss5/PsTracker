package com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.CompositeKey.AnnouncmnentTeamId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "announcement_teams")
@Data
@Builder
public class AnnouncmentTeam {


    @EmbeddedId
    private AnnouncmnentTeamId announcmnetTeamId;


    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @MapsId("announcmentId")
    @JoinColumn(name = "announcement_id", referencedColumnName = "id")
    private Announcment announcment;


    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @MapsId("teamId")
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;
}
