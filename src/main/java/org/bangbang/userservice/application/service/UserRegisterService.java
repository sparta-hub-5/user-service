package org.bangbang.userservice.application.service;

import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bangbang.userservice.application.dto.UserRegister;
import org.bangbang.userservice.infrastructure.keycloak.config.KeycloakProperties;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakProperties.class)
public class UserRegisterService {
    private final KeycloakProperties properties;
    private final Keycloak keycloak;

    public void register(UserRegister dto) {
        // Keycloak에 사용자 생성
        UsersResource usersResource = keycloak.realm(properties.getRealm()).users();
        System.out.println("dto:" + dto);
        System.out.println("properties:" + properties);

        // 사용자 표현 객체 생성
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());

        // 사용자 속성 설정
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("mobile", List.of(dto.mobile()));
        user.setAttributes(attributes);

        Response response = usersResource.create(user);

        if (response.getStatus() != 201) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, response.getStatusInfo().getReasonPhrase());
        }

        // 사용자 생성 성공, 비밀번호 설정
        String userId = CreatedResponseUtil.getCreatedId(response);

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(dto.password());

        usersResource.get(userId).resetPassword(passwordCred);

        // 기본 Role 부여
        RoleRepresentation userRole = keycloak.realm(properties.getRealm()).roles().get("ROLE_USER").toRepresentation();

        usersResource.get(userId).roles().realmLevel().add(List.of(userRole));
    }
}
