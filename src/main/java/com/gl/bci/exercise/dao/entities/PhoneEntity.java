package com.gl.bci.exercise.dao.entities;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "phones")
@Getter
@Setter
@EqualsAndHashCode(of = {"countryCode", "cityCode", "phoneNumber"})
public class PhoneEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "id")
  private UUID id;

  @Column(name = "country_code")
  private Short countryCode;

  @Column(name = "city_code")
  private Short cityCode;

  @Column(name = "phone_number")
  private Long phoneNumber;
}
