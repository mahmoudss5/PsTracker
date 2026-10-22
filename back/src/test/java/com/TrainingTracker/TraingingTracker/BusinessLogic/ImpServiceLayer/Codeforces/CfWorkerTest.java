package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Codeforces;

import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Problem.ProblemMapper;
import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Submission.SubmissionMapper;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.Queue.CfJob;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.ContestRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.ProblemRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.SubmissionRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CfWorkerTest {

    @Mock
    private CfQueue cfQueue;

    @Mock
    private RestClient restClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProblemMapper problemMapper;

    @Mock
    private SubmissionMapper submissionMapper;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private ContestRepository contestRepository;

    @InjectMocks
    private CfWorker worker;

    @Test
    void processDoesNothingWhenQueueIsEmpty() {
        when(cfQueue.dequeue()).thenReturn(null);

        worker.process();

        verifyNoInteractions(restClient);
    }
}
