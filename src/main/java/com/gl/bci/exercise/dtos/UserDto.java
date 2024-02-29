package com.gl.bci.exercise.dtos;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(String name, String email, String password, List<UserPhoneDto> phones) {

  public static UserDto of(String name, String email, String password, List<UserPhoneDto> phones) {
    return new UserDto(name, email, password, phones);
  }
}
