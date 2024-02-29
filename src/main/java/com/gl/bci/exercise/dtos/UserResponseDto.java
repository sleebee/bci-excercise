package com.gl.bci.exercise.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserResponseDto {
  @JsonProperty("mensaje")
  private final String message;
}
