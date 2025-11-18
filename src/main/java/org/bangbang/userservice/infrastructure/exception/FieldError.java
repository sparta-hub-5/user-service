package org.bangbang.userservice.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FieldError {

    private String field;
    private Object value;
    private String reason;
}
