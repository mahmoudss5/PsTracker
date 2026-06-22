package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Submission;


import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Submission.result.CodeforcesSubmissionResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class codeforcesSubmissionDto {
   private String status;
   List<CodeforcesSubmissionResult> result;

}
