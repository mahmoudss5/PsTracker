package com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites;


import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.CompositeKey.AnnouncmentUserId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "announcement_users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncmentUser {

    @EmbeddedId
    private AnnouncmentUserId announcmentUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("announcmentId")
    @JoinColumn(name = "announcement_id", referencedColumnName = "id")
    private Announcment announcment;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


}
