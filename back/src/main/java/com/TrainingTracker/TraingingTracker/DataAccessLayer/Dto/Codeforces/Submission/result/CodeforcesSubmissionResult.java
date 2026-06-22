package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Submission.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeforcesSubmissionResult {
  private Long id ;
  private CodeforcesProblem problem;
  private String verdict;
  private Long timeConsumedMillis;
  private Long memoryConsumedBytes;
}
