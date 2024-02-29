package com.gl.bci.exercise.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.gl.bci.exercise.dao.PhoneRepository;
import com.gl.bci.exercise.dao.UserRepository;
import com.gl.bci.exercise.dao.entities.PhoneEntity;
import com.gl.bci.exercise.dao.entities.UserEntity;
import com.gl.bci.exercise.dtos.UserDto;
import com.gl.bci.exercise.dtos.UserPhoneDto;
import com.gl.bci.exercise.dtos.UserResponseDto;
import com.gl.bci.exercise.dtos.UserSuccessResponseDto;
import com.gl.bci.exercise.security.jwt.JwtService;

public class UserServiceImplTest {

  UserRepository userRepository = null;
  PhoneRepository phoneRepository = null;
  JwtService jwtService = null;
  UserServiceImpl service = null;
  PasswordEncoder passwordEncoder = null;

  @BeforeEach
  private void init() {
    userRepository = Mockito.mock(UserRepository.class);
    phoneRepository = Mockito.mock(PhoneRepository.class);
    jwtService = Mockito.mock(JwtService.class);
    passwordEncoder = Mockito.mock(PasswordEncoder.class);

    service = new UserServiceImpl(userRepository, phoneRepository, jwtService, passwordEncoder);
  }

  @Test
  @DisplayName("createNewUser should return a null object, and call zero times the repositories, when receiving a null input")
  void givenNullInput_whenCreateNewUser_thenReturnNull() {
    UserSuccessResponseDto response = service.createNewUser(null);
    assertNull(response);
    Mockito.verify(userRepository, Mockito.never()).save(ArgumentMatchers.any(UserEntity.class));
    Mockito.verify(phoneRepository, Mockito.never()).save(ArgumentMatchers.any(PhoneEntity.class));
  }

  @Test
  @DisplayName("createNewUser should return a null object, and call zero times the repositories, when receiving a null input")
  void givenFullInput_whenCreateNewUser_thenReturnSuccessResponse() {
    String mockToken = "mockToken";
    String mockHash = "m0ckH4$h";
    UserDto dto = UserDto.of("Mock Name", "mock@email.com", "mockP4$w0rd",
        List.of(new UserPhoneDto("123", "456", "789"), new UserPhoneDto("456", "789", "123"),
            new UserPhoneDto("789", "123", "456")));

    Mockito.when(jwtService.generateToken(ArgumentMatchers.any(UserDetails.class)))
        .thenReturn(mockToken);
    Mockito.when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn(mockHash);
    UserSuccessResponseDto response = service.createNewUser(dto);

    Mockito.verify(userRepository, Mockito.times(1)).save(ArgumentMatchers.any(UserEntity.class));
    Mockito.verify(phoneRepository, Mockito.times(3)).save(ArgumentMatchers.any(PhoneEntity.class));

    assertNotNull(response);
    assertNotNull(response.getCreated());
    assertNotNull(response.getModified());
    assertNotNull(response.getLastLogin());
    assertNotNull(response.getToken());

    assertNull(response.getPassword());

    assertEquals("User created", response.getMessage());
    assertEquals(mockToken, response.getToken());
    assertEquals(dto.name(), response.getName());
    assertEquals(dto.email(), response.getEmail());
    assertEquals(dto.phones().size(), response.getPhones().size());
    assertTrue(dto.phones().containsAll(response.getPhones()));
    assertTrue(response.getPhones().containsAll(dto.phones()));
  }

  @Test
  @DisplayName("validateUniqueUserEmail should return an error when the input email exists in the persistence unit")
  void givenAnExistingEmail_whenValidateUniqueUserEmail_thenReturnAnErrorResponse() {

    Mockito.when(userRepository.findByEmail(ArgumentMatchers.anyString()))
        .thenReturn(Optional.of(new UserEntity()));
    UserResponseDto response = service.validateUniqueUserEmail("mock@email.com");

    assertNotNull(response);
    assertEquals("The email {mock@email.com} already exists in the system", response.getMessage());
  }

  @Test
  @DisplayName("validateUniqueUserEmail should return null when the input email does not exists in the persistence unit")
  void givenANewEmail_whenValidateUniqueUserEmail_thenReturnNull() {
    Mockito.when(userRepository.findByEmail(ArgumentMatchers.anyString()))
        .thenReturn(Optional.empty());
    UserResponseDto response = service.validateUniqueUserEmail("mock@email.com");

    assertNull(response);
  }

