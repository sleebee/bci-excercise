package com.gl.bci.exercise.dao;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import com.gl.bci.exercise.dao.entities.UserEntity;

public interface UserRepository extends CrudRepository<UserEntity, UUID> {

  Optional<UserEntity> findByEmail(String email);
}
