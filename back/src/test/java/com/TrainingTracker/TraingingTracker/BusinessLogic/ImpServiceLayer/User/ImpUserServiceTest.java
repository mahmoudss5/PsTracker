package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.User;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.TraineResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Team;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Types.Role;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.TeamRepository;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import com.TrainingTracker.TraingingTracker.Util.SecuiryUserUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImpUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserServiceMapper userMapper;

    @InjectMocks
    private ImpUserService userService;

    private User createTestUser(Long id, String username, String email) {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .codeforcesHandle(username + "_cf")
                .password("securePassword123")
                .role(Role.Trainee)
                .rate(1200L)
                .maxRate(1400L)
                .rank("Newbie")
                .maxRank("Pupil")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testGetUserById_Success() {
        Long userId = 1L;
        User expectedUser = createTestUser(userId, "alice", "alice@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getUserById(userId);

        assertNotNull(actualUser);
        assertEquals(userId, actualUser.getId());
        assertEquals("alice", actualUser.getUsername());
        assertEquals("alice@example.com", actualUser.getEmail());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_NotFound() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserById(userId));
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserResponseById_Success() {
        Long userId = 1L;
        User user = createTestUser(userId, "bob", "bob@example.com");
        TraineResponse expectedResponse = new TraineResponse(
                userId, "bob", "Trainee", "bob@example.com", "Team A", 42L
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toTraineResponse(user)).thenReturn(expectedResponse);

        TraineResponse actualResponse = userService.getUserResponseById(userId);

        assertNotNull(actualResponse);
        assertEquals(userId, actualResponse.id());
        assertEquals("bob", actualResponse.userName());
        assertEquals("Team A", actualResponse.teamName());
        assertEquals(42L, actualResponse.numberOfSolveProblems());
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toTraineResponse(user);
    }

    @Test
    void testGetCurrentUser_Success() {
        Long userId = 42L;
        User user = createTestUser(userId, "charlie", "charlie@example.com");
        TraineResponse expectedResponse = new TraineResponse(
                userId, "charlie", "Trainee", "charlie@example.com", "Team B", 100L
        );

        try (MockedStatic<SecuiryUserUtil> securityUtilMockedStatic = mockStatic(SecuiryUserUtil.class)) {
            securityUtilMockedStatic.when(SecuiryUserUtil::getCurrntUserId).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userMapper.toTraineResponse(user)).thenReturn(expectedResponse);

            TraineResponse actualResponse = userService.getCurrentUser();

            assertNotNull(actualResponse);
            assertEquals(userId, actualResponse.id());
            assertEquals("charlie", actualResponse.userName());
            assertEquals("Team B", actualResponse.teamName());
            assertEquals(100L, actualResponse.numberOfSolveProblems());

            securityUtilMockedStatic.verify(SecuiryUserUtil::getCurrntUserId, times(1));
            verify(userRepository, times(1)).findById(userId);
            verify(userMapper, times(1)).toTraineResponse(user);
        }
    }

    @Test
    void testGetAllUsers_Success() {
        User user1 = createTestUser(1L, "user1", "user1@example.com");
        User user2 = createTestUser(2L, "user2", "user2@example.com");
        TraineResponse resp1 = new TraineResponse(1L, "user1", "Trainee", "user1@example.com", "Team A", 10L);
        TraineResponse resp2 = new TraineResponse(2L, "user2", "Trainee", "user2@example.com", "Team B", 20L);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toTraineResponse(user1)).thenReturn(resp1);
        when(userMapper.toTraineResponse(user2)).thenReturn(resp2);

        List<TraineResponse> results = userService.getAllUsers();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(resp1, results.get(0));
        assertEquals(resp2, results.get(1));
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toTraineResponse(user1);
        verify(userMapper, times(1)).toTraineResponse(user2);
    }

    @Test
    void testGetAllUserByTeamId_Success() {
        Long teamId = 100L;
        User trainee1 = createTestUser(1L, "trainee1", "t1@example.com");
        User trainee2 = createTestUser(2L, "trainee2", "t2@example.com");
        Team team = Team.builder()
                .id(teamId)
                .teamName("Alpha Team")
                .trainees(List.of(trainee1, trainee2))
                .build();

        TraineResponse resp1 = new TraineResponse(1L, "trainee1", "Trainee", "t1@example.com", "Alpha Team", 5L);
        TraineResponse resp2 = new TraineResponse(2L, "trainee2", "Trainee", "t2@example.com", "Alpha Team", 15L);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(userMapper.toTraineResponse(trainee1)).thenReturn(resp1);
        when(userMapper.toTraineResponse(trainee2)).thenReturn(resp2);

        List<TraineResponse> results = userService.getAllUserByTeamId(teamId);

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(resp1, results.get(0));
        assertEquals(resp2, results.get(1));
        verify(teamRepository, times(1)).findById(teamId);
        verify(userMapper, times(1)).toTraineResponse(trainee1);
        verify(userMapper, times(1)).toTraineResponse(trainee2);
    }

    @Test
    void testGetAllUserByTeamId_TeamNotFound() {
        Long teamId = 999L;
        when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getAllUserByTeamId(teamId));
        assertEquals("Team not found with id: " + teamId, exception.getMessage());
        verify(teamRepository, times(1)).findById(teamId);
        verifyNoInteractions(userMapper);
    }

    @Test
    void testGetAllUserEntites_Success() {
        User user1 = createTestUser(1L, "user1", "user1@example.com");
        User user2 = createTestUser(2L, "user2", "user2@example.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> results = userService.getAllUserEntites();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals(user1, results.get(0));
        assertEquals(user2, results.get(1));
        verify(userRepository, times(1)).findAll();
    }
}
