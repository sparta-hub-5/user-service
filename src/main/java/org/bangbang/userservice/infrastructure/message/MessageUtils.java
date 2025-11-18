package org.bangbang.userservice.infrastructure.message;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class MessageUtils {
    private final MessageSource messageSource;
    private final HttpServletRequest request;

    public String getMessage(String code) {
        return getMessage(code, null, null);
    }

    public String getMessage(String code, String defaultMessage) {
        return getMessage(code, null, defaultMessage);
    }

    public String getMessage(String code, Object[] args) {
        return getMessage(code, args, null);
    }

    public String getMessage(String code, Object[] args, String defaultMessage) {
        ResourceBundleMessageSource ms = (ResourceBundleMessageSource) messageSource;
        ms.setUseCodeAsDefaultMessage(false);

        try {
            Locale locale = request.getLocale();
            defaultMessage = StringUtils.hasText(defaultMessage) ? defaultMessage : "";

            return ms.getMessage(code, args, defaultMessage, locale);
        } catch (Exception e) {
            return "";
        } finally {
            ms.setUseCodeAsDefaultMessage(true);
        }
    }

    public List<String> getMessages(List<String> codes) {
        return codes == null
            ? null
            : codes.stream().map(this::getMessage)
                .filter(StringUtils::hasText)
                .toList();
    }
    }