package com.gl.bci.exercise.services;

import java.util.List;
import com.gl.bci.exercise.dtos.UserDto;
import com.gl.bci.exercise.dtos.UserResponseDto;
import com.gl.bci.exercise.dtos.UserSuccessResponseDto;

public interface UserService {

  /**
   * Creates a new user in the persistence unit (DB)
   * 
   * @param userToCreate
   * @return A user response with all requested parameters; null if there was an error
   */
  UserSuccessResponseDto createNewUser(UserDto userToCreate);

  /**
   * Validates email existence in the persistence unit (DB)
   * 
   * @param email
   * @return An error message if the email already exists; null if it does not
   */
  UserResponseDto validateUniqueUserEmail(String email);

  /**
   * Returns the information of the user based on the JWT
   * 
   * @return user information
   */
  UserSuccessResponseDto getCurrentUser();

  /**
   * Lists all registered users
   * 
   * @return list of all registered users
   */
  List<UserSuccessResponseDto> listAllUsers();
}
