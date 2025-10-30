package com.users.demo.validation;

import com.users.demo.config.ValidationProperties;
import com.users.demo.model.dto.UserRequest;
import com.users.demo.util.Messages;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UserValidator {

    private final ValidationProperties validationProperties;

    public UserValidator(ValidationProperties validationProperties) {
        this.validationProperties = validationProperties;
    }

    public void validate(UserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(Messages.INVALID_REQUEST);
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException(Messages.USER_NAME_REQUIRED);
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException(Messages.USER_EMAIL_REQUIRED);
        }

        if (!Pattern.matches(validationProperties.getEmailRegex(), request.getEmail())) {
            throw new IllegalArgumentException(Messages.USER_INVALID_EMAIL_FORMAT);
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException(Messages.USER_PASSWORD_REQUIRED);
        }

        if (!Pattern.matches(validationProperties.getPasswordRegex(), request.getPassword())) {
            throw new IllegalArgumentException(Messages.USER_INVALID_PASSWORD_FORMAT);
        }

        if (request.getPhones() != null) {
            for (var phone : request.getPhones()) {
                if (phone.getNumber() == null || phone.getNumber().isBlank()) {
                    throw new IllegalArgumentException(Messages.USER_PHONE_REQUIRED);
                }
                if (phone.getCityCode() == null || phone.getCityCode().isBlank()) {
                    throw new IllegalArgumentException(Messages.USER_PHONE_CITYCODE_REQUIRED);
                }
                if (phone.getCountryCode() == null || phone.getCountryCode().isBlank()) {
                    throw new IllegalArgumentException(Messages.USER_PHONE_COUNTRYCODE_REQUIRED);
                }
            }
        }
    }
}
