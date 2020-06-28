package io.github.mths0x5f.keycloak.providers.sms.spi.impl;

import io.github.mths0x5f.keycloak.providers.sms.constants.TokenCodeType;
import io.github.mths0x5f.keycloak.providers.sms.exception.MessageSendException;
import io.github.mths0x5f.keycloak.providers.sms.representations.TokenCodeRepresentation;
import io.github.mths0x5f.keycloak.providers.sms.spi.MessageSenderService;
import io.github.mths0x5f.keycloak.providers.sms.spi.PhoneMessageService;
import io.github.mths0x5f.keycloak.providers.sms.spi.TokenCodeService;
import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;

import javax.ws.rs.ServiceUnavailableException;

public class PhoneMessageServiceImpl implements PhoneMessageService {

    private static final Logger logger = Logger.getLogger(PhoneMessageServiceImpl.class);
    private final KeycloakSession session;
    private final String realmName;
    private final Scope config;
    private final String service;

    PhoneMessageServiceImpl(KeycloakSession session, Scope config) {
        this.session = session;
        this.realmName = session.getContext().getRealm().getName();
        this.config = config;
        this.service = session.listProviderIds(MessageSenderService.class)
                .stream().filter(s -> s.equals(config.get("service")))
                .findFirst().orElse(
                        session.listProviderIds(MessageSenderService.class)
                                .stream().findFirst().get()
                );
    }

    @Override
    public void close() {
    }

    private TokenCodeService getTokenCodeService() {
        return session.getProvider(TokenCodeService.class);
    }

    @Override
    public void sendVerificationCode(UserModel user) {

        String phoneNumber = user.getFirstAttribute("phoneNumber");

        if (getTokenCodeService().ongoingProcess(user, TokenCodeType.VERIFY_PHONE_NUMBER) != null) {
            logger.info(String.format("No need of sending a new verification code for %s", phoneNumber));
            return;
        }

        TokenCodeRepresentation token = TokenCodeRepresentation.forUser(user);
        final String MESSAGE = String.format("%s - verification code: %s", realmName, token.getCode());

        try {

            session.getProvider(MessageSenderService.class, service).sendMessage(phoneNumber, MESSAGE);
            getTokenCodeService().persistCode(token, TokenCodeType.VERIFY_PHONE_NUMBER);

            logger.info(String.format("Sent verification code to %s over %s for user %s",
                    phoneNumber, service, user.getId()));

        } catch (MessageSendException e) {

            logger.error(String.format("Message sending to %s failed with %s: %s",
                    phoneNumber, e.getErrorCode(), e.getErrorMessage()));
            throw new ServiceUnavailableException();
        }
    }

    @Override
    public void sendAuthenticationCode(UserModel user) {

        String phoneNumber = user.getFirstAttribute("phoneNumber");

        if (getTokenCodeService().ongoingProcess(user, TokenCodeType.OTP_MESSAGE) != null) {
            logger.info(String.format("No need of sending a new OTP code for %s", phoneNumber));
            return;
        }

        TokenCodeRepresentation token = TokenCodeRepresentation.forUser(user);
        final String MESSAGE = String.format("%s - authentication code: %s", realmName, token.getCode());

        try {

            session.getProvider(MessageSenderService.class, service).sendMessage(phoneNumber, MESSAGE);
            getTokenCodeService().persistCode(token, TokenCodeType.OTP_MESSAGE);

            logger.info(String.format("Sent OTP code to %s over %s for user %s",
                    phoneNumber, service, user.getId()));

        } catch (MessageSendException e) {

            logger.error(String.format("Message sending to %s failed with %s: %s",
                    phoneNumber, e.getErrorCode(), e.getErrorMessage()));
            throw new ServiceUnavailableException();
        }
    }
}