  @Test
  @DisplayName("phoneDtosToEntities should return null on a null input")
  void givenNullList_whenPhoneDtosToEntities_thenReturnNull() {
    List<PhoneEntity> response = service.phoneDtosToEntities(null);
    assertNull(response);
  }

  @Test
  @DisplayName("phoneDtosToEntities should return null on a null input")
  void givenEmptyList_whenPhoneDtosToEntities_thenReturnEmptyList() {
    List<PhoneEntity> response = service.phoneDtosToEntities(List.of());
    assertTrue(response.isEmpty());
  }

  @Test
  @DisplayName("phoneDtosToEntities should return a list of entities on a proper input")
  void givenFullList_whenPhoneDtosToEntities_thendReturnFullEntityList() {

    List<PhoneEntity> expected = List.of(phoneEntityOf((long) 123, (short) 456, (short) 789),
        phoneEntityOf((long) 456, (short) 789, (short) 123),
        phoneEntityOf((long) 789, (short) 123, (short) 456));
    List<UserPhoneDto> dtos = List.of(new UserPhoneDto("123", "456", "789"),
        new UserPhoneDto("456", "789", "123"), new UserPhoneDto("789", "123", "456"));
    List<PhoneEntity> response = service.phoneDtosToEntities(dtos);

    assertEquals(expected.size(), response.size());
    assertTrue(expected.containsAll(response));
    assertTrue(response.containsAll(expected));
  }

  @Test
  @DisplayName("phoneEntitiesToDtos should return null on a null input")
  void givenNullList_whenPhoneEntitiesToDtos_thenReturnNull() {
    List<UserPhoneDto> response = service.phoneEntitiesToDtos(null);
    assertNull(response);
  }

  @Test
  @DisplayName("phoneEntitiesToDtos should return null on a null input")
  void givenEmptyList_whenPhoneEntitiesToDtos_thenReturnEmptyList() {
    List<UserPhoneDto> response = service.phoneEntitiesToDtos(List.of());
    assertTrue(response.isEmpty());
  }

  @Test
  @DisplayName("phoneDtosToEntities should return a list of entities on a proper input")
  void givenFullList_whenPhoneEntitiesToDtos_thenReturnFullDtoList() {

    List<UserPhoneDto> expected = List.of(new UserPhoneDto("123", "456", "789"),
        new UserPhoneDto("456", "789", "123"), new UserPhoneDto("789", "123", "456"));
    List<PhoneEntity> entities = List.of(phoneEntityOf((long) 123, (short) 456, (short) 789),
        phoneEntityOf((long) 456, (short) 789, (short) 123),
        phoneEntityOf((long) 789, (short) 123, (short) 456));
    List<UserPhoneDto> response = service.phoneEntitiesToDtos(entities);

    assertEquals(expected.size(), response.size());
    assertTrue(expected.containsAll(response));
    assertTrue(response.containsAll(expected));
  }

  private PhoneEntity phoneEntityOf(Long number, Short cityCode, Short countryCode) {
    PhoneEntity entity = new PhoneEntity();

    entity.setPhoneNumber(number);
    entity.setCityCode(cityCode);
    entity.setCountryCode(countryCode);

    return entity;
  }

  @Test
  void givenANullEntity_whenUserEntityToDto_thenReturnNull() {

    UserSuccessResponseDto response = service.userEntityToDto(null);
    assertNull(response);

  }

  @Test
  void givenAnEntity_whenUserEntityToDto_thenReturnDto() {

    UUID uuid = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    UserEntity entity = new UserEntity();
    entity.setName("mock name");
    entity.setEmail("mock@email.com");
    entity.setPassword("mockP4$w0rd");
    entity.setPhones(List.of(phoneEntityOf((long) 123, (short) 456, (short) 789),
        phoneEntityOf((long) 456, (short) 789, (short) 123),
        phoneEntityOf((long) 789, (short) 123, (short) 456)));
    entity.setId(uuid);
    entity.setCreated(now);
    entity.setLastLogin(now);
    entity.setModified(now);
    entity.setToken("mockToken");

    UserSuccessResponseDto expected = UserSuccessResponseDto.of(
        "mock name", "mock@email.com", null, List.of(new UserPhoneDto("123", "456", "789"),
            new UserPhoneDto("456", "789", "123"), new UserPhoneDto("789", "123", "456")),
        uuid, now, now, now, "mockToken");
    UserSuccessResponseDto response = service.userEntityToDto(entity);
    assertEquals(expected, response);

  }
}
