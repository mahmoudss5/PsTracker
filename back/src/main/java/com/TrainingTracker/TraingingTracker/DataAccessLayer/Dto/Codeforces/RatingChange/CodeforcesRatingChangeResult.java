package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.RatingChange;

import lombok.Data;

@Data
public class CodeforcesRatingChangeResult {
    private Long contestId;
    private String contestName;
    private String handle;
    private Integer rank;
    private Long ratingUpdateTimeSeconds;
    private Integer oldRating;
    private Integer newRating;
}
