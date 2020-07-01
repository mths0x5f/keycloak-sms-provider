package io.github.mths0x5f.keycloak.providers.sms.spi;

import org.keycloak.provider.Provider;

public interface PhoneMessageService extends Provider {

    int sendVerificationCode(String phoneNumber);

    int sendAuthenticationCode(String phoneNumber);
}
