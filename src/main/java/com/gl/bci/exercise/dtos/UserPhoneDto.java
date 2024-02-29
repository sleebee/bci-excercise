package com.gl.bci.exercise.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserPhoneDto(String number, @JsonProperty("citycode") String cityCode,
    @JsonProperty("countrycode") String countryCode) {
}
