package com.TrainingTracker.TraingingTracker.unit.controller;


import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.UserService;
import com.TrainingTracker.TraingingTracker.Controllers.UserController;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.TraineResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UpdatePasswordDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UpdateProfileDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.User.UserIdRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {


    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;


    private

   TraineResponse getTraineResponse(){
      TraineResponse dto = new TraineResponse(
              1L,
              "John",
              "Doe",
              "john.doe@example.com",
              null,
              null,
              20L,
              120L,
12L,
              30L,
              300L,
              "Sasky",
              1230L,
              1400L,
              "pupil",
              "specialist");
      return dto;
  }
  TraineResponse getAnotherTraineResponse(){
      TraineResponse dto = new TraineResponse(
              2L,
              "Jane",
              "Smith",
              "jane.smith@example.com",
              null,
              null,
              20L,
              120L,
              12L,
              30L,
              300L,
              "Sasky",
              1230L,
              1400L,
              "pupil",
              "specialist");
      return dto;
  }


    @Test
    public void testGetCurrentUser(){
       TraineResponse dto=getTraineResponse();
       when(userService.getCurrentUser()).thenReturn(dto);
       ResponseEntity<TraineResponse> response = userController.getCurrentUser();
       assertEquals(HttpStatus.OK, response.getStatusCode());
       assertEquals(dto, response.getBody());
       verify(userService).getCurrentUser();
    }
    @Test
    public void testGetAllUsers(){
        List<TraineResponse> dtoList = Arrays.asList(getTraineResponse(), getAnotherTraineResponse());
        when(userService.getAllUsers()).thenReturn(dtoList);
        ResponseEntity<List<TraineResponse>> response = userController.getAllUsers();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(userService).getAllUsers();
    }

    @Test
    public void testGetAllUsersByTeamId() {
        Long teamId = 10L;
        List<TraineResponse> dtoList = Arrays.asList(getTraineResponse(), getAnotherTraineResponse());
        when(userService.getAllUserByTeamId(teamId)).thenReturn(dtoList);

        ResponseEntity<List<TraineResponse>> response = userController.getAllUsersByTeamId(teamId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtoList, response.getBody());
        verify(userService).getAllUserByTeamId(teamId);
    }

    @Test
    public void testGetAllUsersByTeamIdWhenTeamHasNoTrainees() {
        Long teamId = 10L;
        when(userService.getAllUserByTeamId(teamId)).thenReturn(List.of());

        ResponseEntity<List<TraineResponse>> response = userController.getAllUsersByTeamId(teamId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(), response.getBody());
        verify(userService).getAllUserByTeamId(teamId);
    }

    @Test
    public void testGetUserById() {
        TraineResponse dto = getTraineResponse();
        UserIdRequestDto request = new UserIdRequestDto(dto.id());
        when(userService.getUserResponseById(dto.id())).thenReturn(dto);

        ResponseEntity<TraineResponse> response = userController.getUserById(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(userService).getUserResponseById(dto.id());
    }

    @Test
    public void testGetUserByIdNotFound() {
        Long userId = 99L;
        UserIdRequestDto request = new UserIdRequestDto(userId);
        String message = "User not found with id: " + userId;
        when(userService.getUserResponseById(userId)).thenThrow(new RuntimeException(message));

        RuntimeException error = assertThrows(RuntimeException.class, () -> userController.getUserById(request));

        assertEquals(message, error.getMessage());
        verify(userService).getUserResponseById(userId);
    }

    @Test
    public void testUpdateProfile() {
        TraineResponse dto = getTraineResponse();
        UpdateProfileDto request = new UpdateProfileDto(dto.userName());
        when(userService.updateProfile(request)).thenReturn(dto);

        ResponseEntity<TraineResponse> response = userController.updateProfile(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(userService).updateProfile(request);
    }

    @Test
    public void testUpdatePassword() {
        UpdatePasswordDto request = new UpdatePasswordDto("oldPassword", "newPassword");

        ResponseEntity<Void> response = userController.updatePassword(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).updatePassword(request);
    }

    @Test
    public void testUpdatePasswordWithIncorrectOldPasswordShouldFail() {
        UpdatePasswordDto request = new UpdatePasswordDto("wrongPassword", "newPassword");
        String message = "Incorrect old password";
        doThrow(new IllegalArgumentException(message)).when(userService).updatePassword(request);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, () -> userController.updatePassword(request)
        );

        assertEquals(message, error.getMessage());
        verify(userService).updatePassword(request);
    }
}
