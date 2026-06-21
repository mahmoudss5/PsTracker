package com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.CompositeKey;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class AnnouncmentUserId implements Serializable {

    private Long announcmentId;
    private Long userId;
}
