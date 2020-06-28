package io.github.mths0x5f.keycloak.providers.sms.spi;

import io.github.mths0x5f.keycloak.providers.sms.exception.MessageSendException;
import org.keycloak.provider.Provider;

public interface MessageSenderService extends Provider {

    void sendMessage(String phoneNumber, String message) throws MessageSendException;
}
