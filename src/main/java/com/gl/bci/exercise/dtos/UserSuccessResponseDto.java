package com.gl.bci.exercise.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Getter
public class UserSuccessResponseDto extends UserResponseDto {

  private final String name;
  private final String email;
  private final String password;
  private final List<UserPhoneDto> phones;
  @JsonProperty("id")
  private final UUID userUuid;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private final LocalDateTime created;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private final LocalDateTime modified;
  @JsonProperty("last_login")
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private final LocalDateTime lastLogin;
  private final String token;

  private UserSuccessResponseDto(String error, String name, String email, String password,
      List<UserPhoneDto> phones, UUID userUuid, LocalDateTime created, LocalDateTime modified,
      LocalDateTime lastLogin, String token) {
    super(error);
    this.name = name;
    this.email = email;
    this.password = password;
    this.phones = phones;
    this.userUuid = userUuid;
    this.created = created;
    this.modified = modified;
    this.lastLogin = lastLogin;
    this.token = token;
  }

  public static UserSuccessResponseDto of(String name, String email, String password,
      List<UserPhoneDto> phones, UUID userUuid, LocalDateTime created, LocalDateTime modified,
      LocalDateTime lastLogin, String token) {
    return new UserSuccessResponseDto("User created", name, email, password, phones, userUuid,
        created, modified, lastLogin, token);
  }

}
