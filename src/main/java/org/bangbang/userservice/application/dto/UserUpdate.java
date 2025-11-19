package org.bangbang.userservice.application.dto;

import lombok.Builder;

@Builder
public record UserUpdate(
    String firstName,
    String lastName,
    String email,
    String mobile
) {}
