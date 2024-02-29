package com.gl.bci.exercise.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.gl.bci.exercise.dao.PhoneRepository;
import com.gl.bci.exercise.dao.UserRepository;
import com.gl.bci.exercise.dao.entities.PhoneEntity;
import com.gl.bci.exercise.dao.entities.UserEntity;
import com.gl.bci.exercise.dtos.UserDto;
import com.gl.bci.exercise.dtos.UserPhoneDto;
import com.gl.bci.exercise.dtos.UserResponseDto;
import com.gl.bci.exercise.dtos.UserSuccessResponseDto;
import com.gl.bci.exercise.security.jwt.JwtService;
import com.gl.bci.exercise.services.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PhoneRepository phoneRepository;

  private final JwtService jwtService;

  private final PasswordEncoder passwordEncoder;

  @Override
  public UserSuccessResponseDto createNewUser(UserDto userToCreate) {
    UserEntity entity = new UserEntity();
    entity.setName(userToCreate.name());
    entity.setPassword(passwordEncoder.encode(userToCreate.password()));
    entity.setEmail(userToCreate.email());
    entity.setPhones(phoneDtosToEntities(userToCreate.phones()));
    LocalDateTime now = LocalDateTime.now();
    entity.setCreated(now);
    entity.setModified(now);
    entity.setLastLogin(now);
    entity.setToken(jwtService.generateToken(entity));

    entity.getPhones().forEach(phoneRepository::save);
    userRepository.save(entity);

    return userEntityToDto(entity);
  }

  @Override
  public UserResponseDto validateUniqueUserEmail(String email) {
    Optional<UserEntity> existingEmail = userRepository.findByEmail(email);
    if (existingEmail.isPresent())
      return new UserResponseDto(
          String.format("The email {%s} already exists in the system", email));

    return null;
  }

  @Override
  public UserSuccessResponseDto getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserEntity currentUser = (UserEntity) authentication.getPrincipal();

    return userEntityToDto(currentUser);
  }

  @Override
  public List<UserSuccessResponseDto> listAllUsers() {
    final List<UserSuccessResponseDto> allUsers = new ArrayList<>();
    userRepository.findAll().forEach(userEntity -> {
      allUsers.add(userEntityToDto(userEntity));
    });
    return allUsers;
  }

  private UserSuccessResponseDto userEntityToDto(UserEntity entity) {
    return UserSuccessResponseDto.of(entity.getName(), entity.getEmail(), null,
        phoneEntitiesToDtos(entity.getPhones()), entity.getId(), entity.getCreated(),
        entity.getModified(), entity.getLastLogin(), entity.getToken());
  }

  private List<PhoneEntity> phoneDtosToEntities(List<UserPhoneDto> phones) {
    if (phones == null)
      return null;

    List<PhoneEntity> entities = new ArrayList<>();
    phones.forEach(phone -> {
      PhoneEntity entity = new PhoneEntity();
      entity
          .setCountryCode(phone.countryCode() == null ? null : Byte.parseByte(phone.countryCode()));
      entity.setCityCode(Byte.parseByte(phone.cityCode()));
      entity.setPhoneNumber(Long.parseLong(phone.number()));

      entities.add(entity);
    });

    return entities;
  }

  private List<UserPhoneDto> phoneEntitiesToDtos(List<PhoneEntity> phones) {
    if (phones == null)
      return null;

    List<UserPhoneDto> dtos = new ArrayList<>();
    phones.forEach(phone -> {
      UserPhoneDto dto = new UserPhoneDto(
          phone.getPhoneNumber() != null ? phone.getPhoneNumber().toString() : null,
          phone.getCityCode() != null ? phone.getCityCode().toString() : null,
          phone.getCountryCode() != null ? phone.getCountryCode().toString() : null);

      dtos.add(dto);
    });

    return dtos;
  }
}
