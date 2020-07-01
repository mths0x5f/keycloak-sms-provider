package io.github.mths0x5f.keycloak.providers.sms.spi.impl;

import io.github.mths0x5f.keycloak.providers.sms.constants.TokenCodeType;
import io.github.mths0x5f.keycloak.providers.sms.jpa.TokenCode;
import io.github.mths0x5f.keycloak.providers.sms.representations.TokenCodeRepresentation;
import io.github.mths0x5f.keycloak.providers.sms.spi.TokenCodeService;
import io.github.mths0x5f.keycloak.requiredactions.sms.UpdatePhoneNumberRequiredAction;
import org.jboss.logging.Logger;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TemporalType;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import java.time.Instant;
import java.util.Date;

public class TokenCodeServiceImpl implements TokenCodeService {

    private static final Logger logger = Logger.getLogger(TokenCodeServiceImpl.class);
    private final KeycloakSession session;

    TokenCodeServiceImpl(KeycloakSession session) {
        this.session = session;
        if (getRealm() == null) {
            throw new IllegalStateException("The service cannot accept a session without a realm in its context.");
        }
    }

    private EntityManager getEntityManager() {
        return session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    private RealmModel getRealm() {
        return session.getContext().getRealm();
    }

    @Override
    public TokenCodeRepresentation ongoingProcess(String phoneNumber, TokenCodeType tokenCodeType) {

        try {
            TokenCode entity = getEntityManager()
                    .createNamedQuery("ongoingProcess", TokenCode.class)
                    .setParameter("realmId", getRealm().getId())
                    .setParameter("phoneNumber", phoneNumber)
                    .setParameter("now", new Date(), TemporalType.TIMESTAMP)
                    .setParameter("type", tokenCodeType.name())
                    .getSingleResult();

            TokenCodeRepresentation tokenCodeRepresentation = new TokenCodeRepresentation();

            tokenCodeRepresentation.setId(entity.getId());
            tokenCodeRepresentation.setPhoneNumber(entity.getPhoneNumber());
            tokenCodeRepresentation.setCode(entity.getCode());
            tokenCodeRepresentation.setType(entity.getType());
            tokenCodeRepresentation.setCreatedAt(entity.getCreatedAt());
            tokenCodeRepresentation.setExpiresAt(entity.getExpiresAt());
            tokenCodeRepresentation.setConfirmed(entity.getConfirmed());

            return tokenCodeRepresentation;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void persistCode(TokenCodeRepresentation tokenCode, TokenCodeType tokenCodeType, int tokenExpiresIn) {

        TokenCode entity = new TokenCode();
        Instant now = Instant.now();

        entity.setId(tokenCode.getId());
        entity.setRealmId(getRealm().getId());
        entity.setPhoneNumber(tokenCode.getPhoneNumber());
        entity.setCode(tokenCode.getCode());
        entity.setType(tokenCodeType.name());
        entity.setCreatedAt(Date.from(now));
        entity.setExpiresAt(Date.from(now.plusSeconds(tokenExpiresIn)));
        entity.setConfirmed(tokenCode.getConfirmed());

        getEntityManager().persist(entity);
    }

    @Override
    public void validateCode(UserModel user, String phoneNumber, String code) {

        TokenCodeRepresentation tokenCode = ongoingProcess(phoneNumber, TokenCodeType.VERIFY_PHONE_NUMBER);
        if (tokenCode == null) throw new BadRequestException("There is no valid ongoing verification process");

        if (!tokenCode.getCode().equals(code)) throw new ForbiddenException("Code does not match with expected value");

        logger.info(String.format("User %s correctly answered the verification code", user.getId()));
        session.users()
                .searchForUserByUserAttribute("phoneNumber", phoneNumber, session.getContext().getRealm())
                .stream().filter(u -> !u.getId().equals(user.getId()))
                .forEach(u -> {
                    logger.info(String.format("User %s also has phone number %s. Un-verifying.", u.getId(), phoneNumber));
                    u.setSingleAttribute("isPhoneNumberVerified", "false");
                });

        user.setSingleAttribute("isPhoneNumberVerified", "true");
        user.setSingleAttribute("phoneNumber", phoneNumber);
        user.removeRequiredAction(UpdatePhoneNumberRequiredAction.PROVIDER_ID);
        validateProcess(tokenCode.getId());
    }

    @Override
    public void validateProcess(String tokenCodeId) {
        TokenCode entity = getEntityManager().find(TokenCode.class, tokenCodeId);
        entity.setConfirmed(true);
        getEntityManager().persist(entity);
    }

    @Override
    public void close() {
    }
}
