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

        TokenCodeRepresentation tokenCode = new TokenCodeRepresentation();

        tokenCode.id = KeycloakModelUtils.generateId();
        tokenCode.phoneNumber = user.getFirstAttribute("phoneNumber");
        tokenCode.code = generateTokenCode();
        tokenCode.confirmed = false;

        return tokenCode;
    }

    private static String generateTokenCode() {
        SecureRandom secureRandom = new SecureRandom();
        Integer code = secureRandom.nextInt(999_999);
        return String.format("%06d", code);
    }
}
