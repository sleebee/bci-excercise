package com.gl.bci.exercise.controllers.api;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import com.gl.bci.exercise.dtos.UserDto;
import com.gl.bci.exercise.dtos.UserResponseDto;
import com.gl.bci.exercise.dtos.UserSuccessResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "BCI User API", description = "BCI user registration and API")
public interface UserApi {

  @Operation(summary = "Registers a user",
      description = "Validates the user information and creates a user in the system")
  @ApiResponses(value = {@ApiResponse(responseCode = "200",
      content = @Content(schema = @Schema(implementation = UserSuccessResponseDto.class),
          mediaType = "application/json"),
      description = "Successful operation returns the created user with additional information (such as the JWT)"),
      @ApiResponse(responseCode = "400", description = "Some information provided is malformed"),
      @ApiResponse(responseCode = "403",
          description = "The email provided is already present in the system"),
      @ApiResponse(responseCode = "500",
          description = "An unknown error occurred while creating the user")})
  ResponseEntity<? extends UserResponseDto> createUser(@RequestBody UserDto userToCreate);

  @Operation(summary = "Retrieve the current user information",
      description = "Retrieves the information of the user recorded in the JWT")
  @SecurityRequirement(name = "Bearer Authentication")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          content = @Content(schema = @Schema(implementation = UserSuccessResponseDto.class),
              mediaType = "application/json")),
      @ApiResponse(responseCode = "403",
          description = "User was not correctly authenticated by JWT")})
  ResponseEntity<UserSuccessResponseDto> authenticatedUser();

  @Operation(summary = "List all existing users",
      description = "Retrieves the information for all existing users")
  @SecurityRequirement(name = "Bearer Authentication")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          content = @Content(schema = @Schema(implementation = List.class),
              mediaType = "application/json")),
      @ApiResponse(responseCode = "403",
          description = "User was not correctly authenticated by JWT")})
  ResponseEntity<List<UserSuccessResponseDto>> allUsers();
}
