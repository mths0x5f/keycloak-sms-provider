package io.github.mths0x5f.keycloak.providers.sms.spi;

import io.github.mths0x5f.keycloak.providers.sms.constants.TokenCodeType;
import io.github.mths0x5f.keycloak.providers.sms.representations.TokenCodeRepresentation;
import org.keycloak.models.UserModel;
import org.keycloak.provider.Provider;

public interface TokenCodeService extends Provider {

    TokenCodeRepresentation ongoingProcess(String phoneNumber, TokenCodeType tokenCodeType);

    boolean isAbusing(String phoneNumber, TokenCodeType tokenCodeType);

    void persistCode(TokenCodeRepresentation tokenCode, TokenCodeType tokenCodeType, int tokenExpiresIn);

    void validateCode(UserModel user, String phoneNumber, String code);

    void validateCode(UserModel user, String phoneNumber, String code, TokenCodeType tokenCodeType);

    void validateProcess(String tokenCodeId);

    void cleanUpAction(UserModel user);
}
