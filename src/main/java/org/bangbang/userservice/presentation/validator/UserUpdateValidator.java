package org.bangbang.userservice.presentation.validator;

import org.bangbang.infrastructure.exception.ApplicationException;
import org.bangbang.infrastructure.exception.ErrorCode;
import org.bangbang.userservice.presentation.dto.PasswordChangeRequest;
import org.bangbang.userservice.presentation.dto.UserUpdateRequest;
import org.springframework.util.StringUtils;

public class UserUpdateValidator implements PasswordValidator, MobileValidator {

    // 회원정보 수정 추가 검증
    public void validateUpdateProfile(UserUpdateRequest req) {
        String mobile = req.mobile();
        if (StringUtils.hasText(mobile) && checkMobile(mobile)) {
            throw new ApplicationException(ErrorCode.REQUEST_VALIDATION_ERROR, "휴대전화번호 형식이 아닙니다.");
        }
    }

    // 비밀번호 변경 추가 검증
    public void validateChangePassword(PasswordChangeRequest req) {
        String password = req.password();
        String confirmPassword = req.confirmPassword();

        // 비밀번호 복잡성 체크
        if (!checkAlpha(password, false) || !checkNumber(password) || !checkSpecialChars(password)) {
            throw new ApplicationException(ErrorCode.REQUEST_VALIDATION_ERROR, "비밀번호는 알파벳 대소문자, 숫자, 특수 문자 포함 8자리 이상 입력하세요. ");
        }

        // 비밀번호, 비밀번호 확인 일치 여부
        if (!password.equals(confirmPassword)) {
            throw new ApplicationException(ErrorCode.REQUEST_VALIDATION_ERROR, "비밀번호가 일치하지 않습니다.");
        }
    }
}
