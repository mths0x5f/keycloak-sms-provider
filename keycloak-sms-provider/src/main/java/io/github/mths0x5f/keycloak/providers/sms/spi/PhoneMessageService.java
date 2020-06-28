package io.github.mths0x5f.keycloak.providers.sms.spi;

import org.keycloak.models.UserModel;
import org.keycloak.provider.Provider;

public interface PhoneMessageService extends Provider {

    void sendVerificationCode(UserModel user);

    void sendAuthenticationCode(UserModel user);
}
