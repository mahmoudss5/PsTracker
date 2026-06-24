package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Codeforces;

import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Problem.ProblemMapper;
import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Submission.SubmissionMapper;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.ProblemRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.SubmissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.JsonNode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImpCfServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private UserService userService;

    @Mock
    private ProblemMapper problemMapper;

    @Mock
    private SubmissionMapper submissionMapper;

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @InjectMocks
    private ImpCfService cfService;

    @Test
    void testCheckIfUserCfAccountExist_Success() {
        String handle = "tourist";
        
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        JsonNode mockBody = mock(JsonNode.class);
        JsonNode mockStatus = mock(JsonNode.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq(handle))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(JsonNode.class)).thenReturn(ResponseEntity.ok(mockBody));
        when(mockBody.get("status")).thenReturn(mockStatus);
        when(mockStatus.asText()).thenReturn("OK");

        boolean exists = cfService.checkIfUserCfAccountExist(handle);

        assertTrue(exists);
    }

    @Test
    void testCheckIfUserCfAccountExist_NotFound() {
        String handle = "nonexistent";

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq(handle))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(JsonNode.class)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        boolean exists = cfService.checkIfUserCfAccountExist(handle);

        assertFalse(exists);
    }

    @Test
    void testCheckIfUserCfAccountExist_ServerError() {
        String handle = "tourist";

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq(handle))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(JsonNode.class)).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> cfService.checkIfUserCfAccountExist(handle));
        assertEquals("couldn't connect to codeforces", exception.getMessage());
    }

    @Test
    void testGetUserRating_Success() {
        String handle = "tourist";

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
        JsonNode mockBody = mock(JsonNode.class);
        JsonNode mockStatus = mock(JsonNode.class);
        JsonNode mockResult = mock(JsonNode.class);
        JsonNode mockUserInfo = mock(JsonNode.class);
        JsonNode mockRating = mock(JsonNode.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), eq(handle))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(JsonNode.class)).thenReturn(ResponseEntity.ok(mockBody));
        
        when(mockBody.get("status")).thenReturn(mockStatus);
        when(mockStatus.asText()).thenReturn("OK");
        when(mockBody.get("result")).thenReturn(mockResult);
        when(mockResult.get(0)).thenReturn(mockUserInfo);
        when(mockUserInfo.has("rating")).thenReturn(true);
        when(mockUserInfo.get("rating")).thenReturn(mockRating);
        when(mockRating.asLong()).thenReturn(3500L);

        Long rating = cfService.getUserRating(handle);

        assertEquals(3500L, rating);
    }
}
