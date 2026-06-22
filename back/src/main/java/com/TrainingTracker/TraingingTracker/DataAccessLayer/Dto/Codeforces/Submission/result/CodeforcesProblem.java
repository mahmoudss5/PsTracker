package com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Submission.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeforcesProblem {

private String name;
private String index;
private String rating;
List<String> tags;

}
