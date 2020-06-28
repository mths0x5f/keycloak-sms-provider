package io.github.mths0x5f.keycloak.providers.sms.representations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;

import java.security.SecureRandom;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenCodeRepresentation {

    private String id;
    private String phoneNumber;
    private String code;
    private String type;
    private Date createdAt;
    private Date expiresAt;
    private Boolean confirmed;

    public static TokenCodeRepresentation forUser(UserModel user) {

        user.setSingleAttribute("isPhoneNumberVerified", "false");

        TokenCodeRepresentation verificationCode = new TokenCodeRepresentation();

        verificationCode.id = KeycloakModelUtils.generateId();
        verificationCode.phoneNumber = user.getFirstAttribute("phoneNumber");
        verificationCode.code = generateVerificationCode();
        verificationCode.confirmed = false;

        return verificationCode;
    }

    private static String generateVerificationCode() {
        SecureRandom secureRandom = new SecureRandom();
        Integer code = secureRandom.nextInt(999_999);
        return String.format("%06d", code);
    }
}
