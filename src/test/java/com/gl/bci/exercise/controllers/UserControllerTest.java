package com.gl.bci.exercise.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.gl.bci.exercise.dtos.UserDto;
import com.gl.bci.exercise.dtos.UserResponseDto;
import com.gl.bci.exercise.dtos.UserSuccessResponseDto;
import com.gl.bci.exercise.services.UserService;

public class UserControllerTest {

  private UserService service = null;
  private UserController controller = null;

  @BeforeEach
  private void init() {
    service = Mockito.mock(UserService.class);
    controller = new UserController(service);
  }

  @Test
  @DisplayName("validateUser should return an error on a null input")
  void givenNullInput_whenValidateUser_thenReturnError() {
    UserResponseDto response = controller.validateUser(null);
    assertNotNull(response);
    assertEquals("The user object cannot be null", response.getMessage());
  }

  @Test
  @DisplayName("validateUser should return an error on a malformed email")
  void givenInputWithMalformedEmail_whenValidateUser_thenReturnError() {
    UserDto input = new UserDto("mock name", "some.wrong.@email.com.", "m0ckP4$w0rd", null);
    UserResponseDto response = controller.validateUser(input);
    assertNotNull(response);
    assertEquals("The email {some.wrong.@email.com.} does not have a valid email format",
        response.getMessage());
  }

  @Test
  @DisplayName("validateUser should return an error on a malformed password")
  void givenInputWithMalformedPassword_whenValidateUser_thenReturnError() {
    controller.passwordPattern =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$";
    controller.passwordComment =
        "Password must be at least 8 chars long; contain at least one digit; contain at least one lowercase and one uppercase char; contain at least one special character (!@#%$^); does not contain spaces, tabs, etc.";

    UserDto input = new UserDto("mock name", "some@email.com", "pwd", null);
    UserResponseDto response = controller.validateUser(input);
    assertNotNull(response);
    assertEquals(String.format("Incorrect password format: %s", controller.passwordComment),
        response.getMessage());
  }

  @Test
  @DisplayName("validateUser should return null on a correct user")
  void givenCorrectInput_whenValidateUser_thenReturnNull() {
    controller.passwordPattern =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$";
    UserDto input = new UserDto("mock name", "some@email.com", "m0ckP4$sw0rd", null);
    UserResponseDto response = controller.validateUser(input);
    assertNull(response);
  }

  @Test
  @DisplayName("createUser should return an error response on a null user payload")
  void givenANullUser_whenCreateUser_thenReturnABadRequestError() {
    ResponseEntity<? extends UserResponseDto> response = controller.createUser(null);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(new UserResponseDto("The user object cannot be null"), response.getBody());
  }

  @Test
  @DisplayName("createUser should return an error response on an invalid user payload")
  void givenAnInvalidUser_whenCreateUser_thenReturnABadRequestError() {
    UserDto input = new UserDto("mock name", "some.wrong.@email.com.", "pwd", null);
    ResponseEntity<? extends UserResponseDto> response = controller.createUser(input);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(
        new UserResponseDto(
            "The email {some.wrong.@email.com.} does not have a valid email format"),
        response.getBody());
  }

  @Test
  @DisplayName("createUser should return an error response on an existing email")
  void givenAnExistingEmail_whenCreateUser_thenReturnAForbiddenError() {
    String email = "some@email.com";
    UserDto input = new UserDto("mock name", email, "m0ckP4$sw0rd", null);

    controller.passwordPattern =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$";
    controller.passwordComment =
        "Password must be at least 8 chars long; contain at least one digit; contain at least one lowercase and one uppercase char; contain at least one special character (!@#%$^); does not contain spaces, tabs, etc.";

    Mockito.when(service.validateUniqueUserEmail(email)).thenReturn(
        new UserResponseDto(String.format("The email {%s} already exists in the system", email)));

    ResponseEntity<? extends UserResponseDto> response = controller.createUser(input);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertEquals(
        new UserResponseDto(String.format("The email {%s} already exists in the system", email)),
        response.getBody());
  }

  @Test
  @DisplayName("createUser should return an error response on a valid input due to some internal error")
  void givenAnInput_whenCreateUser_thenReturnAInternalServerError() {
    String email = "some@email.com";
    UserDto input = new UserDto("mock name", email, "m0ckP4$sw0rd", null);

    controller.passwordPattern =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$";
    controller.passwordComment =
        "Password must be at least 8 chars long; contain at least one digit; contain at least one lowercase and one uppercase char; contain at least one special character (!@#%$^); does not contain spaces, tabs, etc.";

    Mockito.when(service.validateUniqueUserEmail(email)).thenReturn(null);
    Mockito.when(service.createNewUser(input)).thenReturn(null);

    ResponseEntity<? extends UserResponseDto> response = controller.createUser(input);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(
        new UserResponseDto(
            String.format("An error occurred when persisting the user %s", input.toString())),
        response.getBody());
  }

  @Test
  @DisplayName("createUser should return a success response on a valid input")
  void givenAnInput_whenCreateUser_thenReturnASuccessResponse() {
    String name = "mock name";
    String email = "some@email.com";
    String password = "m0ckP4$sw0rd";
    LocalDateTime now = LocalDateTime.now();
    UUID uuid = UUID.randomUUID();

    UserDto input = new UserDto(name, email, password, null);

    controller.passwordPattern =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$";
    controller.passwordComment =
        "Password must be at least 8 chars long; contain at least one digit; contain at least one lowercase and one uppercase char; contain at least one special character (!@#%$^); does not contain spaces, tabs, etc.";

    UserSuccessResponseDto expected =
        UserSuccessResponseDto.of(name, email, password, null, uuid, now, now, now, "mockToken");
    Mockito.when(service.validateUniqueUserEmail(email)).thenReturn(null);
    Mockito.when(service.createNewUser(input)).thenReturn(expected);

    ResponseEntity<? extends UserResponseDto> response = controller.createUser(input);
    assertNotNull(response);
    assertNotNull(response.getBody());
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(expected, response.getBody());
  }
}
