package com.jobtracker.jobtracker_app.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LocalizationUtils {
    MessageSource messageSource;

    public String getLocalizedMessage(String messageKey){
        return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }

    public String getLocalizedMessage(String messageKey, Object... args){
        return messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale());
    }

}
