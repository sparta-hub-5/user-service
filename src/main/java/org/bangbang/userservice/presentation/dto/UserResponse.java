package org.bangbang.userservice.presentation.dto;

import java.util.UUID;

public record UserResponse(
    UUID userId,
    String username,
    String email,
    String name,
    String mobile
) {}
