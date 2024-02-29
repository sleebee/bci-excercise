package com.gl.bci.exercise.dao;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import com.gl.bci.exercise.dao.entities.PhoneEntity;

public interface PhoneRepository extends CrudRepository<PhoneEntity, UUID> {

}
