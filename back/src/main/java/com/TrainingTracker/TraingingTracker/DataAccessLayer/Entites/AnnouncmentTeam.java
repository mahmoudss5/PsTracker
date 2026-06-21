package com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.CompositeKey.AnnouncmnentTeamId;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "announcment_team")
@Data
@Builder
public class AnnouncmentTeam {


    @EmbeddedId
    private AnnouncmnentTeamId announcmnetTeamId;


    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @MapsId("announcmetId")
    @JoinColumn(name = "announcmet_id", referencedColumnName = "id")
    private Announcment announcment;


    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @MapsId("teamId")
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;
}
