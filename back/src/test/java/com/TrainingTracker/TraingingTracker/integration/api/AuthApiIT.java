package com.TrainingTracker.TraingingTracker.integration.api;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.CfService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.CodeforcesUserInfo;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Types.Role;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.CfUserHandlerError;
import com.TrainingTracker.TraingingTracker.Security.RateLimting.RateLimitDecision;
import com.TrainingTracker.TraingingTracker.Security.RateLimting.RedisRateLimiter;
import com.TrainingTracker.TraingingTracker.TraingingTrackerApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TraingingTrackerApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class AuthApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private CfService cfService;

    @MockitoBean
    private RedisRateLimiter rateLimiter;

    @Test
    void registerCreatesCoachAndReturnsAccessToken() throws Exception {
        when(cfService.getUserInfo("coach_cf"))
                .thenReturn(new CodeforcesUserInfo(1200L, 1400L, "Newbie", "Pupil"));
        when(rateLimiter.checkRateLimit(anyString(), anyInt(), anyLong()))
                .thenReturn(new RateLimitDecision(true, 99, 0));

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userName": "Coach John",
                                  "email": "coach@example.test",
                                  "codeforcesHandle": "coach_cf",
                                  "password": "password123",
                                  "isCoach": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.userName").value("Coach John"))
                .andReturn();

        String cookie = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        assertNotNull(cookie);
        assertTrue(cookie.contains("refreshToken="));
        assertTrue(cookie.contains("HttpOnly"));

        User savedUser = userRepository.findByEmail("coach@example.test").orElseThrow();
        assertEquals(Role.Coach, savedUser.getRole());
        assertEquals(1200L, savedUser.getRate());
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
        verify(cfService).getUserInfo("coach_cf");
    }

    void registerUserToTheSystem() {
        User user = new User();
        user.setUsername("Coach John");
        user.setEmail("coach@example.test");
        user.setCodeforcesHandle("coach_cf");
        user.setRole(Role.Coach);
        user.setRate(1200L);
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.saveAndFlush(user);
    }


    @Test
    void registeredUserShouldNotBeAbleToRegisterAgain() throws Exception {
        registerUserToTheSystem();

        when(rateLimiter.checkRateLimit(anyString(), anyInt(), anyLong()))
                .thenReturn(new RateLimitDecision(true, 99, 0));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userName": "Coach John",
                                  "email": "coach@example.test",
                                  "codeforcesHandle": "coach_cf",
                                  "password": "password123",
                                  "isCoach": true
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Email is already registered"));
    }

    @Test
    void registerUserWithInvalidCodeforcesHandleShouldFail() throws Exception {
        when(cfService.getUserInfo("invalid_handle"))
                .thenThrow(new CfUserHandlerError("User not found on Codeforces"));
        when(rateLimiter.checkRateLimit(anyString(), anyInt(), anyLong()))
                .thenReturn(new RateLimitDecision(true, 99, 0));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userName": "Coach John",
                                  "email": "coach@example.test",
                                  "codeforcesHandle": "invalid_handle",
                                  "password": "password123",
                                  "isCoach": true
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Codeforces account does not exist"));
    }
}
