package com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.CompositeKey;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncmentUserId implements Serializable {

    private Long announcmentId;
    private Long userId;
}
