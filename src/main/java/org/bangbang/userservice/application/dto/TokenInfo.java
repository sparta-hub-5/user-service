package org.bangbang.userservice.application.dto;

public record TokenInfo(
    String access_token,
    int expires_in,
    int refresh_expires_in,
    String refresh_token,
    String token_type
) {}
