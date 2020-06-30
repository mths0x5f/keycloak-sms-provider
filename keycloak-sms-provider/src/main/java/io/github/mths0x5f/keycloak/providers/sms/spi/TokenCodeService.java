package io.github.mths0x5f.keycloak.providers.sms.spi;

import io.github.mths0x5f.keycloak.providers.sms.constants.TokenCodeType;
import io.github.mths0x5f.keycloak.providers.sms.representations.TokenCodeRepresentation;
import org.keycloak.models.UserModel;
import org.keycloak.provider.Provider;

public interface TokenCodeService extends Provider {

    TokenCodeRepresentation ongoingProcess(UserModel user, TokenCodeType tokenCodeType);

    void persistCode(TokenCodeRepresentation tokenCode, TokenCodeType tokenCodeType);

    void validateCode(UserModel user, String code);

    void validateProcess(String tokenCodeId);
}
