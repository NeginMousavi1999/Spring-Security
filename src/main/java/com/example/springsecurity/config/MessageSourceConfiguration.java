package com.example.springsecurity.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class MessageSourceConfiguration {
    private final MessageSource messageSource;
    private final Locale locale = new Locale("fa");

    public MessageSourceConfiguration(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String code) {
        return getMessage(code, locale);
    }

    public String getMessage(String code, String locale) {
        return getMessage(code, new Locale(locale));
    }

    private String getMessage(String code, Locale locale) {
        return messageSource.getMessage(code, null, "", locale);
    }
}
