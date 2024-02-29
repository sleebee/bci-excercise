package com.gl.bci.exercise.controllers;

import java.util.List;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gl.bci.exercise.controllers.api.UserApi;
import com.gl.bci.exercise.dtos.UserDto;
import com.gl.bci.exercise.dtos.UserResponseDto;
import com.gl.bci.exercise.dtos.UserSuccessResponseDto;
import com.gl.bci.exercise.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class UserController implements UserApi {

  @Value("${bci.user.password.pattern}")
  private String passwordPattern;

  @Value("${bci.user.password.comment}")
  private String passwordComment;

  private final UserService service;

  @Override
  @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
  public ResponseEntity<? extends UserResponseDto> createUser(@RequestBody UserDto userToCreate) {
    log.info("Validating received user [{}]", userToCreate);
    UserResponseDto invalidUser = validateUser(userToCreate);
    if (invalidUser != null) {
      log.error("The user format was not valid. [{}]", invalidUser.getMessage());
      return ResponseEntity.badRequest().body(invalidUser);
    }

    log.info("Validating received user email uniqueness [{}]", userToCreate.email());
    UserResponseDto emailCheck = service.validateUniqueUserEmail(userToCreate.email());
    if (emailCheck != null) {
      log.error("The email [{}] is already present in the database", userToCreate.email());
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(emailCheck);
    }

    UserResponseDto successResponse = service.createNewUser(userToCreate);

    if (successResponse == null) {
      log.error("An error occurred when creating the user [{}]", userToCreate);
      return ResponseEntity.internalServerError().body(new UserResponseDto(
          String.format("An error occurred when persisting the user %s", userToCreate.toString())));
    }

    log.info("User created correctly");
    return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
  }

  /**
   * Validates email and password format
   * 
   * @param userToCreate
   * @return a validation error, or null if all checks passed
   */
  private UserResponseDto validateUser(UserDto userToCreate) {
    if (userToCreate == null)
      return new UserResponseDto("The user object cannot be null");

    final String EMAIL_REGEX =
        "^(?=.{1,64}@)[A-Za-z0-9_-]+(.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(.[A-Za-z0-9-]+)*(.[A-Za-z]{2,})$";

    log.debug("Validating email [{}]", userToCreate.email());
    if (!Pattern.compile(EMAIL_REGEX).matcher(userToCreate.email()).matches())
      return new UserResponseDto(
          String.format("The email {%s} does not have a valid email format", userToCreate.email()));

    log.debug("Validating password");
    if (!Pattern.compile(passwordPattern).matcher(userToCreate.password()).matches())
      return new UserResponseDto(String.format("Incorrect password format: %s", passwordComment));

    return null;
  }

  @Override
  @GetMapping(value = "/me", produces = "application/json")
  public ResponseEntity<UserSuccessResponseDto> authenticatedUser() {
    UserSuccessResponseDto currentUser = service.getCurrentUser();
    return ResponseEntity.ok(currentUser);
  }

  @Override
  @GetMapping(produces = "application/json")
  public ResponseEntity<List<UserSuccessResponseDto>> allUsers() {

    return ResponseEntity.ok(service.listAllUsers());
  }

}
