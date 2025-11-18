package org.bangbang.userservice.application.service;

import org.bangbang.userservice.application.dto.TokenInfo;

public interface TokenGenerateService {
    TokenInfo generate(String username, String password);
}
