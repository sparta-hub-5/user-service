package org.bangbang.userservice.presentation.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.bangbang.infrastructure.exception.ApplicationException;
import org.bangbang.userservice.presentation.dto.UserRegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
class UserRegisterValidatorTest {

    private final UserRegisterValidator validator = new UserRegisterValidator();

    @Test
    @DisplayName("정상적인 회원가입 요청은 검증을 통과한다")
    void validate_success() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "testuser",
            "Password123!", // 대소문자, 숫자, 특수문자 포함
            "Password123!",
            "test@example.com",
            "First",
            "Last",
            "01012345678"
        );

        // When & Then
        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    @DisplayName("비밀번호 복잡성이 부족하면 예외가 발생한다")
    void validate_fail_password_complexity() {
        // Given (특수문자 없음)
        UserRegisterRequest request = new UserRegisterRequest(
            "testuser",
            "Password123",
            "Password123",
            "test@example.com",
            "First",
            "Last",
            "01012345678"
        );

        // When & Then
        assertThatThrownBy(() -> validator.validate(request))
            .isInstanceOf(ApplicationException.class)
            .hasMessageContaining("비밀번호는 알파벳 대소문자");
    }

    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 다르면 예외가 발생한다")
    void validate_fail_password_mismatch() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "testuser",
            "Password123!",
            "Different123!",
            "test@example.com",
            "First",
            "Last",
            "01012345678"
        );

        // When & Then
        assertThatThrownBy(() -> validator.validate(request))
            .isInstanceOf(ApplicationException.class)
            .hasMessageContaining("비밀번호가 일치하지 않습니다");
    }

    @Test
    @DisplayName("잘못된 휴대전화 번호 형식이면 예외가 발생한다")
    void validate_fail_mobile_format() {
        // Given
        UserRegisterRequest request = new UserRegisterRequest(
            "testuser",
            "Password123!",
            "Password123!",
            "test@example.com",
            "First",
            "Last",
            "010-1234-5678" // 하이픈 포함 (MobileValidator 로직에 따라 다름, 현재 로직은 replaceAll로 제거하므로 사실 통과될 수도 있음. 로직 확인 필요)
        );

        // MobileValidator가 replaceAll("\\D", "")를 수행하므로,
        // 숫자가 아닌 문자가 들어가도 정규식 통과 가능성이 있음.
        // 하지만 엄격한 테스트를 위해 010이 아닌 번호로 테스트
        UserRegisterRequest badRequest = new UserRegisterRequest(
            "testuser",
            "Password123!",
            "Password123!",
            "test@example.com",
            "First",
            "Last",
            "0212345678" // 01[016] 패턴 아님
        );

        // When & Then
        assertThatThrownBy(() -> validator.validate(badRequest))
            .isInstanceOf(ApplicationException.class)
            .hasMessageContaining("휴대전화 번호 형식이 아닙니다");
    }
}