package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.RatingChange;

import lombok.Data;
import java.util.List;

@Data
public class CodeforcesRatingChangeResponse {
    private String status;
    private List<CodeforcesRatingChangeResult> result;
}
