package com.awsspringboot.controllers.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CognitoTokenResponseDto(String access_token) {
}
