package com.citamedica.backend.domain.service;

import com.citamedica.backend.exception.domain.InvalidPhoneNumberException;

public class NotificationPreferenceDomainService {

    private static final String PHONE_REGEX = "^\\+?[0-9]{9,15}$";

    public void validate(boolean smsEnabled, String phone) {
        if (smsEnabled && (phone == null || !phone.matches(PHONE_REGEX))) {
            throw new InvalidPhoneNumberException("SMS requires valid phone number");
        }
    }
}
